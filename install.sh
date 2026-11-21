#!/usr/bin/env bash
set -Eeuo pipefail

CONTAINER='ultra-cloud-android'
IMAGE='redroid/redroid:14.0.0_64only-latest'
PKG='net.christianbeier.droidvnc_ng'
ACCESS_KEY='de32550a6efb43a8f5d145e6c07b2cde'
BASE='/opt/ultra-cloud-phone'
DATA="$BASE/data"
SECRETS="$BASE/access.txt"

say(){ printf '\n[Ultra Cloud] %s\n' "$*"; }
fail(){ printf '\n[ERRO] %s\n' "$*" >&2; exit 1; }

[[ "$EUID" -eq 0 ]] || fail 'Execute assim: sudo bash install.sh'
[[ "$(uname -m)" == 'aarch64' || "$(uname -m)" == 'arm64' ]] || fail 'Este instalador exige servidor ARM64 (aarch64).'
[[ -r /etc/os-release ]] || fail 'Não foi possível identificar o Linux.'
. /etc/os-release
[[ "${ID:-}" == 'ubuntu' ]] || fail 'Use Ubuntu Server ARM64 22.04 ou 24.04.'

say 'Instalando Docker, ADB, curl, jq, Python e ferramentas necessárias.'
export DEBIAN_FRONTEND=noninteractive
apt-get update
apt-get install -y ca-certificates curl docker.io android-tools-adb jq python3 openssl
systemctl enable --now docker

if ! command -v tailscale >/dev/null 2>&1; then
  say 'Instalando Tailscale pelo instalador oficial.'
  curl -fsSL https://tailscale.com/install.sh -o /tmp/ultra-tailscale-install.sh
  bash /tmp/ultra-tailscale-install.sh
  rm -f /tmp/ultra-tailscale-install.sh
fi
systemctl enable --now tailscaled

TAIL_IP="$(tailscale ip -4 2>/dev/null | head -n1 || true)"
if [[ -z "$TAIL_IP" ]]; then
  say 'Autorize este servidor na sua rede privada Tailscale.'
  echo 'Abra o endereço de autenticação exibido abaixo, autorize o servidor e execute novamente: sudo bash install.sh'
  tailscale up || true
  exit 20
fi

say 'Verificando os módulos Binder exigidos pelo Redroid.'
KMOD_PACKAGE="linux-modules-extra-$(uname -r)"
if apt-cache show "$KMOD_PACKAGE" >/dev/null 2>&1; then
  apt-get install -y "$KMOD_PACKAGE" || true
fi
modprobe binder_linux devices='binder,hwbinder,vndbinder' || true
modprobe ashmem_linux 2>/dev/null || true
for dev in /dev/binder /dev/hwbinder /dev/vndbinder; do
  [[ -e "$dev" ]] || fail "O kernel desta VM não forneceu $dev. Esta máquina não é compatível com Redroid. Não instale módulos de fontes aleatórias."
done

install -d -m 0750 "$DATA" "$BASE"
say 'Baixando a imagem Android 14 ARM64 do Redroid.'
docker pull "$IMAGE"

if docker container inspect "$CONTAINER" >/dev/null 2>&1; then
  say 'Recriando o container antigo sem apagar os dados persistentes.'
  docker rm -f "$CONTAINER" >/dev/null
fi

docker run -d \
  --name "$CONTAINER" \
  --restart unless-stopped \
  --privileged \
  --publish 127.0.0.1:5555:5555 \
  --publish "$TAIL_IP:5800:5800" \
  --publish "$TAIL_IP:5900:5900" \
  --volume "$DATA:/data" \
  "$IMAGE" \
  androidboot.redroid_width=720 \
  androidboot.redroid_height=1280 \
  androidboot.redroid_dpi=320 \
  androidboot.redroid_fps=15 >/dev/null

say 'Aguardando o Android iniciar (até 5 minutos).'
READY=0
for _ in $(seq 1 60); do
  adb connect 127.0.0.1:5555 >/dev/null 2>&1 || true
  STATE="$(adb -s 127.0.0.1:5555 get-state 2>/dev/null || true)"
  BOOT="$(adb -s 127.0.0.1:5555 shell getprop sys.boot_completed 2>/dev/null | tr -d '\r' || true)"
  if [[ "$STATE" == 'device' && "$BOOT" == '1' ]]; then READY=1; break; fi
  sleep 5
done
if [[ "$READY" -ne 1 ]]; then
  docker logs --tail 120 "$CONTAINER" || true
  fail 'O Android virtual não concluiu o boot. A VM ou o kernel pode não ser compatível.'
fi

say 'Baixando o APK oficial droidVNC-NG e verificando SHA-256.'
RELEASE_JSON="$(curl -fsSL https://api.github.com/repos/bk138/droidVNC-NG/releases/latest)"
read -r APK_URL APK_SHA < <(python3 -c 'import json,sys; d=json.load(sys.stdin); a=[x for x in d.get("assets",[]) if x.get("name","").lower().endswith(".apk")]; assert a, "Release sem APK"; x=a[0]; h=x.get("digest",""); assert h.startswith("sha256:"), "Release sem SHA-256"; print(x["browser_download_url"],h.split(":",1)[1])' <<< "$RELEASE_JSON")
TMP_APK="$(mktemp /tmp/ultra-vnc-XXXXXX.apk)"
trap 'rm -f "${TMP_APK:-}"' EXIT
curl -fL --retry 3 "$APK_URL" -o "$TMP_APK"
printf '%s  %s\n' "$APK_SHA" "$TMP_APK" | sha256sum -c -
adb -s 127.0.0.1:5555 install -r "$TMP_APK"

say 'Configurando captura de tela e controle por toque.'
adb -s 127.0.0.1:5555 shell cmd appops set "$PKG" PROJECT_MEDIA allow || true
CURRENT_ACCESS="$(adb -s 127.0.0.1:5555 shell settings get secure enabled_accessibility_services | tr -d '\r')"
COMPONENT="$PKG/.InputService"
if [[ "$CURRENT_ACCESS" == 'null' || -z "$CURRENT_ACCESS" ]]; then
  NEW_ACCESS="$COMPONENT"
elif [[ ":$CURRENT_ACCESS:" == *":$COMPONENT:"* ]]; then
  NEW_ACCESS="$CURRENT_ACCESS"
else
  NEW_ACCESS="$CURRENT_ACCESS:$COMPONENT"
fi
adb -s 127.0.0.1:5555 shell settings put secure enabled_accessibility_services "$NEW_ACCESS"
adb -s 127.0.0.1:5555 shell settings put secure accessibility_enabled 1

# VNC classic authentication uses an 8-character password; the private Tailscale network adds transport encryption.
VNC_PASSWORD="$(openssl rand -hex 4)"
cat > "$SECRETS" <<CREDS
TAILSCALE_IP=$TAIL_IP
VNC_PASSWORD=$VNC_PASSWORD
VNC_PORT=5900
WEB_PORT=5800
CREDS
chmod 0600 "$SECRETS"

say 'Iniciando o servidor VNC/noVNC dentro do Android virtual.'
adb -s 127.0.0.1:5555 shell am start-foreground-service \
  -n "$PKG/.MainService" \
  -a "$PKG.ACTION_START" \
  --es "$PKG.EXTRA_ACCESS_KEY" "$ACCESS_KEY" \
  --ei "$PKG.EXTRA_PORT" 5900 \
  --es "$PKG.EXTRA_PASSWORD" "$VNC_PASSWORD" \
  --ez "$PKG.EXTRA_VIEW_ONLY" false \
  --ez "$PKG.EXTRA_FALLBACK_SCREEN_CAPTURE" true

say 'Testando a página noVNC.'
WEB_OK=0
for _ in $(seq 1 20); do
  if curl -fsS --max-time 3 "http://$TAIL_IP:5800/vnc.html" -o /dev/null; then WEB_OK=1; break; fi
  sleep 2
done
if [[ "$WEB_OK" -ne 1 ]]; then
  docker logs --tail 120 "$CONTAINER" || true
  fail 'Android iniciou, mas noVNC não respondeu. A captura de tela do Redroid pode não ser compatível com esta VM.'
fi

cat <<DONE

============================================
 ULTRA CLOUD PHONE — SERVIDOR INSTALADO
============================================
Android:    $(adb -s 127.0.0.1:5555 shell getprop ro.build.version.release | tr -d '\r')
Painel:     http://$TAIL_IP:5800/vnc.html?autoconnect=true&host=$TAIL_IP&port=5900
Senha VNC:  $VNC_PASSWORD
Dados:      $DATA

Instale o Tailscale no celular e entre na mesma conta do servidor.
Depois abra o endereço do Painel no Chrome e informe a senha VNC.
A senha também fica em: $SECRETS

ADB está somente em localhost. VNC/noVNC estão vinculados ao IP privado Tailscale.
============================================
DONE

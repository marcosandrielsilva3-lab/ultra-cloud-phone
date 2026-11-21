#!/usr/bin/env bash
set -Eeuo pipefail

IMAGE="redroid/redroid:14.0.0_64only-latest"
CONTAINER="ultra-cloud-android"
DATA_DIR="/opt/ultra-cloud-phone/data"

say() { printf '\n[Ultra Cloud] %s\n' "$*"; }
die() { printf '\n[ERRO] %s\n' "$*" >&2; exit 1; }

[[ "$EUID" -eq 0 ]] || die 'Execute: sudo ./server/install-redroid.sh'
[[ "$(uname -m)" == "aarch64" || "$(uname -m)" == "arm64" ]] || die 'Esta configuração exige ARM64.'
[[ -r /etc/os-release ]] || die 'Não foi possível identificar o sistema.'
. /etc/os-release
[[ "${ID:-}" == "ubuntu" ]] || die 'O instalador automático exige Ubuntu.'

export DEBIAN_FRONTEND=noninteractive
say 'Instalando Docker, ADB e ferramentas.'
apt-get update
apt-get install -y ca-certificates docker.io android-tools-adb kmod

KMOD_PACKAGE="linux-modules-extra-$(uname -r)"
if apt-cache show "$KMOD_PACKAGE" >/dev/null 2>&1; then
  apt-get install -y "$KMOD_PACKAGE" || true
fi

modprobe binder_linux devices="binder,hwbinder,vndbinder" || true
modprobe ashmem_linux 2>/dev/null || true

for dev in /dev/binder /dev/hwbinder /dev/vndbinder; do
  [[ -e "$dev" ]] || die "Dispositivo Binder ausente: $dev. Consulte server/check-host.sh; não instale módulos de fontes desconhecidas."
done

systemctl enable --now docker
docker info >/dev/null 2>&1 || die 'Docker não respondeu.'

say "Baixando imagem: $IMAGE"
docker pull "$IMAGE"
install -d -m 0750 "$DATA_DIR"

if docker container inspect "$CONTAINER" >/dev/null 2>&1; then
  say 'Container já existe; iniciando sem apagar os dados.'
  docker start "$CONTAINER" >/dev/null || true
else
  say 'Criando Android virtual; ADB ficará limitado ao localhost.'
  docker run -d \
    --name "$CONTAINER" \
    --restart unless-stopped \
    --privileged \
    --publish 127.0.0.1:5555:5555 \
    --volume "$DATA_DIR:/data" \
    "$IMAGE" \
    androidboot.redroid_width=720 \
    androidboot.redroid_height=1280 \
    androidboot.redroid_dpi=320 \
    androidboot.redroid_fps=15 >/dev/null
fi

say 'Aguardando o Android iniciar (até 3 minutos).'
READY=false
for attempt in $(seq 1 36); do
  adb connect 127.0.0.1:5555 >/dev/null 2>&1 || true
  if [[ "$(adb -s 127.0.0.1:5555 get-state 2>/dev/null || true)" == "device" ]]; then
    BOOT="$(adb -s 127.0.0.1:5555 shell getprop sys.boot_completed 2>/dev/null | tr -d '\r' || true)"
    if [[ "$BOOT" == "1" ]]; then READY=true; break; fi
  fi
  sleep 5
done

if [[ "$READY" != true ]]; then
  docker logs --tail 100 "$CONTAINER" || true
  die 'Android não confirmou a inicialização. Consulte os logs.'
fi

say 'Android virtual iniciou.'
printf 'Android: '
adb -s 127.0.0.1:5555 shell getprop ro.build.version.release || true
printf 'ABI: '
adb -s 127.0.0.1:5555 shell getprop ro.product.cpu.abi || true
say 'ADB vinculado a 127.0.0.1:5555. Não abra a porta no firewall público.'

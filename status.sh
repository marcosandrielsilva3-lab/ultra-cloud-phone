#!/usr/bin/env bash
set -Eeuo pipefail
[[ "$EUID" -eq 0 ]] || { echo 'Use: sudo bash status.sh'; exit 1; }
BASE='/opt/ultra-cloud-phone'
SECRETS="$BASE/access.txt"
echo '=== ULTRA CLOUD PHONE ==='
docker ps -a --filter 'name=^/ultra-cloud-android$' --format 'Container: {{.Status}}' || true
if [[ -r "$SECRETS" ]]; then
  # shellcheck disable=SC1090
  source "$SECRETS"
  echo "Tailscale: $TAILSCALE_IP"
  echo "Painel: http://$TAILSCALE_IP:$WEB_PORT/vnc.html?autoconnect=true&host=$TAILSCALE_IP&port=$VNC_PORT"
  echo "Senha VNC: guardada em $SECRETS (permissão 600)"
else
  echo 'Execute sudo bash install.sh para instalar.'
fi
adb connect 127.0.0.1:5555 >/dev/null 2>&1 || true
printf 'Boot Android: '
adb -s 127.0.0.1:5555 shell getprop sys.boot_completed 2>/dev/null | tr -d '\r' || echo 'indisponível'

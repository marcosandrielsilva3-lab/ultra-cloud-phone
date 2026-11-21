#!/usr/bin/env bash
set -Eeuo pipefail
[[ "$EUID" -eq 0 ]] || { echo 'Use: sudo bash start.sh'; exit 1; }
BASE='/opt/ultra-cloud-phone'
CONTAINER='ultra-cloud-android'
PKG='net.christianbeier.droidvnc_ng'
ACCESS_KEY='de32550a6efb43a8f5d145e6c07b2cde'
SECRETS="$BASE/access.txt"
[[ -r "$SECRETS" ]] || { echo 'Instale primeiro: sudo bash install.sh'; exit 1; }
# shellcheck disable=SC1090
source "$SECRETS"
docker start "$CONTAINER" >/dev/null
for _ in $(seq 1 60); do
  adb connect 127.0.0.1:5555 >/dev/null 2>&1 || true
  BOOT="$(adb -s 127.0.0.1:5555 shell getprop sys.boot_completed 2>/dev/null | tr -d '\r' || true)"
  [[ "$BOOT" == '1' ]] && break
  sleep 5
done
[[ "$(adb -s 127.0.0.1:5555 shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')" == '1' ]] || { echo 'Android não iniciou. Execute sudo bash status.sh.'; exit 1; }
adb -s 127.0.0.1:5555 shell am start-foreground-service -n "$PKG/.MainService" -a "$PKG.ACTION_START" --es "$PKG.EXTRA_ACCESS_KEY" "$ACCESS_KEY" --ei "$PKG.EXTRA_PORT" "$VNC_PORT" --es "$PKG.EXTRA_PASSWORD" "$VNC_PASSWORD" --ez "$PKG.EXTRA_VIEW_ONLY" false
printf 'Cloud Phone ligado. Abra: http://%s:%s/vnc.html?autoconnect=true&host=%s&port=%s\n' "$TAILSCALE_IP" "$WEB_PORT" "$TAILSCALE_IP" "$VNC_PORT"

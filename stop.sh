#!/usr/bin/env bash
set -Eeuo pipefail
[[ "$EUID" -eq 0 ]] || { echo 'Use: sudo bash stop.sh'; exit 1; }
PKG='net.christianbeier.droidvnc_ng'
ACCESS_KEY='de32550a6efb43a8f5d145e6c07b2cde'
adb -s 127.0.0.1:5555 shell am start-foreground-service -n "$PKG/.MainService" -a "$PKG.ACTION_STOP" --es "$PKG.EXTRA_ACCESS_KEY" "$ACCESS_KEY" >/dev/null 2>&1 || true
docker stop ultra-cloud-android >/dev/null
echo 'Cloud Phone desligado. Aplicativos e dados foram preservados.'

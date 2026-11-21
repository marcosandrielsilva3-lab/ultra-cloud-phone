#!/usr/bin/env bash
set -Eeuo pipefail
NAME="ultra-cloud-android"
command -v docker >/dev/null 2>&1 || { echo 'Docker não instalado.'; exit 1; }
docker container inspect "$NAME" >/dev/null 2>&1 || { echo 'Container ainda não criado.'; exit 1; }

echo '=== Container ==='
docker ps -a --filter "name=^/${NAME}$" --format 'Nome: {{.Names}} | Estado: {{.Status}}'
echo
echo '=== Porta ADB (esperado: 127.0.0.1:5555) ==='
docker port "$NAME" 5555/tcp || true
echo
if command -v adb >/dev/null 2>&1; then
  adb connect 127.0.0.1:5555 >/dev/null 2>&1 || true
  adb -s 127.0.0.1:5555 get-state 2>/dev/null || echo 'ADB ainda não está pronto.'
  adb -s 127.0.0.1:5555 shell getprop sys.boot_completed 2>/dev/null || true
  adb -s 127.0.0.1:5555 shell getprop ro.build.version.release 2>/dev/null || true
else
  echo 'ADB não instalado no host.'
fi

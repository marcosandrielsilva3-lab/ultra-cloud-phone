#!/usr/bin/env bash
set -Eeuo pipefail
NAME="ultra-cloud-android"
[[ "$EUID" -eq 0 ]] || { echo 'Execute com sudo.'; exit 1; }
if docker container inspect "$NAME" >/dev/null 2>&1; then
  docker stop "$NAME"
  echo 'Android parado; dados persistentes preservados.'
else
  echo 'Container não encontrado.'
fi

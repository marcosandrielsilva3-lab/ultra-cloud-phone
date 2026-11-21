#!/usr/bin/env bash
set -Eeuo pipefail
NAME="ultra-cloud-android"
[[ "$EUID" -eq 0 ]] || { echo 'Execute com sudo.'; exit 1; }
docker container inspect "$NAME" >/dev/null 2>&1 || { echo 'Container não existe. Execute install-redroid.sh primeiro.'; exit 1; }
docker start "$NAME"

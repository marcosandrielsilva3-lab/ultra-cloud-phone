#!/usr/bin/env bash
set -Eeuo pipefail
NAME="ultra-cloud-android"
command -v docker >/dev/null 2>&1 || { echo 'Docker não instalado.'; exit 1; }
docker container inspect "$NAME" >/dev/null 2>&1 || { echo 'Container não encontrado.'; exit 1; }
docker logs --tail 200 --follow "$NAME"

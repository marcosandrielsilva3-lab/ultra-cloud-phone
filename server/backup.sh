#!/usr/bin/env bash
set -Eeuo pipefail
[[ "$EUID" -eq 0 ]] || { echo 'Execute com sudo.'; exit 1; }
DATA_DIR="/opt/ultra-cloud-phone/data"
BASE_DIR="/opt/ultra-cloud-phone"
BACKUP_DIR="$BASE_DIR/backups"
[[ -d "$DATA_DIR" ]] || { echo 'Dados ainda não existem.'; exit 1; }
install -d -m 0750 "$BACKUP_DIR"
STAMP="$(date -u +%Y%m%d-%H%M%S)"
ARCHIVE="$BACKUP_DIR/ultra-cloud-data-$STAMP.tar.gz"
WAS_RUNNING=false
if docker inspect -f '{{.State.Running}}' ultra-cloud-android 2>/dev/null | grep -q true; then
  WAS_RUNNING=true
  docker stop ultra-cloud-android >/dev/null
fi
cleanup() {
  if [[ "$WAS_RUNNING" == true ]]; then docker start ultra-cloud-android >/dev/null || true; fi
}
trap cleanup EXIT
tar -czf "$ARCHIVE" -C "$BASE_DIR" data
chmod 0600 "$ARCHIVE"
echo "Backup criado: $ARCHIVE"

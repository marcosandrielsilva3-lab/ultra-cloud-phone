#!/usr/bin/env bash
set -Eeuo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
for script in "$ROOT"/server/*.sh; do bash -n "$script"; done
grep -F -- '--publish 127.0.0.1:5555:5555' "$ROOT/server/install-redroid.sh" >/dev/null
if grep -E -- '(-p|--publish)[[:space:]]+5555:5555' "$ROOT/server/install-redroid.sh" >/dev/null; then
  echo 'ERRO: ADB publicado sem localhost.' >&2
  exit 1
fi
grep -F 'redroid/redroid:14.0.0_64only-latest' "$ROOT/server/install-redroid.sh" >/dev/null
grep -F 'sys.boot_completed' "$ROOT/server/install-redroid.sh" >/dev/null
echo 'Verificações estáticas aprovadas.'

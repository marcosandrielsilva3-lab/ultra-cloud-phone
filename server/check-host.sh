#!/usr/bin/env bash
set -Eeuo pipefail

ok()   { printf '\033[32m[OK]\033[0m %s\n' "$*"; }
warn() { printf '\033[33m[AVISO]\033[0m %s\n' "$*"; }
fail() { printf '\033[31m[ERRO]\033[0m %s\n' "$*"; }

echo '=== Ultra Cloud Phone: diagnóstico ==='
printf 'Arquitetura: '; uname -m
printf 'Kernel: '; uname -r
ARCH="$(uname -m)"
if [[ "$ARCH" == "aarch64" || "$ARCH" == "arm64" ]]; then
  ok 'ARM64 detectado.'
else
  fail 'Esta configuração inicial exige ARM64.'
fi

if [[ -r /etc/os-release ]]; then
  . /etc/os-release
  printf 'Sistema: %s\n' "${PRETTY_NAME:-Linux}"
  [[ "${ID:-}" == "ubuntu" ]] && ok 'Ubuntu detectado.' || warn 'Instalador automático preparado para Ubuntu.'
else
  warn 'Não foi possível identificar a distribuição.'
fi

if command -v docker >/dev/null 2>&1 && docker info >/dev/null 2>&1; then
  ok 'Docker instalado e ativo.'
elif command -v docker >/dev/null 2>&1; then
  warn 'Docker existe, mas não responde.'
else
  warn 'Docker ainda não instalado.'
fi

if command -v modinfo >/dev/null 2>&1 && modinfo binder_linux >/dev/null 2>&1; then
  ok 'Módulo binder_linux encontrado.'
else
  warn 'Módulo binder_linux não encontrado por modinfo.'
fi

for dev in /dev/binder /dev/hwbinder /dev/vndbinder; do
  [[ -e "$dev" ]] && ok "$dev encontrado." || warn "$dev ausente."
done

echo
echo 'Memória:'
free -h 2>/dev/null || true
echo
echo 'Armazenamento:'
df -h / 2>/dev/null || true
echo
echo 'O diagnóstico não garante compatibilidade total do Redroid.'

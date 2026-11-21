# Solução de problemas

## Arquitetura diferente de ARM64
O instalador atual exige `aarch64`/ARM64. Não tente forçar a imagem em outra arquitetura.

## Binder ausente
O kernel da VM pode não fornecer `binder_linux` ou os dispositivos `/dev/binder`, `/dev/hwbinder` e `/dev/vndbinder`. Pare a instalação e envie a saída de `sudo ./server/check-host.sh` sem dados pessoais. Não baixe módulos de kernel aleatórios.

## Docker não inicia
Confira `sudo systemctl status docker` e `sudo journalctl -u docker --no-pager -n 100`.

## Android não conclui boot
Execute `sudo ./server/status.sh` e `sudo ./server/logs.sh`. O log do container pode mostrar incompatibilidade com o kernel.

## VM gratuita indisponível
A capacidade Always Free não é garantida em todas as regiões. Não mude para uma oferta paga sem autorização do titular da conta.

## Segurança
Não exponha a porta ADB 5555. Ao compartilhar logs, remova IP público, nomes de usuário e qualquer dado pessoal.

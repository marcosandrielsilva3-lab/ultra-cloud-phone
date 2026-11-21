# Ultra Cloud Phone

Projeto experimental para executar um Android virtual em uma VM Linux e, numa etapa posterior, transmitir a tela para o celular.

**Estado real da versão:** este repositório instala e verifica o Android virtual Redroid em um servidor compatível. Ainda não oferece streaming de vídeo/toque para o celular, não é um serviço de Cloud Gaming pronto e não contém APK cliente.

## O que já está incluído
- diagnóstico de arquitetura, kernel, Binder, memória e disco;
- instalador para Ubuntu Server ARM64;
- Docker e ADB;
- Android 14 Redroid em container com dados persistentes;
- ADB publicado somente em `127.0.0.1:5555`;
- comandos de iniciar, parar, status, logs e backup;
- GitHub Actions para validar scripts e empacotar o código.

## Requisitos
- VM Ubuntu Server ARM64 (`aarch64`), preferencialmente 22.04/24.04;
- acesso SSH e permissão sudo;
- kernel com Binder e dispositivos `/dev/binder`, `/dev/hwbinder`, `/dev/vndbinder`;
- espaço em disco e RAM suficientes;
- Docker e imagem Redroid baixados da internet pelo instalador.

A Oracle Cloud Always Free pode ser testada, mas a disponibilidade de máquinas ARM gratuitas depende da região, das cotas e da capacidade. O uso gratuito não é garantido. Não escolha uma forma paga para contornar falta de capacidade.

## Instalação no servidor
Depois de criar uma VM Linux compatível e entrar nela por SSH:

```bash
git clone https://github.com/marcosandrielsilva3-lab/ultra-cloud-phone.git
cd ultra-cloud-phone
chmod +x server/*.sh tests/*.sh
sudo ./server/check-host.sh
```

Leia o diagnóstico. Se arquitetura, kernel ou Binder não forem compatíveis, pare aqui e consulte [solução de problemas](docs/troubleshooting.md). Não instale módulos de kernel aleatórios.

Se o diagnóstico estiver adequado:

```bash
sudo ./server/install-redroid.sh
sudo ./server/status.sh
```

Comandos:
```bash
sudo ./server/start.sh
sudo ./server/stop.sh
sudo ./server/status.sh
sudo ./server/logs.sh
sudo ./server/backup.sh
```

Os dados persistentes ficam em `/opt/ultra-cloud-phone/data`; backups em `/opt/ultra-cloud-phone/backups`.

## Segurança
- **Nunca abra a porta 5555 no firewall público.** O ADB fica vinculado a `127.0.0.1`.
- Não publique senhas, chaves SSH privadas, tokens ou dados de pagamento no GitHub.
- O Redroid usa modo privilegiado do Docker para acessar recursos do kernel; execute somente em uma VM dedicada e confiável.
- A conexão remota futura deverá usar túnel SSH ou VPN privada e autenticação.

## Limitações importantes
1. Esta versão ainda não transmite vídeo/áudio nem envia toques do celular para o Android virtual.
2. Redroid é a camada Android; streaming remoto seguro é uma etapa separada que ainda precisa ser implementada e testada.
3. Não há garantia de Play Store, Play Integrity ou compatibilidade com todos os jogos.
4. Sem GPU dedicada, jogos 3D podem ter desempenho baixo. Não há promessa de FPS, ausência de lag ou compatibilidade com Free Fire/Roblox.
5. O streaming também depende de internet, latência e decodificação de vídeo no telefone.

## GitHub Actions
A cada push ou pull request, o workflow **Validate and package** verifica a sintaxe Bash, executa testes estáticos e cria o artefato ZIP `ultra-cloud-phone-server-kit`. Isso valida o código; não cria nem mantém uma VM Android ligada.

## Próximas etapas
1. validar o diagnóstico em uma VM real;
2. iniciar Android e confirmar `sys.boot_completed=1`;
3. implementar uma ponte de vídeo e entrada segura;
4. testar streaming real no telefone;
5. somente então decidir se vale criar um APK cliente.

Veja [arquitetura](docs/architecture.md) e [solução de problemas](docs/troubleshooting.md).

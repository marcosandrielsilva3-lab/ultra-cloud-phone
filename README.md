# Ultra Cloud Phone

**Versão 0.3 — primeira tentativa de Cloud Phone controlável pelo navegador.**

O projeto monta um Android virtual no servidor e permite controlá-lo pelo navegador do celular usando Redroid + droidVNC-NG/noVNC + Tailscale.

## Instalação simplificada

**Importante:** GitHub guarda o instalador, mas não é o servidor que executa Android. É necessária uma VM de nuvem Linux. O instalador foi preparado para Ubuntu Server 22.04/24.04 ARM64 com os módulos Binder exigidos pelo Redroid.

1. Crie uma VM Ubuntu ARM64 e conecte-se a ela por SSH.
2. Na VM, execute:

```bash
sudo apt-get update && sudo apt-get install -y git
git clone https://github.com/marcosandrielsilva3-lab/ultra-cloud-phone.git
cd ultra-cloud-phone
sudo bash install.sh
```

3. Na primeira execução, autorize o servidor no Tailscale usando o link mostrado. Depois execute novamente `sudo bash install.sh`.
4. Instale o Tailscale no seu celular e entre na mesma conta.
5. Ao final, o instalador mostrará o endereço noVNC e a senha. Abra o endereço no Chrome do celular.

O Tailscale Personal tem plano gratuito para uso pessoal (até 6 usuários). Consulte os termos atuais antes de criar a rede: https://tailscale.com/pricing

## Comandos

```bash
sudo bash start.sh
sudo bash status.sh
sudo bash stop.sh
```

Os aplicativos e dados do Android virtual ficam em `/opt/ultra-cloud-phone/data` e são preservados ao desligar/iniciar o container. A senha é guardada em `/opt/ultra-cloud-phone/access.txt`, legível apenas por root.

## O que a instalação configura

- Redroid Android 14 ARM64 em container Docker.
- Resolução inicial 720x1280 e limite configurado de 15 FPS para servidor sem GPU.
- APK oficial mais recente do droidVNC-NG, baixado do release do projeto e validado pelo SHA-256 publicado.
- Serviço VNC com visualizador noVNC integrado.
- Tailscale para conexão privada entre servidor e telefone.
- ADB vinculado somente a `127.0.0.1:5555`.
- Portas 5800/5900 vinculadas somente ao IP privado do Tailscale.

## Limitações reais

- O instalador falha de propósito se o host não for ARM64 ou não tiver `/dev/binder`, `/dev/hwbinder` e `/dev/vndbinder`.
- A Oracle Cloud Always Free não garante capacidade em todas as regiões nem compatibilidade do kernel com Binder.
- A captura de tela do droidVNC-NG pode não funcionar em todo ambiente Android virtualizado. O instalador testa se a página noVNC responde; isso ainda não substitui um teste visual real do vídeo e do toque.
- A VM gratuita não fornece GPU dedicada garantida. Jogos 3D podem ficar lentos, e não há promessa de FPS alto, ausência de lag, Play Integrity ou compatibilidade com anti-cheat.
- Alguns jogos detectam emuladores/ambientes virtuais ou bloqueiam a execução.
- Esta versão usa o navegador; ainda não existe APK próprio Ultra Cloud.

## Segurança

- **Nunca exponha ADB (5555) à internet.**
- Não abra 5800/5900 no IP público. O instalador só publica essas portas no IP Tailscale.
- Use senha VNC aleatória. Não compartilhe o arquivo `access.txt`.
- O droidVNC-NG informa que o protocolo VNC não criptografa a sessão inteira; o tráfego deve permanecer dentro da rede privada Tailscale.
- Nunca publique tokens, senhas, chaves SSH ou dados de pagamento no repositório.

## Arquivos

O projeto foi simplificado para os quatro scripts de operação e este README. O workflow dentro de `.github/workflows` valida o shell e prepara um ZIP de instalação com os arquivos na raiz, sem pastas internas desnecessárias.

## Componentes e documentação

- Redroid: https://github.com/remote-android/redroid-doc
- droidVNC-NG: https://github.com/bk138/droidVNC-NG
- Tailscale: https://tailscale.com/docs

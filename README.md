# Ultra Cloud Phone

**Aplicativo Android cliente v0.2.0 + instalador experimental do servidor.**

O aplicativo abre o painel noVNC de um Android virtual remoto. Ele não cria sozinho uma máquina na nuvem: primeiro é necessário ter uma VM Linux compatível, instalar o servidor Ultra Cloud Phone nela e ligá-la.

## Baixar o aplicativo Android

Abra a página de versões e baixe somente **UltraCloudPhone.apk**:
https://github.com/marcosandrielsilva3-lab/ultra-cloud-phone/releases/latest

Não baixe **Source code (ZIP)**: esse arquivo é o código-fonte, não é o aplicativo instalável.

## O que mudou na versão 0.2.0

- Corrigido o layout da tela inicial e os textos que apareciam sem largura.
- Explicação visível de que o campo recebe o endereço do servidor, não um vídeo.
- Botão **AINDA NÃO TENHO UM SERVIDOR** com instruções e link para este guia.
- Mensagens de erro mais claras ao tentar conectar.
- A tela remota mantém o telefone acordado enquanto a sessão está aberta.

## O que colocar no campo de endereço

O campo recebe o endereço do painel **noVNC** impresso pelo instalador do servidor. O formato é parecido com:

```
http://100.x.x.x:5800/vnc.html
```

Esse é apenas um exemplo de formato, não um endereço real. Não cole link de vídeo, link do GitHub ou um IP inventado. Sem o servidor instalado e ligado, deixe o campo vazio.

O app também aceita somente o IP Tailscale, e completa a porta e o caminho padrão automaticamente.

## Preparar o servidor Android virtual

O GitHub hospeda os arquivos do projeto, mas não é a máquina que executa o Android. É necessária uma VM de nuvem compatível.

O instalador foi preparado experimentalmente para Ubuntu Server 22.04/24.04 ARM64 com os dispositivos Binder disponíveis no kernel.

1. Crie uma VM Ubuntu ARM64 compatível e conecte-se a ela por SSH.
2. Na VM, execute:

```bash
sudo apt-get update && sudo apt-get install -y git
git clone https://github.com/marcosandrielsilva3-lab/ultra-cloud-phone.git
cd ultra-cloud-phone
sudo bash install.sh
```

3. Na primeira execução, autorize o servidor no Tailscale usando o link que aparecer. Depois execute novamente `sudo bash install.sh`.
4. Instale o Tailscale no celular e entre na mesma rede privada.
5. Se a instalação terminar com sucesso, copie o endereço noVNC e a senha VNC exibidos no final.
6. Abra o Ultra Cloud Phone, cole o endereço real noVNC e conecte.

## Comandos do servidor

```bash
sudo bash start.sh
sudo bash status.sh
sudo bash stop.sh
```

Os dados Android ficam em `/opt/ultra-cloud-phone/data`.

## Limitações e segurança

- O instalador é experimental. Ele verifica ARM64, Ubuntu e dispositivos Binder; se o kernel não fornecer o suporte necessário, a instalação para.
- A disponibilidade de uma VM gratuita ARM64 e a compatibilidade do kernel não são garantidas.
- Redroid fornece a camada Android; VNC/noVNC é uma ponte separada. A transmissão precisa ser validada numa VM real.
- A captura fallback do droidVNC-NG não substitui aceleração gráfica. Sem GPU compatível, o desempenho pode ser limitado.
- Não há garantia de FPS, ausência de lag, Play Integrity ou compatibilidade com todos os jogos.
- ADB fica somente em localhost. Não abra a porta 5555 na internet.
- VNC/noVNC devem permanecer dentro da rede privada Tailscale. Não exponha as portas 5800/5900 no IP público.
- O APK é o cliente remoto, não contém um Android virtual nem jogos.

## Projeto

- Redroid: https://github.com/remote-android/redroid-doc
- droidVNC-NG: https://github.com/bk138/droidVNC-NG
- Tailscale: https://tailscale.com/docs

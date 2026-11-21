# Ultra Cloud Phone

**APK cliente v0.1.3 + instalador do servidor experimental.**

O aplicativo Android abre a interface noVNC do seu Android virtual remoto dentro do próprio app. Ele não cria sozinho a VM; primeiro é necessário preparar um servidor Linux compatível.

## Baixar o aplicativo Android
O arquivo instalável é **UltraCloudPhone.apk**. Abra a aba **Releases** do GitHub e baixe esse APK diretamente (não baixe o código-fonte ZIP):
https://github.com/marcosandrielsilva3-lab/ultra-cloud-phone/releases/latest

O APK é um cliente WebView para noVNC. Ele guarda o último endereço, abre a interface de controle e encaminha os toques pela página noVNC. No primeiro uso, ainda não haverá servidor configurado para conectar.

## O que o APK faz
- Interface Android própria, sem mostrar pastas do projeto.
- Campo para inserir o endereço noVNC do servidor.
- Aceita IP Tailscale simples (adiciona porta 5800 e caminho /vnc.html) ou URL completa.
- Abre noVNC dentro do app, com JavaScript e armazenamento web ativados.
- Guarda o último endereço para facilitar reconexão.
- Botão voltar/desconectar e indicação de erro de conexão.

## Preparar o servidor Android virtual
O GitHub hospeda o código e o APK; não é a máquina que executa o Android. É necessária uma VM de nuvem. O instalador foi preparado para Ubuntu Server 22.04/24.04 ARM64 com Binder no kernel.

1. Crie uma VM Ubuntu ARM64 e conecte-se por SSH.
2. Na VM, execute:

```bash
sudo apt-get update && sudo apt-get install -y git
git clone https://github.com/marcosandrielsilva3-lab/ultra-cloud-phone.git
cd ultra-cloud-phone
sudo bash install.sh
```

3. Na primeira execução, autorize o servidor no Tailscale com o link mostrado. Depois execute novamente `sudo bash install.sh`.
4. Instale o Tailscale no celular e entre na mesma conta do servidor.
5. Guarde o endereço noVNC e a senha VNC que o instalador mostrar.
6. Abra o APK Ultra Cloud Phone, cole o endereço noVNC e conecte.

## Comandos do servidor

```bash
sudo bash start.sh
sudo bash status.sh
sudo bash stop.sh
```

Os dados Android ficam em `/opt/ultra-cloud-phone/data`.

## Limitações e segurança
- O instalador exige ARM64 e os dispositivos Binder. Se eles estiverem ausentes, ele deve parar.
- A capacidade da Oracle Cloud Always Free e a compatibilidade do kernel não são garantidas.
- Redroid fornece a camada Android; VNC/noVNC é uma ponte separada. A transmissão precisa ser confirmada em servidor real.
- A captura fallback do droidVNC-NG não substitui aceleração gráfica e pode ser lenta.
- Não há GPU gratuita garantida. Não prometemos FPS alto, ausência de lag, Play Integrity ou compatibilidade com todos os jogos.
- ADB fica em localhost. Não abra a porta 5555 na internet.
- VNC/noVNC devem permanecer dentro da rede privada Tailscale. Não exponha as portas 5800/5900 no IP público.
- O arquivo APK atual é um cliente remoto. Ainda não é um Cloud Phone pronto sem servidor.

## Projeto
- Redroid: https://github.com/remote-android/redroid-doc
- droidVNC-NG: https://github.com/bk138/droidVNC-NG
- Tailscale: https://tailscale.com/docs

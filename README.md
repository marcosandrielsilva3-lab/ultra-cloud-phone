# Ultra Cloud Phone

**Aplicativo Android cliente.**

## Download

Baixe e instale somente o arquivo **UltraCloudPhone.apk** na página de versões:

https://github.com/marcosandrielsilva3-lab/ultra-cloud-phone/releases/latest

Não há instalador de servidor para instalar no Android. Arquivos `.sh`, ZIP e código-fonte não são aplicativos Android.

## O que o aplicativo faz

O Ultra Cloud Phone abre, dentro do app, o painel noVNC de um Android virtual remoto. Ele permite informar o endereço do servidor, conectar e controlar a tela remota.

O aplicativo não cria nem hospeda sozinho uma máquina Android na nuvem. Para a conexão funcionar, ainda é necessário configurar uma máquina Linux compatível em um serviço de nuvem e executar nela os componentes do servidor.

## Endereço do servidor

O campo do aplicativo recebe o endereço noVNC fornecido pelo servidor depois da configuração. O formato costuma ser parecido com:

```
http://100.x.x.x:5800/vnc.html
```

Esse endereço é apenas um exemplo, não é um servidor real. Não cole link de vídeo ou link do GitHub. Até existir um servidor configurado e ligado, deixe o campo vazio.

## Servidor (etapa técnica separada)

O servidor experimental foi preparado para Ubuntu Server 22.04/24.04 ARM64 com dispositivos Binder disponíveis no kernel. A instalação deve ser feita no terminal SSH da VM, não no telefone.

Os scripts técnicos estão no repositório para quem estiver configurando a VM:

```bash
git clone https://github.com/marcosandrielsilva3-lab/ultra-cloud-phone.git
cd ultra-cloud-phone
sudo bash install.sh
```

O processo exige autorizar o dispositivo servidor no Tailscale e executar novamente o instalador. O celular também precisa entrar na mesma rede privada.

## Limitações e segurança

- A VM gratuita ARM64 pode não estar disponível, e o kernel pode não oferecer Binder.
- Redroid fornece a camada Android; VNC/noVNC é uma ponte separada. A transmissão precisa ser validada numa VM real.
- A captura fallback não substitui aceleração gráfica. Sem GPU compatível, o desempenho pode ser limitado.
- Não há garantia de FPS, ausência de lag, Play Integrity ou compatibilidade com todos os jogos.
- ADB fica somente em localhost. Não abra a porta 5555 na internet.
- VNC/noVNC devem permanecer dentro da rede privada Tailscale. Não exponha as portas 5800/5900 no IP público.
- O APK é o cliente remoto, não contém um Android virtual nem jogos.

## Componentes do projeto

- Redroid: https://github.com/remote-android/redroid-doc
- droidVNC-NG: https://github.com/bk138/droidVNC-NG
- Tailscale: https://tailscale.com/docs

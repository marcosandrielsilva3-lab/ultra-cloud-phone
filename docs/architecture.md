# Arquitetura

## Etapa implementada
Celular (sem cliente nesta versão) -> conexão remota futura protegida -> VM Ubuntu ARM64 -> Docker -> Redroid Android 14.

O container mantém os dados em `/opt/ultra-cloud-phone/data`. O ADB do host escuta somente em `127.0.0.1:5555` para evitar exposição pública.

## Etapa ainda não implementada
É necessário adicionar uma ponte de streaming que capture e codifique a tela Android, transporte o vídeo com autenticação/criptografia e encaminhe eventos de toque, arraste e teclado. Também são necessários reconexão, gestão de sessão e testes de latência em hardware real.

Não declarar o produto como Cloud Phone funcional até comprovar vídeo e entrada no celular. A execução do Android no servidor, isoladamente, não transmite o jogo.

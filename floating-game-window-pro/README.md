# Floating Game Window Pro — 0.1.0

Módulo Android experimental com lista de apps, inicialização e controles flutuantes.

**Limitação essencial:** Android não permite a um app comum inserir a Activity de outro app numa View própria nem redimensionar a tela de outro jogo. Esta versão move/redimensiona apenas o painel de controle. O jogo continua no app original. Para janela real, use Multi-Window/Pop-up View do fabricante quando disponível.

Recursos: lista de apps iniciáveis, iniciar jogo, sobreposição, painel arrastável, aumentar/diminuir painel, minimizar em bolha e restaurar. Fechar o painel não envia force-stop ao jogo.

Compilação: JDK 17, Gradle 8.9, Android SDK 35; executar gradle assembleDebug nesta pasta.

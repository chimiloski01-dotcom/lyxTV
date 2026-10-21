# LIX TV — Kodi Edition (protótipo 0.1)
Este diretório inicia a implementação baseada em Kodi, mantendo o app Android Java v0.6 separado e intacto.

## Compatibilidade
O complemento em Python 3 destina-se a Kodi 19+ (Matrix ou posterior); não funciona em Kodi 18 Leia.
**Não é um APK independente**: exige Kodi já instalado. Android 6 pode não ser compatível com as edições atuais do Kodi. Alvo solicitado para a futura distribuição Kodi: **Android 13 / ARM64 (arm64-v8a)**. O processador RK3528 suporta 64 bits, mas a ABI do Android ainda não foi confirmada no aparelho; não considerar a compatibilidade da APK comprovada antes de testar.

## Recursos efetivamente implementados
- Navegação por controle remoto através da interface do Kodi.
- Filmes demonstrativos abertos com reprodução pelo player do Kodi (sujeitos à disponibilidade dos links).
- Lista pessoal de canais via playlist M3U HTTPS configurada pelo usuário, reprodução resolvida pelo player do Kodi.
- Limite de tamanho de resposta e timeout para playlist.
- Nenhum stream comercial ou addon Vikings foi copiado.

## Teste
Compactar **o diretório plugin.video.lixtv** como ZIP mantendo addon.xml na raiz da pasta e instalar no Kodi em Add-ons > Instalar de um arquivo ZIP. Abrir Complementos de vídeo > LIX TV.
Depois acessar Configurações para adicionar URL M3U com autorização.
A lista de canais é carregada dinamicamente e a disponibilidade de cada stream depende do fornecedor.

## Próxima fase
Criar skin Kodi com navegação cinematográfica e marca Weblyxx, portar favoritos, explorar conexões TMDB e verificar requisitos legais e técnicos de cada complemento. Adaptar uma distribuição Kodi requer build nativa por CPU/Android e testes em hardware; não resulta do Gradle deste repositório.

## Decisão de plataforma — 20/10/2026
- Prioridade de build: **ARM64 / arm64-v8a**, Android TV 13, Rockchip RK3528 (H96 Max).
- A distribuição personalizada do Kodi exigirá código-fonte e pipeline de compilação nativa para ARM64, com marca/skin própria. O projeto Android Java atual não se transforma automaticamente em um fork Kodi.
- A compilação Gradle existente continua construindo apenas o aplicativo Android Java; não produz uma Kodi Edition ARM64.
- Validação pendente: ABI do Android, execução do Kodi oficial e reprodução no aparelho.

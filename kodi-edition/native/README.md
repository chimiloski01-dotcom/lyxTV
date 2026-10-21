# Build nativo ARM64 — LIX TV (infraestrutura)
O script prepara a compilação **nativa** do motor Kodi para Android ARM64, a partir do código oficial. Não gera, ainda, o produto LIX TV personalizado.

Documentação oficial: https://github.com/xbmc/xbmc/blob/master/docs/README.Android.md

Requisitos: host Linux configurado com Android SDK/NDK apropriados à versão do Kodi; clone completo xbmc/xbmc; espaço em disco e capacidade de compilação; confirmação da ABI arm64-v8a do Android da TV Box.

Uso: defina as variáveis ANDROID_HOME e KODI_SRC (pasta com checkout do Kodi) e execute bash kodi-edition/native/build-arm64.sh. BUILD_JOBS limita paralelismo.

Próximas etapas obrigatórias: verificar a build ARM64; desenvolver a skin com a marca Weblyxx; integrar o complemento LIX TV com fontes autorizadas; personalizar nome/pacote, assinatura e ícone; testar instalação e reprodução no H96 Max.

A compilação padrão do Gradle no repositório continua sendo a versão Java, não uma compilação Kodi. O manifesto do Brazuca Play menciona proibição de venda do addon; fontes comerciais requerem permissões próprias.

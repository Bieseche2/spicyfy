# Spicyfy

Esqueleto inicial do projeto Android. Nenhuma tela ou serviço está
implementado de verdade ainda — cada arquivo tem `TODO`s marcando onde
a lógica entra.

## Estrutura

```
app/src/main/java/com/spicyfy/app/
├── SpicyfyApp.kt              # Application class
├── ui/
│   ├── MainActivity.kt        # activity única (navegação entre telas)
│   ├── home/                  # tela Início
│   ├── search/                # tela Buscar
│   ├── nowplaying/            # tela Tocando agora (player + letra)
│   └── library/                # tela Biblioteca (playlists do usuário)
├── player/
│   └── PlaybackService.kt     # serviço de reprodução em background (Media3)
├── extractor/
│   └── YoutubeMusicSource.kt  # busca e resolve streams via NewPipeExtractor
├── lyrics/
│   └── LyricsRepository.kt    # busca letra sincronizada (LRCLIB)
├── network/
│   ├── ApiConfig.kt           # ⚠️ único lugar com a URL do backend — troca só aqui
│   └── AccountApi.kt          # endpoints de conta/playlists (a definir)
└── data/model/
    ├── Track.kt
    └── Playlist.kt
```

## Idiomas

`res/values/` é o fallback (português). `values-en/` e `values-es/`
já existem como exemplo. O Android escolhe a pasta certa sozinho, com
base no idioma do celular — não precisa de código extra pra detectar
nada. Pra adicionar um idioma novo: criar `values-XX/strings.xml` com
as mesmas chaves.

## Quando o backend estiver de pé

Só mexer em `network/ApiConfig.kt` (a URL) e implementar os métodos
de `AccountApi.kt` — o resto do app já está desenhado pra não conhecer
a URL diretamente.

## Ainda faltando (próximos passos)

- Layouts XML (ou Compose) das 4 telas
- Implementar `YoutubeMusicSource` com o NewPipeExtractor de verdade
- Implementar `PlaybackService` com ExoPlayer
- Parsear o formato LRC no `LyricsRepository`
- Ícone do app (`mipmap-anydpi-v26/`)

# 🌶️ Spicyfy

[![Kotlin](https://img.shields.io/badge/Kotlin-Android-purple?logo=kotlin)](https://kotlinlang.org/)
[![Android](https://img.shields.io/badge/Platform-Android-green?logo=android)](https://www.android.com/)
[![Open Source](https://img.shields.io/badge/Open%20Source-Yes-brightgreen)](#)

> A lightweight, open-source music app for Android.

Spicyfy is an Android music application focused on a clean, modern interface, music playback, playlists, lyrics, recommendations, favorites, and offline downloads.

---

# 🇺🇸 English

## 📖 About

Spicyfy is a lightweight Android music application written in Kotlin.

The project aims to provide a polished music-listening experience without unnecessary complexity or weight. Its interface takes inspiration from familiar modern music applications while maintaining its own visual identity and implementation.

## ✨ Features

- 🎵 Music playback
- 🔎 Music search
- ❤️ Favorite tracks
- 📚 Personal library
- 📋 Playlist creation and management
- ▶️ Playlist playback
- 🔀 Shuffle playback
- 🔁 Repeat playback
- 🕘 Recently played tracks
- 🎤 Lyrics support
- 💡 Personalized/recommended content
- 📥 Downloads for offline listening
- 🔊 Background playback
- 📱 Android media controls
- 🌙 Dark, warm visual design
- 🌎 Multiple languages

## 🎧 Player

Spicyfy uses Android media components for music playback.

Playback is separated from the main interface through a dedicated playback service, allowing music to continue while the user navigates through the application or leaves its interface.

The player layer includes:

- `PlaybackService.kt`
- `PlayerViewModel.kt`
- `TrackDownloader.kt`

## 📥 Downloads

Spicyfy includes functionality for downloading tracks for offline listening.

Download-related logic is separated into dedicated components:

- `DownloadRepository.kt`
- `TrackDownloader.kt`
- `DownloaderImpl.kt`

## 🎤 Lyrics

Lyrics functionality is separated into its own repository:

`LyricsRepository.kt`

This keeps lyrics-related logic independent from the main player and UI.

## 📚 Playlists

Playlists are handled through dedicated models, repositories, adapters, and screens.

Relevant components include:

- `Playlist.kt`
- `PlaylistRepository.kt`
- `PlaylistAdapter.kt`
- `PlaylistDetailFragment.kt`
- `PlaylistTrackAdapter.kt`

## 🔎 Search

Search has its own UI and ViewModel layer:

- `SearchFragment.kt`
- `SearchViewModel.kt`
- `TrackAdapter.kt`

## 🏠 Home

The Home screen provides shortcuts, personalized sections, recommendations, artists, and track content.

Main components:

- `HomeFragment.kt`
- `HomeViewModel.kt`
- `HomeTrackAdapter.kt`

## 🗂️ Library

The Library contains playlists and other music-related content.

Main components include:

- `LibraryFragment.kt`
- `LibraryViewModel.kt`
- `PlaylistAdapter.kt`
- `PlaylistDetailFragment.kt`
- `PlaylistTrackAdapter.kt`

## 🧱 Project Structure

```text
spicyfy/
├── README.md
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/spicyfy/app/
│       │   ├── SpicyfyApp.kt
│       │   ├── data/
│       │   │   ├── model/
│       │   │   │   ├── Playlist.kt
│       │   │   │   └── Track.kt
│       │   │   └── repository/
│       │   │       ├── DownloadRepository.kt
│       │   │       ├── PlaylistRepository.kt
│       │   │       └── RecentlyPlayedRepository.kt
│       │   ├── extractor/
│       │   │   ├── DownloaderImpl.kt
│       │   │   └── YoutubeMusicSource.kt
│       │   ├── lyrics/
│       │   │   └── LyricsRepository.kt
│       │   ├── network/
│       │   │   ├── AccountApi.kt
│       │   │   └── ApiConfig.kt
│       │   ├── player/
│       │   │   ├── PlaybackService.kt
│       │   │   ├── PlayerViewModel.kt
│       │   │   └── TrackDownloader.kt
│       │   └── ui/
│       │       ├── MainActivity.kt
│       │       ├── home/
│       │       ├── library/
│       │       ├── nowplaying/
│       │       └── search/
│       └── res/
│           ├── color/
│           ├── drawable/
│           ├── layout/
│           ├── menu/
│           ├── values/
│           ├── values-en/
│           ├── values-es/
│           └── xml/
├── build.gradle.kts
├── gradle/
│   └── wrapper/
├── gradle.properties
└── settings.gradle.kts
```

## 🧩 Main Packages

### `data`

Contains application data models and repositories.

- `model/` contains core data structures such as tracks and playlists.
- `repository/` contains data-management logic for downloads, playlists, and recently played tracks.

### `extractor`

Contains components responsible for obtaining music data and handling downloads.

### `lyrics`

Contains the lyrics repository.

### `network`

Contains account/API configuration and networking components.

### `player`

Contains the playback layer, including the playback service, player ViewModel, and track downloading logic.

### `ui`

Contains the user interface, separated into:

- `home/`
- `library/`
- `nowplaying/`
- `search/`

## 🎨 Resources

The `res/` directory contains Android resources used by the application.

- `drawable/` — icons, backgrounds, cards, buttons, and other visual resources
- `layout/` — XML layouts for screens and UI components
- `menu/` — navigation resources
- `color/` — color resources
- `values/` — colors, strings, and themes
- `values-en/` — English strings
- `values-es/` — Spanish strings
- `xml/` — additional Android XML resources

## 🛠️ Technologies

- Kotlin
- Android SDK
- Android Media3 / ExoPlayer
- Android MediaSession
- XML layouts
- Gradle
- GitHub Actions

## ⚙️ Compilation

Build a debug APK locally with the standard Gradle command:

```bash
./gradlew assembleDebug
```

The generated APK is normally located at:

```text
app/build/outputs/apk/debug/
```

## 🤖 GitHub Actions

GitHub Actions can be used to automate Android builds.

A workflow can run Gradle, build the APK, and expose the resulting file as a workflow artifact for testing.

## 🚀 Installation

Download a release APK or a build artifact provided by the project and install it on a compatible Android device.

## 🤝 Contributing

Contributions are welcome.

Before opening a pull request:

1. Keep changes related to the project.
2. Preserve the existing structure when possible.
3. Test your changes.
4. Clearly describe what changed.
5. Avoid unrelated modifications.

For larger changes, an issue can be used to discuss the idea before implementation.

## 🐛 Issues

When reporting a bug, include:

- A clear description
- Steps to reproduce
- Expected behavior
- Actual behavior
- Android version
- Device information when relevant
- Logs or screenshots when useful

Please check existing issues before creating a duplicate.

## 🗺️ Roadmap

The roadmap may change as development continues.

Possible future work includes:

- Improving existing features
- Player refinements
- Search and recommendation improvements
- Download improvements
- UI/UX improvements
- Bug fixes and performance improvements
- Additional translations
- General application polish

## ❓ FAQ

### Is Spicyfy Spotify?

No. Spicyfy is a separate Android application and is not a modified version of the Spotify application.

### Is Spicyfy open source?

Yes. The source code is publicly available in the project repository.

### Is Spicyfy free?

The project is intended to be free to use.

### Does Spicyfy support offline listening?

Yes. Spicyfy includes functionality for downloading tracks for offline listening.

### Does playback continue in the background?

Yes. Playback is handled through a dedicated playback service and Android media components.

### What is Spicyfy written in?

Kotlin for Android.

## 📄 License

See the repository's license file for the exact terms governing the project.

---

# 🇧🇷 Português

## 📖 Sobre

Spicyfy é um aplicativo de música leve para Android, desenvolvido em Kotlin.

O projeto busca oferecer uma experiência moderna de reprodução musical sem complexidade ou peso desnecessários. A interface possui inspiração em aplicativos modernos de música, mantendo sua própria identidade visual e implementação.

## ✨ Recursos

- 🎵 Reprodução de músicas
- 🔎 Pesquisa de músicas
- ❤️ Músicas favoritas
- 📚 Biblioteca pessoal
- 📋 Criação e gerenciamento de playlists
- ▶️ Reprodução de playlists
- 🔀 Reprodução aleatória
- 🔁 Repetição
- 🕘 Músicas reproduzidas recentemente
- 🎤 Suporte a letras
- 💡 Conteúdo personalizado/recomendado
- 📥 Downloads para ouvir offline
- 🔊 Reprodução em segundo plano
- 📱 Controles de mídia do Android
- 🌙 Interface escura com cores quentes
- 🌎 Múltiplos idiomas

## 🎧 Player

O Spicyfy utiliza componentes de mídia do Android para a reprodução.

A reprodução é separada da interface principal por meio de um serviço dedicado, permitindo que a música continue enquanto o usuário navega pelo aplicativo ou deixa sua interface.

A camada do player inclui:

- `PlaybackService.kt`
- `PlayerViewModel.kt`
- `TrackDownloader.kt`

## 📥 Downloads

O Spicyfy possui funcionalidade para baixar músicas e ouvi-las offline.

A lógica relacionada aos downloads é separada em componentes específicos:

- `DownloadRepository.kt`
- `TrackDownloader.kt`
- `DownloaderImpl.kt`

## 🎤 Letras

A funcionalidade de letras possui seu próprio repositório:

`LyricsRepository.kt`

Isso mantém a lógica das letras separada do player e da interface.

## 📚 Playlists

As playlists possuem modelos, repositórios, adapters e telas próprios.

Componentes relacionados:

- `Playlist.kt`
- `PlaylistRepository.kt`
- `PlaylistAdapter.kt`
- `PlaylistDetailFragment.kt`
- `PlaylistTrackAdapter.kt`

## 🔎 Pesquisa

A pesquisa possui sua própria camada de interface e ViewModel:

- `SearchFragment.kt`
- `SearchViewModel.kt`
- `TrackAdapter.kt`

## 🏠 Início

A tela inicial apresenta atalhos, seções personalizadas, recomendações, artistas e conteúdos relacionados às músicas.

Componentes principais:

- `HomeFragment.kt`
- `HomeViewModel.kt`
- `HomeTrackAdapter.kt`

## 🗂️ Biblioteca

A Biblioteca reúne playlists e outros conteúdos relacionados às músicas.

Componentes principais:

- `LibraryFragment.kt`
- `LibraryViewModel.kt`
- `PlaylistAdapter.kt`
- `PlaylistDetailFragment.kt`
- `PlaylistTrackAdapter.kt`

## 🧱 Estrutura do Projeto

```text
spicyfy/
├── README.md
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/spicyfy/app/
│       │   ├── SpicyfyApp.kt
│       │   ├── data/
│       │   │   ├── model/
│       │   │   └── repository/
│       │   ├── extractor/
│       │   ├── lyrics/
│       │   ├── network/
│       │   ├── player/
│       │   └── ui/
│       │       ├── home/
│       │       ├── library/
│       │       ├── nowplaying/
│       │       └── search/
│       └── res/
│           ├── color/
│           ├── drawable/
│           ├── layout/
│           ├── menu/
│           ├── values/
│           ├── values-en/
│           ├── values-es/
│           └── xml/
├── build.gradle.kts
├── gradle/
├── gradle.properties
└── settings.gradle.kts
```

## 🧩 Principais Pacotes

### `data`

Contém os modelos de dados e repositórios utilizados pelo aplicativo.

- `model/` contém estruturas como músicas e playlists.
- `repository/` contém a lógica de gerenciamento de downloads, playlists e músicas reproduzidas recentemente.

### `extractor`

Contém componentes responsáveis pela obtenção dos dados das músicas e pelo gerenciamento dos downloads.

### `lyrics`

Contém o repositório de letras.

### `network`

Contém componentes relacionados à conta, configuração da API e comunicação de rede.

### `player`

Contém a camada de reprodução, incluindo o serviço de reprodução, o ViewModel do player e a lógica de download.

### `ui`

Contém a interface do aplicativo, dividida em:

- `home/`
- `library/`
- `nowplaying/`
- `search/`

## 🎨 Recursos

A pasta `res/` contém os recursos Android utilizados pelo aplicativo.

- `drawable/` — ícones, fundos, cards, botões e outros recursos visuais
- `layout/` — layouts XML das telas e componentes
- `menu/` — recursos de navegação
- `color/` — recursos de cores
- `values/` — cores, strings e temas
- `values-en/` — strings em inglês
- `values-es/` — strings em espanhol
- `xml/` — outros recursos XML

## 🛠️ Tecnologias

- Kotlin
- Android SDK
- Android Media3 / ExoPlayer
- Android MediaSession
- XML layouts
- Gradle
- GitHub Actions

## ⚙️ Compilação

Para gerar um APK de debug localmente:

```bash
./gradlew assembleDebug
```

O APK normalmente será gerado em:

```text
app/build/outputs/apk/debug/
```

## 🤖 GitHub Actions

O GitHub Actions pode ser utilizado para automatizar a compilação do aplicativo Android.

Um workflow pode executar o Gradle, gerar o APK e disponibilizar o arquivo como artifact para testes.

## 🚀 Instalação

Baixe um APK de release ou um artifact disponibilizado pelo projeto e instale-o em um dispositivo Android compatível.

## 🤝 Contribuição

Contribuições são bem-vindas.

Antes de abrir um pull request:

1. Mantenha as alterações relacionadas ao projeto.
2. Preserve a estrutura existente quando possível.
3. Teste suas alterações.
4. Explique claramente o que foi alterado.
5. Evite modificações sem relação com o objetivo.

Para mudanças maiores, uma issue pode ser usada para discutir a ideia antes da implementação.

## 🐛 Issues

Ao reportar um bug, inclua:

- Descrição clara
- Passos para reproduzir
- Comportamento esperado
- Comportamento observado
- Versão do Android
- Informações do dispositivo quando relevantes
- Logs ou capturas de tela quando úteis

Procure issues existentes antes de criar uma duplicata.

## 🗺️ Roadmap

O roadmap pode mudar conforme o desenvolvimento continua.

Possíveis trabalhos futuros incluem:

- Melhorias nas funcionalidades existentes
- Refinamentos no player
- Melhorias na pesquisa e recomendações
- Melhorias nos downloads
- Melhorias de UI/UX
- Correções de bugs e desempenho
- Novas traduções
- Polimento geral do aplicativo

## ❓ Perguntas Frequentes

### O Spicyfy é o Spotify?

Não. O Spicyfy é um aplicativo Android separado e não é uma versão modificada do aplicativo Spotify.

### O Spicyfy é open source?

Sim. O código-fonte está disponível publicamente no repositório do projeto.

### O Spicyfy é gratuito?

O projeto foi desenvolvido para ser gratuito.

### O Spicyfy permite ouvir músicas offline?

Sim. O Spicyfy possui funcionalidade para baixar músicas para reprodução offline.

### A reprodução continua em segundo plano?

Sim. A reprodução é gerenciada por um serviço dedicado e componentes de mídia do Android.

### Em qual linguagem o Spicyfy foi desenvolvido?

Kotlin para Android.

## 📄 Licença

Consulte o arquivo de licença do repositório para conhecer os termos exatos do projeto.

---

## ❤️ Credits

Spicyfy is an independent open-source Android project.

Desenvolvido com Kotlin, Android e uma quantidade questionável de café. ☕🌶️

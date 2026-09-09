pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // NewPipeExtractor é distribuído via JitPack
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "Spicyfy"
include(":app")

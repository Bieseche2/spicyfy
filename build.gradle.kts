// Root build file — configura plugins compartilhados por todos os módulos
plugins {
    // AGP 8.13.0: suporta compileSdk até API 36.1, exige Gradle 8.13+ e JDK 17
    id("com.android.application") version "8.13.0" apply false
    // Kotlin 2.1.0: versão estável documentada, compatível com AGP 8.13
    id("org.jetbrains.kotlin.android") version "2.4.0" apply false
}

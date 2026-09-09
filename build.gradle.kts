// Root build file — configura plugins compartilhados por todos os módulos
plugins {
    // AGP 8.7.3: suporta compileSdk até API 35, exige Gradle 8.9+ e JDK 17
    id("com.android.application") version "8.7.3" apply false
    // Kotlin 2.1.0: versão estável documentada, compatível com AGP 8.7
    id("org.jetbrains.kotlin.android") version "2.1.0" apply false
}

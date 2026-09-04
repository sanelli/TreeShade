plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.4.0"
    id("org.jetbrains.intellij.platform") version "2.18.1"
}

group = "com.treeshade"
version = "0.1.0-preview.5"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        // Rider does not support installer-based SDK resolution yet
        rider("2026.2") {
            useInstaller = false
        }
        bundledModule("intellij.rider.rdclient.dotnet")
    }
}

kotlin {
    jvmToolchain(21)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "262"
            untilBuild = provider { null }
        }
    }

    // Signing uses CERTIFICATE_CHAIN, PRIVATE_KEY, and PRIVATE_KEY_PASSWORD.
    // Values may be Base64-encoded; the Gradle plugin decodes them automatically.
    // See: https://plugins.jetbrains.com/docs/intellij/plugin-signing.html
    signing {
        certificateChain = providers.environmentVariable("CERTIFICATE_CHAIN")
        privateKey = providers.environmentVariable("PRIVATE_KEY")
        password = providers.environmentVariable("PRIVATE_KEY_PASSWORD")
    }
}

// build.gradle.kts (project level)
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.google.services) apply false
}

// Scripts de build
buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:8.13.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
        classpath("com.google.gms:google-services:4.4.0")
    }
}

// Configurações para otimização e compatibilidade
allprojects {
    configurations.all {
        resolutionStrategy {
            force(
                "org.jetbrains.kotlin:kotlin-stdlib:1.9.22",
                "org.jetbrains.kotlin:kotlin-stdlib-common:1.9.22",
                "org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.9.22",
                "org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.22",
                "org.jetbrains.kotlin:kotlin-reflect:1.9.22"
            )
        }
    }
}
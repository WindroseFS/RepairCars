// build.gradle.kts (project level)
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.google.services) apply false
}

// Configurações para otimização
allprojects {
    configurations.all {
        resolutionStrategy {
            force(
                "org.jetbrains.kotlin:kotlin-stdlib:1.9.22",
                "org.jetbrains.kotlin:kotlin-stdlib-common:1.9.22",
                "org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.9.22",
                "org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.22"
            )
        }
    }
}
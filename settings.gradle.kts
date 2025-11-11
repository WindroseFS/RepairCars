// settings.gradle (arquivo no diretório raiz do projeto)
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    plugins {
        id("com.android.application") version "8.13.1"
        id("org.jetbrains.kotlin.android") version "2.0.21"
        id("androidx.navigation.safeargs") version "2.7.7"
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "RepairCars"
include(":app")
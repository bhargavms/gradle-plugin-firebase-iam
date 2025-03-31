@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}

rootProject.name = "firebase-iam-gradle-plugin"

dependencyResolutionManagement {
    repositories {
        gradlePluginPortal() // org.gradle.android.cache-fix:org.gradle.android.cache-fix.gradle.plugin
        mavenCentral()
        google()
    }
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
}

include("firebase-iam")

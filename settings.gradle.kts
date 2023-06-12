@file:Suppress("UnstableApiUsage")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Elmslie"

include(":elmslie-android")
include(":elmslie-core")

include(":sample-coroutines-loader")
project(":sample-coroutines-loader").projectDir = file("samples/coroutines-loader")
include(":sample-kotlin-calculator")
project(":sample-kotlin-calculator").projectDir = file("samples/kotlin-calculator")


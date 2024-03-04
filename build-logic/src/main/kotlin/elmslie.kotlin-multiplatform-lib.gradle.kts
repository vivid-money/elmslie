import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform")
    id("elmslie.detekt")
}

kotlin {
    applyDefaultHierarchyTemplate()

    jvm {
        compilations.all {
            compilerOptions.configure {
                jvmTarget.set(JvmTarget.JVM_11)
            }
        }
    }

    iosArm64()
    iosSimulatorArm64()
    iosX64()
}
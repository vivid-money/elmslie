@file:OptIn(
  ExperimentalWasmDsl::class,
  ExperimentalKotlinGradlePluginApi::class,
  ExperimentalAbiValidation::class,
)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeSimulatorTest

plugins {
  kotlin("multiplatform")
  id("com.android.kotlin.multiplatform.library")
  id("org.jetbrains.dokka")
  id("elmslie.detekt")
  id("elmslie.spotless")
}

kotlin {
  explicitApi()

  applyDefaultHierarchyTemplate {
    common {
      group("jvmShared") {
        withCompilations {
          it.target.platformType == KotlinPlatformType.jvm ||
            it.target.platformType == KotlinPlatformType.androidJvm
        }
      }
      group("commonWeb") {
        withJs()
        withWasmJs()
      }
    }
  }

  abiValidation { keepLocallyUnsupportedTargets.set(true) }

  jvm { compilerOptions { jvmTarget.set(JvmTarget) } }

  android {
    compileSdk = AndroidCompileSdk
    minSdk = AndroidMinSdk
    compilerOptions { jvmTarget.set(JvmTarget) }
    withHostTest {}
  }

  iosArm64()
  iosSimulatorArm64()
  iosX64()

  macosArm64()

  tvosArm64()
  tvosSimulatorArm64()

  watchosArm32()
  watchosArm64()
  watchosSimulatorArm64()

  linuxArm64()
  linuxX64()

  mingwX64()

  js { browser() }
  wasmJs { browser() }
}

tasks.withType<KotlinNativeSimulatorTest>().configureEach {
  if (!name.startsWith("ios")) enabled = false
}

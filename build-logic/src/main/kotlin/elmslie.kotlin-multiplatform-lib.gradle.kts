@file:OptIn(ExperimentalWasmDsl::class, ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
  kotlin("multiplatform")
  id("elmslie.detekt")
  id("elmslie.spotless")
}

kotlin {
  applyDefaultHierarchyTemplate {
    common {
      group("commonWeb") {
        withJs()
        withWasmJs()
      }
    }
  }

  jvm { compilerOptions { jvmTarget.set(JvmTarget) } }

  iosArm64()
  iosSimulatorArm64()
  iosX64()

  js(IR) { browser() }
  wasmJs { browser() }
}

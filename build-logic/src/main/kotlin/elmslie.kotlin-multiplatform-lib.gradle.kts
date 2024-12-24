import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

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
        withWasm()
      }
    }
  }

  jvm { compilations.all { compilerOptions.configure { jvmTarget.set(JvmTarget.JVM_11) } } }

  iosArm64()
  iosSimulatorArm64()
  iosX64()

  js(IR) { browser() }
  @OptIn(ExperimentalWasmDsl::class) wasmJs { browser() }
}

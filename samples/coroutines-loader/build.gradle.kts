plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("elmslie.detekt")
  id("elmslie.spotless")
}

android {
  namespace = "money.vivid.elmslie.samples.coroutines.timer"

  compileSdk = 35
  buildToolsVersion = "35.0.0"

  buildFeatures { buildConfig = true }

  defaultConfig {
    minSdk = 21
    targetSdk = 35
  }

  compileOptions {
    targetCompatibility = JavaVersion.VERSION_11
    sourceCompatibility = JavaVersion.VERSION_11
  }

  kotlinOptions { jvmTarget = JavaVersion.VERSION_11.toString() }
}

dependencies {
  implementation(projects.elmslieAndroid)
  implementation(projects.elmslieCore)

  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.fragmentKtx)
  implementation(libs.google.material)
  implementation(libs.kotlinx.coroutinesCore)
}

plugins {
  id("com.android.application")
  id("elmslie.base-lib")
  id("elmslie.detekt")
  id("elmslie.spotless")
}

android {
  namespace = "money.vivid.elmslie.samples.coroutines.timer"

  compileSdk = 37

  buildFeatures { buildConfig = true }

  defaultConfig {
    minSdk = 23
    targetSdk = 37
  }

  compileOptions {
    targetCompatibility = JavaVersion.VERSION_11
    sourceCompatibility = JavaVersion.VERSION_11
  }
}

dependencies {
  implementation(projects.elmslieAndroid)
  implementation(projects.elmslieCore)

  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.fragmentKtx)
  implementation(libs.google.material)
  implementation(libs.kotlinx.coroutinesCore)
}

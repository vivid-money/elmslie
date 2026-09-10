plugins {
  id("elmslie.kotlin-multiplatform-lib")
  id("elmslie.publishing")
}

elmsliePublishing {
  pom {
    name = "Elmslie core"
    description = "Elmslie is a minimalistic reactive implementation of TEA/ELM"
  }
}

kotlin {
  android { namespace = "money.vivid.elmslie.core" }

  sourceSets {
    val commonMain by getting { dependencies { api(libs.kotlinx.coroutinesCore) } }
    val commonTest by getting {
      dependencies {
        implementation(libs.kotlinx.coroutinesTest)
        implementation(libs.kotlin.test)
      }
    }
  }
}

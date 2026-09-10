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
    commonMain { dependencies { api(libs.kotlinx.coroutinesCore) } }
    commonTest {
      dependencies {
        implementation(libs.kotlinx.coroutinesTest)
        implementation(libs.kotlin.test)
      }
    }
  }
}

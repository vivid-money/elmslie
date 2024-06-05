plugins {
    id("elmslie.kotlin-multiplatform-lib")
    id("elmslie.publishing")
    alias(libs.plugins.binaryCompatibilityValidator)
}

elmsliePublishing {
    pom {
        name = "Elmslie core"
        description = "Elmslie is a minimalistic reactive implementation of TEA/ELM"
    }
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutinesCore)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlinx.coroutinesTest)
                implementation(libs.kotlin.test)
            }
        }
    }
}

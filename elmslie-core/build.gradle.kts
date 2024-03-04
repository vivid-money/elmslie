plugins {
    id("elmslie.kotlin-multiplatform-lib")
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
plugins {
    id("elmslie.kotlin-jvm-lib")
    id("elmslie.tests-convention")
}

dependencies {
    implementation(libs.kotlinx.coroutinesCore)
    testImplementation(libs.kotlinx.coroutinesTest)
}

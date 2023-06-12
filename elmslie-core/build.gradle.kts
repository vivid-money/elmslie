plugins {
    id("elmslie.kotlin-lib")
    `java-test-fixtures`
}

dependencies {
    implementation(libs.kotlinx.coroutinesCore)
    testFixturesImplementation(libs.kotlinx.coroutinesTest)
    testFixturesImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutinesTest)
}

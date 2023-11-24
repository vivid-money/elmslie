plugins {
    id("org.jetbrains.kotlin.jvm")
    id("elmslie.detekt")
    id("elmslie.tests-convention")
}

dependencies {
    implementation(projects.elmslieCore)
    implementation(libs.kotlinx.coroutinesCore)

    testImplementation(projects.elmslieCore)
    testImplementation(libs.kotlinx.coroutinesTest)
}

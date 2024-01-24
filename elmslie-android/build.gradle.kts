plugins {
    id("elmslie.android-lib")
}

android {
    namespace = "money.vivid.elmslie.android"
}

dependencies {
    implementation(projects.elmslieCore)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtimeKtx)
    implementation(libs.androidx.lifecycle.viewmodelSavedstate)
    implementation(libs.androidx.startup.runtime)
}
plugins {
    id("elmslie.android-lib")
    id("elmslie.publishing")
    alias(libs.plugins.binaryCompatibilityValidator)
}

android {
    namespace = "money.vivid.elmslie.android"
}

elmsliePublishing {
    pom {
        name = "Elmslie Android"
        description =
            "Elmslie is a minimalistic reactive implementation of TEA/ELM. Android specific. https://github.com/vivid-money/elmslie/"
    }
}

dependencies {
    implementation(projects.elmslieCore)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtimeKtx)
    implementation(libs.androidx.lifecycle.viewmodelSavedstate)
    implementation(libs.androidx.startup.runtime)
}

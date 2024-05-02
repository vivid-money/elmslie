import publishing.PublishingExtension

plugins {
    id("elmslie.android-lib")
    id("publishing.elmslie")
    alias(libs.plugins.binaryCompatibilityValidator)
}

android {
    namespace = "money.vivid.elmslie.android"
}

elmsliePublishing {
    configure(
        PublishingExtension.Pom(
            name = "Elmslie Android",
            description = "Elmslie is a minimalistic reactive implementation of TEA/ELM. Android specific. https://github.com/vivid-money/elmslie/",
        ),
    )
}

dependencies {
    implementation(projects.elmslieCore)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtimeKtx)
    implementation(libs.androidx.lifecycle.viewmodelSavedstate)
    implementation(libs.androidx.startup.runtime)
}

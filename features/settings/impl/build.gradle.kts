plugins {
    id("pettycash.feature.impl")
    alias(libs.plugins.kotlinx.serialization)
}

android {
    namespace = "com.example.pettycash.features.settings.impl"
}

dependencies {
    implementation(project(":features:settings:api"))
    implementation(project(":features:home:api"))
    implementation(project(":features:invite:api"))
    implementation(project(":features:onboarding:api"))
    implementation(libs.kotlinx.serialization.json)
}

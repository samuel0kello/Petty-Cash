plugins {
    id("pettycash.feature.impl")
    alias(libs.plugins.kotlinx.serialization)
}

android {
    namespace = "com.example.pettycash.features.invite.impl"
}

dependencies {
    implementation(project(":features:invite:api"))
    implementation(project(":features:auth:api"))
    implementation(project(":features:home:api"))
    implementation(libs.kotlinx.serialization.json)
}

plugins {
    id("pettycash.feature.impl")
    alias(libs.plugins.kotlinx.serialization)
}

android {
    namespace = "com.example.pettycash.features.spends.impl"
}

dependencies {
    implementation(project(":features:spends:api"))
    implementation(project(":features:home:api"))
    implementation(project(":features:invite:api"))
    implementation(libs.kotlinx.serialization.json)
}

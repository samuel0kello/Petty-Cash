plugins {
    id("pettycash.feature.impl")
    alias(libs.plugins.kotlinx.serialization)
}

android {
    namespace = "com.example.pettycash.features.company.impl"
}

dependencies {
    implementation(project(":features:company:api"))
    implementation(project(":features:home:api"))
    implementation(libs.kotlinx.serialization.json)
}

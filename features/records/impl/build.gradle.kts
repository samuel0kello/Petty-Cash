plugins {
    id("pettycash.feature.impl")
    alias(libs.plugins.kotlinx.serialization)
}

android {
    namespace = "com.example.pettycash.features.records.impl"
}

dependencies {
    implementation(project(":features:records:api"))
    implementation(project(":features:home:api"))
    implementation(project(":features:requests:api"))
    implementation(libs.kotlinx.serialization.json)
}

plugins {
    id("pettycash.feature.impl")
    alias(libs.plugins.kotlinx.serialization)
}

android {
    namespace = "com.example.pettycash.features.reconciliation.impl"
}

dependencies {
    implementation(project(":features:reconciliation:api"))
    implementation(project(":features:home:api"))
    implementation(libs.kotlinx.serialization.json)
}

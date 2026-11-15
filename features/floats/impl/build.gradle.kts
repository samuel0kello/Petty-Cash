plugins {
    id("pettycash.feature.impl")
    alias(libs.plugins.kotlinx.serialization)
}

android {
    namespace = "com.example.pettycash.features.floats.impl"
}

dependencies {
    implementation(project(":features:floats:api"))
    implementation(project(":features:home:api"))
    implementation(project(":features:reconciliation:api"))
    implementation(project(":features:spends:api"))
    implementation(libs.kotlinx.serialization.json)
}

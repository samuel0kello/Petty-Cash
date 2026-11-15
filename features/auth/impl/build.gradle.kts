plugins {
    id("pettycash.feature.impl")
}

android {
    namespace = "com.example.pettycash.features.auth.impl"
}

dependencies {
    implementation(project(":features:auth:api"))
    implementation(project(":features:home:api"))
    implementation(project(":features:company:api"))
}

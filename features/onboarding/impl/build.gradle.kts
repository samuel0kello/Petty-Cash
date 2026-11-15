plugins {
    id("pettycash.feature.impl")
}

android {
    namespace = "com.example.pettycash.features.onboarding.impl"
}

dependencies {
    implementation(project(":features:onboarding:api"))
    implementation(project(":features:auth:api"))
}

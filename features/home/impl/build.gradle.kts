plugins {
    id("pettycash.feature.impl")
}

android {
    namespace = "com.example.pettycash.features.home.impl"
}

dependencies {
    implementation(project(":features:home:api"))
    implementation(project(":features:company:api"))
    implementation(project(":features:invite:api"))
}

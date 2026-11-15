plugins {
    id("pettycash.android.library")
    alias(libs.plugins.kotlinx.serialization)
}

android {
    namespace = "com.example.pettycash.datasource.remote"
}

dependencies {
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.core)
}

plugins {
    id("pettycash.android.library")
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.pettycash.datasource.local"
}

dependencies {
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    implementation(libs.androidx.datastore.preferences)
}

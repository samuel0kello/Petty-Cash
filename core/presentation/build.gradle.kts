plugins {
    id("pettycash.android.library")
}

android {
    namespace = "com.example.pettycash.presentation"
}

dependencies {
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.kotlinx.coroutines.test)
}

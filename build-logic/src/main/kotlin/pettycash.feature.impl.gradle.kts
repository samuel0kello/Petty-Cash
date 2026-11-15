plugins {
    id("pettycash.android.library")
}

android {
    testOptions {
        screenshotTests.create("screenshotTest") {
            engineVersion = "0.0.1-alpha17"
            targetVariants.add("debug")

            dependencies {
                implementation(library("androidx-compose-ui-tooling"))
                implementation(library("screenshot-validation-api"))
                implementation(library("androidx-customview-poolingcontainer"))
            }
        }
    }
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":core:presentation"))
    implementation(project(":core:datasource:local"))
    implementation(project(":core:datasource:remote"))

    implementation(library("androidx-navigation3-runtime"))
    implementation(library("androidx-navigation3-ui"))
    implementation(library("androidx-lifecycle-viewmodel-compose"))
    implementation(library("koin-core"))
    implementation(library("koin-android"))
    implementation(library("koin-compose"))
    implementation(library("koin-compose-viewmodel"))
    implementation(library("koin-compose-navigation3"))
    implementation(library("koin-compose-viewmodel-navigation"))
    implementation(library("kotlinx-coroutines-core"))
    implementation(library("androidx-customview-poolingcontainer"))

    testImplementation(library("kotlinx-coroutines-test"))
}

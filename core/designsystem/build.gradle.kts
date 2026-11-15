plugins {
    id("pettycash.android.library")
}

android {
    namespace = "com.example.pettycash.mai"

    testOptions {
        screenshotTests.create("screenshotTest") {
            engineVersion = "0.0.1-alpha17"
            targetVariants.add("debug")

            dependencies {
                implementation(library("androidx-compose-ui-tooling"))
                implementation(libs.screenshot.validation.api)
                implementation(library("androidx-customview-poolingcontainer"))
            }
        }
    }
}

dependencies {
    implementation(library("androidx-customview-poolingcontainer"))
}

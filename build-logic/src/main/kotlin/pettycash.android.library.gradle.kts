import com.android.build.api.variant.HasHostTestsBuilder
import com.android.build.api.variant.HostTestBuilder
import com.android.build.api.variant.LibraryAndroidComponentsExtension

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.compose")
    jacoco
}

android {
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

dependencies {
    implementation(platform(library("androidx-compose-bom")))
    implementation(library("androidx-compose-ui"))
    implementation(library("androidx-compose-material3"))
    implementation(library("androidx-compose-material-icons-extended"))
    implementation(library("androidx-compose-ui-tooling-preview"))
    debugImplementation(library("androidx-compose-ui-tooling"))

    testImplementation(library("junit"))
    testImplementation(library("kotlinx-coroutines-test"))
    testImplementation(library("org-robolectric"))
    testImplementation(library("io-mockk"))
    testImplementation(library("androidx-test-core"))
    testImplementation(library("androidx-compose-ui-test-junit4"))
    debugImplementation(library("androidx-compose-ui-test-manifest"))
}

the<LibraryAndroidComponentsExtension>().apply {
    beforeVariants {
        (it as? HasHostTestsBuilder)?.hostTests?.get(HostTestBuilder.UNIT_TEST_TYPE)?.enable = true
    }
}

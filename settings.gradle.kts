pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Petty Cash"
include(":app")
include(":core:designsystem")
include(":core:presentation")
include(":core:datasource:local")
include(":core:datasource:remote")
include(":features:onboarding:api")
include(":features:onboarding:impl")
include(":features:auth:api")
include(":features:auth:impl")
include(":features:home:api")
include(":features:home:impl")
include(":features:company:api")
include(":features:company:impl")
include(":features:invite:api")
include(":features:invite:impl")
include(":features:requests:api")
include(":features:requests:impl")
include(":features:spends:api")
include(":features:spends:impl")
include(":features:refunds:api")
include(":features:refunds:impl")
include(":features:floats:api")
include(":features:floats:impl")
include(":features:reconciliation:api")
include(":features:reconciliation:impl")

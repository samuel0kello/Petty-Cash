# Testing strategy

## Stack

- **Unit tests**: JUnit4 + kotlinx-coroutines-test, run on the JVM.
- **Robolectric 4.15.1** for Android-framework-dependent unit tests and Compose UI behavior tests.
- **MockK** for mocking; **koin-test** for Koin module verification.
- **Compose UI tests** (`ui-test-junit4`) run locally under Robolectric — no emulator needed.
- **Screenshot tests**: AGP test suites + Compose Preview Screenshot engine `0.0.1-alpha17` (experimental, enabled via `android.experimental.enableScreenshotTest` and `android.experimental.testSuiteSupport` in `gradle.properties`).
- **JaCoCo** applied to every module for unit-test coverage.
- **androidTest** (instrumented) kept for on-device tests; Room DAO tests will use an in-memory database there.

## Where tests live

| Kind | Source set | Runner |
|---|---|---|
| Plain JVM unit tests | `src/test` | JUnit4 |
| Robolectric / Compose UI tests | `src/testDebug` | RobolectricTestRunner |
| Screenshot (`@PreviewTest`) | `src/screenshotTest` | AGP screenshot suite |
| Instrumented | `src/androidTest` | AndroidJUnitRunner |

Compose UI tests must go in `src/testDebug`, not `src/test`: `ui-test-manifest` is a `debugImplementation` dependency, so only `testDebugUnitTest` has the manifest that declares `ComponentActivity` for Robolectric.

## Commands

```bash
# All unit tests (per module)
./gradlew testDebugUnitTest

# Single module
./gradlew :core:designsystem:testDebugUnitTest

# Generate / update screenshot golden images
./gradlew :<module>:updateDebugScreenshotTestDefaultTestSuite

# Verify screenshots against goldens
./gradlew :<module>:testDebugScreenshotTestDefaultTestSuite

# JaCoCo report (per module, after unit tests)
./gradlew :<module>:jacocoTestReport
```

Golden images live in `src/screenshotTestDefaultDebug/reference/`. Reports land in `build/reports/tests/<task>/index.html`.

## Conventions

- ViewModels, repositories and DAOs get unit tests; screens get Compose behavior tests + `@PreviewTest` screenshots.
- Screenshot tests cover every component state (enabled/disabled/loading/error, light/dark) and every screen at minimum; add font-scale 1.5 and alternative themes where the UI varies.
- Prefer semantic matchers (`onNodeWithText`, `onNodeWithContentDescription`) over `testTag`; use `testTag` only when a matcher would need more than ~3 matchers.
- Fakes over mocks for repositories/datasources; MockK for everything else.
- `customview-poolingcontainer` must stay on the classpath of any module with screenshot tests — Layoutlib crashes with `NoClassDefFoundError: androidx/customview/poolingcontainer/R$id` without it.
- Do not downgrade `screenshot-validation-api` below `0.0.1-alpha17`; alpha16 fails with the poolingcontainer `R$id` error.

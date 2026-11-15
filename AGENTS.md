# PettyCash

Jetpack Compose Android app (single platform, `com.example.pettycash`). Design source of truth: Figma file `jDcrdsZqFDe7cRMjx02auI` ("PettyCash KE – v2"). Features are built screen-by-screen from the Figma "Ready for development" page; the Foundations/Components/Motion/Error states pages drive the shared design system.

## Modules

- `:app` — Application, MainActivity, Nav3 host, Koin root module.
- `:core:designsystem` — **MAI** design system: tokens (`mai/theme/`: colors incl. extended success/warning/info, typography incl. `amount*`/`code*`/`brandDisplay` styles, spacing, shapes, motion) and components (`mai/components/`).
- `:core:presentation` — MVI base (`MviViewModel`), `ViewState`/`ViewListState`, `Navigator`, `LocalNavigator`, `CollectOneTimeEvent`.
- `:core:datasource:local` — Room + DataStore Preferences.
- `:core:datasource:remote` — Ktor client + `RemoteResult`/`safeRemoteCall` contract (unused for now; app is local-only).
- Features will be `features/<name>/api` + `features/<name>/impl` pairs using the `pettycash.feature.api` / `pettycash.feature.impl` convention plugins in `build-logic/`.

## Conventions

- Navigation 3: serializable `NavKey`s, Koin `koinEntryProvider()`, `navigation<Key> { }` registrations. Stepped flows are ONE NavKey with internal step state — never push a route per step.
- DI: Koin. Local data only for now — no backend.
- Design tokens live only in MAI; features use `MaiTheme.colors/.spacing/.shapes/.motion/.extendedColors/.customTypography` and `mai/components/` — no raw hex/dimens in feature code.
- Comments: only when the code cannot show the why (platform quirk, non-obvious constraint). No section headers, no restating names.
- Material Symbols Rounded icons via `material-icons-extended`.

## Testing

See [docs/testing.md](docs/testing.md). Unit tests in `src/test`, Robolectric/Compose tests in `src/testDebug`, screenshot goldens via `updateDebugScreenshotTestDefaultTestSuite` / `testDebugScreenshotTestDefaultTestSuite`.

# Architecture decisions

Accepted decisions are append-only. Proposed entries require approval before application implementation. Supersede an accepted entry with a new ADR and cross-reference both.

| ID | Date | Status | Subject |
|---|---|---|---|
| ADR-001 | 2026-11-18 | Accepted | Documentation contracts are proposed until Kotlin validation exists |
| ADR-002 | 2026-11-18 | Proposed | Application package namespace |
| ADR-003 | 2026-11-18 | Accepted | Retain existing namespace for M1 foundation |
| ADR-004 | 2026-11-18 | Accepted | Expressive theme and asset stack for M1 foundation |
| ADR-005 | 2026-11-18 | Accepted | Navigation Compose for foundation screens |

## ADR-001 — Configuration authority before app implementation

- Date/status: 2026-11-18 / Accepted for bootstrap documentation only.
- Context: Bootstrap requires JSON design and widget catalogs, but the starter has no Compose theme or widget registry.
- Options: claim JSON executable authority; treat JSON as proposed until generated or validated against Kotlin.
- Decision/rationale: Mark JSON `sourceOfTruth: proposed`; future Kotlin implementation is authoritative until generation/validation closes drift.
- Tradeoffs/consequences: Contract can guide implementation, but no runtime behavior is guaranteed. Add parity validation before claiming JSON canonical.
- Affected files: `config/*`, `docs/DESIGN_SYSTEM.md`, `docs/WIDGET_CATALOG.md`.
- PRD IDs: M1-DESIGN-001, M1-WIDGET-001.

## ADR-002 — Package namespace reconciliation

- Date/status: 2026-11-18 / Proposed.
- Context: PRD suggests `com.example.ridecompanion`; existing Gradle uses `com.kapav.ridemode`.
- Options: retain current namespace; rename to PRD example.
- Proposal: retain current namespace because the PRD path is explicitly suggested, but require authorization before source/package changes.
- Tradeoffs/consequences: Docs use relative package structure; source implementation remains pending.
- Affected files: future Android source, Gradle only if renamed.
- PRD IDs: M1-APP-001.

## ADR-003 — Retain existing namespace for M1 foundation

- Date/status: 2026-11-18 / Accepted; resolves the ADR-002 proposal without rewriting it.
- Context: User approved implementation following the documented architecture, which names `com.kapav.ridemode`; the PRD uses an example package.
- Options: retain current namespace; rename the app and package to the PRD example.
- Decision/rationale: Retain `com.kapav.ridemode`, matching Gradle, tests and architecture with no package migration.
- Tradeoffs/consequences: PRD example paths are interpreted structurally; future source uses the existing namespace.
- Affected files: `app/src/main/java/com/kapav/ridemode/*`, `app/src/main/AndroidManifest.xml`, `docs/ARCHITECTURE.md`.
- PRD IDs: M1-APP-001.

## ADR-004 — Expressive theme and assets for M1 foundation

- Date/status: 2026-11-18 / Accepted.
- Context: User requested Material 3 Expressive components and morphing loader, wallpaper colors, Roboto Flex wide titles and normal body, and Phosphor icons. The installed AGP 8.13.2 and API 36 cannot compile Material 3 `1.5.0-alpha23` or `1.5.0-rc01`, which require AGP 9.1/API 37.
- Options: upgrade the Android toolchain; use stable Material 3 `1.4.0` with inaccessible Expressive APIs; use the compatible public Expressive API release `1.5.0-alpha18`.
- Decision/rationale: Use `1.5.0-alpha18` with `MaterialExpressiveTheme`, dynamic wallpaper colors on API 31+, Expressive defaults below API 31, and a bundled Roboto Flex font. Use the `com.adamglin:phosphor-icon:1.0.0` Compose port. Keep Kotlin tokens authoritative under ADR-001.
- Tradeoffs/consequences: Expressive API is prerelease and may change; dynamic colors are unavailable on API 24–30; wide font axis needs API 26, so API 24–25 uses normal width. Revisit dependency version when the toolchain is updated.
- Affected files: Gradle catalog/build files, `app/src/main/java/com/kapav/ridemode/core/ui/*`, font resource, `config/design-tokens.json`, `docs/DESIGN_SYSTEM.md`.
- PRD IDs: M1-APP-001, M1-DESIGN-001.

## ADR-005 — Navigation Compose for foundation screens

- Date/status: 2026-11-18 / Accepted.
- Context: M1-APP-001 requires navigation among Home, Settings, onboarding and permission explanations. The user requested the next foundation part while retaining the Compose architecture and bounded scope.
- Options: hand-written screen state/back stack; Navigation Compose graph.
- Decision/rationale: Use `androidx.navigation:navigation-compose:2.9.8` with four static string routes and a top-level `NavHost`. Screens receive callbacks; the controller remains in `RideApp`. This provides standard back-stack restoration without adding serialization or a routing layer for screens with no arguments.
- Tradeoffs/consequences: One new AndroidX dependency and IDE sync are required; routes are internal constants and can migrate to typed routes if argument needs arise. No navigation logic enters feature screens.
- Affected files: Gradle catalog/build files, `MainActivity.kt`, `core/navigation/RideApp.kt`, `feature/{home,settings,onboarding,permissions}`.
- PRD IDs: M1-APP-001.

## ADR template

`ADR-NNN — Title`; date; status (Proposed/Accepted/Superseded); context; options; decision; rationale; tradeoffs; consequences; affected files; PRD IDs; supersedes, if any.

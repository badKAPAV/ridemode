# M1-HOME-GRID

- Date/status: 2026-11-20 / IN_PROGRESS pending physical-device acceptance.
- Milestone/requirements: M1; M1-APP-001, M1-DESIGN-001, M1-QA-001.
- Objective/current scope (ADR-073 supersedes ADR-072): restore the original full-width Home hero, Start a Ride copy, title/button styling and original neutral RideInfoCard row designs, descriptions and icon tones. Only arrange the lower four menu options in a two-column grid where two 156 dp widths scaled by fontScale (at least 1) plus 12 dp gap fit; otherwise one column.
- Files: feature/home/HomeScreen.kt, RideUiTokens.kt; restore previous Home-only edits to RideComponents.kt, RideApp.kt and strings.xml; proposed design tokens and reconciled docs.
- Dependencies/authorization: explicit user correction. Existing Compose, Material 3, Phosphor icons and shared RideScreen/RideInfoCard/RideActionButton. No dependency, permission, architecture or storage changes. Preserve unrelated dirty work including release baseline profiles and confirmation refinements.
- Exclusions: alternate/active-session hero, full-tile accent backgrounds, extra section heading, content cap, wide split; other screens, orientation policy, saved widget geometry, native integration and persistent settings.
- Acceptance: original Home hero/copy/colors and original five callbacks retained; only menu geometry changes. Text wraps, matching row heights can grow, a single scroll owner remains, narrow/large-font fallback preserves readability and existing touch targets.
- Risks: original horizontal cards wrap longer descriptions within narrower grid cells; device visual/font-scale/tap/TalkBack acceptance remains open.
- Verification: Gradle testDebugUnitTest/lintDebug/assembleDebug, documentation validator and diff whitespace checks. User verifies physical-device light/dark Home, normal/large fonts, scrolling and all five actions before DONE.

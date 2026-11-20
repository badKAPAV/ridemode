# M1-RIDE-INTRO-REDESIGN

2026-11-20. IN_PROGRESS pending user device acceptance. User delegates intro layout/timing replacement; ADR-060 supersedes old motorcycle presentation/timing.

Scope/requirements: M1-APP-001, M1-RIDE-001, M1-PERSIST-001, M1-DESIGN-001, M1-QA-001. Files: RideStartupAnimation, RideUiTokens, strings, proposed design tokens and reconciled docs. Reuse existing Compose/Phosphor/theme/dashboard/intro preference. No new dependencies, permissions, settings or widget types. Prior uncommitted changes preserved.

Acceptance: centered titleLarge RIDE stays fixed; HARD fades fully out before SAFE fades in within a fixed-width slot. Bottom-center helmet advice is one line with a leading Warning icon and safe insets. Entire opaque intro slides upward revealing the existing dashboard. Nominal total 2400 ms with 360 ms upward reveal; no motorcycle. No dashboard interaction before completion. Default-on setting, Reduce motion, active-Ride reopen and lifecycle/PiP bypass remain correct.

Risk/verification: advice can shrink on narrow/large-font screens; actual readability, positioning, pacing and interruptions require a real device. Run existing unit/lint/debug APK, docs validator and diff check; no new mirror/UI unit tests. Device behavior NOT VERIFIED; user acceptance remains open.

Pacing refinement (ADR-061): HARD 800 ms, SAFE 1000 ms, transition speeds unchanged; 2400 ms nominal total.

Warning-position repair (ADR-062): use visibility-independent bar/cutout reservation and an explicit fitted warning-text width; icon/text center must remain steady until the entire intro starts moving upward. Preserve the user's local 70 sp title. Verify gesture/three-button navigation, cutouts and transient bar visibility on device; reported glitch NOT VERIFIED here.

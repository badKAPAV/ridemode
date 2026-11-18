# Requirement traceability

Source of IDs: [PRD](PRD.md). M1 foundation has partial code; no full application requirement is complete. `—` means no code or test evidence yet. Update on each relevant code change.

| Requirement ID | Milestone | Implementation path | Test evidence | State |
|---|---|---|---|---|
| M1-APP-001 | M1 | `MainActivity.kt`, `core/navigation/RideApp.kt`, `feature/{home,settings,onboarding,permissions}`, `core/ui/*`; functional settings/persistence pending | Gradle unit/lint/APK; emulator and Pixel 8a navigation smoke | IN_PROGRESS |
| M1-RIDE-001 | M1 | `feature/ride/RideActivity.kt`, `RideDashboard.kt`, Home launch callback and manifest; fixed preview pages only | Gradle unit/lint/APK; Pixel 8a orientation, swipe, rail, End Ride, window flags; no-call immersive and persisted enabled pages pending | IN_PROGRESS |
| M1-PAGE-001 | M1 | — | — | TODO |
| M1-PERSIST-001 | M1 | — | — | TODO |
| M1-WIDGET-001 | M1 | — | — | TODO |
| M1-OLED-001 | M1 | — | — | TODO |
| M1-ASSIST-001 | M1 | — | — | TODO |
| M1-DESIGN-001 | M1 | `MainActivity.kt` system bar icon contrast, `core/ui/theme`, `core/ui/components`, foundation and Ride screens; wider accessibility work pending | Gradle lint; Pixel 8a light foundation and dark Ride appearance inspected; TalkBack and physical dark foundation appearance pending | IN_PROGRESS |
| M1-QA-001 | M1 | — | — | TODO |
| M2-MEDIA-001 | M2 | — | — | TODO |
| M2-VOLUME-001 | M2 | — | — | TODO |
| M2-CONTACT-001 | M2 | — | — | TODO |
| M2-CALLS-001 | M2 | — | — | TODO |
| M2-NOTIFY-001 | M2 | — | — | TODO |
| M2-MAPS-001 | M2 | — | — | TODO |
| M2-TURN-001 | M2 | — | — | TODO |
| M2-WEATHER-001 | M2 | — | — | TODO |
| M2-QA-001 | M2 | — | — | TODO |
| M3-ASR-001 | M3 | — | — | TODO |
| M3-AUDIO-001 | M3 | — | — | TODO |
| M3-CMD-001 | M3 | — | — | TODO |
| M3-FEEDBACK-001 | M3 | — | — | TODO |
| M3-RELEASE-001 | M3 | — | — | TODO |

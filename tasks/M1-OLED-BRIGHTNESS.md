# M1-OLED-BRIGHTNESS

- Milestone/state: M1 / IN_PROGRESS pending physical-device acceptance.
- Requirements: M1-OLED-001, M1-PERSIST-001, M1-DESIGN-001, M1-QA-001.
- Scope/files: RideActivity, RideDashboard, OledBrightness, RideUiTokens, SettingsScreen, OledBrightnessTest, proposed design tokens and reconciled docs. Preserve existing user token/OLED layout edits.
- Authorization/dependencies: user explicitly requests automatic OLED dimming and a significantly darker Dim screen tier; existing window API, preference and Compose rendering. ADR-070. No new dependency/permission/global brightness mutation.
- Risk: panel minimum/adaptive brightness differs; normalized window requests do not guarantee measured luminance. Low-intensity content needs nighttime/daylight readability check.
- Acceptance: both Classic/Row dim automatically; Dim screen is significantly darker; wake restores normal Ride and consumes first touch; background/PiP restores system control and late saver callbacks cannot reapply; preference changes apply on next saver update with no stale capture. Existing saved Dim screen setting remains authoritative.
- Verification: full unit/lint/debug APK, docs validator, whitespace. Unit tests cover window gate and tier order; device must check both styles with Dim screen off/on, low/high/auto system brightness, touch wake, Home/PiP, resume, preference change and relaunch. No physical-device result claimed by host tests.

Host verification (2026-11-20): JBR 21 full unit/lint/debug APK exit 0 (62 tasks, 1m 21s). Final unit rerun after redundant test removal exit 0 (14s), 55 tests/0 failures/errors. Docs validator and whitespace check exit 0. Device brightness/lifecycle acceptance NOT VERIFIED; task stays IN_PROGRESS.

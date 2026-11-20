# M1-WIDGET-READABILITY — Clock, Battery and notification metadata

Status: IN_PROGRESS (2026-11-20); code/build checks implemented, device acceptance pending.

- Requirements: M1-WIDGET-001, M1-DESIGN-001, M1-PAGE-001, M1-QA-001 and M2-NOTIFY-001 (timestamp UI).
- Scope: one/two-row Clock without weather and larger time/date; Battery horizontal/vertical independent percentage/status slots without This phone; available notification posting date/time.
- Files: ClockWidget, BatteryWidget, NotificationFeedWidget, WidgetCard, WidgetRegistry, legacy Weather conversion in RideRepository, RideUiTokens, widget/design JSON, GridLayoutTest/BatteryContentPlacementTest and docs.
- Acceptance: Clock registry/JSON min 2×1 with 4×3 default; saved row count controls weather visibility: rows one/two omit weather and use larger fitting text, weather begins at three rows. Battery retains N×2/2×N (N≥4), value at start/bottom, power icon at end/top, fill-aware independent placement/contrast, no overlap at empty/full/transition levels and explicit Mock fallback. Notification dates use existing positive posting timestamps; no fabricated time and no Privacy mode leakage.
- Dependencies/risks: no new dependencies, permissions or schema; preserve placements and unrelated uncommitted work. Very narrow/large-font widgets still rely on text fitting. Dates indicate system posting, not guaranteed message-send time.
- Verification: Gradle unit/lint/APK, docs validator and diff check; device Clock 2×1/4×1/4×2/3+ rows, Battery horizontal/vertical low/half/full/charging, timestamps/time format/font scaling/Privacy mode.
- Excluded: speedometer/lean-angle widget code, sensor collection, new permissions, widget registrations and production telemetry architecture. User requested feasibility discussion only. ADR-055/056 cover implemented UI choices; new ideas remain unapproved for implementation.

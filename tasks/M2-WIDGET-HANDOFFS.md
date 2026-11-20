# M2-WIDGET-HANDOFFS — Turn guidance and native assistant entry

2026-11-20. IN_PROGRESS pending user visual/device acceptance. Requirements: M2-TURN-001, new M2-ASSIST-001, M1-ASSIST-001, M1-RIDE-001, M1-PAGE-001, M1-WIDGET-001, M1-DESIGN-001, M1-QA-001.

User-approved scope: disable the Ride rail assistant; launch the current device assistant from its widget; hide Open Maps from editor add menus; redesign notification-derived Turn guidance. User clarified width 4–7 columns, minimum 4 rows, maximum height only the actual grid boundary. Reuse current confirmation component/styles, platform APIs and tokens. No new permission, dependency, service or Room schema. Preserve uncommitted changes, local confirmation styles/title size and saved placements.

Files: RideDashboard; new AssistantWidget/ExternalAssistant, TurnGuidancePresentation and rewritten TurnGuidanceWidget; AdaptiveWidgetText opt-in minimum/ellipsis; WidgetCard, PlannedWidgets, WidgetRegistry/editor picker, RideRepository default template, ExternalMaps strict launch option; config, validator and GuidancePresentationTest.

Acceptance:

- Footer microphone remains visible but disabled and opens no mock dialog. Assistant widget uses Activity.showAssist to ask Android for the current default assistant, with no hard-coded provider, Ridemode mic grant, hotword or fake listening state. Editor preview is inert; no configured/available assistant shows feedback.
- Open Maps stays registered/renderable for saved instances but is hidden from Add widget; new/reset templates do not seed it. No deletion of saved user tiles. Native Google Maps widget and optional Maps toolbar action remain available.
- Turn guidance min 4×4, default 7×4, max 7 columns/device rows. Out-of-contract saved shapes retain their record and show the existing resize/Fit flow. No geometry silently discarded.
- Dark surfaceContainerHigh background, large Phosphor Bold direction vectors, adaptive distance/instruction/footer layout. Only current recognizable notification content renders; stale/missing/unavailable states show exactly “Where do you wanna go?” No experimental/source/preview headings. Supporting details deduplicate without inventing metrics. Text keeps a readable floor and ellipsizes long content rather than shrinking to tiny sizes.
- Tap opens the existing large five-second Ride confirmation; confirm opens Google Maps explicitly, timeout/Back/dismiss does not launch. No browser fallback for this widget; missing/restricted Maps shows feedback. Editor actions are disabled. Both current/idle content use the guarded confirmation.

Risks/limits: Maps notification parsing remains best-effort and currently recognizes English maneuver text; no public turn feed or SDK routing integration is introduced. Notification access still comes from Access and Privacy. Current default assistant behavior/permissions/network are owned by Android/provider. The current assistant may receive context through the platform assist gesture; the app adds no custom notification/route payload. Small cards/long instructions/font scales, real provider handoffs and missing-app states need user device review.

Verification: compile, unit/lint/debug APK and docs/diff checks in SESSION_STATE. GuidancePresentationTest covers stale/invalid content, absent metrics, dedupe, 4..7 sizing/tall shapes and hidden-picker persistence. Native assistant/Maps were not automatically launched for testing; user initiates them explicitly.

Final verification: 49 JVM tests (6 new presentation/size/picker policies), 0 failures/errors; full JDK 17 unit/lint/debug APK exit 0 (62 tasks, 1m 11s). Docs validator/diff checks pass. Native handoff and visual/font-scale acceptance remain pending.

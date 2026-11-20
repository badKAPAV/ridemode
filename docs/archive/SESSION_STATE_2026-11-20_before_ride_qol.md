# Session state

- Timestamp: 2026-11-20 08:57 IST (Asia/Kolkata)
- Current milestone: M1 acceptance and user-directed M2 widget integration; acceptance gates remain open
- Current task: M2-NAV-001 ongoing-navigation voice/confirmation/cleanup refinement IN_PROGRESS pending user device acceptance
- Git branch/head: main / 5bbf0d3; preexisting uncommitted work preserved, including the user's 16 dp footer side inset (now shared sectionGap token); no commit/push
- Changed files (this run): GoogleMapsController.kt, GoogleMapsWidget.kt, MapNavigationCleanup.kt, RideDashboard.kt, MapNavigationCleanupTest.kt, config/design-tokens.json, README.md, docs/DECISIONS.md, docs/PRD.md, docs/DESIGN_SYSTEM.md, docs/ARCHITECTURE.md, docs/ANDROID_CAPABILITIES.md, docs/WIDGET_CATALOG.md, docs/ROADMAP.md, docs/TRACEABILITY.md, docs/SESSION_STATE.md and tasks/M2-NAV.md.
- Verified behavior: compilation, unit regressions, Android lint and debug APK pass. Expanded guidance has Mute/All/Alerts only; End opens a five-second whole-widget confirmation while guidance continues; timeout/Keep/Back/collapse/pause cancels it. Rounded outer/footer corners restored. Teardown disables SDK navigation UI, attempts stop/clear/cleanup, reports errors with Retry end, and restores default map source/padding/north-up current-location camera. These native behaviors and actual SDK notification disappearance are NOT VERIFIED on a device.
- Last verification: JDK 17 `./gradlew --no-daemon --max-workers=1 :app:testDebugUnitTest :app:lintDebug :app:assembleDebug` exit 0 (62 tasks; BUILD SUCCESSFUL in 35s); `bash scripts/verify-docs.sh` exit 0 (24 required files, 25 IDs, 11 widgets); `git diff --check` exit 0. No device/UI tests run.
- Blockers: user device voice/Bluetooth, SDK driving-notification removal, stop/timeout/arrival/permission/task-removal, camera reset and outdoor GPS acceptance. Prior M1/M2 gates remain open.
- Next action: install app/build/outputs/apk/debug/app-debug.apk; test full-grid voice choices, compact voice omission, End → timeout/Keep/Back/confirm, arrival/End Ride, SDK notification removal and rounded idle camera reset; recheck GPS outdoors with a clear sky view.

## Latest mutating run: 2026-11-20 08:57 IST M2-NAV ongoing navigation controls and lifecycle

- Scope: M2-NAV-001, M1-DESIGN-001, M2-QA-001; existing SDK 6.1.0 only, no dependency/permission/persistence/widget-size change. User's existing footer inset preserved via sectionGap.
- Decision: accepted ADR-053 records delegated voice/confirmation/corner/lifecycle fixes. Voice uses installed SDK integer flags and retains Bluetooth for spoken modes. A monotonic five-second confirmation deadline is enforced both by countdown and confirm guard; no automatic stop on timeout. Arrival, permission loss and End Ride bypass user confirmation. SDK task removal uses QUIT_SERVICE.
- Cleanup: shared teardown attempts stopGuidance, clearDestinations and cleanup in order even after failure. Failures are logged and retain Retry end rather than false idle success. Disable navigation UI outside guidance; reset default map location source, padding and north-up/zero-tilt camera after stop and asynchronous SDK UI changes. SDK-ended guidance is checked on UI changes/resume, and stale initialization must not stop a newer request. No NotificationManager cancel-all or hidden ongoing service was added; Ridemode Ride notification remains until End Ride.
- GPS finding: user confirmed the warning occurs indoors/under cover despite a visible location. SDK GPS freshness differs from a cached/network map fix; no one-minute app navigation timeout exists. Genuine SDK warnings remain visible; hardware reception/outdoor behavior is NOT VERIFIED and no fake fixes were added. Sources checked: official Navigator lifecycle/audio flags, NavigationView mode switching and Navigation SDK GPS FAQ; API availability also checked against the local 6.1.0 jar/javadocs.
- Regression checks: MapNavigationCleanupTest covers teardown ordering, continuation after each SDK failure, no false success, countdown boundary/delayed tick and voice/Bluetooth flags. Initial compile exit 1 for a delegated-state smart cast was corrected. Initial proxy-based tests exited 1 because SDK reflection required unavailable JVM classes; the production teardown helper now takes SDK call closures so failure/order tests need no native SDK initialization. An intermediate lint run exited 1 with stale-source indentation reports while code was changing; unchanged-source final unit/lint/APK runs passed (44s, then final 35s after compact touch-target refinement). Final docs and diff checks exit 0.
- README.md: current behavior, GPS explanation and latest successful build reconciled.
- docs/DECISIONS.md: ADR-053 appended; previous ADRs remain immutable.
- docs/ROADMAP.md: ongoing-navigation implementation recorded; native acceptance remains IN_PROGRESS.
- docs/SESSION_STATE.md: current task/files/checks/evidence/blockers/next action reconciled.
- docs/TRACEABILITY.md: Maps implementation plus M1 design and M2 QA regression evidence reconciled; device gate remains open.
- Other docs reconciled: PRD, DESIGN_SYSTEM, ARCHITECTURE, ANDROID_CAPABILITIES, WIDGET_CATALOG, tasks/M2-NAV and proposed design-token config. Registry sizes, manifest permissions, Gradle dependencies and environment-key contracts checked 2026-11-20 and unchanged.

## Latest mutating run: 2026-11-20 08:20 IST M2-NAV keep results after keyboard dismissal

- Scope/acceptance: M2-NAV-001 and M1-DESIGN-001; hiding the keyboard must preserve active search/query/results so a location remains selectable. Only explicit Back or location selection closes the active field. No dependencies, permissions, persistence or architecture changes.
- Fix: removed keyboardWasVisible/IME visibility effect and its unused imports/opt-in from GoogleMapsWidget. Kept the existing controller Back and selection paths, keyboard focus-on-entry and full-window safeDrawing layout. No state mutation is now tied to IME visibility.
- Checks: JDK 17 full unit/lint/APK command above exit 0; docs validator exit 0 and diff check exit 0. Corrected on-device behavior NOT VERIFIED; task stays IN_PROGRESS for device acceptance.
- Decisions: mechanical correction implementing the user's explicit behavior; no new autonomous design choice or ADR.
- README.md: search retention and latest successful build reconciled.
- docs/DECISIONS.md: checked 2026-11-20; unchanged. ADR-051/052 hosting/back/selection contracts still apply.
- docs/ROADMAP.md: active search retention recorded under open M2-NAV acceptance.
- docs/SESSION_STATE.md: task, files, exact checks/results and device next action updated.
- docs/TRACEABILITY.md: M2-NAV-001 includes query/results retention after keyboard dismissal.
- Other docs: PRD user clarification, DESIGN_SYSTEM and tasks/M2-NAV corrected to remove keyboard dismissal as an exit trigger. ARCHITECTURE, WIDGET_CATALOG, ANDROID_CAPABILITIES and config checked 2026-11-20; no contract/token/permission change.

## Latest mutating run: 2026-11-20 08:12 IST M2-NAV keyboard visibility correction

- Scope/files/requirements: M2-NAV-001, M1-DESIGN-001, M2-QA-001; GoogleMapsWidget and RideDashboard plus tracking docs. No dependency, permission, widget-size or persistence change; existing uncommitted work retained.
- Finding: search inherited square-cell canvas and rail bounds, then consumed IME padding inside the reduced viewport. A full-window search host removes the grid constraint and uses one safeDrawing inset owner, including IME/system bars/cutouts. Predictions precede map-pin selection so the action does not displace matches in a short result viewport.
- Decision: ADR-052 records the full-window search correction to ADR-051 and the saver timer pause needed because modal input bypasses the canvas touch observer. From remains non-editable Current location; existing selection/back/draft/route request behavior is retained.
- Acceptance: field above scrollable results, full-window search, fixed origin and mounted map are implemented and pass compile/unit/lint/APK. Actual keyboard rendering, keyboard show/dismiss/back, prediction visibility/select and pin return remain NOT VERIFIED until user device recheck; task stays IN_PROGRESS.
- Checks: JDK 17 full unit/lint/APK command above exit 0 (initial pass 57s; final source pass 37s), docs validator exit 0 and diff check exit 0. Existing build warnings remain; no device evidence claimed.
- README.md: search hosting and latest successful build reconciled.
- docs/DECISIONS.md: appended accepted ADR-052; earlier ADRs unchanged.
- docs/ROADMAP.md: keyboard fix tracked under open M2-NAV acceptance.
- docs/SESSION_STATE.md: current task, branch/head, exact checks/outcomes, blockers and next action updated.
- docs/TRACEABILITY.md: M2-NAV-001 maps to ADR-052/full-window keyboard handling, with device gate open.
- Other docs reconciled: DESIGN_SYSTEM, ARCHITECTURE, WIDGET_CATALOG and tasks/M2-NAV. PRD, ANDROID_CAPABILITIES and config checked 2026-11-20 and unchanged: fullscreen-search requirement, API/permission contracts and visual tokens still apply.

## Latest mutating run: 2026-11-20 07:59 IST M2-NAV responsive map UI

- Scope: M2-NAV-001, M1-DESIGN-001 and M2-QA-001; no dependency, permission, widget-size, persistence or architecture change.
- Decision: ADR-051 records the delegated UI contract. The user's final clarification fixes From to non-editable Current location; editable origins, current-location destinations and intermediate-stop behavior were removed before final verification. Only To supports Places or Choose from map. Pin destinations use coordinates without reverse geocoding; Start ride remains the only route request.
- Checks: initial sandbox Gradle attempt exit 1 because the user-cache lock was inaccessible; approved cache access allowed builds. Intermediate lint runs exited 1 for observable locale, explicit SecurityException handling and an unused layout wrapper; all corrected. Final JDK 17 unit/lint/APK command above exit 0. Docs validator and diff check exit 0. Build warnings remain for existing deprecated APIs/dependency versions/PiP recommendations; no device evidence claimed.
- Review: automatic approval review rejected an optional HEAD-based token-format reconstruction because it could overwrite uncommitted edits. That command did not execute. The safe formatting step used only the current JSON and asserted parsed-value equality; no Git version was restored.
- README.md: updated current Maps behavior, Bold icon exception and latest successful build.
- docs/DECISIONS.md: appended accepted ADR-051; earlier ADR text remains immutable.
- docs/ROADMAP.md: M2-NAV remains IN_PROGRESS with revised implementation and device acceptance evidence.
- docs/SESSION_STATE.md: current branch/head, changed files, exact checks/outcomes and next action reconciled.
- docs/TRACEABILITY.md: M2-NAV-001 implementation/evidence reconciled; device gate open.
- Other docs reconciled: PRD, DESIGN_SYSTEM, ARCHITECTURE, WIDGET_CATALOG, ANDROID_CAPABILITIES, tasks/M2-NAV and proposed design-token config. Widget catalog sizes, permissions, Gradle and environment-key contracts checked 2026-11-20 and unchanged.

## Latest mutating run: 2026-11-19 23:50 IST M2-NAV location and status-label correction

- Scope: M2-NAV-001 and related M1-DESIGN-001 visual contract. Fixed the reported false location-unavailable state without a new dependency or permission. Existing Turn guidance, Maps shortcut and all other widgets remain independent and unchanged.
- Decision: ADR-049 records sequential fused/network/GPS one-shot lookup, fresh cache, eight-second per-provider timeout, cancellation on pause/detach/new request and recent-location status. The top-left idle/status label is black filled text with a white outline and no surface.
- Checks: Kotlin compile exit 0, JDK 17 unit/lint/debug APK exit 0, docs validator exit 0 and diff check exit 0. Corrected location and appearance NOT VERIFIED on a device; user will test. README, DECISIONS, ROADMAP and SESSION_STATE substantively updated; TRACEABILITY, ARCHITECTURE, DESIGN_SYSTEM, WIDGET_CATALOG, ANDROID_CAPABILITIES and task file reconciled. PRD and JSON config checked 2026-11-19; requirement, widget sizes and permissions did not change.
- Next: real-device recenter check and remaining M2-NAV acceptance. No commit/push.

## Latest mutating run: 2026-11-19 23:29 IST M2-NAV implementation

- Scope: M2-NAV-001 only. Registered an independent 4×4-minimum Google Maps widget with NavigationView, India-limited Places Autocomplete (New), 1-second/3-character search gate, session tokens, minimal selected-place Details, point-only confirmation, explicit Start-only route request, active-route-preserving Grid collapse and arrival/end cleanup. Other widget data/content stays independent; Turn guidance received only a no-behavior receiver-scoping compile fix.
- Decision: ADR-048 pins SDK versions compatible with Kotlin 2.0 and excludes duplicate standalone Maps classes. Ignored `.env` keys feed manifest/BuildConfig without committing secrets. SDK merges background location, but app asks only for foreground precise location.
- Checks: Kotlin compile and debug APK exit 0; unit and lint tasks passed; docs validator exit 0 (24 files, 25 IDs, 11 widgets); diff check exit 0. A first combined APK build failed on duplicate Maps classes; the exclusion fixed it. Real-device/UI behavior NOT VERIFIED at the user's direction.
- Reconciliation: README, DECISIONS, ROADMAP, SESSION_STATE, TRACEABILITY, ARCHITECTURE, DESIGN_SYSTEM, WIDGET_CATALOG, ANDROID_CAPABILITIES, task file, registry/catalog and build configuration updated. No ADR invented for mechanical changes; ADR-048 records the SDK/lifecycle choice. No commit/push.
- Next: device acceptance, including Navigation SDK notices and background guidance policy before release. Task remains IN_PROGRESS.

## Latest mutating run: 2026-11-19 22:42 IST M2-NAV contract clarification

- Scope: Documentation-only M2-NAV-001 correction. The user specified start/end point confirmation only, no ETA, a billable Navigation SDK route request only on Start navigation, and complete independence from existing Turn guidance and all other widgets. ADR-047 supersedes the conflicting/open parts of ADR-046; existing Turn guidance/Maps shortcut contracts remain unchanged. No source, config, dependency, permission or credential change.
- Files: `tasks/M2-NAV.md`, README, PRD, DECISIONS, ROADMAP, SESSION_STATE, TRACEABILITY, ARCHITECTURE, DESIGN_SYSTEM, WIDGET_CATALOG and ANDROID_CAPABILITIES. The capabilities table's planned paragraph was also moved below its final row to restore Markdown structure.
- Verification: `bash scripts/verify-docs.sh` exit 0 (24 required files, 25 requirement IDs, 10 widget types); `git diff --check` exit 0. New map implementation and device behavior NOT VERIFIED; prior Turn guidance Kotlin compile/APK evidence remains as recorded in the previous run.
- Blocker/next: user will complete GCP setup; then decide only this widget's location/lifecycle details and implement its isolated map/search/navigation flow. Current M2-NAV status TODO. Proposed JSON files checked 2026-11-19; no widget ID/size registration or runtime contract changed. Preexisting work preserved.

## Latest mutating run: 2026-11-19 22:31 IST Turn guidance then docs-only full Maps plan

- Scope: M2-TURN-001 concrete implementation was already in progress when the user directed a documentation-only M2-NAV-001 plan. No source, config, dependency, permission or architecture implementation was changed after that direction. User specified a 4×4 minimum in-app Navigation SDK/Places SDK (New) widget, full-grid expansion, India autocomplete after three characters and one idle second, Start-only routing, collapse rules and arrival reset. ADR-045 records the implemented conservative notification adapter; ADR-046 is a proposal for the separate future SDK integration.
- Code before docs-only direction: reused the listener for ongoing Google Maps notifications, parsed recognizable maneuver plus available text/ETA/distance, two-minute expiry, removal/revocation clear, accessible compact/expanded cue and tap to source/Maps; shared Ride/editor state. No new dependency or permission. New map widget has no code, config registration or credentials.
- Exact checks: first JDK 17 `:app:compileDebugKotlin` exit 1 on a Compose import, fixed; final JDK 17 `./gradlew --no-daemon --max-workers=1 :app:compileDebugKotlin` exit 0 (18 tasks), `:app:assembleDebug` exit 0 (41 tasks). `bash scripts/verify-docs.sh` exit 0 (24 files, 25 IDs, 10 widget types); `git diff --check` exit 0. No unit/device test by user direction; Maps notification behavior NOT VERIFIED. Earlier clean unit/lint/APK suite passed before this code.
- Reconciliation: README, DECISIONS (ADR-045/046), ROADMAP, SESSION_STATE, TRACEABILITY, PRD, ARCHITECTURE, ANDROID_CAPABILITIES, DESIGN_SYSTEM, WIDGET_CATALOG and both task files updated. Proposed JSON feature flags, design tokens, ride defaults and widget catalog checked 2026-11-19; no runtime registration, key, permission, ID or bounds were changed for the future map widget. Preexisting user changes preserved. M2-NAV remains TODO and M2-TURN IN_PROGRESS.

## Latest mutating run: 2026-11-19 18:33 IST six-row square grid and widget fitting

- Scope: M1-PAGE-001, M1-RIDE-001, M1-WIDGET-001, M1-PERSIST-001, M1-QA-001, plus device-width M2 Calls/Notifications/Volume bounds. User corrected the prior fixed-width square grid using a screenshot, then requested six rows on Pixel 8a, larger cells on bigger screens, dynamic columns and robust widget resizing. ADR-042 supersedes the even-grid rule; ADR-043 supersedes its fixed-column geometry; ADR-044 supersedes the seven-row minimum. No dependency, permission, service or Room schema change.
- Code: Square side uses 52 dp baseline from the connected Pixel 8a's read-only 1080×2400 px/420 dpi metrics, with 52/60/68 dp density choices, six-row/twelve-column minima and extra whole cells on larger screens. Both renderers and repository/editor geometry use device-derived rows/columns. Volume, Calls and Notifications may span all device columns. Rotate offers catalog-valid swapped dimensions only when a free spot exists; it writes transactionally without displacement. On startup, overflowed saved widgets retain ID/content and fit to the largest valid size and nearest free spot when possible; unfit records remain and show Fit to grid. Invalid placements do not expose unsafe resize handles; drag/resize clamps cannot invert.
- Exact checks: initial dynamic-column unit command exit 1 on one template call, fixed and rerun exit 0 (31 tasks). After six-row/saved-fit changes, focused unit command exit 0 (31 tasks). Final JDK 17 `./gradlew --no-daemon --max-workers=1 clean :app:testDebugUnitTest :app:lintDebug :app:assembleDebug` exit 0 (61 tasks). `GridLayoutTest` covers dynamic columns, six-row reference, larger-screen cell floor, overflow/legacy fitting, rotation and bounds. Real-device UI, TalkBack, saved-layout migration and cross-device behavior NOT VERIFIED. `bash scripts/verify-docs.sh` exit 0 (24 files, 24 IDs, 10 widgets); `git diff --check` exit 0.
- Reconciliation: README, DECISIONS (ADR-042/043/044), ROADMAP, SESSION_STATE, TRACEABILITY, PRD, ARCHITECTURE, DESIGN_SYSTEM, WIDGET_CATALOG, ride defaults/widget catalog, docs validator and M1 checklist updated. ANDROID_CAPABILITIES, design tokens and feature flags checked 2026-11-19; underlying permission, design-token and flag contracts unchanged. Preexisting user work preserved.

## Latest mutating run: 2026-11-19 17:42 IST even device-sized grid

- Scope: M1-PAGE-001, M1-RIDE-001, M1-WIDGET-001 and M1-QA-001. User explicitly directed even counts on both grid axes and upward rounding of odd natural counts. ADR-041 records the contract and shorter-cell tradeoff. No dependency, permission, service or persistence schema change.
- Code: `RideGridConstraints.maxRows` now rounds natural row counts up to even with a four-row minimum; twelve columns were already even. Repository bounds, editor dots/drag/resize and Ride widget geometry already consume that same value, so no saved coordinates were moved. Focused tests cover 32/40/48 dp targets and small/odd/even/fractional device sizes.
- Exact checks: JDK 17 `./gradlew --no-daemon --max-workers=1 :app:testDebugUnitTest :app:lintDebug :app:assembleDebug` exit 0 (60 tasks; 21 executed, 39 up-to-date). Real-device grid fit, widget legibility and touch NOT VERIFIED. `bash scripts/verify-docs.sh` exit 0 (24 files, 24 IDs, 10 widgets); `git diff --check` exit 0.
- Reconciliation: README, DECISIONS (ADR-041), ROADMAP, SESSION_STATE, TRACEABILITY, PRD, ARCHITECTURE, DESIGN_SYSTEM, WIDGET_CATALOG and proposed ride-defaults config updated. ANDROID_CAPABILITIES, design tokens, widget catalog config and feature flags checked 2026-11-19; no underlying permission, token, widget ID/size or flag contract change. Preexisting user work preserved.

## Latest mutating run: 2026-11-19 17:37 IST M2 Calls/Notifications and grid minimum

- Scope: M2-CALLS-001, M2-NOTIFY-001 feed slice, M2-QA-001 and shared M1-WIDGET-001. User chose true system Recents in a private APK with an unavailable state if the restricted grant fails, and specified 4×5 minimum to full-grid maximum. ADR-039 records the native access and in-memory feed; ADR-040 records the size contract.
- Code: Up to five system call-log rows and up to ten active notification rows render in a single child scroll per widget. Messages sort before high-priority other alerts; sender/avatar or initials and two-line body truncation are wired. Call rows show available contact/number and require confirmation plus use-time CALL_PHONE before direct calling; emergency numbers open the system dialer. Grant, revoke, removal, privacy and unavailable states are explicit. Registry/catalog set minimum 4×5, default 6×5, maximum device grid; undersized saved widgets prompt for resize. No separate persisted call history or additional notification service. A concurrent user edit to the Notifications heading was preserved.
- Exact checks: initial clean suite exit 1 on API-level lint checks; fixed guards. Subsequent clean suite exit 0 before grid change. First clean suite after grid change exit 1 in generated Kapt output without source diagnostic; final JDK 17 `./gradlew --no-daemon --max-workers=1 clean :app:testDebugUnitTest :app:lintDebug :app:assembleDebug` exit 0 (61 tasks). `CallHistoryPolicyTest`, `NotificationFeedTest` and `GridLayoutTest` included. Real-device installer/grant, Recents, calling, notification listener/ranking/revocation, scrolling and resizing NOT VERIFIED.
- Reconciliation: README, DECISIONS (ADR-039/040), ROADMAP, SESSION_STATE, TRACEABILITY, PRD, ARCHITECTURE, ANDROID_CAPABILITIES, DESIGN_SYSTEM, WIDGET_CATALOG, widget catalog config and `tasks/M2-CALLS-NOTIFICATIONS.md` updated. Ride defaults, design tokens and feature flags checked 2026-11-19; underlying contracts unchanged. `bash scripts/verify-docs.sh` exit 0 (24 files, 24 IDs, 10 widgets); `git diff --check` exit 0. Preexisting uncommitted work preserved.

## Latest mutating run: 2026-11-19 17:03 IST M2 live weather

- Scope: M2-WEATHER-001, M2-QA-001, shared M1-WIDGET-001 and M1-OLED-001. User requested live weather, offline/retry/location-change behavior, low power use and exact thunderstorm fallback. ADR-038 records the provider, permission, cache and lifecycle policy.
- Code: One foreground-only repository uses coarse network location, a 15-minute/10-km location threshold, 0.1° coordinates, Open-Meteo HTTPS current conditions, 30-minute stable-location refresh, one two-hour cache and 1/5/15/30-minute capped retry. Network return can wake a due retry. Clock and both OLED styles share the source and show `Something's not right` with filled CloudLightning when no usable reading exists. Settings requests coarse location and credits Open-Meteo; manifest adds INTERNET, ACCESS_NETWORK_STATE and ACCESS_COARSE_LOCATION. No dependency, background service or Room schema change.
- Exact checks: first `:app:compileDebugKotlin` exit 0 (18 tasks). Clean JDK 17 `./gradlew --no-daemon clean :app:testDebugUnitTest :app:lintDebug :app:assembleDebug` exit 0 (61 tasks), then final clean rerun after retry/validation refinements exit 0 (61 tasks). `WeatherPolicyTest` covers cache expiry, bounded retry and unknown WMO rejection. `bash scripts/verify-docs.sh` exit 0; `git diff --check` exit 0. Real-device provider/permission/offline/movement/retry and power behavior NOT VERIFIED; existing connected device was not modified, consistent with user-run UI acceptance.
- Reconciliation: README, DECISIONS (ADR-038), ROADMAP, SESSION_STATE, TRACEABILITY, PRD, ARCHITECTURE, ANDROID_CAPABILITIES, DESIGN_SYSTEM, WIDGET_CATALOG and `tasks/M2-WEATHER.md` updated. Widget ID/size catalog, ride defaults, design tokens and proposed feature flags checked 2026-11-19; underlying contracts unchanged. Preexisting uncommitted work preserved.

## Latest mutating run: 2026-11-19 16:49 IST M2 system volume

- Scope: M2-VOLUME-001, M2-QA-001 and shared M1-WIDGET-001 renderer. User requested full volume implementation after music. ADR-037 records the music-stream source and observation choice.
- Code: `VolumeRepository` reads/writes bounded `STREAM_MUSIC`, observes settings and audio-device changes, and refreshes on Ride/editor resume. Both orientations use actual percentage; editor canvas remains read-only and preview uses the live control. Hardware keys target music stream in both activities. No dependency, permission, service or schema change.
- Exact checks: first sandbox Gradle attempt exit 1 on external cache lock; escalated incremental compile/unit/APK passed but lint exit 1 on generated Hilt file race. JDK 17 clean `./gradlew --no-daemon clean :app:testDebugUnitTest :app:lintDebug :app:assembleDebug` exit 0 (61 tasks). `VolumeStateTest` included. `bash scripts/verify-docs.sh` exit 0; `git diff --check` exit 0. `adb devices` exit 0 and shows a Pixel device, but no on-device volume test was run; route/key/touch/call behavior NOT VERIFIED.
- Reconciliation: README, DECISIONS (ADR-037), ROADMAP, SESSION_STATE, TRACEABILITY, ARCHITECTURE, ANDROID_CAPABILITIES, DESIGN_SYSTEM, WIDGET_CATALOG and `tasks/M2-VOLUME.md` updated. PRD and config checked 2026-11-19; requirement, widget size/default and proposed feature-flag contracts unchanged. Preexisting uncommitted work preserved.

## Latest mutating run: 2026-11-19 15:25 IST saver transition and M2 music

- Scope: M1-OLED-001, M1-DESIGN-001, M1-QA-001, M2-MEDIA-001 and M2-QA-001. User explicitly directed M2 music start before the M1 device gate closed. ADR-036 records the adapter and permission decision.
- Code: Added a 350 ms two-way saver fade with Reduce motion override. A Hilt singleton observes Android active media sessions through a user-enabled notification-listener component and exposes metadata, art, bounded progress and supported action flags. Shared Ride/editor Music and Classic/Row OLED song areas use the real state. Ride controls supported transport; editor preview cannot. Settings provides a grant path and explains notification-access scope. No new dependency or Room schema; a listener service was added to the manifest.
- Exact checks: first `:app:compileDebugKotlin` exit 1 on nullable art URI; fixed, rerun exit 0. Default-Java unit/lint/APK suite exit 1 during Android unit-test task configuration (`Type T not present`). JDK 17 `:app:testDebugUnitTest :app:lintDebug :app:assembleDebug` exit 0 (60 tasks); final-copy `:app:compileDebugKotlin` exit 0 (18 tasks). `bash scripts/verify-docs.sh` exit 0 (24 files, 24 IDs, 10 widgets); `git diff --check` exit 0. Device UI, external apps and grant/revoke NOT VERIFIED.
- Reconciliation: README, DECISIONS (ADR-036), ROADMAP, SESSION_STATE, TRACEABILITY, PRD, ARCHITECTURE, ANDROID_CAPABILITIES, DESIGN_SYSTEM, WIDGET_CATALOG, design config, M1 checklist and new M2 task updated. Widget registry/catalog JSON, ride defaults and feature flags checked 2026-11-19: no widget ID/size, default, or runtime feature-flag contract changed; proposed JSON flags remain disabled and do not gate code. Preexisting uncommitted work preserved.

## Latest mutating run: 2026-11-19 15:12 IST Row saver corrections

- Scope: M1-OLED-001, M1-DESIGN-001, M1-QA-001. User requested an Outline-aware heavier clock with minimal hour/minute gap, wider panel separation, 1 dp strokes and song/weather icons. ADR-034/035 record the visual corrections.
- Code: Row saver now uses black backgrounds, white text/icons and panel outlines, with no category labels. The clock follows the existing Outline setting; a Roboto Flex weight-1000/grade-150 family uses the font's maximum supported grade and tighter two-line arrangement. Classic retains its earlier appearance. No new dependency, permission, schema or native source.
- Exact checks: first incremental Gradle suite exit 1 from a lint-generated Kapt stub race; clean suite exit 0 (61 tasks) after the first monochrome correction. After the second user correction, clean suite exit 1 in Kapt without a source diagnostic; isolated `:app:kaptDebugKotlin --stacktrace` exit 0 and final Gradle compile/unit/lint/APK suite exit 0 (60 tasks). Docs validator exit 0 (24 files, 24 IDs, 10 widgets); `git diff --check` exit 0. Device UI NOT VERIFIED per user-run acceptance plan.
- Reconciliation: README, DECISIONS (ADR-034/035), ROADMAP, SESSION_STATE, TRACEABILITY, PRD, ARCHITECTURE, DESIGN_SYSTEM, WIDGET_CATALOG, design config and M1 task updated. ANDROID_CAPABILITIES, widget catalog, ride defaults and feature flags checked 2026-11-19; no underlying permission, widget registry or default contract change. Preexisting uncommitted work preserved.

## Latest mutating run: 2026-11-19 15:02 IST page manager and OLED styles

- Scope: M1-PAGE-001, M1-PERSIST-001, M1-OLED-001, M1-DESIGN-001, M1-QA-001; M2-CONTACT-001 scope correction. User requested direct page drag/add/remove/name editing, a selectable row OLED style with Classic default, and real SOS picker/photo/edit work in M2. ADR-033 records the delegated choice.
- Code: Added transactional arbitrary page move, page-manager sheet with direct controls and long-press drag, and retained one-step move actions. Added a persisted `saver_style` preference and Row UI with chunky stacked clock/date and three song/weather/battery panels; local battery level and plugged-in state come from Android battery data. Existing first-touch interception and window-brightness behavior remains shared. No new dependency, manifest permission, Room schema or native contact access.
- Exact checks: incremental `:app:compileDebugKotlin :app:testDebugUnitTest :app:lintDebug :app:assembleDebug` exit 1 from four `Locale.getDefault()` Compose lint errors; changed to observable `LocalLocale`. Clean JDK 17 `:app:compileDebugKotlin :app:testDebugUnitTest :app:lintDebug :app:assembleDebug` exit 0 (61 tasks); `bash scripts/verify-docs.sh` exit 0 (24 files, 24 IDs, 10 widgets); `git diff --check` exit 0. Device UI NOT VERIFIED per user-run acceptance plan.
- Reconciliation: README, DECISIONS (ADR-033), ROADMAP, SESSION_STATE, TRACEABILITY, PRD, ARCHITECTURE, DESIGN_SYSTEM, WIDGET_CATALOG, design/default config and M1 task updated. ANDROID_CAPABILITIES, widget catalog and feature flags checked 2026-11-19; no new permission, widget registry ID/size or feature flag contract. Preexisting uncommitted work preserved.

## Latest mutating run: 2026-11-19 14:39 IST app label correction

- Scope: M1-APP-001 and M1-DESIGN-001; Android label resource, Home title contract and tracking docs. User requested `Ridemode` as the app name while keeping visible `RIDEMODE` with primary-colored `MODE`. No dependency, permission, schema or architecture change.
- Code: Changed only `app_name` to `Ridemode`; the manifest already reads that resource. Home's separate annotated title still appends `RIDE` then primary-colored `MODE`.
- Exact checks: `bash scripts/verify-docs.sh` exit 0 (24 files, 24 IDs, 10 widgets); `git diff --check` exit 0. First Gradle attempt under default Java 24 exit 1 during `:app:testDebugUnitTest` configuration; retry with installed JDK 17 ran `:app:testDebugUnitTest :app:lintDebug :app:assembleDebug` and exited 0 (60 tasks). Launcher/Home appearance on device NOT VERIFIED.
- Reconciliation: README last build/name and TRACEABILITY implementation path updated. DECISIONS and ROADMAP checked 2026-11-19 with no substantive decision or status change. SESSION_STATE updated. PRD, ARCHITECTURE, DESIGN_SYSTEM, WIDGET_CATALOG, ANDROID_CAPABILITIES and all config checked 2026-11-19; their requirements and contracts are unchanged. M1 gate remains TODO; M2 tasks remain TODO behind the documented M1 gate. Preexisting uncommitted work preserved.

## Latest mutating run: 2026-11-19 10:45 IST Ride startup animation and icon exceptions

- Scope: M1-APP-001, M1-RIDE-001, M1-PERSIST-001, M1-DESIGN-001, M1-QA-001. User directed a Settings-toggleable Ride intro, then corrected its duration to about three seconds and bike size to 2.4× the first pass. X and Plus are stroke exceptions. ADR-031/032 record the choices.
- Code: Added a DataStore `ride_start_animation` preference, default on, and Settings switch. Fresh Ride only reads the saved value before showing centered Hard→Safe text and helmet reminder, then a 3.24× viewport-width filled Motorcycle moving from fully offscreen left to right while the dashboard is revealed. The timeline is 650+300+750+1300 = 3000 ms. Reduce motion, notification reopen, PiP and Activity recreation/interruption skip or end the intro. Overlay consumes touch. Ride X and editor Plus controls use Phosphor Regular; all other in-app Phosphor icons stay Fill. Cleared one whitespace-only Home diff line. No dependency, permission or Room schema change.
- Exact checks: first incremental `./gradlew --no-daemon :app:compileDebugKotlin` exit 1 on two Compose receiver mistakes; corrected and rerun exit 0 (18 tasks). Final `./gradlew --no-daemon clean :app:compileDebugKotlin :app:testDebugUnitTest :app:lintDebug :app:assembleDebug` exit 0 (61 tasks) after `requiredSize` and three-second adjustment. Static requirement audit exit 0 for 3000 ms total, 2.4× size, offscreen travel and Regular-only X/Plus exceptions; `bash scripts/verify-docs.sh` exit 0 (24 files, 24 IDs, 10 widgets); `git diff --check` exit 0. Device UI NOT VERIFIED; user performs UI testing.
- Reconciliation: README, DECISIONS (ADR-031/032), ROADMAP, SESSION_STATE, TRACEABILITY, PRD, ARCHITECTURE, DESIGN_SYSTEM, ride/design config and M1 task updated. WIDGET_CATALOG, ANDROID_CAPABILITIES, widget catalog and feature flags checked 2026-11-19; no underlying widget/permission contract change. Preexisting uncommitted work preserved.

## Latest mutating run: 2026-11-19 10:14 IST solid Phosphor icons

- Scope: M1-APP-001, M1-WIDGET-001, M1-DESIGN-001, M1-QA-001; in-app icons across Home, secondary pages, editor, Ride and widgets. ADR-030 records the user-directed style rule.
- Code: Replaced every Phosphor Bold icon import/reference with its Fill counterpart in 11 Kotlin UI files. All 31 used glyph names have a Fill source in the pinned library; all 51 app references now use Fill. Existing icon meanings, tint, size, content descriptions and actions remain. The Android notification icon was already a filled monochrome vector. No dependency, permission, schema or M2/M3 change.
- Exact checks: `./gradlew --no-daemon :app:compileDebugKotlin` exit 0 (18 tasks); `./gradlew --no-daemon clean :app:compileDebugKotlin :app:testDebugUnitTest :app:lintDebug :app:assembleDebug` exit 0 (61 tasks); icon source audit exit 0 (51 Fill, 0 other Phosphor variants); `bash scripts/verify-docs.sh` exit 0 (24 files, 24 IDs, 10 widgets); `git diff --check` exit 0. Device UI NOT VERIFIED per user testing preference.
- Reconciliation: README, DECISIONS (ADR-030), ROADMAP, SESSION_STATE, TRACEABILITY, PRD, DESIGN_SYSTEM, WIDGET_CATALOG, design config and M1 task updated. ARCHITECTURE, ANDROID_CAPABILITIES, widget catalog, ride defaults and feature flags checked 2026-11-19; no underlying architecture, permission, widget registry or default contract change. Preexisting uncommitted work preserved.

## Latest mutating run: 2026-11-19 10:06 IST deferred widget presentation

- Scope: M1-WIDGET-001, M1-DESIGN-001, M1-QA-001; four previously generic widget previews and shared renderer only. ADR-029 records the delegated visual and state-input choice.
- Code: Calls, Notifications, Turn guidance and Assistant now have distinct Material 3 tonal layouts; smallest one-row cards use a short label. Typed states cover unavailable/empty/content, notification redaction, guidance stale and assistant mock/live states. `WidgetCard` accepts optional `DeferredWidgetContent`; M1 defaults are unavailable or mock. No Android service, permission, dependency, persistence schema or native action added.
- Exact checks: initial `./gradlew --no-daemon :app:compileDebugKotlin` exit 1 because the sandbox blocked the Gradle cache; escalated retry exit 0 (18 tasks). First full incremental Gradle suite exit 1 on generated Hilt class transform; clean full suite exit 0 (61 tasks), then final clean full suite exit 0 (61 tasks) after source refinements. `bash scripts/verify-docs.sh` exit 0; `git diff --check` exit 0. User device UI NOT VERIFIED.
- Reconciliation: README, DECISIONS (ADR-029), ROADMAP, SESSION_STATE, TRACEABILITY, PRD, ARCHITECTURE, DESIGN_SYSTEM, WIDGET_CATALOG, design config and M1 task updated. ANDROID_CAPABILITIES, widget catalog, ride defaults and feature flags checked 2026-11-19; no underlying permission, ID/size or default contract change. Preexisting uncommitted work preserved.

## Latest mutating run: 2026-11-19 09:52 IST non-Ride spacing correction

- Scope: M1-APP-001, M1-DESIGN-001, M1-QA-001. User requested a smaller, inset Back visual and less screen/container padding on non-Ride/editor pages. ADR-028 records the user-directed density choice.
- Code: Kept the 56 dp `IconButton` hit area and drew a 40 dp tonal circle with a 20 dp arrow after an 8 dp start inset. Introduced non-Ride screen/card/icon/toggle spacing tokens; applied them through shared `RideScreen`, `RideInfoCard`, Home hero and Settings panels. Ride/editor spacing unchanged. No dependency, permission, Room or DataStore change.
- Exact checks: Gradle compile/unit/lint/APK exit 0 (60 tasks); docs validator exit 0; diff check exit 0. UI/device NOT VERIFIED by user request.
- Reconciliation: README, DECISIONS (ADR-028), ROADMAP, SESSION_STATE, TRACEABILITY, PRD, DESIGN_SYSTEM and design config updated. ARCHITECTURE, WIDGET_CATALOG, ANDROID_CAPABILITIES, widget catalog/ride defaults/feature flags checked 2026-11-19; no underlying contract change.

## Latest mutating run: 2026-11-19 09:44 IST Expressive navigation and controls

- Scope: M1-APP-001, M1-DESIGN-001, M1-QA-001. User linked Material 3 Expressive guidance and requested app-bar titles beside Back, distinct icons, redundant button removal and more expressive non-Ride/editor pages. ADR-027 records the delegated screen choices.
- Code: Shared `RideScreen` uses `TopAppBar` with filled tonal Back; content starts with optional supporting text. Home navigation and Settings controls use distinct Phosphor icons; `RideInfoCard` alternates tonal icon containers. Settings uses connected expressive `ToggleButton` choices for idle delay, with radio semantics. Removed onboarding Return home button and string. No dependency, permission, data or architecture change.
- Exact checks: first Gradle compile failed on missing `role` import; corrected. Final Gradle compile/unit/lint/APK exit 0 (60 tasks); docs validator exit 0; diff check exit 0. UI/device NOT VERIFIED by user request.
- Reconciliation: README, DECISIONS (ADR-027), ROADMAP, SESSION_STATE, TRACEABILITY, PRD, ARCHITECTURE and DESIGN_SYSTEM updated. WIDGET_CATALOG, ANDROID_CAPABILITIES, widget/design/default/feature config checked 2026-11-19; no widget, permission, API or numeric token contract change.

## Latest mutating run: 2026-11-19 09:35 IST M1 visual refresh

- Scope: M1-APP-001, M1-WIDGET-001, M1-DESIGN-001, M1-QA-001. User asked for a tighter large Music header, modern Home/other screens and clean custom Material 3 Expressive previews for the remaining widgets. ADR-026 records the delegated visual choices.
- Code: Home primary hero and compact action cards; tonal editor/Ride rails; common title/subtitle header, tonal row cards, grouped Settings switches and selected delay chips; onboarding and access pages use the same screen language. Calls, Notifications, Turn guidance and Assistant route through size-aware tonal preview variants with explicit M2/M3 status. Music title/artist share a centered bounded block with 4 dp gap. No native integration, permission, dependency or schema change.
- Exact checks: Gradle compile/unit/lint/APK exit 0 (60 tasks). Docs validator exit 0; diff check exit 0. UI/device NOT VERIFIED by user request.
- Reconciliation: README, DECISIONS (ADR-026), ROADMAP, SESSION_STATE, TRACEABILITY, PRD, ARCHITECTURE, DESIGN_SYSTEM, WIDGET_CATALOG and design config updated. ANDROID_CAPABILITIES, widget catalog/ride defaults/feature flags checked 2026-11-19; no API, size or default contract change.

## Latest mutating run: 2026-11-19 09:04 IST editor and worked-widget polish

- Scope: M1-PAGE-001, M1-RIDE-001, M1-WIDGET-001, M1-DESIGN-001, M1-QA-001; existing redesigned widgets and shared editor/Ride layout only. ADR-025 records the user-directed design changes.
- Code: Add flow searches valid grid sizes from shortest area; editor shows dotted grid, all-side selection handles and bottom-rail widget options; Ride uses saved geometry and clickable page names. Roboto Flex body uses title axes with weight 400/grade 0. Music metadata centers vertically, Clock has a rounded container and centered weather group, Maps icon/title center together, Battery defaults to 4×2 and shows plugged-in icon, SOS fits multiple contacts in a short row. Remaining generic widgets, M2 native actions and permissions remain unchanged.
- Exact checks: final Gradle compile/unit/lint/APK suite exit 0 (60 tasks); docs validator exit 0; diff check exit 0. UI/device NOT VERIFIED by request.
- Reconciliation: README, DECISIONS (ADR-025), ROADMAP, SESSION_STATE, TRACEABILITY, PRD, ARCHITECTURE, DESIGN_SYSTEM, WIDGET_CATALOG, widget/design config updated. ANDROID_CAPABILITIES, ride defaults and feature flags checked 2026-11-19; no API, permission or default-page contract change.

## Latest mutating run: 2026-11-19 00:22 IST responsive widget sizing and text

- Scope: M1-WIDGET-001, M1-PAGE-001, M1-DESIGN-001, M1-QA-001. User specified 2×N/N×2 Battery (N≥4), square-or-landscape Music from 5×4, text that wraps without tiny fonts, and multi-row SOS. ADR-024 records the user-directed contract and implementation tradeoffs.
- Code: Registry/catalog enforce the new Battery/Music shapes. Battery draws horizontal or vertical split from the saved shape, with reduced text insets; Music metadata uses larger max sizes. Shared `AdaptiveWidgetText` measures actual bounds, scales line height, wraps, prefers 14 sp (higher for Battery percent) and shrinks further only when otherwise overflowing. Maps title no longer has a width-fraction cap. SOS chooses up to six tiles from width and height with a 90 dp preferred tile minimum. Remaining generic cards and resize guidance use the shared fitter. No native integration, permission, dependency or schema change.
- Exact checks: `./gradlew --no-daemon :app:compileDebugKotlin :app:testDebugUnitTest` exit 0 (31 tasks) after the first sizing pass; `./gradlew --no-daemon :app:testDebugUnitTest :app:lintDebug :app:assembleDebug` exit 0 (60 tasks) after the readable-font correction, before final SOS minimum/docs edits; final `bash scripts/verify-docs.sh`, `git diff --check` and `./gradlew --no-daemon :app:testDebugUnitTest :app:lintDebug :app:assembleDebug` all exit 0 (60 Gradle tasks) after the badge-clearance edit. No UI/device test was run; user manual checklist is in `tasks/M1-COMPLETION.md`.
- Reconciliation: README, DECISIONS (ADR-024), ROADMAP, SESSION_STATE, TRACEABILITY, PRD, ARCHITECTURE, DESIGN_SYSTEM, WIDGET_CATALOG, widget/design config and M1 task updated. ANDROID_CAPABILITIES checked 2026-11-19; no API/permission change. Other config checked 2026-11-19; no contract change. User changes and uncommitted state preserved.

## Latest mutating run: 2026-11-18 23:43 IST Battery, Maps and SOS visual pass

- Scope: M1-WIDGET-001, M1-PAGE-001, M1-DESIGN-001, M1-QA-001. User supplied three image references for split Battery, filled Maps shortcut and square SOS favorites with image/initials fallback. ADR-023 records the user-directed contract and Battery minimum-size choice.
- Code: dedicated renderers bypass generic cards. Battery reads local capacity and ACTION_BATTERY_CHANGED while composed, draws two gap-separated segments, puts thin wide `This phone` above large bold percentage and switches both to the side with room; labeled 63% mock fallback if unavailable. Battery registry is 4×2 / 6×3 / 12×6; legacy smaller cards are preserved with resize prompt. Maps is a filled primary rounded tile with filled NavigationArrow and bold wide title, no M1 Intent. SOS uses six mock contacts, optional supplied image painter or initials, and width-based 1–6 square tiles with even spacing, no M1 call action. No schema, dependency or permission added.
- Exact checks: `./gradlew --no-daemon :app:testDebugUnitTest :app:lintDebug :app:assembleDebug` exit 0 (60 tasks); grid tests cover Battery minimum, Battery text-side choice and SOS 0/1/2/3/6 counts. `bash scripts/verify-docs.sh` exit 0 (24 files, 24 IDs, 10 widgets); `git diff --check` exit 0. UI/device NOT VERIFIED per user instruction; manual scenarios in `tasks/M1-COMPLETION.md`.
- Reconciliation: README, DECISIONS, ROADMAP, SESSION_STATE, TRACEABILITY, PRD, ARCHITECTURE, DESIGN_SYSTEM, WIDGET_CATALOG, ANDROID_CAPABILITIES, widget/design config and M1 task updated. Default pages and feature flags checked 2026-11-18, unchanged contracts. Preexisting user work preserved.

## Latest mutating run: 2026-11-18 23:15 IST Clock typography refinement

- Scope: M1-WIDGET-001, M1-DESIGN-001, M1-QA-001. User corrected date/weather typography to the same wide Roboto Flex family with less grade/weight, smaller size and centered layout. ADR-022 supersedes ADR-021's Jakarta Sans supporting text and top-left placement only.
- Code: Clock date and weather use `RideThinTitleFontFamily` (width 151, weight 300, grade 0) at 32% of responsive time size, bounded 11–18 sp; time keeps bold title axes. The whole block, text lines and weather row are centered; weather icon has 14 dp floor. Removed the prior extra Light Jakarta font mapping. No catalog, persistence, schema, permission or native weather change.
- Exact checks: `./gradlew --no-daemon :app:testDebugUnitTest :app:lintDebug :app:assembleDebug` exit 0 (60 tasks); `bash scripts/verify-docs.sh` exit 0 (24 files, 24 IDs, 10 widgets); `git diff --check` exit 0. UI/device NOT VERIFIED per user instruction; 2×2/default/full-size and large-font checks in `tasks/M1-COMPLETION.md`.
- Reconciliation: README, DECISIONS, ROADMAP, SESSION_STATE, TRACEABILITY, PRD, ARCHITECTURE, DESIGN_SYSTEM, WIDGET_CATALOG, design config and M1 task updated. ANDROID_CAPABILITIES, widget/default catalog config and feature flags checked 2026-11-18, unchanged API/permission/grid contracts. Preexisting user work preserved.

## Latest mutating run: 2026-11-18 23:09 IST Clock and weather combination

- Scope: M1-WIDGET-001, M1-PAGE-001, M1-DESIGN-001, M1-QA-001. User requested large time, smaller thin day/date, mock icon/temperature/condition beneath and no background; separate Weather widget retired. ADR-021 records the combination and saved-placement policy.
- Code: dedicated `ClockWidget` uses local time/date, condition-mapped Phosphor icon and explicitly labeled mock 24°C partly cloudy sample. Clock is 2×2 minimum, 4×3 default, 12×24 maximum, clamped to device rows. Weather removed from active registry/catalog/add sheet and new Clock & Weather template. `ensureSeeded` converts saved Weather instances to Clock in place, retaining geometry and IDs; legacy one-row tiles show resize prompt. Light Jakarta font registration supports thin date. No schema, dependency, permission or live weather provider change.
- Exact checks: initial Gradle unit/lint/APK suite exit 1 from non-observable `Locale.getDefault()` lint; changed to `LocalLocale.current.platformLocale`, then suite exit 0 (60 tasks). Rerun after Light font registration exit 0 (60 tasks). Incremental `:app:compileDebugKotlin` exit 1 on generated `R.jar` transform; clean unit/lint/APK suite exit 0 (61 tasks). `bash scripts/verify-docs.sh` exit 0 (24 files, 24 IDs, 10 widgets); `git diff --check` exit 0. UI/device and saved Weather conversion NOT VERIFIED per user instruction; manual scenarios in `tasks/M1-COMPLETION.md`.
- Reconciliation: README, DECISIONS, ROADMAP, SESSION_STATE, TRACEABILITY, PRD, ARCHITECTURE, DESIGN_SYSTEM, WIDGET_CATALOG, widget/default/design config and M1 task updated. ANDROID_CAPABILITIES and feature flags checked 2026-11-18, unchanged permission/provider contract. Preexisting user work preserved.

## Latest mutating run: 2026-11-18 22:33 IST volume structure correction

- Scope: M1-WIDGET-001, M1-DESIGN-001, M1-QA-001. User supplied a second reference clarifying two detached segments, a crossing divider, dynamic colors and a wide bold title counter. ADR-020 supersedes ADR-019's continuous-track visual only; grid geometry and mock-only behavior remain.
- Code: `VolumeWidget` now draws separate paths with rounded outer ends and flat inner ends, a transparent 14 dp gap and 4 dp primary divider. Empty uses translucent dynamic primary; fill uses solid dynamic primary. The number uses the existing Roboto Flex title style. Saved grid geometry determines vertical/horizontal mode. No dependency, permission, Room schema or AudioManager change.
- Exact checks: `./gradlew --no-daemon :app:testDebugUnitTest :app:lintDebug :app:assembleDebug` exit 0 (60 tasks) after path drawing, after orientation argument and after endpoint rounding (60 tasks each). `bash scripts/verify-docs.sh` exit 0; `git diff --check` exit 0. UI/device NOT VERIFIED per user instruction; manual scenarios in `tasks/M1-COMPLETION.md`.
- Reconciliation: README, DECISIONS, ROADMAP, SESSION_STATE, TRACEABILITY, PRD, ARCHITECTURE, DESIGN_SYSTEM, WIDGET_CATALOG, design config and M1 task updated. ANDROID_CAPABILITIES and widget catalog config checked 2026-11-18, unchanged API/permission and grid contracts. Preexisting user work preserved.

## Latest mutating run: 2026-11-18 22:26 IST volume visual pass

- Scope: M1-WIDGET-001, M1-PAGE-001, M1-DESIGN-001, M1-QA-001. User supplied a Pixel-style vertical bar reference and requested transparent outer layout, one-cell thickness, max grid width/height, horizontal support, animated fill/counter and immediate mock value feedback. ADR-019 records the user-directed contract.
- Code: dedicated `VolumeWidget` with local saved mock percentage, vertical/horizontal rounded track, primary fill and divider, adaptive animated number, pointer and progress semantics; generic outer card removed for volume. Registry/catalog accept only N×1 or 1×N (N≥2), with max 12 columns/device-bounded rows. A lone volume widget retains its grid bounds in Ride. Existing invalid saved shapes show resize prompt; no Room schema, dependency, permission or M2 AudioManager change.
- Exact checks: initial sandboxed Gradle suite exit 1 due out-of-workspace cache lock; approved `./gradlew --no-daemon :app:testDebugUnitTest :app:lintDebug :app:assembleDebug` exit 0 (60 tasks). `bash scripts/verify-docs.sh` exit 0; `git diff --check` exit 0. UI/device NOT VERIFIED per user instruction; manual scenarios in `tasks/M1-COMPLETION.md`.
- Reconciliation: README, DECISIONS, ROADMAP, SESSION_STATE, TRACEABILITY, PRD, ARCHITECTURE, DESIGN_SYSTEM, WIDGET_CATALOG, widget/design config and M1 task updated. ANDROID_CAPABILITIES checked 2026-11-18, unchanged API/permission boundary. Other config checked 2026-11-18, unchanged. Preexisting user work preserved.

## Latest mutating run: 2026-11-18 22:11 IST music reference correction and spacing

- User refined music to device-row max height, separate cover/detail surfaces, filled capsule-when-paused play button, 8 dp widget spacing and animated shape; then supplied an image clarifying that cover art overlays the left of one full-width metadata background. ADR-016/017/018 record successive user-directed visual contracts without rewriting prior ADRs.
- Code: catalog music max 24 rows with device clamp; full 12-column width may reach the final grid row, while partial widths remain wider than tall. Music header now uses a full-width low-opacity primary details surface, right-inset mock text, and a separate rounded square artwork tile at left, matching the reference. Filled center Button animates corner radius over 220 ms; shared editor/Ride widget gap is 8 dp. Mock-only state and M2 boundary preserved. No dependency, permission or Room schema change.
- Exact checks: first incremental Gradle suite exit 1 due missing dex archive; clean suite exit 0 (61 tasks). After spacing/animation, initial compile exit 1 due wrong import, corrected; final `./gradlew --no-daemon :app:testDebugUnitTest :app:lintDebug :app:assembleDebug` exit 0 (60 tasks) after image-based header correction. `bash scripts/verify-docs.sh` and `git diff --check` exit 0. User UI/device check NOT VERIFIED.
- Reconciliation: README, DECISIONS, ROADMAP, SESSION_STATE, TRACEABILITY, PRD, ARCHITECTURE, DESIGN_SYSTEM, WIDGET_CATALOG, widget/design config and M1 task updated. ANDROID_CAPABILITIES checked 2026-11-18, unchanged API/permission contract. Other config checked 2026-11-18, unchanged. Preexisting user work preserved.

## Latest mutating run: 2026-11-18 22:04 IST M1 music-widget refinement

- Scope: M1-WIDGET-001, M1-PAGE-001, M1-DESIGN-001, M1-QA-001. User asked for device-grid maximum music height, an always-filled play control with capsule paused shape, and separate vertically stacked artwork/details surfaces with low-opacity primary metadata color. ADR-016 records this explicit direction; mock-only M1 boundary remains.
- Music max height is 24 catalog rows and actual placements remain clamped to device rows; full 12-column width may use the screen's full row count, while partial-width music must stay wider than tall. Added registry assertions. Artwork and details now occupy separate full-width rows above the horizontal controls. Play uses a filled Material button in Ride, with capsule paused shape and rounded rectangle playing shape. Editor keeps noninteractive filled preview so drag remains available. No dependency, permission, Room schema or native media integration changed.
- Verification: initial `./gradlew --no-daemon :app:testDebugUnitTest :app:lintDebug :app:assembleDebug` exit 1 on missing local dex archive; `./gradlew --no-daemon clean :app:testDebugUnitTest :app:lintDebug :app:assembleDebug` exit 0 (61 tasks). Registry assertion was added after that clean build; final checks pending. UI/device NOT VERIFIED per user instruction.
- Reconciliation: README, DECISIONS, ROADMAP, SESSION_STATE, TRACEABILITY, PRD, ARCHITECTURE, DESIGN_SYSTEM, WIDGET_CATALOG, widget/design config and M1 task updated. ANDROID_CAPABILITIES checked 2026-11-18, unchanged permission/API contract. Other config checked 2026-11-18, unchanged. Preexisting work preserved.

## Latest mutating run: 2026-11-18 20:00 IST M1 music-widget design

- Scope: M1-WIDGET-001, M1-PAGE-001, M1-DESIGN-001, M1-QA-001. User specified a landscape-only responsive music column, attached mock art/details, transparent previous/next and filled stateful play/pause. ADR-015 records the user-directed design and replaces the prior fixed-control design choice for music only.
- Added `MusicWidget.kt`, routed music out of the generic outer card, and added local-only mock track/playing state. Music registry rejects grid width at or below height; JSON catalog and docs validator reflect the rule. Added focused orientation assertions. No new dependency, permission, Room schema or M2 media integration.
- Exact checks: `./gradlew --no-daemon :app:testDebugUnitTest :app:lintDebug :app:assembleDebug` exit 0 (60 tasks) after full-width revision; `bash scripts/verify-docs.sh` exit 0; `git diff --check` exit 0. User UI/device checks NOT VERIFIED; manual scenarios in `tasks/M1-COMPLETION.md`.
- Reconciliation: README, DECISIONS, ROADMAP, SESSION_STATE, TRACEABILITY, PRD, ARCHITECTURE, DESIGN_SYSTEM, WIDGET_CATALOG, widget/design config and M1 task updated. ANDROID_CAPABILITIES checked 2026-11-18, unchanged permission/API boundary. Other config checked 2026-11-18, unchanged contract. Preexisting user work preserved.

## Latest mutating run: 2026-11-18 19:47 IST Ride visual refinement

- Scope: M1-RIDE-001, M1-OLED-001, M1-DESIGN-001, M1-QA-001. User supplied exact Roboto Flex specimen axes, single-column OLED order, thin wide adjacent page names, outlined two-line End/ride control and secondary mic color. ADR-014 records this and supersedes the conflicting visual parts of ADR-013.
- Changed `RideTypography.kt`, `RideDashboard.kt`, `config/design-tokens.json` and M1 documentation. No dependency, permission, Room schema or native M2 change. Kept existing page animation and first-touch saver behavior.
- Exact checks: `./gradlew --no-daemon :app:testDebugUnitTest :app:lintDebug :app:assembleDebug` exit 0 (60 tasks); `bash scripts/verify-docs.sh` exit 0; `git diff --check` exit 0. UI/device checks NOT VERIFIED per user instruction; user checklist is in `tasks/M1-COMPLETION.md`.
- Reconciliation: README, DECISIONS, ROADMAP, SESSION_STATE, TRACEABILITY, PRD, ARCHITECTURE, DESIGN_SYSTEM, WIDGET_CATALOG, design config and M1 task updated. ANDROID_CAPABILITIES checked 2026-11-18, unchanged permission/API contract. Other config checked 2026-11-18, unchanged contract. Preexisting work preserved.

## Latest mutating run: 2026-11-18 19:33 IST Ride visual follow-up

- User said the previous UI looks good and requested a wider/heavier Roboto Flex title and outlined OLED clock, battery icon/percentage at the OLED bottom, labeled mock song/weather, icon-led Ride actions and a simultaneous animated previous/current/next page indicator. This confirms the previous slice visually but is not a full M1 gate report.
- ADR-013 records the user-directed visual choice and supersedes ADR-012 only for the mock OLED preview omission. Code changes are limited to RideDashboard, typography and shared tokens. No dependency, permission, schema or native M2 integration changed.
- Exact check: `./gradlew --no-daemon :app:testDebugUnitTest :app:lintDebug :app:assembleDebug` exit 0 (60 tasks). No UI/device checks run per user direction. New Ride visuals are NOT VERIFIED on device until user reports them.
- Reconciliation: README, DECISIONS, ROADMAP, SESSION_STATE, TRACEABILITY, PRD, ARCHITECTURE, DESIGN_SYSTEM, WIDGET_CATALOG, design config and M1 task updated. ANDROID_CAPABILITIES checked 2026-11-18, unchanged API/permission boundary. Other config checked 2026-11-18, unchanged contract. Preexisting work preserved.

## Latest mutating run: 2026-11-18 19:17 IST M1 UI follow-up

- Scope: M1-PAGE-001, M1-WIDGET-001, M1-OLED-001, M1-DESIGN-001, M1-QA-001; no dependency, permission or Room schema change. ADR-012 records the delegated single-widget rendering, displacement and available-data OLED choice.
- Code: atomic one-widget displacement with nearest valid placement, non-UI geometry test, add-widget bottom sheet over blurred editor, full-canvas one-widget Ride/preview rendering, local battery and subdued bottom wake hint in OLED; saver clock now inherits Roboto Flex title style. Page Move earlier/later already existed. Native media/weather are absent, so OLED does not fabricate them.
- Checks: Gradle unit/lint/APK exit 0 (60 tasks) after a corrected Material experimental opt-in. Docs validator exit 0 and diff check exit 0 after reconciliation. No UI/device tests run per user direction; new UI behavior NOT VERIFIED on device.
- Reconciliation: README, DECISIONS, ROADMAP, SESSION_STATE, TRACEABILITY, PRD, ARCHITECTURE, DESIGN_SYSTEM, WIDGET_CATALOG, design config and M1 task updated. ANDROID_CAPABILITIES checked 2026-11-18, unchanged permission/API boundary. Other config checked 2026-11-18, unchanged contracts. Preexisting user files preserved.

## Latest mutating run: 2026-11-18 M1 completion implementation

- 15:44 IST addendum: Confirmed DataStore reduced-motion override survived force-stop/relaunch; restored original off setting. A call interrupted the next temporary editor page-action check, so device interaction stopped until the editor again held focus. Deleted the temporary seventh page and confirmed six pages remain; left device on Home. The page-action check remains NOT VERIFIED. The Pixel connects over wireless ADB, so offline startup was NOT VERIFIED to avoid disconnecting the only device test connection. README, ROADMAP, TRACEABILITY and this state reconciled; DECISIONS checked 2026-11-18 with no new choice. No code or config contract change.

- 15:39 IST addendum: Pixel 8a OLED saver test passed at selected 15 seconds: true-black outlined clock appeared; tap on underlying End ride location woke without activation; next tap ended Ride. Restored original saver disabled and 30-second delay and left device on Home. Notification prompt was denied to preserve the user's stated denial path; no additional denial behavior was evaluated. No code change; README, ROADMAP, TRACEABILITY, DESIGN_SYSTEM and this state updated. DECISIONS checked 2026-11-18, no new decision. ANDROID_CAPABILITIES, WIDGET_CATALOG and config checked 2026-11-18, unchanged contracts.

- 15:34 IST addendum: Tested temporary seventh page on Pixel 8a: added clock, resized 4×2 to 5×3 with corner point, dragged one grid cell, force-stopped/reopened app and confirmed saved geometry, then deleted the test page; six original pages remain. Fixed overlapping touch areas on short widgets by showing only the corner handle there. Final full Gradle suite exit 0 (91 tasks, 3 device tests), APK reinstalled and Home restored. Documentation and config reconciled; no new capability or dependency. No commit/tag.

- 15:26 IST addendum: User specified bold wide Roboto Flex titles per supplied image, Plus Jakarta Sans body/labels, more landscape side space, full width/device-bounded height volume sizing and larger music controls. ADR-011 records this. Added OFL font/license, 40 dp shared horizontal insets, aspect-aware full-area mock volume slider, 64 dp music art/transport, reduced editor motion preference and stricter editor action feedback. Fixed resize preview when a legacy widget cannot fit its new minimum at its current x/y. Added registry boundary unit coverage. User clarified M1 prioritizes flexible bounded widgets and a reliable editor, with final visual polish deferred. Latest full Gradle suite, docs validator and diff check passed. README, DECISIONS, ROADMAP, SESSION_STATE, TRACEABILITY, PRD, ARCHITECTURE, DESIGN_SYSTEM, WIDGET_CATALOG, config and task file reconciled. ANDROID_CAPABILITIES checked 2026-11-18; no capability change. User `.idea` files preserved.

- 15:16 IST addendum: User required a landscape WYSIWYG editor, constrained widget sizes, music art/metadata/three large controls and more icon-led UI. ADR-010 records the choice. Added landscape editor Activity, shared fixed geometry and preview, 6×4 minimum music card, icons and 150 ms preview transition. Clean build fixed stale Hilt generated component that initially crashed on editor launch. Full Gradle unit/lint/APK/connected-device suite passed (exit 0, 91 tasks, 3 device tests); docs validator passed (exit 0). README, DECISIONS, ROADMAP, TRACEABILITY, ARCHITECTURE, DESIGN_SYSTEM, WIDGET_CATALOG, PRD, config and this state reconciled. ANDROID_CAPABILITIES checked 2026-11-18; no capability change. User `.idea` files preserved.

- User accepted the notification-denial behavior as manually verified and requested M1 development. User then specified selected-widget resize points, a fixed device-sized non-scrolling Ride canvas, compact bold rounded-rectangle rail buttons and no duplicate top page title. ADR-008 records that responsive/editor interaction; ADR-009 records Hilt/ViewModel build integration. ADR-006 supplies the already approved Room/DataStore schema. No new sensitive permission or native M2/M3 service was added.
- Added Room version 1 pages/widgets, schema export, DataStore preferences, Hilt/ViewModel StateFlows, seeded editable templates, editor operations/drag/resize, shared registered widget cards, fixed Ride canvas, OLED saver and assistant mock states. Legacy untouched vertical seed positions are repacked only when their original IDs/geometry match exactly. User data in Git and device was preserved.
- Commands so far: `:app:compileDebugKotlin` exit 0 after latest resize-preview edit; full unit/lint/APK/device tests exit 0 earlier (91 tasks, 2 Pixel 8a device tests); `bash scripts/verify-docs.sh` exit 0 before final docs edits; Pixel 8a APK install and visual checks passed for seeded editor and fixed Ride canvas. Hilt default aggregate task failed on JavaPoet linkage; `enableAggregatingTask=false` assembled successfully. Final checks remain.
- Documentation reconciliation: README.md, docs/DECISIONS.md, docs/ROADMAP.md, docs/SESSION_STATE.md, docs/TRACEABILITY.md, docs/ARCHITECTURE.md, docs/DESIGN_SYSTEM.md, docs/WIDGET_CATALOG.md, docs/ANDROID_CAPABILITIES.md and config/ride-defaults.json updated. docs/PRD.md checked 2026-11-18, unchanged requirements. Other config checked 2026-11-18, unchanged contracts.

## Latest mutating run: 2026-11-18 M1 Ride continuity

- User requested PiP on minimizing Ride and a Live Updates-style notification with Open/End Ride after PiP close; user approved ADR-006 separately. ADR-007 records the PiP/notification decision. No Room/DataStore dependency or schema was added in this bounded Ride task.
- Added PiP compact state and an ongoing Ride notification with chronometer and actions. Android 13+ notification permission is requested at Ride start; promotion is requested on supported Android. Pixel 8a Android 17 verified Home → PiP, PiP close → notification, Open → Ride, End Ride → notification removed/Home; system notification record contained `PROMOTED_ONGOING`. Denial path NOT VERIFIED on device.
- `./gradlew --no-daemon :app:testDebugUnitTest :app:lintDebug :app:assembleDebug` exit 0 (52 tasks); APK installed on Pixel 8a with `adb install -r` exit 0. Documentation reconciliation: README.md, docs/DECISIONS.md, docs/ROADMAP.md, docs/SESSION_STATE.md, docs/TRACEABILITY.md, docs/PRD.md, docs/ARCHITECTURE.md, docs/DESIGN_SYSTEM.md, docs/ANDROID_CAPABILITIES.md updated; docs/WIDGET_CATALOG.md and config checked 2026-11-18, unchanged contracts.

## Latest mutating run: 2026-11-18 M1 editor grid geometry

- Git was initialized before this run: `main` at `2d099f1`, working tree clean at start. No commit or push made.
- Added a pure Kotlin 12-column rectangle model and boundary/collision unit tests. No widget type, dependency, permission or runtime editor behavior added. Gradle unit/lint/APK command exited 0.
- ADR-006 proposed, not applied, for the page/widget persistence schema required by M1-PERSIST-001. Documentation reconciliation: README.md, docs/ROADMAP.md, docs/SESSION_STATE.md, docs/TRACEABILITY.md and docs/ARCHITECTURE.md updated; docs/DECISIONS.md appended with proposal. docs/DESIGN_SYSTEM.md, docs/WIDGET_CATALOG.md, docs/ANDROID_CAPABILITIES.md and config checked 2026-11-18, unchanged contracts.

## Latest mutating run: 2026-11-18 M1 ride shell

- Implemented bounded `tasks/M1-RIDE-001.md` using the already planned dedicated Ride Activity; no dependency or sensitive permission added. Static pages are labeled previews, not saved enabled pages. Existing architecture followed; no new ADR.
- Initial Kotlin compile failed on an invalid Compose `weight` import; corrected. Final Gradle unit/lint/APK command exited 0, and the final APK was installed on Pixel 8a with `adb install -r` exit 0. Pixel 8a checks passed for Start/End Ride, orientation restoration, swipe/status, visible rail, and scoped `KEEP_SCREEN_ON`. Screenshot-driven fixes covered the full dark background and readable planned rail labels; Settings/onboarding copy now reflects the available preview. Active call kept the status bar visible, so no-call immersive remains NOT VERIFIED.
- Documentation reconciliation: README.md, docs/ROADMAP.md, docs/SESSION_STATE.md and docs/TRACEABILITY.md updated. docs/DECISIONS.md checked 2026-11-18, unchanged. docs/ARCHITECTURE.md, docs/DESIGN_SYSTEM.md and docs/ANDROID_CAPABILITIES.md updated. docs/WIDGET_CATALOG.md and config checked 2026-11-18, unchanged because no widget/config contract changed.

## Latest mutating run: 2026-11-18 M1 foundation device verification and contrast fix

- Physical Pixel 8a (Android 17): installed the rebuilt debug APK; navigation and system Back passed; screenshot exposed light status icons on a pale background. `MainActivity` now selects system bar icon appearance from light/dark mode. A second screenshot showed readable dark status icons on the light palette. TalkBack was disabled and was not tested.
- `bash scripts/verify-docs.sh` exit 0; `./gradlew --no-daemon :app:testDebugUnitTest :app:lintDebug :app:assembleDebug` exit 0 (52 tasks). No permission or dependency changed.
- Documentation reconciliation: README.md, docs/ROADMAP.md and docs/TRACEABILITY.md updated with device evidence; docs/DESIGN_SYSTEM.md updated for system bars. docs/DECISIONS.md checked 2026-11-18, unchanged; no new substantive decision. docs/ARCHITECTURE.md, docs/WIDGET_CATALOG.md, docs/ANDROID_CAPABILITIES.md and config checked 2026-11-18, unchanged contracts.

## Latest mutating run: 2026-11-18 M1 foundation part 2

Documentation reconciliation:
- README.md: four screen status, prerequisites, permissions, Android Studio sync, emulator evidence and last build updated.
- docs/DECISIONS.md: ADR-005 appended for Navigation Compose; prior ADRs preserved.
- docs/ROADMAP.md: part 2 IN_PROGRESS with build/emulator evidence; foundation remains IN_PROGRESS pending real-device gate.
- docs/SESSION_STATE.md: task, changed files, checks, blockers and next action updated.
- docs/TRACEABILITY.md: M1-APP-001 and M1-DESIGN-001 paths/evidence updated.
- docs/ARCHITECTURE.md and docs/DESIGN_SYSTEM.md: screen graph and reusable UI contract updated. docs/WIDGET_CATALOG.md, docs/ANDROID_CAPABILITIES.md and config checked 2026-11-18; no widget, capability or config contract changed.

## Latest mutating run: 2026-11-18 M1 foundation part 1

Documentation reconciliation:
- README.md: implemented/planned matrix, dependency setup, permissions, limits and last build updated.
- docs/DECISIONS.md: ADR-003 and ADR-004 appended; ADR-001/002 preserved.
- docs/ROADMAP.md: M1 foundation and part 1 IN_PROGRESS; later tasks unchanged.
- docs/SESSION_STATE.md: current task, checks and blockers updated.
- docs/TRACEABILITY.md: partial M1-APP-001 and M1-DESIGN-001 paths/evidence added.
- docs/ARCHITECTURE.md, docs/DESIGN_SYSTEM.md, config/design-tokens.json: contracts reconciled. docs/WIDGET_CATALOG.md and docs/ANDROID_CAPABILITIES.md checked 2026-11-18; no widget type or capability contract changed.

## Latest mutating run: 2026-11-18 bootstrap

Documentation reconciliation:
- README.md: created and reconciled with passing stock starter build; no features claimed.
- docs/DECISIONS.md: ADR-001 accepted for bootstrap contract; ADR-002 proposed for package discrepancy.
- docs/ROADMAP.md: created with all feature work TODO.
- docs/SESSION_STATE.md: created with bootstrap state and checks.
- docs/TRACEABILITY.md: created with every PRD ID, all TODO.

## History

- 2026-11-18: Initial repository scaffold. Existing `docs/BOOTSTRAP_SPEC.md`, `docs/COMMANDS.md`, DOCX PRD and `docs/prompts/WORKFLOW_REFERENCE.md` preserved.

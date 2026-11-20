# M2-NAV-LIFECYCLE — palette recreation and safe idle return

2026-11-20. IN_PROGRESS. Requirements: M2-NAV-001, M2-QA-001, M1-RIDE-001, M1-RIDE-002. User delegates native lifecycle repair, including background/PiP/palette changes and stop-to-idle interactions. No new permission or paid API request is introduced. Prior uncommitted work and local title sizing preserved.

Files: RideActivity/RideSession notification receiver/RideTheme, new RideNavigationViewModel, GoogleMapsController/Widget, NavigationSdkSession, NavigationSessionGate/RetainedNavigationResource, MapNavigationCleanup, unit lifecycle regressions and device recreation test; architecture/capability/catalog/tracking docs. Dependencies: existing Hilt/ViewModel/Compose/Navigation SDK. Risks: SDK 6.1.0 has documented deadlocks; app lifecycle repairs cannot patch its internal locks. Process death cannot retain an in-memory SDK session. SDK/Kotlin update explicitly approved by the user under AGENTS.md rule 2 (ADR-065).

Evidence read from the connected Pixel 8a, without changing its app state:

- ApplicationExitInfo: ANRs at 2026-11-20 12:03 and 12:12, MotionEvent dispatch timeout after 5003 ms.
- Dropbox ANR trace: main thread in Navigation SDK internal sensor onAccuracyChanged, holding a sensor lock while waiting for another SDK lock. NavInternal holds that second lock and waits for the main-thread lock. Same lock inversion in both events. Raw diagnostics remain in /tmp only, not committed.
- Current RideActivity.onDestroy unconditionally finished all map sessions, including configuration recreation. Current stop path cleaned the Navigator while continuing to use its native map and repeated idle restore from UI-change callbacks.

Implemented acceptance targets:

1. Configuration recreation retains widget controller, destination, live guidance/voice/progress in a ViewModel. Application context and weak current Activity binding avoid retaining destroyed Activity/native views. New screen attaches to the same session without another route request. PiP state initializes from the actual Activity.
2. Native save/configuration/trim lifecycle forwarded; detach invalidates map handles before native view destruction; obsolete view callbacks ignored.
3. Stop/arrival/permission loss removes route listeners, stops guidance and clears destinations, then restores idle once. Final SDK cleanup occurs on true End Ride/ViewModel clear, not configuration recreation or pager/PiP disposal. No custom LocationSource reset (unsupported by Navigation SDK).
4. Global SDK leases permit one active widget route. Late/old callbacks cannot stop or replace a newer route/ride. A second widget receives an explicit message instead of overwriting guidance.
5. Native map operation failures are logged, detached and trigger one automatic view rebuild; persistent failures expose Reload map instead of a rebuild loop. Active guidance is preserved during a view-only rebuild. Permission errors have explicit handling. End/cleanup is idempotent.
6. Existing native SDK lock inversion must be addressed by an approved SDK upgrade and real-device stress testing; do not claim 100% reliability from JVM tests.

Verification: existing unit/lint/app APK plus device-test APK compile; new pure gate/resource/cleanup regressions and RideNavigationRecreationTest. Connected phone inspected read-only. Do not auto-start a billable route or replace wallpaper for diagnostics. Real recreation/active-route/PiP/stop/recenter/page/notification behavior remains a device acceptance gate.

## Approved dependency update — ADR-065

Exact catalog change in gradle/libs.versions.toml:

```diff
-kotlin = "2.0.21"
+kotlin = "2.1.21"
-navigationSdk = "6.1.0"
+navigationSdk = "6.3.3"
```

Both Kotlin Android and Compose compiler plugin aliases already use the Kotlin version key. Keep AGP 8.13.2, Gradle 8.13, minSdk 24, target/compile 36, desugar 2.1.5, Places 4.3.1 and all other declared versions unchanged. Validate resolved transitive dependencies, lint, tests, both APKs and device teardown after approval. This is a conservative 6.x update, avoiding unrelated 7.x/8.x API/toolchain migrations. Rollback is the two old catalog versions; rollback also reintroduces known SDK deadlock exposure.

Sources: [Google release notes](https://developers.google.com/maps/documentation/navigation/android-sdk/release-notes) document stop deadlock fix in 6.2.3, device input-dispatch deadlocks in 6.3.0 and camera-animation deadlock fix in 6.3.3. [Kotlin requirements](https://developers.google.com/maps/documentation/navigation/android-sdk/kotlin-compatibility-migration) require Kotlin 2.1 for 6.3–7.2. [Navigator lifecycle](https://developers.google.com/maps/documentation/navigation/android-sdk/reference/com/google/android/libraries/navigation/Navigator) places cleanup at end of session and requires explicit stop/clear. [NavigationView lifecycle](https://developers.google.com/maps/documentation/navigation/android-sdk/reference/com/google/android/libraries/navigation/NavigationView) requires forwarding save/configuration/trim as well as create/start/resume/pause/stop/destroy.

Update status: approved catalog versions applied; clean build/test/lint/device verification recorded in SESSION_STATE. Earlier 6.1.0 baseline had 43 passing JVM tests after the stale-ready ordering regression.

Background notification End Ride invokes the singleton SDK owner directly, independent of UI recomposition. Theme palettes observe configuration changes. The no-Activity receiver test checks Hilt wiring and session dismissal; it does not substitute for live-guidance notification cleanup acceptance. A forced-CREATED ActivityScenario fixture conflicted with automatic PiP and was replaced without changing production PiP behavior.

Final evidence: 43 JVM tests/0 failures, lint and both APKs pass on approved SDK 6.3.3/Kotlin 2.1.21; dependency insight confirms SDK 6.3.3. Final app installed on Pixel 8a preserving data. Real-device component tests pass: 2 tests/2.851s for Activity recreation retention and no-foreground-Activity notification End Ride. No new ANR in exit history. Live route/wallpaper/PiP/stop-to-idle stress remains NOT VERIFIED; no route or wallpaper was changed for automated tests.

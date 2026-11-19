# M2 system volume bar

TASK ID: M2-VOLUME
REQUIREMENTS: M2-VOLUME-001, M2-QA-001; shared renderer M1-WIDGET-001.
STATUS: IN_PROGRESS pending real-device acceptance.
SCOPE: Use actual `AudioManager.STREAM_MUSIC` minimum/current/maximum for the existing horizontal and vertical bar; clamp writes; follow hardware-key, settings and output-device changes; refresh on Ride/editor resume; preserve editor drag/resize gestures and shared preview rendering. No new permission, dependency, service or persisted widget setting.
FILES: `data/VolumeRepository.kt`, `feature/widgets/{VolumeWidget,WidgetCard}.kt`, Ride/editor view model and UI wiring, `VolumeStateTest.kt`, docs.
DEPENDENCIES: Android AudioManager; real device for Bluetooth, hardware-key and call-interruption acceptance. ADR-037 documents the stream and observation choice.
RISKS: Android volume steps and range differ by route. A zero adjustable range or rejected write is shown as unavailable/actual level. Device callbacks and settings observation need device checks.
ACCEPTANCE: In Ride, tap/drag both orientations adjusts media volume within reported bounds and displayed percentage tracks actual system level. Hardware keys and external Settings changes update all placed bars. Bluetooth route attach/detach and call interruption do not leave stale values. Editor canvas is read-only; editor preview uses the live control. Accessibility progress actions set volume. Unit, lint, APK, docs validator and diff checks pass.
VERIFICATION: JDK 17 `./gradlew --no-daemon clean :app:testDebugUnitTest :app:lintDebug :app:assembleDebug`; `bash scripts/verify-docs.sh`; `git diff --check`; real-device Ride/editor and Bluetooth/key/call scenarios. Until device checks pass, M2-VOLUME stays IN_PROGRESS.

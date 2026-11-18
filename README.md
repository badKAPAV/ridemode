# Motorcycle Ride Companion

Android-first motorcycle dashboard, currently in **M1 ride shell** with foundation acceptance still open. The app has a launchable Compose shell, shared visual foundation, Home/Settings/Getting Started/Access and Privacy navigation, and a landscape Ride preview. Functional settings, editable or persisted pages, dashboard widgets, MVVM, StateFlow, Hilt, Room and DataStore remain planned. [MVP requirements](docs/PRD.md) and [roadmap](docs/ROADMAP.md) distinguish planned work.

| Area | Implemented now | Planned |
|---|---|---|
| Android project | Gradle `:app`, Compose launcher, expressive theme, four screen navigation graph, debug APK, template tests | Full M1 app journey |
| Dashboard/pages/widgets | Home and informational Settings/onboarding/permission screens; Start/End Ride; three swipeable preview pages, page indicator and rail; no dashboard widgets | M1 editable/persisted pages, widgets and saver; M2 native integrations |
| Assistant | None | M1 simulated UI; M3 offline recognition and commands |
| Repository contracts | Docs, proposed JSON, validator and workflow prompts | Kotlin parity validation and feature tests |

## Requirements and architecture

Reference device is Pixel 8a. `minSdk=24`, `targetSdk=36` are current Gradle values, not a finalized support policy. Device test matrix: Pixel 8a on Android 17 verified for foundation navigation and light-palette appearance; TalkBack and other devices remain unverified. Single `:app` module and namespace `com.kapav.ridemode`; [architecture](docs/ARCHITECTURE.md) separates implemented foundation from planned features. [Design](docs/DESIGN_SYSTEM.md), [widget catalog](docs/WIDGET_CATALOG.md), [Android capabilities](docs/ANDROID_CAPABILITIES.md), [decisions](docs/DECISIONS.md) and [traceability](docs/TRACEABILITY.md) define implementation boundaries. Kotlin Compose tokens are authoritative; JSON in [config](config/) is proposed pending parity validation (ADR-001). The shell uses Material 3 Expressive `1.5.0-alpha18`, wallpaper dynamic colors on Android 12+, Material 3 defaults on earlier Android, bundled Roboto Flex (wide titles, normal body) and Phosphor vectors. [Font license](docs/licenses/ROBOTO_FLEX_OFL.txt).

## Developer setup and commands

Prerequisites: JDK 17, Android SDK with API 36 and build tools, Python 3 standard library, and Gradle wrapper. `local.properties` is machine-local. For workflow commands, install/configure a Codex CLI that supports `codex exec --sandbox read-only|workspace-write` and stdin prompts. This workspace has CLI `0.169.0-alpha.11.1`; other versions may require wrapper changes. No paid service or backend is needed.

```bash
bash scripts/verify-docs.sh
./gradlew --no-daemon :app:testDebugUnitTest :app:lintDebug
./gradlew --no-daemon :app:assembleDebug
bash scripts/codex-workflow.sh start
bash scripts/codex-workflow.sh implement tasks/M1-RIDE-001.md
bash scripts/codex-workflow.sh audit
```

`implement` requires an existing approved task file based on [template](prompts/TASK_TEMPLATE.md); the current bounded task is [M1 Ride shell](tasks/M1-RIDE-001.md). `start` and `audit` enforce CLI read-only sandbox. The wrapper does not bypass approvals, commit or push. Review changes and rerun validation; Git diff is unavailable in this workspace because `.git` is absent. [Workflow details](docs/COMMANDS.md) and [bootstrap contract](docs/BOOTSTRAP_SPEC.md) remain references. CI runs docs validation and Gradle unit/lint checks; its result is not yet known.

## Permissions, offline behavior and limits

The current manifest declares no sensitive permission and the app asks for none. Ride holds `FLAG_KEEP_SCREEN_ON` while resumed, with no global display change. The Access and Privacy screen explains future grants without requesting them. M2 notification listener uses explicit special access; contacts/calling and optional overlay are use-based; call log is conditional on distribution policy. M3 microphone is tap-to-talk only. See [capability table](docs/ANDROID_CAPABILITIES.md). The Ride preview works offline; live widgets are not implemented. Weather needs optional provider/cache; Google Maps and third-party messages depend on their apps/network. Turn parsing is experimental and cannot be guaranteed. Android owns incoming call UI, emergency dialing and screen-off wake gestures.

## Verification and troubleshooting

Last verified build: **PASS** on 2026-11-18 for `:app:testDebugUnitTest :app:lintDebug :app:assembleDebug` (Gradle exit 0). This verifies compilation, the starter unit test, lint and debug APK assembly. Emulator and Pixel 8a smoke verified foundation navigation. Pixel 8a Android 17 verified Start Ride → landscape preview, page swipe and indicator, End Ride → portrait Home, and `KEEP_SCREEN_ON` on Ride but absent on Home. Screenshots verified the dark Ride surface and readable placeholder rail. The phone had an active call during this check, so full system-bar hiding is **not verified**; TalkBack and physical dark-palette foundation checks also remain open. The Ride pages are fixed previews, not saved enabled pages. Lint has no errors. See [session state](docs/SESSION_STATE.md) for exact checks. If docs validation fails, fix missing files/IDs/JSON/reconciliation records shown by the script. If Gradle cannot resolve SDK or dependencies, check JDK 17, Android SDK/API 36 and local network/cache; do not report a successful build until it runs. **After Gradle file changes, sync the project in Android Studio** so its editor imports the new libraries. If Codex wrapper rejects a mode, check `codex exec --help` for sandbox support.

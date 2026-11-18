# Roadmap

Status vocabulary: TODO, IN_PROGRESS, BLOCKED, DONE. M1 foundation is in progress; later feature work remains TODO. DONE requires acceptance, relevant tests, reconciled docs and no blocker. PRD IDs are canonical in [PRD](PRD.md).

| Task | Requirements | Milestone | Status | Dependencies | Verification gate |
|---|---|---|---|---|---|
| M1-FOUNDATION | M1-APP-001, M1-DESIGN-001 | M1 | IN_PROGRESS | Bootstrap | Compose project builds; Home/Settings/onboarding/permissions and accessibility checks |
| M1-FOUNDATION-PART1 | M1-APP-001, M1-DESIGN-001 | M1 | IN_PROGRESS | Bootstrap | Compose shell, expressive theme/components, lint/unit/APK; Pixel 8a light appearance passed; TalkBack and physical dark appearance pending |
| M1-FOUNDATION-PART2 | M1-APP-001, M1-DESIGN-001 | M1 | IN_PROGRESS | Part 1 | Gradle unit/lint/APK pass; emulator and Pixel 8a Home → Settings → Access and Privacy → Back, Home → Getting Started; TalkBack pending |
| M1-RIDE | M1-RIDE-001, M1-DESIGN-001 | M1 | IN_PROGRESS | Foundation acceptance still open | Pixel 8a landscape entry/portrait exit, preview swipe/rail and visible-only keep-awake passed; persisted enabled pages, no-call immersive and TalkBack pending |
| M1-EDITOR | M1-PAGE-001, M1-PERSIST-001 | M1 | TODO | Foundation | ≥3 pages, edit operations and process-recreation tests |
| M1-WIDGETS | M1-WIDGET-001 | M1 | TODO | Editor | Shared renderer, labeled mock states, catalog tests |
| M1-SAVER | M1-OLED-001 | M1 | TODO | Ride, widgets | Timeout and consumed-first-touch tests |
| M1-ASSIST | M1-ASSIST-001 | M1 | TODO | Ride | Mock UI states, no microphone/action execution |
| M1-GATE | M1-QA-001 | M1 | TODO | All M1 tasks | APK, screenshots, Pixel 8a/no-network/regression evidence |
| M2-MEDIA | M2-MEDIA-001, M2-VOLUME-001 | M2 | TODO | M1 gate, listener grant | Supported sessions, volume/route tests |
| M2-CONTACTS | M2-CONTACT-001, M2-CALLS-001 | M2 | TODO | M1 gate, distribution decision | Six contacts, dial flow; policy-gated recents |
| M2-NOTIFY | M2-NOTIFY-001 | M2 | TODO | M1 gate | Grant/revoke, priority, storms, redaction |
| M2-MAPS | M2-MAPS-001, M2-TURN-001 | M2 | TODO | Listener, M1 gate | Handoff/return and stale-safe experimental cue |
| M2-WEATHER | M2-WEATHER-001 | M2 | TODO | Weather strategy | Cache/unavailable state; provider optional |
| M2-GATE | M2-QA-001 | M2 | TODO | All applicable M2 tasks | APK, offline and device integration evidence |
| M3-ASR | M3-ASR-001, M3-AUDIO-001 | M3 | TODO | M2 gate, languages/headset decision | Offline model and real intercom tests |
| M3-COMMANDS | M3-CMD-001, M3-FEEDBACK-001 | M3 | TODO | ASR | Deterministic command/safety tests |
| M3-GATE | M3-RELEASE-001 | M3 | TODO | M3 tasks | Release APK, privacy/Play/accessibility/regression audit |

Release gate also needs tagged source, setup instructions, no critical crashes, screenshots and limitations per PRD. No task is DONE simply because a document or mock exists.

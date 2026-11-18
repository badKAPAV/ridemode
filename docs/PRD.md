# Motorcycle Ride Companion MVP PRD

Source: [Motorcycle Ride Companion MVP PRD](Motorcycle_Ride_Companion_MVP_PRD.docx), v1.0, 18 November 2026. This Markdown is a requirement index and faithful restatement of the source. Source section numbers are included for audit. IDs remain stable; add new IDs rather than renumbering.

## Product and boundaries (source §§1–3)

Android-first, landscape motorcycle dashboard for a mounted phone. Kotlin, Compose, MVVM, StateFlow, Hilt, Room and DataStore are planned. Pixel 8a is the reference device. Use large controls and configure before moving. Core pages, clock and local settings work offline. No mandatory backend, login, analytics SDK, subscription, paid weather/navigation API or cloud storage. Contacts and notification content stay on device; no content telemetry. Google Maps, weather and third-party message delivery depend on external apps/providers. Do not promise full in-app Maps navigation, custom routing/traffic/tiles, dialer replacement, incoming-call interception/answering, global wake gestures, always-on hotword, cloud LLM, mandatory live weather, or guaranteed turn extraction.

## M1 requirements: UI and local foundation

| ID | Requirement and acceptance evidence | Source |
|---|---|---|
| M1-APP-001 | Build project, theme, navigation, Home, Settings, onboarding and permission explanations. | §§4, 15 |
| M1-RIDE-001 | Start Ride forces landscape immersive dashboard; End Ride restores user orientation. Horizontal swipe among enabled pages, persistent page indicator, Maps and assistant actions. Keep screen awake only while Ride Activity visible. | §§4–5, 13, 15 |
| M1-PAGE-001 | 12-column normalized grid with configurable row height and snapping. Pages are ordered editable entities; templates are editable. Support add, drag, resize, delete, duplicate, reorder, reset, preview; no editing in ride mode. Clamp bounds and surface collisions. | §6 |
| M1-PERSIST-001 | Persist page/widget edits transactionally in Room; survive process death and screen-size change. Instance has unique ID, type, page, x/y/w/h, settings, visibility, minimum size and theme support. User preference overrides persist in DataStore. | §§6, 14–15 |
| M1-WIDGET-001 | Shared renderer for editor and ride mode. Build UIs for music, volume, clock/date, weather, SOS/contacts, calls, notifications, Maps shortcut, turn guidance, battery and assistant; simulations are explicitly labeled. Real clock and battery may be live. | §§5–6, 15 |
| M1-OLED-001 | Saver defaults off; selectable inactivity 15/30/60/120/300 s. Visible Ride Activity switches to true black, monochrome minimal controls, outline numerals and optional reduced window brightness. First touch wakes and is consumed, never activating underlying controls; subsequent touch acts normally. Handle lifecycle pause/resume and accessibility solid-text fallback; keep critical guidance visible and OS call UI unobscured. | §7 |
| M1-ASSIST-001 | Persistent assistant button with idle, pressed, listening, transcript, result and error mock UI; no microphone permission or live command execution in M1. | §§5, 12, 15 |
| M1-DESIGN-001 | Minimum 56 dp touch target; primary controls aim 64–80 dp, gaps 12–16 dp. Clock 64–120 sp, primary labels 20–28 sp, supporting 14–18 sp with font scaling. High contrast, restrained color, minimal motion, TalkBack, reduced motion option, non-color-only priority. No complex editing while riding. | §§2, 5 |
| M1-QA-001 | Test process recreation, saved layouts, dark mode, touch targets, Pixel 8a orientation and no-network startup. M1 gate: create ≥3 pages; add/move/resize/remove; relaunch intact; enter/exit ride; saver timeout and consumed first touch; assistant UI has no actions. Demo needs no backend, live external data or permissions. | §§15–16 |

## M2 requirements: Android integrations

| ID | Requirement and acceptance evidence | Source |
|---|---|---|
| M2-MEDIA-001 | MediaSessionManager active sessions with notification-listener authorization; metadata/state/progress where available. Supported MediaController actions only; clear disappearing sessions and handle no-session/permission states. | §§6, 9, 15–16 |
| M2-VOLUME-001 | AudioManager changes selected stream within actual bounds, reflects true volume and route changes. | §§6, 9, 15 |
| M2-CONTACT-001 | Permission-aware picker and up to six ordered SOS favorites; ACTION_DIAL default. ACTION_CALL requires permission and confirmation; emergency numbers through system dialer. No automatic call from notification text. | §§6, 10, 13, 15 |
| M2-CALLS-001 | Native dialer owns incoming calls. Recent 10 via READ_CALL_LOG only if permission and distribution policy permit; otherwise favorites/system dialer fallback. | §§6, 10, 15–16 |
| M2-NOTIFY-001 | User-granted NotificationListenerService normalizes optional package/time/title/body/channel/category/group/key; local HIGH/NORMAL/MUTED rules with per-app overrides. HIGH popup only in foreground, rate-limited, auto-dismissed, avoiding SOS/navigation. Bounded feed; respect updates/removals/redaction; do not retain full text indefinitely. | §§4, 11, 13, 15–16 |
| M2-MAPS-001 | Explicit Google Maps intent (package/geo/navigation URI for known destination; main app otherwise), missing-app fallback and recoverable return via task history. Optional return overlay requires special access and can be removed; background-start limits apply. | §§4, 8, 13, 15–16 |
| M2-TURN-001 | Experimental, consent-dependent parsing of available Maps notification extras only. States INACTIVE/LISTENING/GUIDANCE/STALE/UNAVAILABLE; confidence and conservative TTL; tap opens Maps. Never invent maneuver/distance or claim official API. | §§8, 15–17 |
| M2-WEATHER-001 | Optional cache/provider only if selected without mandatory paid service; offline uncached state unavailable with freshness label. | §§5–6, 15, 17 |
| M2-QA-001 | Test supported music apps, system volume, six saved contact actions, persisted notification rules, Maps return, stale-safe cues, airplane mode, intercom output, call interruption, notification storms and screen sleep. | §§15–16 |

## M3 requirements: local assistant and hardening

| ID | Requirement and acceptance evidence | Source |
|---|---|---|
| M3-ASR-001 | Tap-to-talk; on-device SpeechRecognizer only when capability and offline model are available; explicit unavailable state, no silent cloud fallback. Optional bundled Vosk evaluation depends on size/license. | §§12–13, 15–17 |
| M3-AUDIO-001 | Test Bluetooth/phone microphone routing, route loss and competing calls; verify at least one real headset. | §§12, 15–16 |
| M3-CMD-001 | Deterministic local MEDIA, VOLUME, CALL, PAGE, MAPS and SYSTEM/time/end-ride commands. Clamp volume, resolve selected contacts and ambiguity, confirm sensitive actions, obey supported media controls and permissions. | §12, 15 |
| M3-FEEDBACK-001 | Optional Android TTS, confirmation, cancellation, timeout, ambiguity and error feedback. | §§12, 15 |
| M3-RELEASE-001 | Optimize recomposition/power/background work; audit permission rationale, accessibility, privacy, Play policy, crash recovery and release builds. Critical journeys regressions; offline commands work when models exist, unsupported devices fail gracefully, no cloud processing. | §§15–18 |

## Detailed behavior and edge cases (source §§4–14, 16)

Flow: launch → Home → Ride → swipe pages → End Ride → Home. Home also reaches Widget Library, Page Editor, Contacts, Notification Rules, Battery Saver, Ride Settings, Permissions and About. Maps opens foreground; both apps are not simultaneously promised visible. Listener produces minimal local events independently of Activity; permissions can be revoked anytime. System services → adapters → repositories/Flow → ViewModels → immutable Compose widget state and callbacks. No unnecessary foreground service.

Templates: Music, SOS/Favorites, Clock & Weather, Calls, Notifications and a Touring custom layout (music 8 + clock 4 above; volume 4 + two contacts 4 each below, with Maps/assistant rail). Widget settings: music artwork/text/transport; volume slider/buttons/increments; clock 12/24 and format/style; weather units/compact; contacts max six; calls recent 10/favorites; notifications app priority/compact; Maps optional destination/label; turn maneuver/distance/label; battery percentage/charging; assistant button placement. Local JSON layout export/import is a later enhancement, not an MVP blocker.

Permission or content loss must show a clear fallback: notification grant revoked, media session gone, contact permission denied, Maps missing, Bluetooth disconnected, app killed, hot phone/low battery, and no network. Notification privacy mode redacts content. Incoming phone call retains OS priority. Avoid aggressive polling. No system-wide double-tap-to-wake claim.

## Acceptance and release gates (source §§15–18)

M1, M2 and M3 acceptance is recorded in the corresponding QA rows above. Each milestone also requires a working APK, tagged source, setup instructions, tested permission flows, no known critical crash, UI screenshots and documented limitations. Release dependencies: before M2 choose distribution (private APK or Play), supported minimum Android version, weather strategy and intercom models; before M3 choose offline speech languages and command vocabulary. Recommended order: foundation → editor/persistence → saver → mock widgets → notification/media proofs → Maps → contacts/calls → assistant. Freeze extra widget types until M2 stable.

## Unresolved source decisions

Distribution channel, final min Android version, weather provider/cache strategy, intercom test models, supported offline speech languages and vocabulary are deliberately open. Exact notification TTL, popup rate limit, grid row height and persistence schema need bounded design decisions during implementation. The source proposes a package path under `com.example.ridecompanion`; existing Gradle namespace is `com.kapav.ridemode`. See [DECISIONS](DECISIONS.md).

## Source transcription (PRD v1.0)

The following preserves the source wording and section order. Requirement IDs above are the stable implementation index.


**MOTORCYCLE RIDE COMPANION**

Product Requirements Document  |  Android MVP  |  Milestones 1–3

Version 1.0  •  18 November 2026  •  Implementation baseline

Platform: Android (Pixel 8a reference device)  |  Kotlin + Jetpack Compose  |  Offline-first


### 1. Executive summary

A configurable, landscape-first motorcycle dashboard for a phone mounted inside a protective case. Riders assemble pages from large, resizable widgets; manage music, volume, contacts and notifications; launch Google Maps; view experimental notification-derived navigation cues; and use an OLED-focused idle presentation. A later milestone adds local voice commands. The app remains useful without network access, cloud accounts, a backend or paid services.


### 2. Goals, principles and scope

- Minimize interaction time: large touch targets, readable at a glance, no editing while riding.
- Offer user-selectable ride pages and custom grid layouts, with persistent local configuration.
- Provide a reliable, offline-capable core: time, local settings, page switching, screen-awake mode, device volume, available media sessions and saved contacts.
- Use Android system facilities rather than replacing the dialer or modifying OS wake gestures.
- Use no mandatory backend, login, analytics SDK, subscription, paid weather provider, or paid navigation API.
- Protect rider privacy: notification contents and contact references remain on device; no content telemetry.

### 2.1 Explicit non-goals

- No in-app full Google Maps navigation SDK, custom routing engine, live traffic engine or map tiles in MVP.
- No replacement dialer, interception of incoming calls, automatic call answering or emergency-services autodialing.
- No promise of globally enabling double-tap-to-wake on a locked or off display.
- No always-on hotword detection or cloud LLM.
- No mandatory live weather or guaranteed extraction of Google Maps turn notifications.

### 3. Personas and operating context

Primary: solo rider using an Android phone in an enclosed handlebar case, Bluetooth helmet intercom, intermittent cellular service, bright sunlight and occasional rain. Secondary: commuter who wants quick callbacks and filtered messages. Configure before moving; while moving, favor voice and intercom controls. Testing must account for vibration, gloves, high temperature, lock screen and loss of network.


### 4. Information architecture and flows

Launch → Home (profile, ride pages preview, preferences, permissions) → Start Ride → landscape immersive dashboard → horizontal swipe across enabled pages → End Ride → Home. From Home: Widget Library, Page Editor, Contacts, Notification Rules, Battery Saver, Ride Settings, Permissions, About.

Maps handoff: Ride widget → explicit Google Maps intent → Maps foreground → optional floating return overlay (permission-gated) or system navigation/notification shortcut → Ride. Never imply both apps remain simultaneously visible.

Notification flow: Android notification → NotificationListenerService → package/channel/category filter → normalized local event → optional high-priority popup while ride is foregrounded → notifications widget/page. Navigation parsing is an isolated consumer of the same listener.


### 5. Design system and layout specification


| Area | Requirements |
|---|---|
| Orientation | Portrait configuration; force landscape on Start Ride. Restore user orientation on End Ride. |
| Dashboard | Immersive dark interface, edge-to-edge, horizontal pages; persistent bottom/side rail for page indicator, Maps shortcut and assistant action. |
| Touch targets | Minimum 56 dp; aim 64–80 dp for primary riding controls; 12–16 dp gaps; no tiny dismiss icons. |
| Typography | Clock 64–120 sp depending on size; primary labels 20–28 sp; supporting labels 14–18 sp; scale with accessibility settings. |
| Visuals | High-contrast backgrounds, restrained color coding, minimal motion; weather and navigation stale indicators visible. |
| Feedback | Clear pressed states, brief optional haptic feedback, no blocking dialogs in ride mode except deliberate confirmations. |
| Accessibility | TalkBack semantics, font scaling, contrast, optional reduced motion, color-independent priority indicators. |


### 5.1 Default page templates


| Page | Suggested composition |
|---|---|
| Music | Large now-playing area (art/title/artist), play/pause, next/previous, volume +/- and slider. |
| SOS / Favorites | 2×3 maximum contact tiles, large photo/initial/name, tap-to-call confirmation or dial action. |
| Clock & Weather | Oversized clock, date, weather condition, temperature and freshness label. |
| Calls | Recent-call history when policy-permitted; otherwise user-selected favorite contacts and call actions. |
| Notifications | Filtered notification cards, priority label, timestamp, safe content preview and app launch. |
| Touring custom | Music 8 columns + clock 4 columns on top; volume 4 + two contact tiles 4 each below; compact Maps/assistant rail. |


### 6. Custom widget and page builder

The editor uses a 12-column normalized grid with configurable row height, snapping and collision resolution. Each widget instance has unique ID, widget type, page ID, grid x/y/w/h, settings JSON, visibility, minimum size and theme support. Widgets must render in both editor preview and live ride mode from the same component implementation. Edit mode supports drag, resize handles, add, delete, duplicate, reorder, reset and preview; riding mode disables editing.

A page is an ordered entity containing widget instances. Default templates are ordinary editable pages. Persist edits transactionally; restore layouts after process death and screen-size changes. Clamp out-of-bounds widgets and surface conflicts in editor. Offer layout reset and a local JSON export/import as a later enhancement, not a blocker.


| Widget | Configuration | MVP behavior |
|---|---|---|
| Music | show artwork, text, transport buttons | Native MediaSession when available; graceful no-session state. |
| Volume | slider/buttons, increments | Adjust selected audio stream, reflect actual system volume. |
| Clock / Date | 12/24-hour, format, font style | Device-local time, updates without needless wakeups. |
| Weather | units, compact/expanded | Optional provider/cache only; unavailable when offline and uncached. |
| SOS / Contacts | up to six selected contacts | Permission-aware contact picker; call/dial with safeguards. |
| Calls | recent 10, favorites fallback | Call-log integration conditional on permission and distribution policy. |
| Notifications | app priorities, compact/expanded | Local listener feed; redacted fallback if content unavailable. |
| Maps shortcut | destination optional, label | Explicit package/URI launch; fallback if Maps absent. |
| Turn guidance | maneuver/distance/label | Experimental parsed notification; stale/unavailable fallback. |
| Battery | percentage, charging | Android battery broadcast / battery manager. |
| Assistant | button placement | M1 visual states only; M3 tap-to-talk and actions. |


### 7. OLED battery saver specification

Preference toggle, default disabled; inactivity delay selectable (15, 30, 60, 120, 300 seconds; extensible). While Ride Activity is visible and saver enabled, no touch for configured interval switches the entire ride UI to true #000000 background, monochrome minimal controls, outline-only large numerals and optional reduced window brightness. First touch is consumed to restore normal UI and reset timer; it MUST NOT activate underlying SOS or media actions. Any subsequent touch acts normally. Battery saver does not alter Android system power saver or global brightness.

Compose implementation: single layout tree + presentation state (NORMAL / OLED_IDLE), widget theme tokens, pointerInput on top-level touch interceptor, monotonic inactivity tracking, lifecycle pause/resume handling. For outline text use native/Compose text rendering with stroke drawing and accessibility fallback to solid text. Saver must preserve critical navigation visibility and not obscure an active call UI. White outline is aesthetic; savings depend mainly on black pixels, brightness and content duty cycle.


### 8. Navigation requirements and limits


#### 8.1 Google Maps launch widget

Use an explicit Intent targeted to com.google.android.apps.maps when installed; support geo/google.navigation URI for known destinations, otherwise launch Maps main activity. Catch ActivityNotFoundException and offer install/open alternative. App switch-back uses standard task history first; optional overlay button requires explicit draw-over-other-apps approval and must be removable. Android background activity-launch restrictions apply.


#### 8.2 Notification-derived turn guidance

NotificationListenerService obtains active notifications with user consent. A Google Maps-specific adapter attempts to parse title/text/bigText and other available extras into Maneuver(text, distance, timestamp, sourcePackage, confidence). No undocumented format is guaranteed; never claim official navigation API access. Show INACTIVE, LISTENING, GUIDANCE, STALE or UNAVAILABLE. Expire guidance after a conservative TTL; tap always opens Maps. No speculative turn icon or distance if data cannot be confidently parsed. Feature labeled Experimental in settings.


### 9. Music and audio

Use MediaSessionManager active sessions with notification-listener authorization and MediaController transport controls; show active app, metadata, playback state, progress if available. Control only sessions exposing supported actions. AudioManager manages volume with actual stream constraints and Bluetooth route changes. No bypass of media DRM or direct access to other apps’ private libraries. Provide empty-state and permission prompts outside ride mode.


### 10. Calls, SOS and contacts

Android remains responsible for incoming-call UI and headset answer/reject behavior. Do not overlay a fake incoming-call answer/reject panel. Provide optional Calls page; recent 10 entries via CallLog only where READ_CALL_LOG permission is allowed and Play policy permits. Otherwise render favorite contacts and optional shortcut to system dialer. Selected SOS contacts max six, configurable order, large tiles. A selected contact action may use ACTION_DIAL as default; ACTION_CALL only with explicit permission and confirmation. Emergency numbers must route through system dialer. No automatic calling from notification text.


### 11. Notifications and prioritization

Notification listener normalizes app package, posted timestamp, title, text when available, channel, category, group ID, and stable key. Default tiers: HIGH (chosen messaging apps/messages), NORMAL (history only), MUTED (ignored). User overrides per app; optional sender rules deferred. HIGH generates non-blocking in-app popup while dashboard foreground, dismisses automatically, is rate-limited and never covers primary navigation or SOS control. A full page and compact widget display a bounded recent feed. Redact sensitive content on request; do not store full notification text indefinitely. Respect deleted/updated notifications and Android content-redaction limits.


### 12. Offline assistant: Milestone 3

Assistant button is persistently visible in ride mode; Milestone 1 includes idle/pressed/listening/transcript/result/error visual states with simulated data, but no microphone permission or live execution. Milestone 3: tap-to-talk → Bluetooth/phone mic routing → on-device SpeechRecognizer if available and offline language model installed → deterministic local intent parser → permission-aware command dispatcher → optional Android TextToSpeech feedback. If offline recognizer unavailable, show explicit unavailable state; optional bundled Vosk model may be evaluated, subject to size/license. No internet fallback by default.


| Intent | Examples | Safety / handling |
|---|---|---|
| MEDIA | pause, play, next song | Dispatch through supported MediaController actions. |
| VOLUME | volume up, set volume to 60% | Clamp to stream limits; optional audible confirmation. |
| CALL | call Mom, call contact two | Resolve among selected contacts; confirmation for ambiguity. |
| PAGE | open music page, next page | Local page navigation. |
| MAPS | open Google Maps | Launch app; cannot promise routing without network. |
| SYSTEM | what time is it, end ride | Speak local time; confirm end ride if needed. |


### 13. Settings and permissions


| Permission / capability | When requested | Fallback |
|---|---|---|
| Notification listener special access | Enable media session control, notification feed or turn parser | Disable affected widgets; settings deep link. |
| Contacts READ_CONTACTS | Only when selecting contacts (or use system picker) | Manual shortcut/dial UI if applicable. |
| Call log READ_CALL_LOG | Only if legally/policy eligible | Favorites instead of recent 10. |
| Call phone CALL_PHONE | Only if direct calling is enabled | ACTION_DIAL. |
| Overlay SYSTEM_ALERT_WINDOW | Optional Maps return bubble | Android back/task switch/notification. |
| Microphone RECORD_AUDIO | Milestone 3 tap-to-talk | Assistant disabled with explanation. |
| Location | Only for optional location-based features | Clock, media, contacts remain available. |
| Network | Not needed for core dashboard | Weather/third-party online content unavailable. |

Screen-awake via FLAG_KEEP_SCREEN_ON only while Ride Activity is visible. Tap-to-wake is a device/OS setting; show instructions or link to system settings where supported, never claim app can globally implement wake on an off screen. Orientation and immersive behavior must respect current Android edge-to-edge rules.


### 14. Architecture and codebase

Single Android application module initially, feature-oriented packages, MVVM, unidirectional StateFlow, repositories behind interfaces, Kotlin coroutines, Hilt, Room for relational page/widget/contact references, DataStore for preferences. Split Gradle modules only after feature boundaries stabilize. Compose widgets receive immutable view state and callbacks, never directly query system services.

app/src/main/java/com/example/ridecompanion/
  MainActivity.kt
  RideApplication.kt
  core/
    model/  database/  datastore/  di/  permissions/
    ui/theme/  ui/components/  util/
    android/audio/  android/telecom/  android/notifications/
    android/maps/  android/speech/  android/display/
  feature/
    home/  settings/  ride/  pageeditor/
    widgets/  music/  contacts/  calls/  notifications/
    navigation/  oledsaver/  assistant/
  service/
    RideNotificationListenerService.kt
  data/
    repository/  parser/  local/
  domain/
    usecase/  contract/
app/src/main/res/


#### 14.1 Suggested contracts

RidePage(id, name, order, enabled); WidgetInstance(id, pageId, type, x, y, width, height, config); RidePreferences(keepAwake, saverEnabled, saverDelayMs, popupEnabled, privacyMode); NotificationItem(key, packageName, title?, body?, priority, postedAt); NavigationCue(text?, distance?, maneuver?, observedAt, status); AssistantUiState(Idle, Listening, Processing, Result, Error).


#### 14.2 Lifecycle and data flow

Android services → adapters → repositories (Flow) → ViewModels → Compose UI. Widget editor writes Room transaction → page renderer observes Room Flow. System-level permission state is observable and can revoke at any time. Notification listener processes minimal data and cannot depend on the Activity existing. No unnecessary foreground service for UI persistence.


### 15. Milestone delivery plan


#### Milestone 1 — Complete UI and local foundation

- Build project, theme, navigation graph, Home, Settings, onboarding and permissions screens.
- Implement landscape Start/End Ride, immersive dashboard, page swiping and persistent action rail.
- Implement widget registry, templates, 12-column editor, drag/resize/reorder, Room persistence and responsive preview.
- Build all widget UIs with representative simulated data and explicit placeholder labels; real clock and local battery can be live.
- Implement functional OLED idle presentation, timeout settings, touch-to-restore and reduced-brightness option.
- Complete assistant button UI (idle/listening/result/error) with mock interactions only.
- Test process recreation, saved layouts, dark mode, touch targets, Pixel 8a orientation and no-network startup.
M1 acceptance: create ≥3 pages, add/move/resize/remove widgets, relaunch without losing layout, enter/exit landscape ride mode, trigger saver at selected delay, restore on first consumed touch; assistant UI present but not executing actions. No backend, live external data or permissions required to demo.


#### Milestone 2 — Android services and integrations

- Implement media sessions and AudioManager volume with permission and no-session handling.
- Implement contact picker, six contact tiles, ACTION_DIAL/default calling behavior and favorites page.
- Add call history only if distribution policy and permission eligibility confirmed; otherwise retain favorites fallback.
- Implement NotificationListenerService, local app priority rules, popup, feed widget and full page.
- Implement Google Maps shortcut and optional permission-gated return overlay.
- Prototype Maps notification parser; ship only with explicit experimental/stale/unavailable states.
- Implement weather cache/optional provider only if chosen without mandatory paid service; otherwise mark weather unavailable offline.
- Test intercom audio output, incoming-call interruption, notification storms, screen sleep and Maps lifecycle.
M2 acceptance: supported music apps controllable; volume reflects system; six saved contacts callable via approved flow; app-priority notification rules persist and work; Maps opens and dashboard can be resumed; experimental navigation never displays stale data as fresh. App usable with airplane mode except inherently external features.


#### Milestone 3 — Offline assistant and release hardening

- Integrate on-device speech recognition, check language model availability and provide no-network fallback states.
- Test Bluetooth intercom microphone route; handle route loss and competing calls.
- Implement deterministic commands for media, volume, pages, time, Maps and approved contact calls.
- Add speech confirmation, cancellation, timeout, ambiguity resolution and error feedback.
- Optimize Compose recompositions, screen-on power, saver rendering and background work.
- Audit permission rationale, accessibility, privacy, Play policy, crash recovery and release builds.
M3 acceptance: command set executes offline when required recognition models are available; unsupported devices fail gracefully; intercom microphone verified on at least one real headset; no cloud processing; all critical journeys pass regression tests.


### 16. QA and acceptance matrix


| Scenario | Expected result |
|---|---|
| No internet / airplane mode | App, pages, clock, layout and local controls function; remote-dependent content clearly unavailable. |
| Screen saver timeout | Switches only after configured idle interval; first touch wakes without triggering action. |
| Google Maps opened | Maps receives intent; dashboard recoverable by supported return path. |
| Google Maps notification changes | Parser degrades to unavailable/stale; never invents maneuver. |
| Notification access revoked | Listener-dependent widgets show permission-required state; no crash. |
| Media session disappears | Controls disable gracefully and metadata clears. |
| Incoming phone call | Android system UI takes priority; ride app resumes cleanly. |
| Contact permission denied | No crash; dialer/favorites fallback displayed. |
| Bluetooth disconnects | Audio/mic route changes reflected; commands fail safely. |
| App process killed | Page configuration persists; transient navigation/notification state rebuilt or cleared. |
| Hot phone / low battery | No aggressive background polling; saver reduces brightness/pixels as configured. |


### 17. Risks, decisions and release gates


| Risk | Mitigation / decision |
|---|---|
| Maps notification format unstable | Experimental flag, parsing confidence, TTL, no safety-critical reliance. |
| Play policy for call log | Do not assume eligibility; favorites-based calls as baseline. |
| Android notification content varies | Normalize optional fields, privacy/redaction, test target apps. |
| Offline ASR device variation | Capability detection; test with downloaded language models; optional bundled engine. |
| Weather without external dependency | Cached data only or omit live temperature until optional provider selected. |
| OLED outline readability | Accessibility option for solid text; test in sunlight and at speed while stationary. |
| Overlay permission / background limits | Optional only; system task switching remains fallback. |
| Heat in sealed case | Reduce brightness, avoid constant GPS polling, test thermal throttling. |


### 18. Definition of done and next actions

Each milestone requires working APK, tagged source, concise setup instructions, tested permission flows, no known critical crashes, UI screenshots, and documented limitations. Before M2, decide distribution (private APK vs Play Store), supported minimum Android version, exact weather data strategy and which intercom models to validate. Before M3, choose supported offline speech languages and command vocabulary.

Recommended build order: foundation → editor/persistence → saver → mock widgets → notification and media proof-of-concepts → Maps handoff → contact/call flow → assistant. Freeze additional widget types until M2 stability is achieved.


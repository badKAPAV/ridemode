# Motorcycle Ride Companion MVP PRD

Source: [Motorcycle Ride Companion MVP PRD](Motorcycle_Ride_Companion_MVP_PRD.docx), v1.0, 18 November 2026. This Markdown is a requirement index and faithful restatement of the source. Source section numbers are included for audit. IDs remain stable; add new IDs rather than renumbering.

## Product and boundaries (source §§1–3)

Android-first, landscape motorcycle dashboard for a mounted phone. Kotlin, Compose, MVVM, StateFlow, Hilt, Room and DataStore are planned. Pixel 8a is the reference device. Use large controls and configure before moving. Core pages, clock and local settings work offline. No mandatory backend, login, analytics SDK, subscription, paid weather/navigation API or cloud storage. Contacts and notification content stay on device; no content telemetry. Google Maps, weather and third-party message delivery depend on external apps/providers. Do not promise full in-app Maps navigation, custom routing/traffic/tiles, dialer replacement, incoming-call interception/answering, global wake gestures, always-on hotword, cloud LLM, mandatory live weather, or guaranteed turn extraction.

## M1 requirements: UI and local foundation

| ID | Requirement and acceptance evidence | Source |
|---|---|---|
| M1-APP-001 | Build project, theme, navigation, Home, Settings, onboarding and permission explanations. | §§4, 15 |
| M1-RIDE-001 | Start Ride forces landscape immersive dashboard; End Ride restores user orientation. Horizontal swipe among enabled pages, persistent page indicator, Maps and assistant actions. Keep screen awake only while Ride Activity visible. User addendum (2026-11-18): fixed non-scrolling canvas sized to the device, compact bold rounded-rectangle rail, page name only in the rail, and larger side insets. | §§4–5, 13, 15; user addendum |
| M1-RIDE-002 | User addendum (2026-11-18): minimizing active Ride enters picture-in-picture on supported devices; closing PiP leaves an ongoing Ride notification with Open and End Ride actions. Request Live Update promotion where Android allows it; preserve a standard notification fallback and respect notification permission denial. | User request |
| M1-PAGE-001 | 12-column normalized grid with configurable row height and snapping. Pages are ordered editable entities; every page name is editable and templates are editable. Support direct page drag ordering and accessible move actions, page add/delete/duplicate/reset/preview, and widget add/drag/resize/delete; no editing in ride mode. Clamp bounds and surface collisions. User addendum (2026-11-18): editor is landscape and its preview matches Ride viewport geometry; resize via points on the selected widget. | §6; user addendum |
| M1-PERSIST-001 | Persist page/widget edits transactionally in Room; survive process death and screen-size change. Instance has unique ID, type, page, x/y/w/h, settings, visibility, minimum size and theme support. User preference overrides persist in DataStore. | §§6, 14–15 |
| M1-WIDGET-001 | Shared renderer for editor and ride mode. Build UIs for music, volume, clock/date, weather, SOS/contacts, calls, notifications, Maps shortcut, turn guidance, battery and assistant; simulations are explicitly labeled. Real clock and battery may be live. User addendum (2026-11-18): music widget must keep album art, song/artist, and three large previous/play-pause/next controls legible at its enforced minimum size. Volume widget can occupy full grid width or device-bounded height. M1 needs flexible bounded sizing and usable editor behavior; final visual polish of every widget can follow later. | §§5–6, 15; user addendum |
| M1-OLED-001 | Saver defaults off; selectable inactivity 15/30/60/120/300 s and persisted Classic/Row styles, Classic by default. Visible Ride Activity switches to true black, monochrome minimal controls, outline numerals for Classic and optional reduced window brightness. First touch wakes and is consumed, never activating underlying controls; subsequent touch acts normally. Handle lifecycle pause/resume and accessibility solid-text fallback; keep critical guidance visible and OS call UI unobscured. | §7 |
| M1-ASSIST-001 | Persistent assistant button with idle, pressed, listening, transcript, result and error mock UI; no microphone permission or live command execution in M1. | §§5, 12, 15 |
| M1-DESIGN-001 | Minimum 56 dp touch target; primary controls aim 64–80 dp, gaps 12–16 dp. Clock 64–120 sp, primary labels 20–28 sp, supporting 14–18 sp with font scaling. High contrast, restrained color, minimal motion, TalkBack, reduced motion option, non-color-only priority. No complex editing while riding. User addendum (2026-11-18): modern icon-led editor and restrained editor-only animation; wide bold Roboto Flex titles as in the supplied image and Plus Jakarta Sans body/labels. | §§2, 5; user addendum |
| M1-QA-001 | Test process recreation, saved layouts, dark mode, touch targets, Pixel 8a orientation and no-network startup. M1 gate: create ≥3 pages; add/move/resize/remove; relaunch intact; enter/exit ride; saver timeout and consumed first touch; assistant UI has no actions. Demo needs no backend, live external data or permissions. | §§15–16 |

Grid refinement (user, 2026-11-19): The device-sized Ride/editor grid uses an even number of cells on each axis. Keep the existing 12 columns; when the device-derived row count is odd, round it up to the next even count while fitting the fixed viewport. This refines M1-PAGE-001, M1-RIDE-001, M1-WIDGET-001 and M1-QA-001.

Grid correction (user, 2026-11-19): Supersede the earlier even-count request. Ride and editor grid cells must be square; fit the maximum whole square rows at the selected grid density, allowing odd row counts. In the widget edit menu, offer Rotate when swapping width and height is supported by that widget and space exists on the page. This refines M1-PAGE-001, M1-RIDE-001, M1-WIDGET-001 and M1-QA-001.

Grid width correction (user screenshot, 2026-11-19): Keep square cells and at least seven rows. Use all available landscape width by allowing the grid to have more than twelve columns when they fit. Column and row counts are device-derived; neither must be even. This further refines M1-PAGE-001, M1-RIDE-001, M1-WIDGET-001 and M1-QA-001.

Grid size correction (user, 2026-11-19): The Pixel 8a should show six square rows instead of seven. The cell side on larger screens must be at least the Pixel six-row cell side; smaller screens may scale down to keep six rows and twelve columns. Columns still adjust to viewport width. Existing widgets must remain usable after the change, with safe bounds and repair for placements that no longer fit. This supersedes the seven-row minimum and refines M1-PAGE-001, M1-RIDE-001, M1-WIDGET-001, M1-PERSIST-001 and M1-QA-001.

User scope correction (2026-11-19): M1 page management includes direct drag ordering, add/delete and rename for every page. M1 OLED saver offers a persisted style choice: the current vertical Classic style remains default; the new Row style puts large stacked hours/minutes and body-style day/date on the left, with chunky song, weather and charging-aware local battery panels on the right. The 2026-11-19 refinements enlarge those numerals further, honor the existing Outline clock switch, use the maximum Roboto Flex grade and heavier weight with little space between hours and minutes, and give the Row panels black interiors, 1 dp white strokes, generous separation, centered content and song/weather icons with no category labels; all Row elements use black and white only. M1 song/weather examples remain explicitly mock. SOS customization is only required when M2 adds real contact selection through the system picker, contact images when available, and editing saved favorites. Other per-widget customization is not an M1 gate. These refine M1-PAGE-001, M1-OLED-001, M1-DESIGN-001, M1-QA-001 and M2-CONTACT-001.

User direction (2026-11-19): Fade into and out of both OLED saver styles, honoring Reduce motion; then start M2 with the complete live music widget while M1 device acceptance remains open. M2-MEDIA-001 covers actual active-session metadata, optional art and progress, supported previous/play-pause/next controls, and honest permission/no-session states in Ride, editor preview and OLED music areas. Android notification-listener access must be explicitly granted by the user; the music listener itself reads only active media sessions. Weather stays mock until M2-WEATHER-001.

## M2 requirements: Android integrations

### M1 UI addendum (user, 2026-11-18)

M1 finishes all UI using explicit mock data where native integrations are unavailable. OLED idle presents the local clock and available battery percentage; actual playing media metadata/icon and actual weather may appear only when their sources exist, and the small wake hint sits at the bottom near 50% opacity. Dedicated single-widget pages fill the Ride page canvas. Page ordering, a blurred-background bottom add-widget sheet, and repositioning an occupied widget during drag are M1 editor work. Wide bold Roboto Flex titles and Plus Jakarta Sans body/labels require device verification. Distinct mostly container-free widget designs, per-widget min/max/grid/orientation properties and initial SOS contact selection/customization is deferred to M2 with the native contact integration. The user runs UI acceptance manually and reports results; non-UI checks may run locally. These points extend M1-PAGE-001, M1-WIDGET-001, M1-OLED-001, M1-DESIGN-001 and M1-QA-001; they do not pull native M2 integrations into M1.

User refinement (2026-11-19): Add each widget at its shortest valid registered size that fits. Editor selection uses a rounded border with resize points on all sides and corners, and widget options sit in the bottom rail. The edit canvas uses a dotted grid and saved widget spacing/geometry is reflected exactly in Ride, including single-widget pages. Worked-on Music, Clock/weather, Maps, Battery and SOS widgets receive alignment and surface polish; Battery defaults to 4×2 and shows charging state, SOS fits multiple contacts in one row when width allows, and Ride page names are clickable. Body and labels use Roboto Flex title axes with lower weight and grade instead of Plus Jakarta Sans. Remaining generic widgets stay unchanged. User performs device UI acceptance manually.

Deferred-widget M1 refinement (2026-11-19): Give Calls, Notifications, Turn guidance and Assistant distinct Material 3 Expressive layouts with typed presentation states that future M2/M3 adapters can supply. Default M1 states must remain visibly unavailable or mock, and guidance must never show an invented or expired maneuver. This extends M1-WIDGET-001, M1-DESIGN-001 and M1-QA-001 without implementing native integrations.

Icon refinement (2026-11-19): Use only solid Phosphor `Fill` icons for in-app UI across Home, secondary pages, editor, Ride and widgets. Preserve icon meaning, touch targets and accessibility labels. This refines M1-APP-001, M1-WIDGET-001, M1-DESIGN-001 and M1-QA-001.

Ride start refinement (2026-11-19): When a fresh Ride starts, show a short, centered `RIDE HARD` to `RIDE SAFE` transition with `ALWAYS WEAR HELMET WHEN HITTING THE ROAD` beneath it; an oversized filled motorcycle icon travels fully across the screen from left to right to reveal Ride. Add a persistent Settings toggle, enabled by default, and honor Reduce motion. Opening an already active Ride must skip the intro. X and Plus button icons use normal Phosphor stroke style as explicit exceptions to the filled-icon rule. Device animation and accessibility acceptance remain user-run. This refines M1-APP-001, M1-RIDE-001, M1-PERSIST-001, M1-DESIGN-001 and M1-QA-001.

Ride start pacing correction (2026-11-19): The startup sequence should last about three seconds total. The filled motorcycle should be 2.4× the size of the first implementation (3.24× viewport width) while still sweeping smoothly from outside the left edge to outside the right edge. This refines M1-RIDE-001, M1-DESIGN-001 and M1-QA-001.

User correction (2026-11-19): On every non-Ride/editor screen, make the Back button visually smaller and inset it from the leading edge. Reduce screen and container padding so more content fits horizontally, while preserving the minimum touch target and keeping Ride/editor layout unchanged. This refines M1-APP-001, M1-DESIGN-001 and M1-QA-001.

Further user direction (2026-11-19): Use applicable refreshed Material 3 Expressive components from the linked Material guidance. On non-Ride/editor pages, place the page name in the app bar beside Back, remove redundant Return home buttons, and use distinct meaningful icons for options. Give these pages a more playful visual hierarchy while preserving their M1 functions and accessibility.

Further user direction (2026-11-19): Tighten vertical spacing between Music song and artist at large sizes. Redesign Home and the other non-Ride screens with a clean Material 3 Expressive hierarchy, and replace the remaining generic Calls, Notifications, Turn guidance and Assistant cards with clearly labeled custom M1 previews. Preserve the later M2/M3 integration boundary. The user will perform UI acceptance manually.

Further user direction (2026-11-18): M1 OLED should include clearly labeled mock song/artist and weather to assess the layout, plus actual battery icon/percentage immediately above the bottom wake hint. Titles and the outlined OLED clock use an even wider, heavier Roboto Flex treatment. Ride controls are X + End ride, location marker + Open, and icon-only microphone assistant. The Ride page indicator shows previous/current/next page names together with lower opacity for adjacent names and an animated change. This overrides only the earlier OLED omission of media/weather previews; no native M2 source is implied.

Latest visual specification (2026-11-18): OLED is one centered column in this order: one-line clock; one-line weather with icon; two-line song and artist with icon; one-line battery percentage without icon; wake hint. Apply the exact Roboto Flex specimen axes shown by the user (`wdth 151`, `wght 648`, `GRAD 150`, `XOPQ 96`, `XTRA 603`, `YOPQ 79`, `YTAS 677`, `YTDE -217`, `YTFI 738`, `YTUC 712`, `YTLC 514`, `slnt 0`, optical size automatic) to titles/clock. Adjacent page names keep that wide family with a thin weight for the transition. End ride is an outlined button with X and a two-line End/ride label at the same height; the microphone uses the secondary palette color, while Maps remains a normal button. This supersedes the battery-icon and side-by-side OLED details in the earlier addendum.

Music widget visual specification (2026-11-18): M1 uses mock tracks and local play/pause state only. The widget is a responsive column. Its top row is a rounded mock album-cover surface attached to a darker song-details surface that fills the remaining width; its lower row gives previous, play/pause and next controls equal shares of all remaining width and height. Play/pause is filled and switches icon with state; previous/next have transparent resting backgrounds. The music layout is landscape-only and cannot be resized to a square/portrait grid rectangle. Real metadata and supported transport actions remain M2-MEDIA-001.

Music refinement (2026-11-18): Music can grow to the device's full grid height. A full-width 12-column layout may use all device rows; partial-width layouts remain wider than tall. The artwork and song-details surfaces are separate full-width containers stacked vertically, with a low-opacity primary details background. Play/pause is a filled button, capsule-shaped when mock playback is paused and the existing rounded rectangle while playing. This supersedes the horizontal attached-row and six-row ceiling details above. M2 still owns real media data/actions.

Further visual refinement (2026-11-18): Increase spacing between distinct widgets in both editor and Ride. Animate the music play/pause button's corner radius smoothly when mock playback toggles. This extends M1-WIDGET-001 and M1-DESIGN-001; it does not add real playback.

Volume widget visual specification (2026-11-18): M1 uses local mock percentage only. The widget has a transparent outer layout and a rounded Material 3 Expressive inspired track with a separated colored fill, based on the supplied reference. It may span the full 12-column width as N×1 or the available device height as 1×N; exactly one axis is one grid cell and N≥2. A percentage counter starts near the leading edge and moves into the blank or filled region based on space, with animated fill and counter position; taps and drags update the mock value immediately. True AudioManager volume and route state remain M2-VOLUME-001.

Volume structure correction (2026-11-18): The second supplied image is authoritative for the bar structure. Draw separate translucent-empty and solid-filled segments with a transparent gap and a detached line across it; the segment ends facing the line are flat and the outer ends rounded. Use dynamic palette colors. The percentage remains at the top/start of the appropriate segment and uses the same wide bold Roboto Flex title font as the app. Both orientations and previously specified size/animation behavior remain.

Clock/widget correction (2026-11-18): M1 Clock has no background and shows local time on the first line, smaller thinner day/date on the second, and one weather line below with a condition-based icon, temperature and condition text. The weather line uses explicitly marked mock data in M1; real provider/cache behavior remains M2-WEATHER-001. Remove Weather as a separate addable widget and from new templates. Preserve old saved Weather placements by converting them in place to combined Clock tiles. The Clock minimum is 2×2 cells so all three lines can appear; older one-row placements need a resize prompt. This supersedes the separate Weather widget part of the original M1-WIDGET-001 list.

Clock typography refinement (2026-11-18): Center the complete three-line Clock group. Use the same wide Roboto Flex title family on day/date and weather text as on time, with lower weight and grade and a visibly smaller responsive font size for both supporting lines. This supersedes the Plus Jakarta Sans supporting-line choice in the prior Clock design only; other body text remains Jakarta Sans.

Battery, Maps and SOS widget visual specification (2026-11-18): Battery uses the supplied split rounded-rectangle reference with the level represented by a dynamic-primary filled segment and a darker empty segment. `This phone` uses the wide thin title variant; the percentage uses the normal bold wide title variant at a larger size. Both lines move to whichever segment has room as the locally available battery percentage changes; an unavailable system level uses an explicitly marked M1 mock fallback. The filled Maps shortcut uses a filled navigation icon and the normal bold wide `Open Maps` title; no Maps Intent launches until M2. SOS favorites use individually filled rounded square tiles, up to six visible according to widget width, height and available contacts, with even spacing. A supplied contact image fills its tile; initials appear when no image is available. M1 uses mock contacts and never dials. These extend M1-WIDGET-001/M1-DESIGN-001 and leave native Maps/contacts in M2.

Music header visual reference (2026-11-18): The separate rounded square album-cover tile sits over the left side of one full-width rounded song-details background, with song and artist text to the right of the artwork. Both share the same height. This replaces the vertically stacked art/details arrangement above; full-height sizing, filled play/pause, shape animation and widget spacing remain as specified.

| ID | Requirement and acceptance evidence | Source |
|---|---|---|
| M2-MEDIA-001 | MediaSessionManager active sessions with notification-listener authorization; metadata/state/progress where available. Supported MediaController actions only; clear disappearing sessions and handle no-session/permission states. | §§6, 9, 15–16 |
| M2-VOLUME-001 | AudioManager changes selected stream within actual bounds, reflects true volume and route changes. | §§6, 9, 15 |
| M2-CONTACT-001 | Permission-aware system contact picker and up to six ordered, editable SOS favorites with contact images when available; ACTION_DIAL default. ACTION_CALL requires permission and confirmation; emergency numbers through system dialer. No automatic call from notification text. | §§6, 10, 13, 15 |
| M2-CALLS-001 | Native dialer owns incoming calls. Recent 5 via READ_CALL_LOG only if permission and distribution policy permit; otherwise favorites/system dialer fallback. | §§6, 10, 15–16 |
| M2-NOTIFY-001 | User-granted NotificationListenerService normalizes optional package/time/title/body/channel/category/group/key; local HIGH/NORMAL/MUTED rules with per-app overrides. HIGH popup only in foreground, rate-limited, auto-dismissed, avoiding SOS/navigation. Bounded feed; respect updates/removals/redaction; do not retain full text indefinitely. | §§4, 11, 13, 15–16 |
| M2-MAPS-001 | Explicit Google Maps intent (package/geo/navigation URI for known destination; main app otherwise), missing-app fallback and recoverable return via task history. Optional return overlay requires special access and can be removed; background-start limits apply. | §§4, 8, 13, 15–16 |
| M2-TURN-001 | Experimental, consent-dependent parsing of available Maps notification extras only. States INACTIVE/LISTENING/GUIDANCE/STALE/UNAVAILABLE; confidence and conservative TTL; tap opens Maps. Never invent maneuver/distance or claim official API. | §§8, 15–17 |
| M2-NAV-001 | Planned interactive in-app Google Maps widget using Navigation SDK for Android for its idle map and turn-by-turn guidance, and Places SDK for Android (New) Autocomplete for destination search. Minimum 4×4 cells. With no destination, center on the rider's current location. Tapping the map or search control expands the widget over the full Ride grid, hiding other widgets while retaining their saved placements. A Grid button appears beside End Ride only while expanded. Search requests start only at three entered characters and one second after the last edit; India-only place predictions, a fresh search session token, out-of-order response suppression and a minimal Place Details field set are required. Selecting a result shows only the start point (Your location), selected end point and Start navigation; no ETA, route line, distance, alternatives or route preview appears. Only an explicit Start navigation tap requests a Navigation SDK route; guidance starts only after successful routing; the SDK ETA card is hidden, while active guidance may show custom ETA, remaining time and distance. Arrival stops/clears guidance and restores the idle centered map. Grid during search/selection discards that draft and restores idle view; Grid during route start or active guidance only collapses the visual expansion and preserves navigation. Offline, denied/approximate location, SDK/key/quota failures, missing route, lifecycle and Ride-end cleanup need explicit states. No implementation until credentials and API/billing setup are available. | User direction 2026-11-19; M2 planning |
| M2-WEATHER-001 | Optional cache/provider only if selected without mandatory paid service; offline uncached state unavailable with freshness label. | §§5–6, 15, 17 |
| M2-QA-001 | Test supported music apps, system volume, six saved contact actions, persisted notification rules, Maps return, stale-safe cues, airplane mode, intercom output, call interruption, notification storms and screen sleep. | §§15–16 |

User direction (2026-11-19): Implement live weather in the combined Clock and OLED styles. Handle no Internet, bounded retries, location changes and battery use. When weather cannot be retrieved or a usable cache has expired, display exactly `Something's not right` with a thunderstorm icon. This refines M2-WEATHER-001.

User direction (2026-11-19): The Calls widget shows the latest five system Recents with available contact name and number, plus a confirmed direct-call action. It must not keep its own call record; if Android cannot grant restricted call-log access, state clearly that the feature cannot be used. Current distribution is private APK, with Google Play later requiring a replacement or eligible default-handler design. The Notifications widget shows up to ten current messages and other high-priority alerts, messages always first, avatar or initials, sender name and up to two lines of message text with ellipsis. Each widget has one child scroll view. These refine M2-CALLS-001 and the feed slice of M2-NOTIFY-001; notification rules/popups remain separate.

User size correction (2026-11-19): Calls and Notifications each require at least 4×5 grid cells, may span the full 12-column width and any available device-bounded height up to the catalog maximum. New defaults are 6×5; smaller saved placements remain but require resize. This refines M1-WIDGET-001, M2-CALLS-001 and M2-NOTIFY-001.

User direction (2026-11-19): Implement the Turn guidance widget using useful available Google Maps live notification fields, including direction arrow, original instruction and ETA when present, with a modern layout. Treat absent/unreadable/stale fields honestly. Concrete implementation now; live-device testing later. This refines M2-TURN-001 and M2-QA-001 without expanding the separate M2-MAPS-001 shortcut/handoff scope.

User direction (2026-11-19): Add a separate full Google Maps widget with the M2-NAV-001 flow above. Record requirements and risks now; do not change code until Google Cloud credentials are available. The existing Maps shortcut, notification-derived Turn guidance and every other widget stay exactly as they are. The new widget owns all of its map/search/navigation state; other widgets neither subscribe to nor depend on it. Full-grid expansion only hides them visually without changing their data or saved layout.

User clarification (2026-11-19): The selected-destination confirmation shows only the start and end points, with no ETA or route preview. The billable Navigation SDK route request occurs solely on Start navigation. India filtering remains required. The Turn guidance widget and every other widget retain their existing logic/content and do not depend on M2-NAV-001. No code work begins until GCP setup is ready.
User navigation UI correction (2026-11-20): After Start navigation succeeds, show live ETA, remaining time and remaining distance from Navigation SDK updates. In the compact widget, keep the route map readable by hiding the SDK turn header, fitting a small progress card and end control, and restoring the follow camera after collapse. The point-only pre-start confirmation remains unchanged. This refines M2-NAV-001.

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
| Orientation | Home and Settings follow system orientation; the page editor and Ride use landscape. Restore user orientation on End Ride. |
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


Widget sizing correction (2026-11-19): Battery accepts horizontal N×2 and vertical 2×N with N≥4, so one axis remains exactly two cells. Music starts at 5×4, accepts squares and other shapes with width≥height, and rejects portrait geometry. Every M1 widget text label should wrap and fit its actual bounds while retaining its title/body font family; a readable size takes priority over a single line. SOS favorites may form multiple rows when height permits, showing at most six mock contacts. These clarify M1-WIDGET-001, M1-PAGE-001, M1-DESIGN-001 and M1-QA-001. Native sources and actions remain M2/M3.

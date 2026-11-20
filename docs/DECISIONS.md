# Architecture decisions

Accepted decisions are append-only. Proposed entries require approval before application implementation. Supersede an accepted entry with a new ADR and cross-reference both.

| ID | Date | Status | Subject |
|---|---|---|---|
| ADR-001 | 2026-11-18 | Accepted | Documentation contracts are proposed until Kotlin validation exists |
| ADR-002 | 2026-11-18 | Proposed | Application package namespace |
| ADR-003 | 2026-11-18 | Accepted | Retain existing namespace for M1 foundation |
| ADR-004 | 2026-11-18 | Accepted | Expressive theme and asset stack for M1 foundation |
| ADR-005 | 2026-11-18 | Accepted | Navigation Compose for foundation screens |
| ADR-006 | 2026-11-18 | Accepted | Page and widget persistence schema for M1 editor |
| ADR-007 | 2026-11-18 | Accepted | Ride PiP and notification continuity |
| ADR-008 | 2026-11-18 | Accepted | Fixed device-sized Ride canvas and widget resize points |
| ADR-009 | 2026-11-18 | Accepted | Hilt/ViewModel wiring with non-aggregating processing |
| ADR-010 | 2026-11-18 | Accepted | Landscape page editor and minimum music controls |
| ADR-011 | 2026-11-18 | Accepted | Wide title type, Jakarta body, spacious sides and flexible controls |
| ADR-012 | 2026-11-18 | Accepted | M1 single-widget canvas, occupied move and availability-aware OLED |
| ADR-013 | 2026-11-18 | Accepted | Ride visual rail, three-page indicator and labeled OLED previews |
| ADR-014 | 2026-11-18 | Accepted | Exact Roboto Flex specimen axes and vertical OLED stack |
| ADR-015 | 2026-11-18 | Accepted | Responsive landscape-only M1 music widget |
| ADR-016 | 2026-11-18 | Accepted | Full-height music, stacked artwork and stateful filled play control |
| ADR-017 | 2026-11-18 | Accepted | Wider widget spacing and animated music play shape |
| ADR-018 | 2026-11-18 | Accepted | Music cover art overlays a full-width metadata surface |
| ADR-019 | 2026-11-18 | Accepted | One-cell-axis mock volume bar with adaptive counter |
| ADR-020 | 2026-11-18 | Accepted | Detached volume segments and wide title counter |
| ADR-021 | 2026-11-18 | Accepted | Combine clock and mock weather in one widget |
| ADR-022 | 2026-11-18 | Accepted | Center Clock and use thin wide Roboto Flex on supporting lines |
| ADR-023 | 2026-11-18 | Accepted | Battery split, filled Maps shortcut and responsive SOS preview tiles |
| ADR-024 | 2026-11-19 | Accepted | Two-axis Battery, square Music, fitted text and SOS grids |
| ADR-025 | 2026-11-19 | Accepted | Exact editor and Ride geometry with compact add |
| ADR-026 | 2026-11-19 | Accepted | Tonal M1 screen and pending-widget refresh |
| ADR-027 | 2026-11-19 | Accepted | Expressive top bars and connected idle choice |
| ADR-028 | 2026-11-19 | Accepted | Compact Back visual and non-Ride spacing |
| ADR-029 | 2026-11-19 | Accepted | Typed M1 states for deferred widgets |
| ADR-030 | 2026-11-19 | Accepted | Filled Phosphor icons |
| ADR-031 | 2026-11-19 | Accepted | Ride startup transition and icon exceptions |
| ADR-032 | 2026-11-19 | Accepted | Three-second Ride intro and larger bike |
| ADR-033 | 2026-11-19 | Accepted | Page manager, OLED styles and SOS M2 boundary |
| ADR-034 | 2026-11-19 | Accepted | Larger monochrome Row saver |
| ADR-035 | 2026-11-19 | Accepted | Row saver outline, spacing and icons |
| ADR-036 | 2026-11-19 | Accepted | Saver fade and live music session adapter |
| ADR-037 | 2026-11-19 | Accepted | Music-stream volume source and route refresh |
| ADR-038 | 2026-11-19 | Accepted | Foreground approximate-location weather with bounded retries |
| ADR-039 | 2026-11-19 | Accepted | Private-APK system Recents and bounded notification widget feed |
| ADR-040 | 2026-11-19 | Accepted | Four-by-five minimum Calls and Notifications list geometry |
| ADR-041 | 2026-11-19 | Superseded by ADR-042 | Even row counts for the device-sized Ride grid |
| ADR-042 | 2026-11-19 | Superseded in part by ADR-043 | Square grid cells and space-checked widget rotation |
| ADR-043 | 2026-11-19 | Superseded in part by ADR-044 | Device-wide square grid with at least seven rows |
| ADR-044 | 2026-11-19 | Accepted | Pixel 8a six-row square-cell baseline and saved-widget fitting |
| ADR-045 | 2026-11-19 | Accepted | Conservative Maps notification guidance |
| ADR-046 | 2026-11-19 | Superseded by ADR-047 | In-app Navigation SDK map and Places search |
| ADR-047 | 2026-11-19 | Accepted | Isolated Maps widget, point-only confirmation and explicit route start |
| ADR-048 | 2026-11-19 | Accepted | SDK versions and isolated Maps lifecycle |
| ADR-049 | 2026-11-19 | Accepted | Bounded multi-provider map recenter lookup |
| ADR-050 | 2026-11-20 | Accepted | Active route progress and compact Maps presentation |
| ADR-051 | 2026-11-20 | Accepted | Bottom map progress, fixed current origin and destination picker |
| ADR-052 | 2026-11-20 | Accepted | Window-sized Maps search and keyboard inset ownership |
| ADR-053 | 2026-11-20 | Accepted | Navigation voice, timed end confirmation and complete idle reset |
| ADR-054 | 2026-11-20 | Accepted | Settled-page map loading and Ride quality-of-life controls |
| ADR-055 | 2026-11-20 | Accepted | One-row Clock, adaptive Battery end labels and notification timestamps |
| ADR-056 | 2026-11-20 | Accepted | Weather starts at three Clock rows |
| ADR-057 | 2026-11-20 | Accepted | Foreground GPS Speedometer and per-tile configuration |
| ADR-058 | 2026-11-20 | Accepted | Independent SOS contact snapshots and confirmed calling |
| ADR-059 | 2026-11-20 | Accepted | Large timed Ride confirmations with draining dismiss fill |
| ADR-060 | 2026-11-20 | Accepted | Stationary RIDE word and upward startup reveal |
| ADR-061 | 2026-11-20 | Accepted | Longer HARD and SAFE holds |
| ADR-062 | 2026-11-20 | Accepted | Stable warning layout through the intro reveal |
| ADR-063 | 2026-11-20 | Accepted | Ride-retained navigation ownership and separate guidance/UI/SDK lifetimes |
| ADR-064 | 2026-11-20 | Proposed | Navigation SDK deadlock-fix upgrade proposal |
| ADR-065 | 2026-11-20 | Accepted | Approved SDK 6.3.3 and Kotlin 2.1.21 deadlock-fix update |
| ADR-066 | 2026-11-20 | Accepted | External assistant widget, disabled rail and content-first guidance |
| ADR-067 | 2026-11-20 | Accepted | Confirmed source-app handoff and widget alignment refinements |
| ADR-068 | 2026-11-20 | Accepted | Device music app chooser sheet when no music is playing |
| ADR-069 | 2026-11-20 | Accepted | Music metadata keeps size-based typography with faded marquee |
| ADR-070 | 2026-11-20 | Accepted | Automatic OLED dimming with a darker Dim screen tier |

## ADR-001 — Configuration authority before app implementation

- Date/status: 2026-11-18 / Accepted for bootstrap documentation only.
- Context: Bootstrap requires JSON design and widget catalogs, but the starter has no Compose theme or widget registry.
- Options: claim JSON executable authority; treat JSON as proposed until generated or validated against Kotlin.
- Decision/rationale: Mark JSON `sourceOfTruth: proposed`; future Kotlin implementation is authoritative until generation/validation closes drift.
- Tradeoffs/consequences: Contract can guide implementation, but no runtime behavior is guaranteed. Add parity validation before claiming JSON canonical.
- Affected files: `config/*`, `docs/DESIGN_SYSTEM.md`, `docs/WIDGET_CATALOG.md`.
- PRD IDs: M1-DESIGN-001, M1-WIDGET-001.

## ADR-002 — Package namespace reconciliation

- Date/status: 2026-11-18 / Proposed.
- Context: PRD suggests `com.example.ridecompanion`; existing Gradle uses `com.kapav.ridemode`.
- Options: retain current namespace; rename to PRD example.
- Proposal: retain current namespace because the PRD path is explicitly suggested, but require authorization before source/package changes.
- Tradeoffs/consequences: Docs use relative package structure; source implementation remains pending.
- Affected files: future Android source, Gradle only if renamed.
- PRD IDs: M1-APP-001.

## ADR-003 — Retain existing namespace for M1 foundation

- Date/status: 2026-11-18 / Accepted; resolves the ADR-002 proposal without rewriting it.
- Context: User approved implementation following the documented architecture, which names `com.kapav.ridemode`; the PRD uses an example package.
- Options: retain current namespace; rename the app and package to the PRD example.
- Decision/rationale: Retain `com.kapav.ridemode`, matching Gradle, tests and architecture with no package migration.
- Tradeoffs/consequences: PRD example paths are interpreted structurally; future source uses the existing namespace.
- Affected files: `app/src/main/java/com/kapav/ridemode/*`, `app/src/main/AndroidManifest.xml`, `docs/ARCHITECTURE.md`.
- PRD IDs: M1-APP-001.

## ADR-004 — Expressive theme and assets for M1 foundation

- Date/status: 2026-11-18 / Accepted.
- Context: User requested Material 3 Expressive components and morphing loader, wallpaper colors, Roboto Flex wide titles and normal body, and Phosphor icons. The installed AGP 8.13.2 and API 36 cannot compile Material 3 `1.5.0-alpha23` or `1.5.0-rc01`, which require AGP 9.1/API 37.
- Options: upgrade the Android toolchain; use stable Material 3 `1.4.0` with inaccessible Expressive APIs; use the compatible public Expressive API release `1.5.0-alpha18`.
- Decision/rationale: Use `1.5.0-alpha18` with `MaterialExpressiveTheme`, dynamic wallpaper colors on API 31+, Expressive defaults below API 31, and a bundled Roboto Flex font. Use the `com.adamglin:phosphor-icon:1.0.0` Compose port. Keep Kotlin tokens authoritative under ADR-001.
- Tradeoffs/consequences: Expressive API is prerelease and may change; dynamic colors are unavailable on API 24–30; wide font axis needs API 26, so API 24–25 uses normal width. Revisit dependency version when the toolchain is updated.
- Affected files: Gradle catalog/build files, `app/src/main/java/com/kapav/ridemode/core/ui/*`, font resource, `config/design-tokens.json`, `docs/DESIGN_SYSTEM.md`.
- PRD IDs: M1-APP-001, M1-DESIGN-001.

## ADR-005 — Navigation Compose for foundation screens

- Date/status: 2026-11-18 / Accepted.
- Context: M1-APP-001 requires navigation among Home, Settings, onboarding and permission explanations. The user requested the next foundation part while retaining the Compose architecture and bounded scope.
- Options: hand-written screen state/back stack; Navigation Compose graph.
- Decision/rationale: Use `androidx.navigation:navigation-compose:2.9.8` with four static string routes and a top-level `NavHost`. Screens receive callbacks; the controller remains in `RideApp`. This provides standard back-stack restoration without adding serialization or a routing layer for screens with no arguments.
- Tradeoffs/consequences: One new AndroidX dependency and IDE sync are required; routes are internal constants and can migrate to typed routes if argument needs arise. No navigation logic enters feature screens.
- Affected files: Gradle catalog/build files, `MainActivity.kt`, `core/navigation/RideApp.kt`, `feature/{home,settings,onboarding,permissions}`.
- PRD IDs: M1-APP-001.

## ADR-006 — Page and widget persistence schema for M1 editor

- Date/status: 2026-11-18 / Accepted by user on 2026-11-18; no persistence dependency or schema implemented yet.
- Context: M1-PAGE-001 and M1-PERSIST-001 require editable ordered pages and widget placements that survive process death. The PRD specifies Room for relational layout data and DataStore for preferences, but does not define keys, table fields, migration policy or seed behavior in code.
- Options: store entire layouts as one DataStore JSON value; use Room tables for pages/widgets plus DataStore preferences; store all state in Room.
- Decision/rationale: Use Room database version 1 with `ride_pages(id TEXT PRIMARY KEY, name TEXT, sort_order INTEGER, enabled INTEGER)` and `widget_instances(id TEXT PRIMARY KEY, page_id TEXT, type_id TEXT, x INTEGER, y INTEGER, width INTEGER, height INTEGER, min_width INTEGER, min_height INTEGER, oled_supported INTEGER, config_json TEXT, visible INTEGER)`. `page_id` is a foreign key with cascade deletion; IDs are UUID strings, booleans use Room Boolean mapping, and `sort_order` is unique within an editor transaction. Expose Flow reads and write page/widget edits in Room transactions. Keep preference overrides (`keepAwake`, saver enable/delay/outline/brightness, popup enable, privacy mode) in DataStore. Seed defaults once from Kotlin values checked against proposed `config/ride-defaults.json`, never on update. Use explicit migrations rather than destructive fallback. This follows the PRD's data boundaries and lets the editor update one page without rewriting every layout.
- Tradeoffs/consequences: Adds Room, its compiler, and DataStore dependencies plus schema/migration tests. The database and Kotlin widget registry must validate type IDs and size constraints before writes. A migration is required for future schema changes; default JSON remains proposed until parity validation exists. Widget minimum size and OLED support are stored as instance snapshots for process restoration and checked against the current registry when edited; catalog changes may require migration or repair.
- Affected files: future Gradle catalog/build, `core/database`, `core/datastore`, `data/repository`, `feature/pageeditor`, `feature/ride`, config validation, architecture and traceability docs.
- PRD IDs: M1-PAGE-001, M1-PERSIST-001, M1-RIDE-001.

## ADR-007 — Ride PiP and notification continuity

- Date/status: 2026-11-18 / Accepted; user explicitly requested this Ride behavior.
- Context: The Ride Activity currently finishes or backgrounds like a normal screen. The user requires a small window on minimization and Open/End Ride controls in the notification shade after the small window closes. Android PiP and Live Update promotion have device, permission and eligibility limits.
- Options: system PiP plus an ongoing notification; custom overlay plus foreground service; notification only.
- Decision/rationale: Use system PiP for the Ride Activity and a single ongoing notification for the user-initiated Ride session, with explicit Open and End Ride PendingIntents. Request notification permission at Ride start on Android 13+. Request system promotion on Android versions supporting Live Updates; treat promotion as system-controlled and retain the ordinary ongoing notification. Keep Ride session actions in the app process with a manifest receiver; no background tracking or foreground service is needed for the current preview.
- Tradeoffs/consequences: PiP is unavailable below Android 8 or on unsupported devices. A denied notification permission prevents the shade fallback. The system can refuse promotion or let users dismiss/demote it. No route progress is claimed; the notification reports only active Ride status and elapsed time. A future background data source may require a separately scoped service decision.
- Affected files: `RideActivity.kt`, `RideDashboard.kt`, Ride notification/receiver code, Home launch flow, manifest, strings, capability/design docs.
- PRD IDs: M1-RIDE-001, M1-RIDE-002.

## ADR-008 — Fixed device-sized Ride canvas and widget resize points

- Date/status: 2026-11-18 / Accepted; user explicitly specified this M1 interaction and layout.
- Context: The first persisted-page renderer scrolled vertically and duplicated the page title; editor controls dominated the screen, and widget resizing was button-only. The user requires on-widget drag/resize points within a fixed grid, a non-scrolling Ride canvas sized to the device, and a smaller, clearer rail.
- Options: keep a scrolling page; scale the 12-column page into the current Ride viewport; paginate overflowing rows. For resize, use only controls; use selected-widget handles; allow unconstrained free placement.
- Decision/rationale: Fit each enabled page's saved grid into the current landscape viewport without vertical scroll. The repository bounds new placements to a device-derived row limit, and the editor shows that bounded grid. Selected widgets expose touch-sized width, height and corner resize points with live snapped preview; drag is clamped and collision is surfaced. Keep button alternatives for precise/accessibility input. Ride uses a 56 dp rail with bold single-line labels, 13 dp corner radius, and page status only at the bottom.
- Tradeoffs/consequences: A dense page scales cell height down and can reduce content legibility; the editor prevents new out-of-bounds placements. Existing untouched vertically seeded templates are repacked once only when their IDs and original positions match exactly, preserving edited layouts. Device/screen-size visual checks remain required. The shorter 56 dp rail meets the minimum touch target but is below the 64–80 dp primary target.
- Affected files: `core/model/RideGridConstraints.kt`, `GridLayout.kt`, `feature/pageeditor/PageEditorScreen.kt`, `feature/ride/FixedRideCanvas.kt`, `RideDashboard.kt`, `core/ui/theme/RideUiTokens.kt`, `data/RideRepository.kt`, design/architecture docs.
- PRD IDs: M1-PAGE-001, M1-RIDE-001, M1-DESIGN-001, M1-QA-001.

## ADR-009 — Hilt/ViewModel wiring with non-aggregating processing

- Date/status: 2026-11-18 / Accepted as implementation of the PRD architecture.
- Context: M1 local state needs a lifecycle-aware StateFlow path and dependency injection. Hilt 2.57.1's aggregating Gradle task failed in this AGP/Kotlin toolchain with a JavaPoet `canonicalName()` linkage error, while compilation itself succeeded.
- Options: manual singleton wiring; Hilt with default aggregating task; Hilt with `enableAggregatingTask = false`.
- Decision/rationale: Use Hilt for the application and activities, one singleton repository provider, and a `RideViewModel` exposing Room/DataStore StateFlows and editor commands. Disable the optional Hilt aggregating task; the non-aggregating kapt path assembled successfully. Keep Java/Kotlin bytecode target at 17 as required by the selected Hilt release.
- Tradeoffs/consequences: Build uses kapt's Kotlin 1.9 stub fallback and may be slower; revisit when the toolchain is upgraded. A successful APK, unit/lint and device database tests are required before claiming the integration verified.
- Affected files: Gradle catalog/build, `RideApplication.kt`, manifest, `core/di/RideModule.kt`, `feature/ride/RideViewModel.kt`, Main/Ride activities, editor/settings/navigation, architecture/README.
- PRD IDs: M1-APP-001, M1-PERSIST-001.

## ADR-010 — Landscape page editor and minimum music controls

- Date/status: 2026-11-18 / Accepted; directly specified by the user for M1.
- Context: Portrait editing could not show the actual Ride composition. A small music card could make transport controls too small to use while riding.
- Options: keep a portrait editor with a scaled preview; use a dedicated landscape editor with the Ride grid geometry. Permit every catalog size; raise the music minimum to fit artwork, metadata and three large controls.
- Decision/rationale: Open a dedicated landscape editor activity. The editor and Ride share the current device's fixed canvas geometry and a 12-column grid, with a compact bottom control rail. Set music minimum to 6×4 cells and default to 8×4, with visible artwork, song and artist mock content and three 56 dp mock controls. Clamp on-widget resize to registered min/max and device rows. Use icons and a short preview transition for clearer editing.
- Tradeoffs/consequences: More rows fit on taller devices; layouts may need repair on smaller devices. Previously edited undersized cards are preserved and flagged for resizing. Clean builds may be needed when adding a Hilt activity because the non-aggregating kapt path can retain stale generated component metadata. Real media controls remain M2.
- Affected files: `feature/pageeditor/*`, `feature/widgets/WidgetCard.kt`, `core/model/{RideGridConstraints,WidgetRegistry}.kt`, `feature/ride/FixedRideCanvas.kt`, `data/RideRepository.kt`, manifest, config, design/architecture/catalog docs.
- PRD IDs: M1-PAGE-001, M1-WIDGET-001, M1-RIDE-001, M1-DESIGN-001, M1-QA-001.

## ADR-011 — Wide title type, Jakarta body, spacious sides and flexible controls

- Date/status: 2026-11-18 / Accepted; user explicitly specified the visual direction.
- Context: The landscape editor and Ride still used narrow side insets and one font family for all text. Music's 56 dp controls and the placeholder volume card did not satisfy the requested control emphasis.
- Options: retain one font and current insets; pair wide bold Roboto Flex titles with Plus Jakarta Sans supporting text and expand the shared side inset. Keep fixed volume dimensions; allow full grid width and device-bounded height with aspect-aware slider rendering.
- Decision/rationale: Bundle the OFL licensed Plus Jakarta Sans variable font for body/labels. Use bold Roboto Flex titles with width axis 125 where supported. Apply 40 dp horizontal insets to both landscape canvas screens while retaining 16 dp vertical insets. Increase music art/transport targets to 64 dp. Let volume grow to 12 columns and up to the device row boundary, displaying a mock slider across the available horizontal or vertical dimension.
- Tradeoffs/consequences: Wider insets reduce cell width on small phones; music's 6×4 minimum is retained and the Pixel 8a must be visually rechecked. Existing undersized volume cards are preserved and must be resized. The slider is a local mock until M2 audio integration. The font asset and license are added to the APK/repository.
- Affected files: `core/ui/theme/*`, font resources/license, `feature/ride/RideDashboard.kt`, `feature/pageeditor/LandscapePageEditorScreen.kt`, `feature/widgets/WidgetCard.kt`, `core/model/{RideGridConstraints,WidgetRegistry}.kt`, config and M1 design/catalog docs.
- PRD IDs: M1-DESIGN-001, M1-RIDE-001, M1-PAGE-001, M1-WIDGET-001, M1-QA-001.

## ADR-012 — M1 single-widget canvas, occupied move and availability-aware OLED

- Date/status: 2026-11-18 / Accepted; user delegated this M1 UI behavior.
- Context: A single widget on a dedicated page left unused canvas space; drag rejected occupied cells; the add menu was a text popup and OLED showed only a clock and prominent wake prompt.
- Options: preserve exact saved geometry in all views or fill a single-widget Ride canvas; reject occupied moves or move one displaced widget; show mock OLED media/weather or only available facts.
- Decision/rationale: Render one visible widget across the Ride/preview canvas while keeping its saved grid geometry for editing. On a move into one occupied widget, find the nearest valid location to the vacated origin for the displaced widget and write both in one Room transaction; reject resize overlaps and moves that would displace multiple widgets. Use a scrollable Material bottom sheet with blurred editor content for adding widgets. OLED displays clock and readable local battery; media/weather remain absent until actual sources exist. Place the half-opacity wake hint at the bottom. The final M1 pass will define unique, mostly container-free widget visuals and size/orientation/customization contracts.
- Tradeoffs/consequences: A single-widget preview visually fills the canvas even when its saved editor rectangle is smaller. Crowded grids may reject a move when no valid displaced location exists. The initial OLED cannot show media/weather because M2 sources are absent. User-run UI acceptance remains open.
- Affected files: `core/model/GridLayout.kt`, `data/RideRepository.kt`, `feature/pageeditor/*`, `feature/ride/{FixedRideCanvas,RideDashboard}.kt`, UI tokens, M1 docs and config.
- PRD IDs: M1-PAGE-001, M1-WIDGET-001, M1-OLED-001, M1-DESIGN-001, M1-QA-001.

## ADR-013 — Ride visual rail, three-page indicator and labeled OLED previews

- Date/status: 2026-11-18 / Accepted; user explicitly directed the visual changes. Supersedes only ADR-012's M1 OLED rule that media/weather stay absent without live sources.
- Context: The user accepted the prior layout but requested bolder/wider titles, a matching outline clock, low-opacity adjacent page names, icon-led rail actions and OLED sample media/weather to evaluate the future UI before M2 integration.
- Options: retain 125-width/700-weight titles or use Roboto Flex width 150/weight 900; show only the current page or animate previous/current/next labels; omit unavailable OLED content or display clearly labeled static previews.
- Decision/rationale: Apply Roboto Flex width 150 and weight 900 to all headline/title styles, with the OLED outline clock inheriting that style. Show previous/current/next page names at 0.34/0.78/0.34 opacity with a 220 ms directional slide, disabled when reduced motion is enabled. Use X + End ride, MapPin + Open (still disabled until M2), and icon-only microphone with an accessibility label. Show static song and weather examples marked mock in OLED, and place real battery icon/percentage above the subdued wake hint.
- Tradeoffs/consequences: Wider text can truncate on narrow landscape viewports; visual checks must cover the reference device and large font scale. OLED sample content is illustrative and must never be mistaken for live media/weather. The three labels do not replace horizontal paging.
- Affected files: `RideTypography.kt`, `RideUiTokens.kt`, `RideDashboard.kt`, design config, M1 docs and task file.
- PRD IDs: M1-RIDE-001, M1-OLED-001, M1-DESIGN-001, M1-QA-001.

## ADR-014 — Exact Roboto Flex specimen axes and vertical OLED stack

- Date/status: 2026-11-18 / Accepted; user supplied the Google Fonts axis settings and Ride layout. Supersedes ADR-013's width-150/weight-900 title axes, adjacent page font, battery icon and OLED side-by-side preview layout.
- Context: The previous Ride visual pass did not match the supplied font specimen or desired single-column OLED reading order; the filled End ride control used too much width.
- Options: continue approximate width/weight styling or apply every supported axis from the bundled Roboto Flex file; place OLED previews side by side or stack them; keep solid buttons or distinguish End and assistant by palette treatment.
- Decision/rationale: Apply `wdth=151`, `wght=648`, `GRAD=150`, `XOPQ=96`, `XTRA=603`, `YOPQ=79`, `YTAS=677`, `YTDE=-217`, `YTFI=738`, `YTUC=712`, `YTLC=514`, `slnt=0`; leave optical size automatic. Adjacent page names use the same wide axis set but `wght=300`, `GRAD=0`. OLED order is clock, one-line weather with icon, two-line song/artist with icon, one-line battery percentage without icon, and wake hint. Use an outlined X + two-line End/ride control; Maps keeps the normal filled control and microphone uses the secondary palette color.
- Tradeoffs/consequences: Exact wide axes and a two-line End label need device checks for clipping at large font scale and narrow landscape width. Weight 648 with grade 150 follows the image even though its raw weight number is lower than the prior 900. M1 song/weather remain labeled mocks; battery remains local.
- Affected files: `RideTypography.kt`, `RideDashboard.kt`, design config, M1 docs and task file.
- PRD IDs: M1-RIDE-001, M1-OLED-001, M1-DESIGN-001, M1-QA-001.

## ADR-015 — Responsive landscape-only M1 music widget

- Date/status: 2026-11-18 / Accepted; user explicitly specified the first final-widget design. Supersedes ADR-010/011 only for fixed-size music controls and ADR-012's deferred music visual polish.
- Context: The old music card used a generic outer surface, a fixed 64 dp artwork/control row and static play-pause icon; it did not fill resized bounds or enforce landscape-only geometry.
- Options: keep fixed 64 dp controls and generic card; render a dedicated responsive composition while retaining current 6×4/8×4/12×6 registry bounds, with an added landscape-only rule.
- Decision/rationale: Route music through a dedicated widget renderer. Use attached rounded album-art and darker full-width metadata surfaces on top; split the remaining width and height equally among previous, play/pause and next. The center control is filled, side controls are transparent. M1 controls only change local mock track/playing state. Reject music rectangles whose grid width is not greater than height. Keep editor controls inactive so widget dragging remains possible.
- Tradeoffs/consequences: On smaller viewports control height may fall below the earlier 64 dp target; the full available area remains tappable and must be visually checked. Existing saved music rectangles that violate the new orientation rule remain in Room but show a resize prompt until repaired. M2 supplies real metadata and controller actions.
- Affected files: `feature/widgets/{WidgetCard,MusicWidget}.kt`, `core/model/WidgetRegistry.kt`, UI tokens, grid unit tests, widget catalog/config validator and M1 docs.
- PRD IDs: M1-WIDGET-001, M1-PAGE-001, M1-DESIGN-001, M1-QA-001; M2-MEDIA-001 remains TODO.

## ADR-016 — Full-height music, stacked artwork and stateful filled play control

- Date/status: 2026-11-18 / Accepted; user explicitly refined the music design. Supersedes ADR-015's attached horizontal artwork/details row, fixed six-row maximum and play button shape, while retaining M1 mock-only state.
- Context: The first music design could not grow to the device's full grid height, its metadata sat beside artwork, and the center control did not match the desired filled/capsule state treatment.
- Options: retain a six-row height ceiling or use the device-bounded maximum; keep attached horizontal surfaces or stack separate full-width surfaces; keep one rounded center shape or vary it with mock play state.
- Decision/rationale: Set music catalog max height to 24 rows, with repository/editor limits still clamped to actual device rows. Permit full-width 12-column music to use all available rows; partial-width music still requires width greater than height. Stack a rounded artwork surface above a separate full-width metadata surface using a low-opacity primary color. Render the center as a Material filled button: capsule while paused, 13 dp rounded rectangle while playing. Side controls remain transparent. Keep mock track and playback state local to M1.
- Tradeoffs/consequences: 6×4 remains cramped for stacked details and controls; user visual acceptance must decide whether to raise the minimum later. Full-width tall layouts consume the page and are physically landscape because they span the entire landscape canvas. Existing saved music placements remain valid where possible. No real media source or action is added.
- Affected files: `feature/widgets/MusicWidget.kt`, `core/model/WidgetRegistry.kt`, grid unit tests, UI/design tokens, widget catalog/config and M1 docs.
- PRD IDs: M1-WIDGET-001, M1-PAGE-001, M1-DESIGN-001, M1-QA-001; M2-MEDIA-001 remains TODO.

## ADR-017 — Wider widget spacing and animated music play shape

- Date/status: 2026-11-18 / Accepted; user explicitly requested both visual refinements. Extends ADR-016's stateful music button with a transition.
- Context: The shared grid left only 4 dp between widgets, and music's filled center button jumped immediately between capsule and rounded rectangle on click.
- Options: keep existing spacing and instant shape switch; use an 8 dp shared grid gap and animate the center button's corner radius.
- Decision/rationale: Increase `RideUiTokens.gridGap` to 8 dp for editor and Ride widget placement. Animate music play/pause corner radius over 220 ms between half the smaller button dimension and the existing 13 dp rounded rectangle. Keep the button filled through the animation and retain mock-only state.
- Tradeoffs/consequences: Each widget has 4 dp less drawable width/height than before, so the 6×4 music size and narrow widgets need user visual checks. Shape animation is a small visual effect; system animation-scale settings still govern Compose animation behavior. No persistence or native media change.
- Affected files: `RideUiTokens.kt`, `MusicWidget.kt`, `config/design-tokens.json`, M1 design/catalog/roadmap/task docs.
- PRD IDs: M1-WIDGET-001, M1-DESIGN-001, M1-QA-001.

## ADR-018 — Music cover art overlays a full-width metadata surface

- Date/status: 2026-11-18 / Accepted; user supplied a visual reference correcting the music header. Supersedes ADR-016's vertically stacked art/details arrangement only.
- Context: The user intended a layered horizontal header: a separate rounded square album cover at left, over a full-width rounded song-details background extending behind and to its right. The previous implementation stacked artwork and metadata vertically.
- Options: keep the vertical stack or lay a square artwork tile over the left side of one full-width metadata surface.
- Decision/rationale: Use a shared-height header occupying half the widget's usable height. Draw the low-opacity primary details surface full width first, inset song/artist text to the right of the square artwork, then draw the rounded artwork tile over the left edge. Keep the existing full-height sizing, 8 dp widget gaps, equal-width controls, filled play button and shape animation.
- Tradeoffs/consequences: On a narrow minimum-sized widget the text has less width and may ellipsize; the user will check 6×4 and full-height sizes. The mock artwork remains an icon placeholder until M2 supplies album art.
- Affected files: `feature/widgets/MusicWidget.kt`, music design token/config and M1 design/catalog/roadmap/task docs.
- PRD IDs: M1-WIDGET-001, M1-DESIGN-001, M1-QA-001; M2-MEDIA-001 remains TODO.

## ADR-019 — One-cell-axis mock volume bar with adaptive counter

- Date/status: 2026-11-18 / Accepted; user specified the visual reference and sizing rule for M1.
- Context: The generic volume card had a narrow slider inside a visible container, no animated counter placement, and allowed thick grid rectangles. The supplied Pixel-style reference shows a rounded dark track, a separated colored fill and a number inside the track.
- Options: retain the generic card and broad sizes; use a dedicated transparent-outer renderer with exactly one grid cell on one axis and a long axis bounded by the grid.
- Decision/rationale: Use 1×N vertical or N×1 horizontal grid rectangles, excluding 1×1. Render a rounded track and separated primary fill without an outer card. Position the live mock percentage at the start in the blank or filled region according to room, animate the fill and counter, and let taps/drags update local M1 state. Preserve saved invalid rectangles and show a resize prompt. A single visible volume widget keeps its saved grid bounds in Ride so it remains one cell thick.
- Tradeoffs/consequences: A one-cell bar may be small on compact screens; user visual and touch checks are required. Older 4×2 volume cards must be resized. No AudioManager or true system volume is used before M2.
- Affected files: `WidgetRegistry.kt`, `VolumeWidget.kt`, `WidgetCard.kt`, `FixedRideCanvas.kt`, `RideUiTokens.kt`, `GridLayoutTest.kt`, widget/design config and M1 docs.
- PRD IDs: M1-WIDGET-001, M1-PAGE-001, M1-DESIGN-001, M1-QA-001; M2-VOLUME-001 remains TODO.

## ADR-020 — Detached volume segments and wide title counter

- Date/status: 2026-11-18 / Accepted; user supplied a second image clarifying the exact volume structure. Supersedes ADR-019's continuous-track visual treatment only; its grid and M1 mock rules remain.
- Context: The first volume renderer drew the colored fill over a full dark track and used a small label. The new reference has separate dark-empty and bright-filled bars with a clear gap and a line between them; their ends toward the line are flat and their outer ends rounded.
- Options: retain the continuous track with a small divider or draw two independent segments and a detached divider. Retain the small label or use the established wide bold title font.
- Decision/rationale: Draw only the empty and filled segments, separated by a transparent gap and a primary-colored divider extending across the short-axis bounds. Use dynamic theme primary for the fill and a translucent primary for the empty segment. Use the app's Roboto Flex title style for the counter, placed at the top/start in the region with room; keep animated movement when that region changes. Use the saved grid shape to choose orientation, so a 1×N widget always renders vertically on different screens.
- Tradeoffs/consequences: The counter may feel tight at the minimum 1×2 or 2×1 size and at large font scales; user device checks determine whether the minimum should change later. The visual matches the supplied structure while retaining M1 mock interaction; system volume remains M2.
- Affected files: `VolumeWidget.kt`, `WidgetCard.kt`, `RideUiTokens.kt`, `config/design-tokens.json`, M1 visual/catalog/task docs.
- PRD IDs: M1-WIDGET-001, M1-DESIGN-001, M1-QA-001; M2-VOLUME-001 remains TODO.

## ADR-021 — Combine clock and mock weather in one widget

- Date/status: 2026-11-18 / Accepted; user explicitly requested the combined design and removal of the separate Weather widget.
- Context: Clock was a generic card with time/date on one line, while Weather was a separate mock card and addable catalog type. The requested layout needs a transparent clock widget with time, smaller thinner day/date, and icon/temperature/condition on a third line.
- Options: keep two types and merely group them visually; remove Weather from new layouts and convert saved Weather placements to the combined Clock type.
- Decision/rationale: Give Clock a dedicated transparent renderer with responsive three-line content and a weather-condition-to-icon mapping. Keep local time live and show visibly labeled mock weather until M2. Set Clock minimum to 2×2, default to 4×3 and max to 12×24. Remove Weather from the active registry, catalog, add sheet and new template; convert existing saved Weather instances to Clock in place at startup, preserving ID, position, size and visibility. A legacy 1-row tile receives a resize prompt. Multiple saved Clock/Weather tiles on one page remain separate combined tiles so no user placement is discarded.
- Tradeoffs/consequences: Existing pages with both types may show two combined Clock tiles; users can remove an extra tile in the editor. A two-row minimum excludes old one-row Clock tiles until resized. Live weather, units and provider error states remain M2-WEATHER-001. No schema, dependency or permission change.
- Affected files: `ClockWidget.kt`, `WidgetCard.kt`, `WidgetRegistry.kt`, `RideRepository.kt`, `RideUiTokens.kt`, grid test, widget/design/default config and M1 docs.
- PRD IDs: M1-WIDGET-001, M1-PAGE-001, M1-DESIGN-001, M1-QA-001; M2-WEATHER-001 remains TODO.

## ADR-022 — Center Clock and use thin wide Roboto Flex on supporting lines

- Date/status: 2026-11-18 / Accepted; user corrected the combined Clock typography and alignment. Supersedes ADR-021's Jakarta Sans supporting-line style and top-left placement only.
- Context: The first combined Clock put day/date and mock weather in Plus Jakarta Sans, larger than intended, and aligned the group to the widget's top-left. The user wants all three lines in the title family, with smaller, lower-weight/lower-grade bottom lines and centered content.
- Options: keep Jakarta support text; use the existing wide thin Roboto Flex variant shared with adjacent page names. Align only individual lines or center the whole three-line group.
- Decision/rationale: Use Roboto Flex width 151, weight 300, grade 0 for day/date and weather text, while time keeps the bold specimen axes. Reduce supporting size to 32% of responsive time size, bounded at 11–18 sp, and center the block and each line within the widget. Keep the condition icon at least 14 dp. Preserve M1 mock weather, sizing and legacy conversion.
- Tradeoffs/consequences: Narrow 2×2 cells and large system font may still ellipsize long localized dates/weather; user visual acceptance remains open. No permission, schema or native provider change.
- Affected files: `ClockWidget.kt`, `RideUiTokens.kt`, `RideTypography.kt`, `config/design-tokens.json`, Clock design/catalog/task docs.
- PRD IDs: M1-WIDGET-001, M1-DESIGN-001, M1-QA-001; M2-WEATHER-001 remains TODO.

## ADR-023 — Battery split, filled Maps shortcut and responsive SOS preview tiles

- Date/status: 2026-11-18 / Accepted; user supplied three widget references and delegated their M1 visual implementation.
- Context: Battery, Maps shortcut and SOS favorites still used generic placeholder cards. Battery needed a two-part level surface and side-switching text; Maps needed a filled icon/title tile; SOS needed square contact shortcuts with optional images and initials fallback.
- Options: retain generic cards until M2 or create dedicated M1 renderers with local/mock data and responsive geometry.
- Decision/rationale: Render Battery as independent primary and subdued segments with a small gap. Read local battery capacity and battery broadcasts without new permission; use a visibly labeled 63% mock fallback only when unavailable. Put thin wide `This phone` above a larger bold percentage and move both to the segment with room. Raise Battery min/default/max to 4×2 / 6×3 / 12×6 so both lines fit. Render Maps as a filled primary rounded tile with filled NavigationArrow and bold wide `Open Maps`; it remains a mock preview in M1. Render SOS as up to six centered square primary tiles; fit count from width, spread tiles evenly, show a supplied image painter when present and initials otherwise. Six named mock contacts exercise width behavior, while dial actions and real contact photos remain M2. Preserve saved placements and show a resize prompt for legacy undersized Battery tiles.
- Tradeoffs/consequences: Existing Battery 3×1 tiles require manual resize. Narrow SOS widgets may show only the first mock contact; page resizing exposes more. M1 mock badges and accessibility descriptions clarify unavailable actions. No new dependency, permission, schema, Maps Intent or call action.
- Affected files: `BatteryWidget.kt`, `MapsShortcutWidget.kt`, `SosFavoritesWidget.kt`, `WidgetCard.kt`, `WidgetRegistry.kt`, `RideUiTokens.kt`, grid tests, widget/design config and M1 docs.
- PRD IDs: M1-WIDGET-001, M1-PAGE-001, M1-DESIGN-001, M1-QA-001; M2-MAPS-001 and M2-CONTACT-001 remain TODO.

## ADR-024 — Two-axis Battery, square Music, fitted text and SOS grids

- Date/status: 2026-11-19 / Accepted; the user specified the size and text behavior and asked for implementation. Supersedes ADR-016's full-width portrait exception and ADR-023's Battery bounds and width-only SOS layout.
- Context: Battery could only stretch horizontally, Music rejected squares, fixed-line text shrank or ellipsized, and SOS could only render one row. The first fitting pass allowed text as small as 8 sp, which the user found too small.
- Options: retain fixed layouts and truncate; add separate layouts for every size; use bounded grid rules, orientation-aware Battery drawing, a shared measured wrapping text component, and a two-dimensional SOS tile solver.
- Decision/rationale: Battery is valid only as 2×N or N×2 with N≥4 and device-bounded length; its default is 6×2. Music is valid from 5×4 through 12×12 whenever width≥height, so 5×5 and 12×12 squares work and portrait sizes fail. Shared widget text measures its actual bounds, wraps, scales line height with font size, prefers at least 14 sp (or a widget-specific floor) and drops below only if the text otherwise overflows. Battery percentage has a larger floor and reduced insets; Music metadata can grow to 28/22 sp; Maps no longer caps its title by a width fraction. SOS chooses up to six square mock contacts using width and height, a 90 dp preferred minimum tile, and even row/column spacing.
- Tradeoffs/consequences: Very small rectangles or unusually large system font scales can still require emergency smaller text; visual acceptance is required. Saved Battery 6×3 or Music portrait placements stay in Room and show resize guidance until repaired. A 12-column Music widget cannot exceed 12 rows under the no-portrait rule even if the device grid is taller. SOS may show fewer contacts when its bounds cannot fit the preferred tile size. No new dependency, permission, schema or native integration is added.
- Affected files: `WidgetRegistry.kt`, `feature/widgets/{AdaptiveWidgetText,BatteryWidget,MusicWidget,MapsShortcutWidget,SosFavoritesWidget,ClockWidget,VolumeWidget,WidgetCard}.kt`, `RideUiTokens.kt`, `GridLayoutTest.kt`, catalog/design config, docs and M1 task.
- PRD IDs: M1-WIDGET-001, M1-PAGE-001, M1-DESIGN-001, M1-QA-001; M2 native widget sources remain planned.

## ADR-025 — Exact editor and Ride geometry with compact add and worked-widget polish

- Date/status: 2026-11-19 / Accepted; user delegated this M1 visual and interaction refinement. Supersedes ADR-012's single-widget full-canvas rule, ADR-023/024's Battery 6×2 default and fixed 90 dp SOS one-row threshold, and ADR-022's transparent Clock treatment.
- Context: Saved page geometry differed when Ride expanded a lone widget. Default-sized additions could fail in smaller open spaces. The selected-widget menu occupied the top of the canvas, edge resize was incomplete, and several worked-on widget alignments were off.
- Options: keep existing editor and Ride exceptions; or render exact saved rectangles in both modes, place the smallest valid registered shape, and refine only the already redesigned widgets.
- Decision/rationale: Use the same grid rectangles and widget renderer in editor preview and Ride, add the smallest valid shape that fits, draw a dotted editor grid, put eight resize handles on a rounded selection border, and move selected-widget options to the bottom rail. Use the existing wide Roboto Flex axes for body text at weight 400/grade 0; center Music details, Clock weather and Maps content; give Clock a rounded surface; set Battery default to 4×2 and show a charging icon from the battery broadcast; allow SOS one-row tiles to size to available height; make Ride page names clickable.
- Tradeoffs/consequences: A lone widget no longer fills a Ride page unless its saved rectangle does. Very small widget shapes may need later manual visual adjustment, and eight resize gesture zones can crowd a minimum-size selection. No M2 native action, new permission, dependency or schema is added. User device UI acceptance remains open.
- Affected files: editor, repository add flow, `FixedRideCanvas.kt`, `RideDashboard.kt`, typography/tokens, Music/Clock/Maps/Battery/SOS renderers, widget/design config, tests and M1 docs.
- PRD IDs: M1-PAGE-001, M1-RIDE-001, M1-WIDGET-001, M1-DESIGN-001, M1-QA-001.

## ADR-026 — Tonal M1 screen and pending-widget refresh

- Date/status: 2026-11-19 / Accepted; user delegated a Material 3 Expressive visual refresh of Home, other app pages and the remaining widget previews.
- Context: Home and secondary screens repeated tall generic cards, Settings presented loose switches, and four registered widget types still used the same placeholder card. Music title and artist spread far apart in large widgets because each occupied half the metadata area.
- Options: make only copy/spacing edits, or build a shared tonal screen language and responsive previews while preserving the existing M1 data and action boundaries.
- Decision/rationale: Use a primary-container Home hero, compact tonal navigation cards, consistent screen header, grouped Settings controls with selected delay chips, shared tonal editor/Ride rails, and four registered compact/expanded preview variants for Calls, Notifications, Turn guidance and Assistant. Keep every unavailable integration labeled and noninteractive. Bound Music metadata line height and center the two lines with a 4 dp gap. Use existing Material 3 Expressive theme, dynamic colors, Roboto Flex and Phosphor icons.
- Tradeoffs/consequences: At very small grid sizes the previews show only icon, title and status; longer explanation appears in larger shapes. A 1.5× font scale and physical dark/light appearance still need user device review. No dependency, permission, database schema or M2/M3 integration changes.
- Affected files: `core/ui/components/RideComponents.kt`, `RideUiTokens.kt`, Home/Settings/onboarding/permissions screens, editor and Ride rails, `MusicWidget.kt`, `PlannedWidgets.kt`, `WidgetCard.kt`, design config and M1 docs.
- PRD IDs: M1-APP-001, M1-WIDGET-001, M1-DESIGN-001, M1-QA-001.

## ADR-027 — Expressive top bars, distinct option icons and connected idle choice

- Date/status: 2026-11-19 / Accepted; user directed use of applicable refreshed Material 3 Expressive components and screen layout.
- Context: Non-Ride/editor page titles sat below a decorative row, Getting Started duplicated Back with Return home, Home reused a gear icon, and Settings reused the same gear for unrelated controls. Idle delay used separate FilterChips rather than the connected single-choice style available in the pinned Material 3 library.
- Options: keep the custom header and chip rows; use the existing library's top app bar, tonal icon buttons, and connected toggle buttons while preserving navigation and DataStore callbacks.
- Decision/rationale: Place every non-Ride/editor screen title in a `TopAppBar`; secondary pages show `FilledTonalIconButton` Back beside the title. Remove the redundant Getting Started Return home button. Give each Home and Settings option a distinct Phosphor icon and vary primary/secondary/tertiary icon-container tones. Use connected expressive `ToggleButton` shapes with radio semantics for the selected OLED idle delay. Keep other expressive components only where their interaction model fits; no FAB menu, slider, navigation rail or split button is needed for these screens.
- Tradeoffs/consequences: Five delay choices may wrap on narrow phones and need user visual/TalkBack review. The top app bar reduces vertical content space but keeps navigation and title visible while scrolling. No dependency, permission, persistence schema or M2/M3 integration changes.
- Affected files: `core/ui/components/RideComponents.kt`, Home/Settings/onboarding/permissions screens, `strings.xml`, README, PRD, design/architecture/roadmap/traceability/session docs.
- PRD IDs: M1-APP-001, M1-DESIGN-001, M1-QA-001.

## ADR-028 — Compact Back visual and non-Ride spacing

- Date/status: 2026-11-19 / Accepted; user corrected the new non-Ride/editor app-bar Back size and screen density. Supersedes ADR-027's full-size filled tonal Back visual only.
- Context: The 56 dp filled Back circle touched the leading edge and looked oversized. Shared 24 dp screen padding plus 20 dp card padding, a 48 dp icon container and 14 dp row gap left too little horizontal room on phones.
- Options: shrink the entire Back hit area and globally reduce spacing, or preserve the 56 dp hit area while drawing a smaller inset visual and use non-Ride-specific spacing tokens.
- Decision/rationale: Keep a 56 dp Material 3 `IconButton` hit area, offset it 8 dp from the leading edge, and draw a 40 dp tonal circle with a 20 dp arrow inside. Use 16 dp horizontal/12 dp vertical screen insets, 12 dp section/card padding, 10 dp card icon gap, 40 dp card icons, and 8 dp Settings-toggle inset/gap. Apply these only to non-Ride/editor screen components and Home/Settings containers.
- Tradeoffs/consequences: Denser text may still wrap at large font scales; user visual and touch review remains required. Ride widgets and editor spacing stay on their established tokens. No dependency, permission, data schema or architecture change.
- Affected files: `RideComponents.kt`, `RideUiTokens.kt`, `HomeScreen.kt`, `SettingsScreen.kt`, `config/design-tokens.json` and M1 docs.
- PRD IDs: M1-APP-001, M1-DESIGN-001, M1-QA-001.

## ADR-029 — Typed M1 presentation states for the four deferred widgets

- Date/status: 2026-11-19 / Accepted; the user delegated preparation of the undesigned widgets for later integrations.
- Context: Calls, Notifications, Turn guidance and Assistant shared one generic preview renderer. It had no content-state inputs for M2/M3 and did not visually distinguish their expanded layouts enough.
- Options: retain the generic preview and replace it during integration; or give each registered type a distinct renderer with typed unavailable, empty and content states now.
- Decision/rationale: Route the four registry IDs to distinct compact/expanded Compose renderers. Reuse Material 3 dynamic tones, Phosphor icons and shared spacing/corner tokens. Calls gets a contact-initial tile; Notifications an inset alert sheet; Turn guidance a navigation tile; Assistant a microphone tile. Give each a typed presentation state and an optional `WidgetCard` content input, defaulting to unavailable or visibly labeled mock. Current guidance shows maneuver and distance only when an adapter supplies Current; stale and unavailable states clear that content. No action or native source is added in M1.
- Tradeoffs/consequences: The state types are renderer inputs, not complete M2 repositories or permission flows; M2 adapters must still validate freshness, grants, redaction and policy before supplying content. The smallest grid sizes show a compact title/summary/status layout; device visual review is open.
- Affected files: `feature/widgets/{PlannedWidgets,WidgetCard}.kt`, `RideUiTokens.kt`, design config, widget/design/architecture docs and M1 tracking docs.
- PRD IDs: M1-WIDGET-001, M1-DESIGN-001, M1-QA-001; M2-CALLS-001, M2-NOTIFY-001, M2-TURN-001 and M3-ASR-001 remain planned.

## ADR-030 — Filled Phosphor icons across the app

- Date/status: 2026-11-19 / Accepted; the user explicitly requested solid filled Phosphor icons throughout the app.
- Context: Home, secondary screens, editor, Ride and most widgets used Phosphor Bold outline-style vectors, while Maps already used Fill. The mixed icon weights weakened the visual consistency of the Material 3 Expressive UI.
- Options: keep mixed Bold/Fill icon families; replace every in-app Phosphor Bold reference with its Fill counterpart from the pinned library.
- Decision/rationale: Use `PhosphorIcons.Fill` for all Compose UI icons, retaining each icon's meaning, color, size, content description and click behavior. The existing Android notification small icon is already a solid monochrome vector and remains suitable for system rendering.
- Tradeoffs/consequences: Some filled glyphs have more visual mass and need user review at small sizes and in dark/light palettes. No dependency, permission, widget registry, native integration or navigation behavior changes.
- Affected files: shared UI components, Home/Settings/onboarding/permissions, editor, Ride, Music/Clock/Battery/deferred widgets, design config and M1 docs.
- PRD IDs: M1-APP-001, M1-WIDGET-001, M1-DESIGN-001, M1-QA-001.

## ADR-031 — Fast Ride startup transition and two stroke-icon exceptions

- Date/status: 2026-11-19 / Accepted; the user specified the startup sequence and X/Plus stroke icons.
- Context: Ride opened directly on the dashboard, with no startup visual. ADR-030 made all Compose icons filled, but the user prefers normal stroke X and Plus controls.
- Options: animate on every RideActivity creation or only a fresh Ride start; keep the intro always on or add a persisted switch; use a small icon transition or an oversized motorcycle sweep.
- Decision/rationale: On a fresh Ride start, show centered `RIDE HARD`, transition to `RIDE SAFE`, and keep `ALWAYS WEAR HELMET WHEN HITTING THE ROAD` underneath. Sweep a Phosphor Fill Motorcycle larger than the viewport from fully outside the left edge to fully outside the right edge, revealing the ready dashboard underneath. Use a roughly 1.6-second sequence with a 620 ms bike sweep and a persistent default-on Settings switch. Skip the intro when the switch is off, Reduce motion is enabled, an active Ride is reopened, PiP begins, or the Activity is recreated/interrupted. Keep X and Plus as Phosphor Regular stroke icons while other in-app icons remain Fill. This supersedes ADR-030 only for X and Plus.
- Tradeoffs/consequences: The launch waits for a DataStore read before choosing the intro, causing a brief dark frame on slow storage. The oversized vector and timing need user device review for smoothness, clipping, screen sizes, light/dark contrast and accessibility. No service, dependency, permission or Room schema changes.
- Affected files: `RideActivity.kt`, `RideStartupAnimation.kt`, `RideRepository.kt`, `RideViewModel.kt`, `SettingsScreen.kt`, `RideDashboard.kt`, editor controls, strings, tokens, defaults and M1 docs.
- PRD IDs: M1-APP-001, M1-RIDE-001, M1-PERSIST-001, M1-DESIGN-001, M1-QA-001.

## ADR-032 — Three-second Ride intro and larger bike sweep

- Date/status: 2026-11-19 / Accepted; the user corrected the initial Ride intro pacing and bike scale.
- Context: ADR-031's approximately 1.6-second startup and 1.35× viewport-width motorcycle felt too fast and small.
- Options: lengthen only the stationary text holds; or distribute about three seconds across both title states and the bike sweep while scaling the bike 2.4×.
- Decision/rationale: Set Hard hold to 650 ms, text transition to 300 ms, Safe hold to 750 ms and bike sweep to 1300 ms, totaling 3000 ms. Increase the filled motorcycle from 1.35× to 3.24× viewport width (exactly 2.4× its previous size) and extend its offscreen travel to 1.8× viewport width on either side. Retain the smooth cubic curve and dashboard reveal. This supersedes ADR-031's durations, bike size and travel only.
- Tradeoffs/consequences: The large vector may cost more rendering time on some devices and needs user timing/sharpness review. Skip rules, Settings preference, text, icon exceptions and M1 scope remain as in ADR-031.
- Affected files: `RideUiTokens.kt`, design config, Ride intro docs and M1 tracking docs.
- PRD IDs: M1-RIDE-001, M1-DESIGN-001, M1-QA-001.

## ADR-033 — Direct page management, selectable OLED layouts and SOS M2 boundary

- Date/status: 2026-11-19 / Accepted; the user specified all three changes and delegated M1 completion before M2.
- Context: Pages could be added/deleted/renamed through a selected-page action menu, but could only be reordered by one-step actions. OLED had one vertical style. Earlier M1 planning still listed generic per-widget settings and mock SOS customization, although real SOS contact selection needs Android contact integration.
- Options: retain the action menu and one saver; add a page-management sheet with direct row actions and drag ordering, persist a Classic/Row saver choice, and move SOS contact customization to M2 with the native picker.
- Decision/rationale: Keep the existing page action menu as an accessibility fallback and expose a page sheet with add/open/rename/delete and hold-to-drag ordering. Persist the final page order transactionally in Room. Every page name remains editable. Keep the existing OLED layout as default Classic; persist a Row option in DataStore. Row places hours above minutes and day/date at left, with three chunky containers for visibly mock song, visibly mock weather and local battery at right. The battery icon reflects charge status when Android reports it. The first-touch wake and brightness behavior is shared. Defer SOS contact selection, available photos and favorite editing to M2; other per-widget customization is not an M1 gate.
- Tradeoffs/consequences: Drag ordering and Row typography need user device and TalkBack acceptance; the existing one-step ordering remains available. M1 song/weather remain mock and battery may show Unavailable. M2 must design contact picker and persistence before adding actual SOS actions. No new dependency, permission, Room schema or native contact access is introduced in M1.
- Affected files: `LandscapePageEditorScreen.kt`, `RideRepository.kt`, `RideViewModel.kt`, `OledSaverContent.kt`, `RideDashboard.kt`, `SettingsScreen.kt`, `RideUiTokens.kt`, config defaults/tokens, M1 task and tracking/spec docs.
- PRD IDs: M1-PAGE-001, M1-PERSIST-001, M1-OLED-001, M1-DESIGN-001, M1-QA-001 and M2-CONTACT-001. Supersedes ADR-012's M1 SOS mock-customization scope only and extends ADR-014's single OLED presentation with an optional second style.

## ADR-034 — Larger monochrome Row saver

- Date/status: 2026-11-19 / Accepted; the user corrected the new Row saver after ADR-033.
- Context: The first Row layout used a modest stacked clock, translucent white card fills, category labels and content aligned from the left. The user requires larger numbers and a strictly black-and-white OLED presentation.
- Options: retain tinted cards and labels; or enlarge the clock while fitting landscape height, draw white strokes around black panels and center only the values/icons.
- Decision/rationale: Scale the hour/minute numerals to 37% of available Row height, bounded 60–144 sp with font-scale compensation to fit two lines. Use 2 dp white strokes and black interiors. Remove song/weather/battery category labels; center each panel's content on both axes. Keep inline `mock` on song/weather values for truthful M1 data. Use white text/icons and a white Row wake hint; Classic keeps its earlier treatment.
- Tradeoffs/consequences: The large numerals and centered panels need device checks at compact landscape heights and large system font sizes. The Row has more lit white pixels than the Classic saver, so actual power savings require device measurement; it still uses black backgrounds and no color accents. No dependency, permission, persistence schema or native integration changes.
- Affected files: `OledSaverContent.kt`, `RideUiTokens.kt`, `config/design-tokens.json`, README, PRD, DESIGN_SYSTEM, ARCHITECTURE, WIDGET_CATALOG, ROADMAP, TRACEABILITY, SESSION_STATE and M1 task.
- PRD IDs: M1-OLED-001, M1-DESIGN-001, M1-QA-001. Supersedes ADR-033's Row visual details only.

## ADR-035 — Row saver outline, spacing and icons

- Date/status: 2026-11-19 / Accepted; the user further corrected the Row style.
- Context: The new clock ignored the existing Outline clock choice, the three right panels were too close and had a 2 dp stroke, and song/weather lost their icons. The clock needed a heavier shape and less vertical space between hour and minute.
- Options: keep a fixed solid clock and text-only panels; or share the Outline setting, use a heavier Roboto Flex instance, enlarge panel gaps, thin their strokes and restore monochrome icons.
- Decision/rationale: The Row clock honors `outlineText`, uses Roboto Flex weight 1000 and grade 150, and overlaps the hour/minute line arrangement by 12 dp. The bundled font's `GRAD` axis tops out at 150, so weight supplies the additional heaviness. Panels have 20 dp gaps and 1 dp white strokes. White MusicNotes and CloudSun icons sit above centered song and weather values. All Row content remains white on black.
- Tradeoffs/consequences: A heavier outlined clock can look different from the Classic outline and needs device review at normal and large font scales. Wide panel gaps reduce available panel height, so compact landscape checks remain required. No dependency, permission, schema or native integration changes.
- Affected files: `RideTypography.kt`, `RideUiTokens.kt`, `OledSaverContent.kt`, design config, README, PRD, DESIGN_SYSTEM, ARCHITECTURE, WIDGET_CATALOG, ROADMAP, SESSION_STATE and M1 task.
- PRD IDs: M1-OLED-001, M1-DESIGN-001, M1-QA-001. Supersedes ADR-034's Row stroke and panel spacing and extends its clock and icon rules.

## ADR-036 — Saver fade and live music session adapter

- Date/status: 2026-11-19 / Accepted; user explicitly requested both the transition and complete M2 music widget.
- Context: The saver appeared/disappeared instantly. Music cards and both saver layouts still showed sample tracks despite M2's requirement for active sessions, artwork, progress and transport controls. M1 device acceptance is open, but the user explicitly directed starting M2 now.
- Options: keep the abrupt saver and mock tracks; or fade the existing touch-consuming saver and connect a user-granted Android media-session source to the shared music renderer.
- Decision/rationale: Fade the saver in and out over 350 ms, or immediately with Reduce motion. Use an enabled `NotificationListenerService` component only to authorize `MediaSessionManager.getActiveSessions`; the service does not inspect notification contents. A singleton repository selects the playing session first, otherwise a session with metadata, and exposes current metadata, artwork, bounded progress and advertised actions. The shared Ride/editor widget and both OLED styles display that state. Ride controls dispatch only advertised previous/play-pause/next actions; the editor remains a noninteractive preview. Settings opens Android's notification-access screen and explains its scope. This starts M2-MEDIA-001 while M1 acceptance stays open.
- Tradeoffs/consequences: Notification access can expose notification contents at the OS grant level even though this service reads only sessions. Some apps omit metadata, artwork, duration or actions, so the widget shows explicit fallbacks and disabled controls. Android may disconnect a listener or revoke access; state clears or becomes unavailable. Device checks with supported apps, revocation and saver touch timing are needed before acceptance.
- Affected files: `RideDashboard.kt`, `RideUiTokens.kt`, `OledSaverContent.kt`, `MusicWidget.kt`, `MediaSessionRepository.kt`, `RideMediaListenerService.kt`, `RideViewModel.kt`, shared widget/editor/Ride wiring, `SettingsScreen.kt`, `PermissionInfoScreen.kt`, manifest, tests, README, PRD, ROADMAP, SESSION_STATE, TRACEABILITY, ARCHITECTURE, ANDROID_CAPABILITIES, DESIGN_SYSTEM, WIDGET_CATALOG and design config.
- PRD IDs: M1-OLED-001, M1-DESIGN-001, M1-QA-001, M2-MEDIA-001, M2-QA-001.

## ADR-037 — Music-stream volume source and route refresh

- Date/status: 2026-11-19 / Accepted; the user requested complete M2 volume implementation.
- Context: The existing volume widget changed only a local mock value. M2-VOLUME-001 requires actual system bounds and updates, including route changes.
- Options: Keep a per-widget mock; observe Android's music stream in one app-process repository; or add a background service. The repository avoids divergent values across widgets and needs no service.
- Decision/rationale: Read and set `AudioManager.STREAM_MUSIC` through a singleton `VolumeRepository`. Clamp to Android's reported minimum/maximum, refresh after writes, observe system settings for external volume changes, and refresh when audio devices are added or removed and when Ride/editor resumes. Route selection remains Android-owned. Ride/editor hardware volume keys target the music stream. The editor canvas is read-only; its preview uses the shared live renderer.
- Tradeoffs/consequences: Volume steps are device and route dependent. The system may reject a write or report no adjustable range; the UI shows unavailable or the returned actual level. Route, Bluetooth, key and call-interruption behavior need real-device verification. No permission, dependency, schema or service is added.
- Affected files: `data/VolumeRepository.kt`, `feature/widgets/{VolumeWidget,WidgetCard}.kt`, Ride/editor wiring and activities, `VolumeStateTest.kt`, M2 tracking and capability docs.
- PRD IDs: M2-VOLUME-001, M2-QA-001, M1-WIDGET-001.

## ADR-038 — Foreground approximate-location weather with bounded retries

- Date/status: 2026-11-19 / Accepted; user requested the complete weather source, offline/retry/location-change behavior and a specific failure state.
- Context: Clock and OLED used fixed mock weather. M2-WEATHER-001 calls for an optional provider/cache, no mandatory paid service, truthful offline behavior and low power use. The repository has no weather provider or location permission yet.
- Options: Keep a mock; require manual city entry; or use approximate foreground location and a free weather provider with a local cache. The latter meets automatic location-change behavior without background tracking.
- Decision/rationale: Request only `ACCESS_COARSE_LOCATION` from Settings. While Ride/editor is resumed, use Android's network location provider with a 15-minute/10-km minimum, a recent last-known fix and a 20-second initial wait. Quantize coordinates to 0.1° before HTTPS requests to Open-Meteo current temperature/WMO code. Share one repository across widgets and saver. Refresh at most every 30 minutes at a stable location, or after movement of 10 km. Retain one cached rounded-location result for at most two hours; show its age when old. Retry failures after 1, 5, 15 and then 30 minutes, with a network-return wakeup. Stop location and network callbacks on Activity pause. If no usable reading remains, show exactly `Something's not right` with the filled CloudLightning icon. No background location or service.
- Tradeoffs/consequences: Network approximate location can be unavailable, delayed or inaccurate. The free Open-Meteo endpoint is limited to non-commercial use and requires attribution; Settings credits it. Commercial release requires a licensed source or a new decision. Weather conditions may lag the provider. Three normal/network/location permissions are added; coarse location requires a user grant. HTTP and UI behavior need real-device online/offline/permission/route tests.
- Affected files: `data/WeatherRepository.kt`, `feature/widgets/{WeatherPresentation,ClockWidget,WidgetCard}.kt`, Ride/editor/OLED wiring, Settings/permission screens, manifest, tests and tracking docs.
- PRD IDs: M2-WEATHER-001, M2-QA-001, M1-WIDGET-001, M1-OLED-001.

## ADR-039 — Private-APK system Recents and bounded notification widget feed

- Date/status: 2026-11-19 / Accepted; user requested last-five system calls and last-ten prioritized notifications, then confirmed private-APK distribution now and a possible Google Play release later.
- Context: Ridemode has a notification listener for music but no notification feed or call history. Android marks `READ_CALL_LOG` hard restricted; Google Play generally allows it only for a default Phone or Assistant handler. The user declined an app-maintained call record and accepts an unavailable state if the permission cannot be granted.
- Options: App-maintained calls only; system dialer handoff only; or a private-APK permission-gated system Recents widget. For notifications, persist a history or show a bounded view of current active notifications from the existing listener.
- Decision/rationale: The private APK declares `READ_CALL_LOG` and reads the newest five system records only while Ride/editor is visible. It stores no separate history; permission denial/restriction shows a clear unavailable state. A call action requires a confirmation and `CALL_PHONE`; emergency numbers open the system dialer, and hidden/nonstandard numbers have no direct-call action. The existing listener now also reads current notification content into a bounded in-memory feed. Messages sort above other alerts; other alerts require high Android importance. Ten visible entries show avatar or initials, sender/title and a two-line message; one nested scroll area contains the rows. Removed/revoked entries are discarded. A persisted Privacy mode switch hides names and text in Ride/editor widgets. No new listener service or persistent notification text store.
- Tradeoffs/consequences: A standard private installer may still refuse the hard-restricted call-log grant. The manifest is not Google Play eligible in this form; before a Play release, remove or replace call-log access or implement a genuine default-handler role with a new decision. Some apps omit sender/photo/text or mark content secret; the UI falls back or redacts. Notification-listener access now reads notification contents, so prior music-only privacy explanations must change. Real-device grant, ranking, revocation, call and scroll checks remain.
- Affected files: manifest; `data/{CallHistoryRepository,NotificationFeedRepository}.kt`; shared listener; Calls/Notifications widgets, Ride/editor/ViewModel/Settings/Access wiring; tests and tracking docs.
- PRD IDs: M2-CALLS-001, M2-NOTIFY-001, M2-QA-001, M1-WIDGET-001.

## ADR-040 — Four-by-five minimum Calls and Notifications list geometry

- Date/status: 2026-11-19 / Accepted; user explicitly changed both list-widget bounds.
- Context: The new scrollable Calls and Notifications widgets were still registered at 3×2 minimum, 6×3 default and 12×6 maximum, leaving too little room for multiple rows and limiting full-height layouts.
- Options: Keep compact bounds; or require at least four columns and five rows, with twelve-column/device-row maximum.
- Decision/rationale: Both registry entries and JSON catalog use 4×5 minimum, 6×5 default and 12×24 catalog maximum, with actual height clamped to available device rows by the editor/repository. New templates use 6×5; existing saved smaller instances stay in place and display a resize prompt rather than being silently altered.
- Tradeoffs/consequences: A compact device with fewer than five usable rows cannot place these widgets. Previously saved 3×2/6×3 instances need user resize. The larger default consumes more page space but gives a usable child scroll area. No permission, dependency or schema change.
- Affected files: `WidgetRegistry.kt`, `config/widget-catalog.json`, `WidgetCard.kt`, `GridLayoutTest.kt`, catalog/design/tracking docs.
- PRD IDs: M1-WIDGET-001, M1-PAGE-001, M2-CALLS-001, M2-NOTIFY-001.

## ADR-041 — Even row counts for the device-sized Ride grid

- Date/status: 2026-11-19 / Accepted; user explicitly requested even cell counts on both axes and upward rounding when a device-derived count is odd.
- Context: The 12-column grid already has an even horizontal count, but `RideGridConstraints` truncated the available landscape height to an odd number such as seven rows. Calls/Notifications need at least five rows, and an odd row count made balanced layouts harder.
- Options: Keep the device-derived count; round odd row counts down; round odd row counts up while fitting the fixed viewport.
- Decision/rationale: Keep twelve columns and round the device-derived row count up to the next even integer, with a four-row minimum. The shared `maxRows` value feeds repository placement/bounds and both editor and Ride renderers, which divide the actual viewport by that count. This gives the extra logical row without introducing scrolling or changing saved coordinates.
- Tradeoffs/consequences: On an odd-fit device, each row becomes slightly shorter than the selected target height; some widgets may need more vertical room or a user resize. Existing saved placements remain unchanged. A very small screen may still have fewer than the five rows required by Calls/Notifications. Device visual and touch acceptance remains open.
- Affected files: `core/model/RideGridConstraints.kt`, `GridLayoutTest.kt`, `config/ride-defaults.json`, README, PRD, architecture, design, roadmap and traceability docs.
- PRD IDs: M1-PAGE-001, M1-WIDGET-001, M1-RIDE-001, M1-QA-001.

## ADR-042 — Square grid cells and space-checked widget rotation

- Date/status: 2026-11-19 / Accepted; user explicitly reversed the even-row rule and requested perfect square cells plus a Rotate menu action for widgets supporting both axes. Supersedes ADR-041.
- Context: Rounding seven natural rows to eight shortened each row while the twelve columns still stretched independently. Ride and editor divided the whole canvas by row/column counts, producing rectangles rather than square cells. A direct rotate action was missing from widget options.
- Options: Stretch all cells to fill the viewport; use the full twelve-column width as square side and risk fewer than five rows for Calls/Notifications; or retain the 32/40/48 dp preferred density, fit the maximum whole square rows and center the resulting grid horizontally. For rotation, allow overlap displacement or search an empty placement without moving other widgets.
- Decision/rationale: Derive row count by flooring available canvas height divided by the smaller of preferred cell size and twelve-column width. Odd counts remain valid. Both renderers use one square side for x and y, bounded by actual canvas width/height, with a centered top-aligned grid. Preserve the density preference and saved widget coordinates. Show Rotate in editor options only when the swapped size is catalog-valid and an empty placement exists; use current x/y first, then nearest empty position. Recheck atomically in the repository before saving and never displace a neighbor.
- Tradeoffs/consequences: Square cells can leave unused horizontal or bottom space. On compact screens with fewer than five fitted rows, Calls/Notifications cannot be placed. Existing saved widgets beyond a new device row bound remain stored and require user adjustment. The previous even-count rule no longer applies. Device visual and touch acceptance remains open.
- Affected files: `core/model/{RideGridConstraints,WidgetRegistry}.kt`, `feature/{ride/FixedRideCanvas,pageeditor/EditorCanvas, pageeditor/LandscapePageEditorScreen}.kt`, `data/RideRepository.kt`, `RideViewModel.kt`, `GridLayoutTest.kt`, config and tracking docs.
- PRD IDs: M1-PAGE-001, M1-RIDE-001, M1-WIDGET-001, M1-QA-001.

## ADR-043 — Device-wide square grid with at least seven rows

- Date/status: 2026-11-19 / Accepted; user supplied a screenshot showing wide unused side bands and clarified that the grid may have more than twelve columns while seven square rows are the minimum. Supersedes the twelve-column limit in ADR-042; its space-checked Rotate behavior remains accepted.
- Context: Centering a fixed twelve-column square grid preserves square cells but leaves large parts of a landscape phone unavailable for widget placement. Calls and Notifications need five rows, and the user requires at least seven.
- Options: Stretch twelve cells into rectangles; retain twelve square columns and blank bands; or derive both row and column counts from the device canvas while retaining a seven-row and twelve-column minimum.
- Decision/rationale: Use the smaller of the selected 32/40/48 dp preferred square side, canvas width divided by twelve, and canvas height divided by seven. Fit whole rows and columns from that side; both counts may grow and either may be odd. Repository placement/bounds, editor dots/drag/resize, Ride rendering, rotation, duplication and density-change checks use device-derived counts. Preserve saved widget coordinates and IDs. Calls, Notifications and Volume may expand to the full device width, with `Int.MAX_VALUE` as their catalog width sentinel and actual bounds clamped by the device grid.
- Tradeoffs/consequences: Existing saved widgets retain their x positions; on an expanded grid they may appear farther left until moved in the editor. A small device reduces square side to retain seven rows and twelve columns, which may affect readability; device checks remain. A catalog sentinel requires validator support. No permission, dependency or database schema change.
- Affected files: `core/model/{RideGridConstraints,GridLayout,WidgetRegistry}.kt`, `data/RideRepository.kt`, `feature/{ride, pageeditor}` grid wiring, tests, `config/widget-catalog.json`, docs validator, PRD and tracking docs.
- PRD IDs: M1-PAGE-001, M1-RIDE-001, M1-WIDGET-001, M1-QA-001, M2-CALLS-001, M2-NOTIFY-001, M2-VOLUME-001.

## ADR-044 — Pixel 8a six-row square-cell baseline and saved-widget fitting

- Date/status: 2026-11-19 / Accepted; user rejected the seven-row cell size, requested six rows on their Pixel 8a, an equal-or-larger square-cell baseline on larger screens, automatic columns and resilient widget resizing. Supersedes the seven-row minimum and 32/40/48 dp choices in ADR-043; its dynamic columns and Rotate behavior remain.
- Context: The 40 dp preference yielded seven rows and visually small widgets. Increasing cell size reduces the Pixel 8a canvas to six rows, so existing seven-row widgets may overflow. A fixed cell size also needs a small-screen fallback.
- Options: keep seven rows; stretch cells; use a six-row reference size with a lower bound on larger screens and scale down only when the viewport cannot fit six rows/twelve columns. For old placements, leave every overflow untouched or fit valid widgets while preserving IDs and leaving unfit records available for manual repair.
- Decision/rationale: Use 52 dp as the preferred and minimum cell side on screens large enough for it, calibrated from the connected Pixel 8a 1080×2400 px at 420 dpi and the app's 100 dp Ride chrome. Offer 52/60/68 dp density choices; prior 32/40/48 dp stored values fall back to 52 without deleting the stored key. Constrain the side by canvas height/six and width/twelve on smaller screens, then fit whole square rows/columns. At startup, transactionally fit only saved widgets that overflow current bounds, keeping the largest catalog-valid size and nearest free position; retain ID/config/visibility. If no fit exists, keep the record and offer Fit to grid in the editor. Suppress unsafe resize handles on invalid placements and clamp drag/resize math.
- Tradeoffs/consequences: Larger screens keep cells at least about 52 dp; smaller screens may need smaller cells to retain six rows and twelve columns. Automatic fitting can change size/position of overflowed saved widgets, but does not delete them. Dense pages may still need manual space clearing; device visual/touch and relaunch checks remain open. No permission, dependency or Room schema change.
- Affected files: `core/model/{RideGridConstraints,WidgetRegistry}.kt`, `data/RideRepository.kt`, editor/ViewModel/widget renderer, grid tests, config/defaults, design and tracking docs.
- PRD IDs: M1-PAGE-001, M1-RIDE-001, M1-WIDGET-001, M1-PERSIST-001, M1-QA-001.

## ADR-045 — Conservative Maps notification guidance

- Date/status: 2026-11-19 / Accepted; the user explicitly requested the concrete Turn guidance implementation and delegated presentation choices.
- Context: Google Maps does not expose a public turn-by-turn data API to this app. The existing notification listener can observe user-granted notification text, but Maps fields vary by version, locale and notification style. Old directions could be dangerous if shown as current.
- Options: Keep a mock; parse every Maps notification; or accept only ongoing Maps notifications with a recognizable maneuver and expire them promptly.
- Decision/rationale: Reuse the current listener and an in-memory singleton. Read standard title/text/big-text/subtext/summary/text-lines extras; recognize a bounded set of explicit maneuver phrases, preserve the source instruction, and show available distance/ETA/remaining/destination/raw details. Use a two-minute one-shot expiry, clear on removal/disconnect/revocation, and distinguish access needed, listening, inactive and stale. A tap uses the notification's PendingIntent when present, then opens Maps. The UI uses a prominent direction arrow and compact/expanded layouts.
- Tradeoffs/consequences: Some valid notifications, especially localized or graphic-only directions, will be omitted. Text-derived arrows may be less precise than Maps' own icon. PendingIntent and Maps opening need device verification. There is no polling, storage, new permission, service or dependency. The separate Maps shortcut/handoff task remains TODO.
- Affected files: `data/TurnGuidanceRepository.kt`, `service/RideMediaListenerService.kt`, `feature/widgets/TurnGuidanceWidget.kt`, Ride/editor/ViewModel wiring, UI tokens and tracking docs.
- PRD IDs: M2-TURN-001, M2-QA-001, M1-WIDGET-001.

## ADR-046 — In-app Navigation SDK map and Places search

- Date/status: 2026-11-19 / Proposed for implementation after Google Cloud setup; user specified the product flow and SDK choices, with code changes explicitly deferred.
- Context: The existing Maps shortcut is a mock and Turn guidance reads external Google Maps notifications. The user wants a separate interactive map and full in-app guidance. Navigation SDK replaces Maps SDK for the map layer and navigation; programmatic Places Autocomplete (New) can support the requested inline search and debounce.
- Options: External Google Maps handoff; separate Maps SDK map plus another navigation engine; or one Navigation SDK map with Places SDK (New). The user selected the latter. The separate Maps SDK cannot coexist in the same app with Navigation SDK.
- Proposed decision/rationale: Add a distinct `google_maps` widget at 4×4 minimum and one SDK map/navigation session across compact and expanded presentations. Expand visually over the saved grid, revealing a Grid rail button; never modify other widget positions. Keep search/selection draft separate from active navigation. Debounce programmatic India-limited autocomplete after three characters and one idle second; use session tokens and only needed Place Details fields. Request the route only on Start navigation. Collapse cancels a draft but preserves route-starting/active guidance. Arrival ends guidance and resets the idle map.
- Tradeoffs/consequences: Map tiles, location and autocomplete/selected-place lookups can still use network and billing before Start; only the route calculation is deferred. Navigation requires an appropriate foreground/possibly background location decision and explicit SDK cleanup. API keys must be restricted; billing/quotas apply. Cost, licensing, attribution, lifecycle, route failure and conflict with external-notification Turn guidance need implementation review. No code, dependency, permission or config change is authorized by this documentation run.
- Affected files when approved: widget registry/catalog, Ride/editor renderer and rail, Navigation/Places integration, location/access UI, manifest, build config, tests and tracking docs. This run changes documentation only.
- PRD IDs: M2-NAV-001, M2-MAPS-001, M2-TURN-001, M2-QA-001, M1-WIDGET-001.

## ADR-047 — Isolated Maps widget, point-only confirmation and explicit route start

- Date/status: 2026-11-19 / Accepted; user clarified the M2-NAV-001 contract. Supersedes ADR-046 where it left the Turn widget relationship open or allowed ETA in the new map widget.
- Context: The planned map confirmation had to be limited to start and end points, and the future SDK integration must not change any other widget. The existing Turn guidance widget independently reads external Google Maps notifications.
- Options: Share Navigator events with Turn guidance or keep separate sources; show route estimates before Start or show point confirmation only; couple map expansion to widget state or make it presentation-only.
- Decision/rationale: The new `google_maps` widget owns its own map, Places search, route and navigation state. Confirmation shows only `Your location`, the selected end point and Start navigation. No ETA, route line, distance or route preview appears. Only an explicit Start navigation tap requests a Navigation SDK route. Hide the Navigation SDK ETA card in this widget. The Turn guidance widget and every other widget keep their existing logic, content and sources; none depend on this widget. Expansion only changes visibility of other widgets, never their data or saved placement. The existing Maps shortcut is likewise unchanged by M2-NAV.
- Tradeoffs/consequences: The user cannot review route shape or ETA before the billable request. Places autocomplete and selected-place Details may still have their own charges; deferring the Navigation SDK route request does not defer those. The isolated Turn widget may remain empty while in-app navigation runs, by design. The map widget remains documentation-only until GCP setup and later implementation authorization.
- Affected files: `tasks/M2-NAV.md`, README, PRD, ROADMAP, SESSION_STATE, TRACEABILITY, ARCHITECTURE, DESIGN_SYSTEM, WIDGET_CATALOG and ANDROID_CAPABILITIES. No source/config/dependency/permission change.
- PRD IDs: M2-NAV-001, M2-TURN-001, M1-WIDGET-001, M2-QA-001.

## ADR-048 — SDK versions and isolated Maps lifecycle

- Date/status: 2026-11-19 / Accepted for M2-NAV implementation; user authorized work after adding ignored Android keys. Builds on ADR-047.
- Context: The project uses Kotlin 2.0.21, Room annotation processing and one landscape Ride Activity. Navigation SDK 7.9 and Places SDK 6.0.2 require newer Kotlin metadata and failed the build. A map can resize with the grid while the route request and Navigator must survive presentation changes.
- Options: Upgrade the app compiler/processors for current SDKs; use SDK versions compatible with Kotlin 2.0; or defer implementation. For state, recreate SDK navigation per size, share with Turn guidance, or keep an Activity-owned isolated controller.
- Decision/rationale: Pin Navigation SDK 6.1.0 and Places SDK 4.3.1, which compile with Kotlin 2.0 here; exclude the standalone `play-services-maps` artifact because Navigation SDK contains the same Maps classes. Inject the ignored `.env` keys into the Navigation SDK manifest placeholder and Places BuildConfig at build time. Use an Activity-owned controller per map widget ID, one live NavigationView for compact/expanded sizing, and precise foreground location requested when needed. Route setting is exclusive to Start navigation; Places search/selection uses a fresh token, cancellation and minimal Details. Clean up guidance and listeners on arrival, explicit end and End Ride. Keep the editor preview static and all other widget adapters independent.
- Tradeoffs/consequences: Older pinned SDKs need planned compatibility and security review. Place Autocomplete and Details can incur charges before Start. Foreground-only location may limit guidance after the app leaves the foreground; real-device lifecycle, permission, SDK attribution/licensing and billing must be checked before release. The Turn widget may remain empty during this in-app route because its external notification source is unchanged. No API key is tracked in Git.
- Affected files: `gradle/libs.versions.toml`, `app/build.gradle.kts`, manifest, `GoogleMapsController`, `GoogleMapsWidget`, Ride canvas/dashboard/activity, registry/catalog, UI tokens and tracking docs. `TurnGuidanceWidget` receives only a receiver-scoping compile correction with no behavior change.
- PRD IDs: M2-NAV-001, M2-QA-001, M1-WIDGET-001.

## ADR-049 — Bounded multi-provider map recenter lookup

- Date/status: 2026-11-19 / Accepted; user delegated the Maps location correction after a real-device no-fix report.
- Context: The first implementation always chose GPS when enabled. GPS can return no fix even while Android has a usable fused or network fix, producing a false location-unavailable state.
- Options: Keep GPS-only selection; add a new Play Services location dependency; or use the existing Android LocationManager providers with a bounded fallback.
- Decision/rationale: Check fresh cached fixes across enabled providers, then request one current fix at a time from fused (API 31+), network and GPS. Give each attempt eight seconds, cancel on a fix, Ride pause, map detachment or a new request, and ignore stale callbacks. Show a recent-location label if a cached fix remains usable after refresh fails. The user's recenter action can retry; no background polling, new dependency or permission is needed. Render the idle map label as black filled text with a white outline and no surface.
- Tradeoffs/consequences: Some devices may take up to 24 seconds to exhaust all providers when no fix is available. A recent fix can be up to two minutes old and is labeled as recent if refresh fails. Real-device confirmation and visual acceptance remain open.
- Affected files: `GoogleMapsController.kt`, `GoogleMapsWidget.kt`, `RideActivity.kt`, `RideUiTokens.kt`, README and M2-NAV tracking/design/capability docs.
- PRD IDs: M2-NAV-001, M2-QA-001, M1-DESIGN-001.

## ADR-050 — Active route progress and compact Maps presentation

- Date/status: 2026-11-20 / Accepted; user supplied expanded and compact navigation screenshots and requested live ETA/distance plus legible compact layout. Refines ADR-047's ETA restriction to the pre-start confirmation and supersedes its blanket active ETA hiding.
- Context: The SDK turn header has fixed dimensions and dominates a 4×4 or 6×4 map, while the widget disabled the SDK ETA card and showed no route progress after guidance began.
- Options: Enable the SDK ETA card at every size; leave its header clipped in compact mode; or render active progress from Navigator callbacks and hide the oversized SDK header only when compact.
- Decision/rationale: Keep the selected-destination confirmation point-only and make no route request until Start. After successful routing, subscribe to Navigator remaining-time/distance changes and show a custom progress card with remaining time, distance and arrival time. Do not poll or issue another route request. Keep the SDK turn header visible when expanded, hide it when compact, show a compact end control, and restore follow-camera mode after resizing. Remove the listener on arrival/end. The SDK ETA card remains hidden to avoid a second progress panel.
- Tradeoffs/consequences: Compact mode omits the SDK's full turn banner so the map remains readable; expanded mode retains it. Progress is absent until the SDK reports a valid estimate. Real-device layout, camera follow, arrival timing and callback behavior remain to be checked.
- Affected files: `GoogleMapsController.kt`, `GoogleMapsWidget.kt`, `RideUiTokens.kt`, PRD and M2-NAV tracking/design/capability docs.
- PRD IDs: M2-NAV-001, M2-QA-001, M1-DESIGN-001.

## ADR-051 — Bottom map progress, fixed current origin and destination picker

- Date/status: 2026-11-20 / Accepted; user delegated responsive Maps polish and clarified that From must be non-editable Current location. Supersedes ADR-050's compact progress placement.
- Context: Top overlays obscure compact navigation; the old destination search and stacked confirmation lack clear route hierarchy. Instant grid changes disrupt map continuity.
- Options: Retain top progress and destination-only search; support arbitrary origins as waypoints; or keep the actual current-location origin and rebuild destination selection and compact chrome. The user explicitly ruled out arbitrary navigation origins.
- Decision/rationale: Anchor a single-line progress footer to the bottom at all sizes, with rounded top/square bottom corners, a highlighted remaining-time label, distance/arrival sentence and same-height End action. Reserve SDK bottom padding for that footer. Use Bold Phosphor idle icons in Material 3 Expressive connected controls. Animate saved-to-grid position/size over 280 ms and search field width/map fade over 180 ms; Reduce motion disables these transitions. Search occupies an opaque grid-sized surface while retaining the mounted NavigationView. From is fixed Current location; only To expands and opens the keyboard. Selection or Back restores the paired fields before a separate Review ride confirmation. Choose from map uses a fixed center pin and the camera target's coordinates, avoiding reverse geocoding or extra services. Only Start ride requests a route, using a place ID or selected coordinates.
- Tradeoffs/consequences: Pinned destinations show coordinates rather than a reverse-geocoded name. Native map resize/follow, small-size typography, keyboard dismissal and attribution require device acceptance. No dependencies, permissions, widget sizes or stored layout contracts change.
- Affected files: `GoogleMapsWidget.kt`, `GoogleMapsController.kt`, `FixedRideCanvas.kt`, `RideDashboard.kt`, `RideUiTokens.kt` and Maps requirement/design/tracking docs and proposed token config.
- PRD IDs: M2-NAV-001, M1-DESIGN-001, M2-QA-001.

## ADR-052 — Window-sized Maps search and keyboard inset ownership

- Date/status: 2026-11-20 / Accepted; user delegated correction of the keyboard-obscured destination search. Supersedes ADR-051's grid-sized search surface hosting only.
- Context: Search inherited square-cell canvas bounds and Ride rail spacing, then applied IME padding inside those bounds. Keyboard appearance could contract the canvas and consume the remaining search/results space.
- Options: Compensate manually for the grid/rail/inset geometry; move search to a root dashboard overlay; or use a full-window Compose Dialog with one inset owner.
- Decision/rationale: Host existing search content in a full-window Dialog with platform width limits disabled and edge-to-edge bounds. Its content consumes safeDrawing insets once (including IME, system bars and cutouts). Keep the field above the independently scrollable results, and place Choose from map after predictions so it does not displace matches. Retain the mounted NavigationView underneath and use the existing back/draft state machine. Suspend the saver timer during modal search because its input is outside the Ride canvas pointer observer; restart normal idle timing after search closes.
- Tradeoffs/consequences: Search temporarily covers the Ride rail. Dialog keyboard/back/inset behavior across devices still needs device acceptance. No dependencies, permissions, API requests, persistence or widget sizes change.
- Affected files: `GoogleMapsWidget.kt`, `RideDashboard.kt`, README and Maps design/architecture/tracking docs.
- PRD IDs: M2-NAV-001, M1-DESIGN-001, M2-QA-001.

## ADR-053 — Navigation voice, timed end confirmation and complete idle reset

- Date/status: 2026-11-20 / Accepted; user delegated ongoing navigation voice controls, rounded corners, confirmed stopping and SDK notification cleanup. Supersedes ADR-051's square lower corners and immediate End action.
- Context: The widget explicitly flattened its bottom corners during guidance. End stopped immediately and cleanup exceptions were silently discarded. Idle NavigationView remained navigation-ready and camera/source reset was incomplete. The user also reported GPS loss indoors despite a visible map position.
- Options: Hide the SDK notification; upgrade SDK/dependencies; or use supported SDK 6.1.0 lifecycle and voice APIs with explicit UI states. Hiding a notification would not stop navigation and SDK upgrades exceed this task.
- Decision/rationale: Show a voice menu only in expanded guidance: Mute, All, Alerts only, mapped to SDK audio flags (Bluetooth retained for spoken modes). Show a whole-widget End confirmation while guidance continues; Keep, Back, collapse, pause or a five-second monotonic deadline dismisses it without stopping. Explicit confirmation alone ends a user-started stop; final arrival, permission loss and End Ride clean up directly. Teardown disables navigation UI, removes listeners, attempts stopGuidance → clearDestinations → cleanup even if a step throws, invalidates stale requests and reports failure with Retry end instead of claiming idle. Set SDK task removal to QUIT_SERVICE. Restore default map location source, zero padding and a north-up zero-tilt current-location camera; repeat reset after asynchronous SDK UI changes. Detect SDK-ended guidance on UI changes/resume. Keep widget/footer corners rounded at all sizes. Do not fabricate GPS fixes or suppress a genuine SDK GPS warning.
- Tradeoffs/consequences: A cleanup failure leaves an explicit retry state. The standard Ridemode Ride notification remains until End Ride; only the SDK driving notification should disappear after route end. SDK cleanup/notification removal, voice output/Bluetooth, timer interactions, indoor/outdoor GPS and map camera reset still require real-device acceptance. No dependencies, permissions, stored widget geometry or other widget sources change.
- Affected files: GoogleMapsController, GoogleMapsWidget, MapNavigationCleanup, RideDashboard, MapNavigationCleanupTest, proposed design-token config and Maps requirement/design/tracking docs.
- PRD IDs: M2-NAV-001, M1-DESIGN-001, M2-QA-001.

## ADR-054 — Settled-page map loading and Ride quality-of-life controls

- Date/status: 2026-11-20 / Accepted; user delegated deferred map loading, timed End Ride, optional Maps rail button, larger page targets and concise actionable Access and Privacy.
- Context: Maps was bound to pager.currentPage, which changes during a swipe, so controller switching/native view creation could compete with the animation. End Ride was immediate, page names had text-sized hit areas, the Maps rail action was disabled and permission descriptions were long/non-actionable.
- Options: Recreate maps while swiping; preload every page's map; or defer first creation until pager settles and retain a loaded view while its page remains composed. Use platform settings links instead of unsupported direct permission-list intents.
- Decision/rationale: Use stable pager page IDs, settledPage and isScrollInProgress. Give each composed page a stable lightweight controller; create its NavigationView only after it is settled plus one frame. Show Material 3 CircularWavyProgressIndicator before initialization, during native readiness, and while swiping. Hide an existing native view during motion without destroying its session; restore it at settle. Add a full-screen five-second End Ride confirmation whose timeout/Keep/Back/resume/PiP cancel without ending the ride; only timely confirmation invokes the existing Ride/map cleanup. Share monotonic deadline math with navigation confirmation in core/util. Persist default-on show_maps_shortcut in DataStore; expose Settings and open Google Maps through a package-targeted Maps URL with browser/unavailable fallback. Page names become transparent TextButtons with 56 dp targets. Replace Access content with short cards for implemented capabilities, linking to app details/Permissions, notification settings and notification-listener detail/list with guarded fallback.
- Tradeoffs/consequences: Deferral moves native creation out of a swipe but initial load can still cost frames; actual jank improvement needs device profiling. Maps are retained only while their pager page is composed, not globally cached. Active guidance remains controller/SDK-owned across view replacement. Android may require a second tap from app details to Permissions. No dependencies, permissions or Room schema changes; the separate Maps shortcut widget remains unchanged. Full known-destination handoff/return integration is still a later M2 acceptance item.
- Affected files: RideDashboard, FixedRideCanvas, GoogleMapsWidget, RideRepository, RideViewModel, SettingsScreen, PermissionInfoScreen, ExternalMaps, core/util/ConfirmationTimer, GoogleMapsController/MapNavigationCleanup/test deadline imports, strings, proposed ride defaults and tracking docs.
- PRD IDs: M1-APP-001, M1-RIDE-001, M1-PERSIST-001, M1-DESIGN-001, M1-QA-001, M2-NAV-001, M2-MAPS-001.

## ADR-055 — One-row Clock, adaptive Battery end labels and notification timestamps

- Date/status: 2026-11-20 / Accepted; user delegated these three widget edits and explicitly excluded code for proposed speedometer/lean telemetry widgets.
- Context: Clock required two rows and always displayed weather; Battery moved its name/percentage together rather than placing the value/status at opposite ends; notification posting timestamps existed in the feed but were not rendered.
- Options: Keep the two-row Clock and pixel-based heuristics; relax Clock to one row using saved grid height; group Battery metadata or place percentage/status independently; display posting time or fabricate a missing timestamp.
- Decision/rationale: Register Clock at 2×1 minimum, retain 4×3 default and pass saved grid height to the renderer. One row omits weather; wide one-row cards show time/date beside each other with larger fitting text, narrower ones retain two centered lines. Two or more rows retain weather. Legacy Weather conversion uses the registry minimum without moving saved records. Keep Battery's existing 2×N/N×2 valid-size contract; remove This phone, retain an explicit Mock label only for unavailable real readings, and place percentage at the logical start/bottom and power status at end/top. Fit each independently into filled/empty segments, accounting for icon space, choosing contrast per segment and preserving no-overlap geometry. Notification rows show localized system posting date/time only when postedAtMs is positive, wrapping within the existing list; Privacy mode still hides complete rows.
- Tradeoffs/consequences: Very narrow one-row clocks and battery segments still require measured text fitting; visual/font-scale/charging acceptance needs a device. Timestamps identify system posting time, not a guaranteed original message-send time. No new permissions/dependencies, Room schema change, telemetry source, sensor code or widget types are added. Speedometer and lean telemetry remain assessment-only ideas.
- Affected files: ClockWidget, BatteryWidget, NotificationFeedWidget, WidgetCard, WidgetRegistry, RideRepository legacy conversion, RideUiTokens, GridLayoutTest, BatteryContentPlacementTest, proposed widget/design config and relevant docs/tasks.
- PRD IDs: M1-WIDGET-001, M1-DESIGN-001, M1-PAGE-001, M1-QA-001, M2-NOTIFY-001.

## ADR-056 — Weather starts at three Clock rows

- Date/status: 2026-11-20 / Accepted; user's in-task clarification. Supersedes ADR-055's Clock weather threshold and compact typography only.
- Context: The user clarified that a two-row Clock must also omit weather and enlarge time/day/date.
- Options: Keep weather from two rows or follow the revised three-row threshold.
- Decision/rationale: Show weather only at three or more saved grid rows. Retain the side-by-side wide one-row layout; compact stacked one/two-row layouts use larger time height/width fractions and a larger date scale/cap so both reclaim the weather row's space.
- Tradeoffs/consequences: Narrow widths/font scaling still constrain measured text. No new integration, permission, dependency, stored size or widget type changes; telemetry ideas remain discussion-only.
- Affected files: ClockWidget, RideUiTokens, proposed design tokens, Clock size-test name and current Clock design/catalog/tracking docs.
- PRD IDs: M1-WIDGET-001, M1-DESIGN-001, M1-PAGE-001, M1-QA-001.

## ADR template

`ADR-NNN — Title`; date; status (Proposed/Accepted/Superseded); context; options; decision; rationale; tradeoffs; consequences; affected files; PRD IDs; supersedes, if any.

## ADR-057 — Foreground speedometer with per-tile units

- Date/status: 2026-11-20 / Accepted; user explicitly delegates speedometer implementation, settings and unrestricted device-bounded sizing. Supersedes ADR-055/056's assessment-only boundary for speedometer only.
- Context/options: Speed could be derived from consecutive coordinates, the Navigation SDK, or Android GPS velocity. Choose GPS velocity independently of navigation, using existing precise foreground permission and existing Hilt/StateFlow patterns.
- Decision/rationale: One SpeedRepository request while Ride is resumed and an enabled page contains a visible speed tile; stop outside that lifecycle. Read only speed-bearing fixes, reject invalid/future/older-than-five-second samples and reported speed error above 3 m/s; show unavailable instead of zero. Accept real stationary zero. No coordinates or speed history are stored. Use DataStore km/h default, mph option, and per-WidgetInstance JSON override plus optional status. Register 1×1 minimum, 3×3 default and device-bounded axes without shape restrictions. Editor uses an explicit Preview state.
- Tradeoffs/consequences: GPS costs battery and can fail indoors; this is an informational measurement, with no background tracking, speed limit service or averaging. Unknown speed accuracy is accepted if the provider supplies speed. Expanded/tall cards show status; tiny cards retain accessible status. No new dependency, permission, service or database migration. Device GPS/font-scale/lifecycle acceptance remains open.
- Files/PRD IDs: SpeedRepository, SpeedometerWidget, WidgetConfigs, WidgetRegistry, RideRepository/ViewModel/Activity, Settings/editor, RideUiTokens, config and SpeedAndSosPolicyTest; M2-SPEED-001, M1-WIDGET-001, M1-PERSIST-001, M1-DESIGN-001.

## ADR-058 — Independent SOS contact snapshots per widget

- Date/status: 2026-11-20 / Accepted; user delegates complete SOS widget integration and explicitly requires independent selections on every widget/page.
- Context/options: A global favorites list would couple widgets. A broad contacts read permission would expose unnecessary data. Alternatives are saved provider IDs with ongoing grants or selected phone-row snapshots.
- Decision/rationale: ACTION_PICK with Phone.CONTENT_TYPE reads only the chosen row, then saves up to six validated ordered name/number/optional bounded thumbnail snapshots inside that WidgetInstance.configJson. Existing Room schema and transaction update the exact saved widget ID. Duplicate widgets/pages start from copied JSON; later edits remain independent. Full-window editor supports add/replace, name, photo/initials, reorder and remove; Save persists, Cancel discards. Copy thumbnails on IO into the snapshot, avoiding persistent URI access. Small SOS layouts scroll to keep all contacts reachable. Default confirmed action opens the dialer; per-tile direct calling requests existing CALL_PHONE after confirmation and falls back to dialer on denial. Emergency/unknown emergency classification always uses dialer. No fake contacts.
- Tradeoffs/consequences: Snapshots do not track later address-book changes; reselect to refresh. Names/numbers/thumbnails remain in the private layout database and are removed with the widget; existing OS app-backup policy remains unchanged (no new app cloud sync). Contact/photo availability depends on providers. Normalized plain numbers exclude extensions/MMI. No READ_CONTACTS, new dependency or Room migration. Real-device picker/photo/permission/call acceptance remains open.
- Files/PRD IDs: WidgetConfigs, SosContactReader, SosFavoritesWidget, WidgetSettingsDialog, LandscapePageEditorScreen, RideRepository, CallHistoryWidget, WidgetCard, config, SpeedAndSosPolicyTest and WidgetConfigPersistenceTest; M2-CONTACT-001, M1-PERSIST-001, M1-PAGE-001, M1-WIDGET-001.

## ADR-059 — Glove-friendly confirmations with a draining dismiss button

- Date/status: 2026-11-20 / Accepted; user delegates larger icon-led confirmation controls and five-second automatic dismissal for call, End Ride and navigation stop.
- Context/options: Existing call AlertDialogs use small text buttons; Ride/navigation confirmations show separate countdown labels. Alternatives are individually enlarged dialogs or one shared timed action surface. Choose a shared Compose confirmation/button component using existing tokens, colors and icons.
- Decision/rationale: SOS and Recents calls use a window-sized custom dialog capped at 720 dp; End Ride retains full-screen coverage and navigation stop retains widget coverage. Actions use bold titleMedium labels and icons, 96 dp height at ≥420 dp available content width and 80 dp otherwise. Compact actions stack icon over text. Confirm uses primary/onPrimary for calls and error/onError for ending. Dismiss uses a secondary-container fill that drains continuously from full to empty over five seconds, with a seconds label and accessible state description; no separate countdown bar. Progress derives from a monotonic deadline, with frame-time drawing and derived integer seconds. RTL mirrors the fill. A resolved latch prevents repeated confirm/dismiss actions; expired taps cancel. Existing Ride/controller deadline safeguards remain.
- Tradeoffs/consequences: Call confirmation ends before permission is requested, with the explicitly confirmed number retained separately for the permission result; auto-dismiss never cancels a pending permission flow. Dialer/direct-call/emergency behavior is unchanged. Countdown is functional time feedback, independent of decorative animation settings. Compact widgets still require font-scale/glove/device acceptance. Editor management dialogs and the mock Assistant stepper are outside Ride action confirmation scope. No new permission, dependency, service, database or registry change.
- Files: core/ui/components/RideConfirmation.kt, core/util/ConfirmationTimer.kt, RideUiTokens.kt, SosFavoritesWidget.kt, CallHistoryWidget.kt, RideDashboard.kt, GoogleMapsWidget.kt, GoogleMapsController.kt, MapNavigationCleanupTest.kt, proposed design tokens and related docs/task.
- PRD IDs: M1-RIDE-001, M1-DESIGN-001, M1-QA-001, M2-CONTACT-001, M2-CALLS-001, M2-NAV-001, M2-QA-001. Supersedes ADR-053/054/058 confirmation presentation only.

## ADR-060 — Stationary RIDE word and upward startup reveal

- Date/status: 2026-11-20 / Accepted; user explicitly delegates the replacement Ride intro. Supersedes ADR-031/032's title, layout, motorcycle sweep and timing only.
- Context/options: The previous full-phrase transition and motorcycle sweep move the title and prolong the startup. Alternatives are an AnimatedContent phrase swap or a fixed RIDE plus a fading second word. Choose fixed layout and layer-only fades/reveal.
- Decision/rationale: Use Material titleLarge for both words, with RIDE held stationary in a centered row and a fixed second-word slot measured to the larger of HARD/SAFE. Hold HARD 450 ms, fade out 100 ms, replace with SAFE and fade in 140 ms, hold 600 ms, then translate the entire opaque intro upward over 360 ms using cubic easing (0.2,0,0,1). Total nominal duration 1650 ms. Place the unchanged helmet advice at bottom center in a single line, preceded by a filled Warning icon; measure available safe-area width and shrink only the advice text as needed to preserve the line. The dashboard is already beneath the overlay. Keep full-window touch consumption until completion and announce the current phrase through a polite live region.
- Tradeoffs/consequences: The motorcycle sweep is removed. Very narrow/large-font configurations may need smaller advice text; real-device timing/readability require review. Existing default-on Settings preference, Reduce motion bypass, active-Ride reopen bypass and lifecycle/PiP interruption behavior remain unchanged. No new permission/dependency/widget/schema change.
- Files: RideStartupAnimation.kt, RideUiTokens.kt, strings.xml, config/design-tokens.json and intro requirements/design/tracking/task docs.
- PRD IDs: M1-APP-001, M1-RIDE-001, M1-PERSIST-001, M1-DESIGN-001, M1-QA-001.

## ADR-061 — Longer HARD and SAFE holds

- Date/status: 2026-11-20 / Accepted; user delegates slightly longer visible holds. Supersedes ADR-060 hold durations only.
- Context/options: Keep 450/600 ms holds or extend reading time while preserving quick transitions.
- Decision/rationale: HARD holds 800 ms and SAFE holds 1000 ms; fades remain 100/140 ms and upward reveal 360 ms, total nominal 2400 ms. More reading time without slower motion.
- Tradeoffs/consequences: Intro lasts 750 ms longer; all layout, preference and bypass behavior stays intact. Device pacing review remains open.
- Files/PRD IDs: RideUiTokens.kt, config/design-tokens.json and intro docs/task; M1-RIDE-001, M1-DESIGN-001, M1-QA-001.

## ADR-062 — Stable warning layout through the intro reveal

- Date/status: 2026-11-20 / Accepted; user requests removal of the warning-line jump before the upward reveal. Refines ADR-060 geometry only; ADR-061 timing retained.
- Context/options: The current intro uses visibility-dependent safeDrawing padding. A system-bar visibility change can alter its bottom/side padding during startup. Options are to ignore safety insets, freeze an initial potentially incomplete inset snapshot, or reserve system-bar space independent of visibility. Choose the latter plus a fixed measured warning-text width.
- Decision/rationale: Reserve systemBarsIgnoringVisibility union displayCutout for the intro, keeping safety space stable as immersive bars hide/show. Cache advice measurement for its text/style/density/measurer and give the text an explicit fitted width so the complete icon/text row stays centered with stable geometry across HARD/SAFE and the whole-screen translation. Existing local 70 sp title override is user work and is preserved.
- Tradeoffs/consequences: The intro reserves bar space even when bars are hidden. No viewport/inset freezing across real window changes. Timings, word fade, full-overlay translation, input shield and lifecycle/settings gates stay unchanged. This addresses an identified layout-instability path; actual reported glitch is not reproduced or verified on a real device in this run.
- Files/PRD IDs: RideStartupAnimation.kt, proposed design tokens and intro docs/task; M1-RIDE-001, M1-DESIGN-001, M1-QA-001. No permission/dependency/schema change.

## ADR-063 — Ride-retained navigation ownership and separate guidance/UI/SDK lifetimes

- Date/status: 2026-11-20 / Accepted; user delegates palette/PiP/navigation-stop lifecycle repair. Supersedes ADR-047 Activity-field ownership and ADR-053 end-of-guidance cleanup timing only.
- Context/options: Wallpaper recreation calls unconditional Activity teardown and loses guidance. Two real-device ANRs show a main/NavInternal SDK sensor lock inversion. Options are suppressing configuration recreation with broad manifest flags, or retaining route state while replacing native views. Choose a retained ViewModel with weak Activity bindings; allow theme resources to refresh normally.
- Decision/rationale: RideNavigationViewModel owns per-widget controllers across configuration recreation. Controllers use application context and weak current Activity references; detached maps/views are invalidated before destruction. Native save/configuration/trim callbacks are forwarded. NavigationSdkSession owns the SDK resource without Activity/View references; monotonic ride/route leases prevent competing widgets or stale callbacks from controlling a newer route. End guidance stops/clears and removes listeners while retaining the SDK for idle; cleanup happens only at true Ride end. UI-change callbacks check current view and coalesce a guidance-state check instead of repeated recenter/reset. Remove unsupported custom LocationSource reset. Guard native map handles/permission races and bound automatic recovery to one view rebuild followed by manual Reload map. Preserve the existing late-tap/confirmation behavior and view-only paging/PiP behavior.
- Tradeoffs/consequences: Only one active route is allowed by the singleton SDK; a competing map receives an explicit message. In-memory retention does not recover process death or an OS force stop. Final SDK errors are logged; native 6.1.0 deadlock exposure remains until an approved SDK update. No new manifest permission, dependency version, schema or paid route request. Actual background/palette/stop stress acceptance remains open; no 100% reliability claim.
- Files: RideActivity, RideNavigationViewModel, GoogleMapsController/Widget, NavigationSdkSession, NavigationSessionGate, MapNavigationCleanup, NavigationSessionLifecycleTest, RideNavigationRecreationTest and tasks/M2-NAV-LIFECYCLE.md.
- PRD IDs: M2-NAV-001, M2-QA-001, M1-RIDE-001, M1-RIDE-002.

## ADR-064 — Navigation SDK deadlock-fix upgrade proposal

- Date/status: 2026-11-20 / Proposed; explicit dependency approval required by AGENTS.md rule 2. Not applied.
- Context/options: Two Pixel 8a ANRs show SDK-internal sensor/main/NavInternal lock inversion. Pinned 6.1.0 predates Google's stop/input-dispatch/camera deadlock fixes. Options are retaining 6.1.0 with known risk, conservatively upgrading within 6.x, or taking a broader 7.x/8.x toolchain/API migration.
- Proposed decision/rationale: Navigation SDK 6.3.3 and Kotlin Android/Compose plugin 2.1.21, with all other declared versions/platform levels unchanged. Google requires Kotlin 2.1 for SDK 6.3–7.2; 6.3.3 contains documented stop/input/camera deadlock fixes without an unrelated major SDK migration. Exact catalog diff and verification plan are in tasks/M2-NAV-LIFECYCLE.md.
- Tradeoffs/consequences: Requires dependency resolution/build/real-device stress tests and sufficient local build storage. A documented fix is evidence for an upgrade, not proof that every observed/internal failure is covered. Do not alter these versions before approval. Rollback restores known deadlock exposure.
- Files/PRD IDs: proposed gradle/libs.versions.toml version keys and affected docs/build evidence; M2-NAV-001, M2-QA-001, M1-RIDE-002.
- Sources: Google Navigation Android release notes and Kotlin compatibility/migration references linked in the lifecycle task. Prior accepted dependency ADR-047 remains immutable until an approved superseding decision.

## ADR-065 — Approved SDK 6.3.3 and Kotlin 2.1.21 deadlock-fix update

- Date/status: 2026-11-20 / Accepted; user explicitly selected “Approve SDK and Kotlin update” after reviewing ADR-064's exact catalog proposal. Supersedes ADR-047's SDK/Kotlin pin and adopts ADR-064 without changing its historical Proposed text.
- Context/options: Actual phone ANRs show SDK main/sensor/NavInternal lock inversion. Retaining 6.1.0 leaves documented stop/input/camera deadlock exposure; broad 7.x/8.x migration adds unrelated API/toolchain changes. Choose the conservative 6.3.3 release and required Kotlin 2.1 line.
- Decision/rationale: Change only navigationSdk 6.1.0 → 6.3.3 and kotlin 2.0.21 → 2.1.21 in the version catalog. Both Android and Compose compiler plugin aliases follow that Kotlin key. Keep AGP/Gradle/Places/desugar/platform levels and all other declared dependency versions as before. Rebuild/test/lint both APKs and inspect the resolved dependency graph/merged permissions; run the non-routing real-device recreation regression before claiming ownership validation.
- Tradeoffs/consequences: New artifacts/transitives must resolve and the native SDK still requires device route stress acceptance; documented fixes are not proof of universal reliability. Initial update attempt exhausted local temporary disk space, then an interrupted resource merge required a clean rebuild; only generated app build outputs were cleaned, no user data or global cache removal. Existing SDK/API keys are unchanged. Real route/wallpaper stress remains explicit acceptance work.
- Files/PRD IDs: gradle/libs.versions.toml, lifecycle ownership/source/tests and reconciled docs; M2-NAV-001, M2-QA-001, M1-RIDE-002. Evidence and source links remain in tasks/M2-NAV-LIFECYCLE.md and docs/verification/M2_NAV_ANR_2026-11-20.md.

## ADR-066 — External assistant widget, disabled rail and content-first guidance

- Date/status: 2026-11-20 / Accepted; user delegates these widget/action edits and clarifies Turn guidance at 4–7 columns, minimum 4 rows, device-bounded height.
- Context/options: The assistant rail/widget expose mock states; the editor adds an unused Open Maps preview; guidance uses small glyphs, category labels and restrictive old bounds. Options are expanding the mock command engine or delegating to the configured system assistant; deleting Maps instances or hiding their picker entry; text arrows or actual Bold vectors. Choose native assistant delegation, preserved legacy Maps instances and real-content guidance.
- Decision/rationale: Disable the rail microphone and remove its mock dialog. A dedicated AssistantWidget uses the foreground Activity.showAssist public API to invoke Android's current assistant with no provider package, Ridemode microphone permission or fake listening state; editor is inert, unavailable launch shows feedback. Keep maps_shortcut registered but hidden from the canonical picker list and new/reset default templates. Turn guidance uses surfaceContainerHigh, enlarged Phosphor Bold maneuvers, measured adaptive distance/instruction/details layouts with a readable floor and ellipsis. Register 4×4 min, 7×4 default, width max 7/height device-bounded, as clarified by the user. Only current recognized notification content is displayed; missing/stale/access states show the requested idle prompt. Tap current/idle content opens the reused five-second confirmation, then explicitly launches Google Maps; no browser fallback on this widget. Keep toolbar fallback behavior unchanged.
- Tradeoffs/consequences: Saved legacy Maps/unsupported-size guidance records are not removed; guidance may need manual resize/Fit. Hidden picker IDs are checked against JSON and remain registered. Best-effort English notification parsing and access/expiry policy remain unchanged; removing labels does not make extraction guaranteed. Long text is truncated before becoming illegible. Platform assistant may receive normal assist context and owns its own permissions/network. Local M3 ASR/commands remain planned. No new dependency/permission/service/schema or billing request.
- Files: RideDashboard, AssistantWidget/ExternalAssistant, TurnGuidanceWidget/Presentation, AdaptiveWidgetText, WidgetCard/PlannedWidgets, WidgetRegistry, editor, RideRepository defaults, ExternalMaps, RideUiTokens, proposed config, validator, GuidancePresentationTest and tasks/M2-WIDGET-HANDOFFS.md.
- PRD IDs: M2-TURN-001, M2-ASSIST-001, M1-ASSIST-001, M1-RIDE-001, M1-PAGE-001, M1-WIDGET-001, M1-DESIGN-001, M1-QA-001. Supersedes earlier assistant mock UI and guidance presentation/sizing only; native in-app Maps remains isolated.
- Platform references: [Activity.showAssist](https://developer.android.com/reference/android/app/Activity#showAssist(android.os.Bundle)), [Android assist intents](https://developer.android.com/reference/android/content/Intent#ACTION_ASSIST).


## ADR-067 — Confirmed source-app handoff and widget alignment refinements

- Date/status: 2026-11-20 / Accepted; implementation choices delegated by the explicit widget edit request.
- Context/options: Music artwork/details had no app handoff. Launching whichever session is current after confirmation could open a different provider; launching only package intents misses provider-specific now-playing screens. Assistant centering and bottom-centered guidance arrival are explicitly requested.
- Decision/rationale: Capture the tapped ready media source package/sessionActivity as an immutable in-memory confirmation target. Reuse RideConfirmationDialog and its five-second dismiss/expiry/late-tap guards. On confirm prefer the provider's sessionActivity, then the captured package's launch intent; show unavailable feedback if neither works. Show the provider app label where available, never a package identifier in the dialog. Android 34+ sender options explicitly allow this user-initiated PendingIntent; Android 36 uses ALLOW_IF_VISIBLE. Keep artwork/details as separate targets above independent transport controls; editor and unavailable source states cannot launch. Center the Assistant icon/label group with Open Assistant wrapping to two lines. Separate real ETA from guidance details and pin it at the bottom center, keeping the existing timed Maps confirmation on every live guidance tap.
- Tradeoffs/consequences: The original tapped provider may open if media changes during the countdown; never silently substitute a new provider. Missing/cancelled session intents use a best-effort package launcher, which can be unavailable or restricted by Android. Notification parsing/TTL and SDK navigation remain unchanged; arrival is omitted when absent. Reuse existing typography/spacing and no new permission, dependency, service or schema. Actual provider and visual acceptance remain device work.
- Files: MediaSessionRepository, MusicWidget, ExternalMusicApp, AssistantWidget, TurnGuidanceWidget/Presentation, MediaSessionStateTest, GuidancePresentationTest, tasks/M2-WIDGET-REFINEMENTS.md and reconciled docs.
- PRD IDs: M2-MEDIA-001, M2-TURN-001, M2-ASSIST-001, M1-WIDGET-001, M1-DESIGN-001, M1-QA-001. Refines ADR-066 presentation and ADR-036 media actions without changing their owners.
- References: [MediaController session activity](https://developer.android.com/reference/android/media/session/MediaController#getSessionActivity()), [Android PendingIntent sender requirements](https://developer.android.com/guide/components/activities/secure-bal).


## ADR-068 — Device music app chooser sheet when no music is playing

- Date/status: 2026-11-20 / Accepted; user explicitly requests that tapping artwork or details when no music is playing opens the device music app chooser.
- Context/options: Previously, tapping music artwork or details when no session was active or no provider was recognized had no click action (`enabled = !editor && source != null`). The user requested that clicking either container when no music is playing should present the system "Open with" chooser sheet for all music player apps on the device.
- Decision/rationale: Implement `shouldOpenChooser()` on `MusicSessionState`. When music is actively playing (`playing == true` and `musicAppTarget() != null`), preserve the existing five-second confirmed active provider handoff via `RideConfirmationDialog`. When no music is playing (`!playing` or `musicAppTarget() == null`), immediately launch the system chooser (`Intent.createChooser`) targeting `Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_MUSIC)`. In addition, query installed activities responding to `android.intent.action.MUSIC_PLAYER` and `android.media.action.MEDIA_PLAY_FROM_SEARCH`, appending distinct non-duplicate package launch intents via `Intent.EXTRA_INITIAL_INTENTS`. Declare targeted `<queries>` in `AndroidManifest.xml` for Android 11+ package visibility. Enable artwork and details surfaces whenever `!editor`; editor previews remain inert. If no music applications resolve or launch fails, display feedback via Toast.
- Tradeoffs/consequences: Uses Android's native system chooser sheet without custom dialog overhead. Does not require broad `QUERY_ALL_PACKAGES` permission; uses targeted manifest queries. Device testing is required to verify chooser presentation with various third-party music players installed.
- Files: `AndroidManifest.xml`, `ExternalMusicApp.kt`, `MusicWidget.kt`, `MediaSessionStateTest.kt`, `docs/DECISIONS.md`, `docs/ROADMAP.md`, `docs/SESSION_STATE.md`, `docs/TRACEABILITY.md`, `docs/ARCHITECTURE.md`, `docs/ANDROID_CAPABILITIES.md`, `README.md`.
- PRD IDs: M2-MEDIA-001, M1-WIDGET-001, M1-DESIGN-001, M1-QA-001. Extends ADR-067 and ADR-036.
- References: [Intent.makeMainSelectorActivity](https://developer.android.com/reference/android/content/Intent#makeMainSelectorActivity(java.lang.String,%20java.lang.String)), [Package visibility filtering](https://developer.android.com/training/package-visibility).


## ADR-069 — Music metadata keeps size-based typography with faded marquee

- Date/status: 2026-11-20 / Accepted; user explicitly requests original size-based song/artist typography, faded marquee and a one-second hold before every pass. Supersedes music metadata wrapping/length-based fitting only in ADR-024 and subsequent shared text-fitting descriptions.
- Context/options: Shared AdaptiveWidgetText shrinks long music metadata to fit both axes. Options are retaining that fit, clipping fixed-size labels, or using the requested single-line marquee for both labels.
- Decision/rationale: MusicMetadataText derives font size from available line height and the existing 28 sp title/22 sp artist caps, preserving theme font family and line-height ratio independently of string length/width. Overflow alone enables Compose basicMarquee with infinite passes and explicit 1000 ms initial/repeat holds. A 12 dp (width-clamped) transparent alpha mask fades both edges on an isolated layer; internal edge padding keeps resting endpoint glyphs readable. Metadata changes restart the hold. Short strings remain static without fades.
- Tradeoffs/consequences: Long metadata takes time to read; offscreen compositing applies only to overflowing lines. Use the existing Compose marquee speed/spacing defaults. Actual motion, font scaling and small-widget clipping require physical-device review. Shared fitter and OLED saver typography retain their existing behavior. No architecture, dependency, permission or registry change.
- Files: feature/widgets/MusicWidget.kt, MusicMetadataText.kt, core/ui/theme/RideUiTokens.kt, proposed config/design-tokens.json and reconciled design/catalog/task docs.
- PRD IDs: M2-MEDIA-001, M1-WIDGET-001, M1-DESIGN-001, M1-QA-001.


## ADR-070 — Automatic OLED dimming with a darker Dim screen tier

- Date/status: 2026-11-20 / Accepted; user delegates automatic OLED brightness reduction and a significantly darker Dim screen option. Supersedes optional-only saver brightness behavior; persisted setting/key remains unchanged.
- Context/options: Saver inherited system brightness with Dim screen off; with it on only a 0.05 window request applied. The brightness effect observed saver alone, missing preference changes, and late saver callbacks could override onPause restoration. Options: hardware-only levels or hardware levels plus subdued saver content for devices with a minimum brightness clamp.
- Decision/rationale: Request per-window brightness 0.05 for ordinary OLED idle and 0.01 for extra dim; apply content opacity 0.65/0.25 over the existing opaque black saver background for both Classic and Row. This makes the extra dim tier visibly darker even when panel brightness clamps. Observe saver, Dim screen preference, resume counter and PiP. Pure policy rejects overrides outside resumed non-PiP OLED; onPause restores system brightness and rejects subsequent delayed callbacks. Wake keeps the existing consumed touch and restores system control. Settings explains the extra dim tier.
- Tradeoffs/consequences: Android brightness values are normalized requests, not measured luminance; hardware/adaptive brightness may differ by device and an already very low system level may not be further reduced by a window request. Content attenuation still lowers emitted foreground intensity. Dim content can be harder to read in sunlight; wake restores ordinary Ride. No global brightness mutation, WRITE_SETTINGS permission, new preference, dependency or architecture change. Physical-device brightness/lifecycle verification remains required.
- Files: RideActivity.kt, RideDashboard.kt, OledBrightness.kt, RideUiTokens.kt, SettingsScreen.kt, OledBrightnessTest.kt, proposed design tokens, tasks/M1-OLED-BRIGHTNESS.md and reconciled docs.
- PRD IDs: M1-OLED-001, M1-PERSIST-001, M1-DESIGN-001, M1-QA-001.

## ADR-071 — Container-free Ride confirmations over dimmed content

- Date/status: 2026-11-20 / Accepted; the user explicitly delegates screen/widget dimming and removal of the alert-dialog container. Supersedes only the card/opaque-backdrop presentation in ADR-059/054/053.
- Context/options: Shared call/app confirmations wrapped their controls in a rounded Surface, while Ride/navigation end replaced underlying content with opaque surfaces. Options were dimming the entire screen for every action or preserving existing screen/widget coverage.
- Decision/rationale: Preserve existing modal scope: call and app handoffs dim the screen using the existing Dialog window; End Ride dims the dashboard; End navigation dims its widget while the map stays visible. Remove the shared dialog card and use a canonical 80% black scrim with white title/detail text, existing dynamic action colors, and a 720 dp content cap. Keep all countdown, dismissal, Back, expiry, guarded confirmation and controller behavior.
- Tradeoffs/consequences: The underlying dashboard/map remains visible and guidance continues. A dark scrim provides contrast in either app theme; sunlight/font scaling/touch behavior need physical-device acceptance. Existing modal windows retain focus and touch isolation. No dependencies, permissions, architecture, persistence or registry changes.
- Files: RideConfirmation.kt, RideUiTokens.kt, RideDashboard.kt, GoogleMapsWidget.kt, config/design-tokens.json, tasks/M1-RIDE-CONFIRMATIONS.md and reconciled docs.
- PRD IDs: M1-RIDE-001, M1-DESIGN-001, M1-QA-001; existing consumers M2-CONTACT-001, M2-CALLS-001, M2-NAV-001, M2-MEDIA-001, M2-TURN-001.

## ADR-072 — Responsive Home grid and Ride panel

- Date/status: 2026-11-20 / Accepted; Home presentation decisions delegated by the user's grid makeover request. Refines Home presentation in ADR-026/028.
- Context/options: Home was a hero followed by four full-width icon rows. Options were a fixed two-column layout, a fixed list, or a grid adapting to width and text scale while preserving all destinations.
- Decision/rationale: Reuse RideScreen's single scroll owner, RideActionButton and RideInfoCard with an opt-in stacked tonal tile layout (existing row callers keep their default). Order Pages/Settings/Guide/Access in row-major order. Use two equal-width tiles per row when content width permits 156 dp per tile scaled by fontScale (at least 1), plus 12 dp gap; otherwise one column. Rows measure to their tallest tile and can grow without truncating text. Cap Home content at 1120 dp; at 840 dp scaled by fontScale, show the Ride panel beside a two-column grid. Use existing dynamic tones, typography, spacing and shape tokens. Feed existing RideSession.active into Home as a Boolean so actual in-process active state gives Return to ride copy/Play icon; retain the existing launch callback and permission flow.
- Tradeoffs/consequences: Larger text takes more vertical space and can switch to one column; Home remains scrollable and no nested lazy grid is introduced. The state label inherits the existing in-process session lifetime, not new persistence. Previews aid development but do not verify device layout, TalkBack or taps. No architecture, permission, dependency, orientation, storage or widget contract changes.
- Files: HomeScreen.kt, RideComponents.kt, RideUiTokens.kt, RideApp.kt, strings.xml, config/design-tokens.json, tasks/M1-HOME-GRID.md and reconciled docs.
- PRD IDs: M1-APP-001, M1-DESIGN-001, M1-QA-001.

## ADR-073 — Restore original Home styling and fade Ride confirmations

- Date/status: 2026-11-20 / Accepted; user explicitly requests original Home styling with a lower menu grid and confirmation overlay fades. Supersedes ADR-072 presentation and extends ADR-071 without changing confirmation actions or safety deadlines.
- Context/options: The colored Home tiles/alternate hero were rejected. Confirmation hosts removed prompts immediately on dismissal, preventing exit animation. Options were merely animating entry, delaying domain dismissal, or retaining only outgoing presentation while dismissed actions become inert.
- Decision/rationale: Restore the original Home hero, copy, title/button styling and original RideInfoCard rows with neutral backgrounds and original icon accents. Arrange only the four lower options in two equal-width columns when 156 dp per option scaled by fontScale (at least 1), plus the existing 12 dp gap fits; otherwise one column. Remove the added stacked-card API, alternate/active hero and wide split. For confirmations, reuse a shared FadeRideConfirmation host with a MutableTransitionState and retained outgoing value until transition idle; animate content alpha and either native window dim amount or inline scrim together for 220 ms. Supply persisted Reduce motion from existing Ride/editor providers; zero duration then. Disable outgoing buttons, guard dismissal callbacks and clear outgoing semantics; preserve original five-second deadline/one-shot latch. End Ride validates the tap immediately and dispatches its exit after the fade; existing call, app handoff and navigation controller actions retain their callbacks.
- Tradeoffs/consequences: Original horizontal cards have longer wrapping text in grid cells; intrinsic row sizing and scrolling preserve all content. A dismissed prompt can remain visible briefly but is inert and blocks background taps until removed. External app, call, permission and Activity lifecycle transitions remain Android-controlled; fade is visible while the host window remains composed. No dependency, permission, schema, widget registration, persistence or architecture change. Physical-device visual/animation/lifecycle/accessibility acceptance remains open.
- Files: HomeScreen.kt, restored RideComponents.kt/RideApp.kt/strings.xml, RideConfirmation.kt, RideUiTokens.kt, MusicWidget/TurnGuidanceWidget/CallHistoryWidget/SosFavoritesWidget, RideDashboard/GoogleMapsWidget, RideActivity/PageEditorActivity, proposed tokens, both task files and reconciled docs.
- PRD IDs: M1-APP-001, M1-RIDE-001, M1-DESIGN-001, M1-QA-001; existing M2 call/media/guidance/navigation consumers inherit presentation only.

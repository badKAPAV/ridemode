# M1-RIDE-QOL — Ride controls, map paging and access links

Status: IN_PROGRESS (2026-11-20); implementation/build evidence is in SESSION_STATE; device acceptance open.

- Requirements: M1-APP-001, M1-RIDE-001, M1-PERSIST-001, M1-DESIGN-001, M1-QA-001, M2-NAV-001, M2-MAPS-001 (toolbar only).
- Scope/files: RideDashboard/FixedRideCanvas/GoogleMapsWidget loading and controls; RideRepository/RideViewModel/SettingsScreen toggle; PermissionInfoScreen/settings links; shared ConfirmationTimer and existing navigation deadline callers/tests; ExternalMaps; strings/defaults/tracking docs.
- Dependencies: existing Compose Material 3 Expressive, DataStore and installed Navigation SDK. No new dependencies, permissions or Room schema change; preserve saved geometry and uncommitted work.
- Risk: native initialization still costs frames after a swipe; no performance claim without a device. Platform Settings destinations differ by Android/OEM. Loaded views are kept only while their page remains composed.
- Acceptance: first map creation waits for a settled non-scrolling page and a frame; circular Expressive loading state before readiness/during motion; no needless controller/view replacement midway through a swipe. Active guidance persists. End Ride shows a full-screen five-second confirmation and does not end on timeout/Keep/Back/resume/PiP; timely confirm performs existing cleanup. Maps visibility persists default on; toolbar action opens Maps/browser fallback. Page buttons have transparent containers and 56 dp targets. Access cards are short, actionable, guarded Settings links and make no automatic grants.
- Verification: JDK 17 unit/lint/debug APK, docs validator and diff check; user device swipe/active-route performance, End Ride timeout/Keep/Back/confirm, toggle force-stop/reopen, page-name touch/TalkBack and all Settings links/missing-app fallback. No UI/device tests claimed.
- Exclusions: no destination handoff/return-overlay integration, separate Maps shortcut widget change, app-wide architecture/dependency/permission update, or blanket performance guarantee. ADR-054 records delegated choices.

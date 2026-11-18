# M1 ride shell

TASK ID: M1-RIDE-001
MILESTONE: M1
OBJECTIVE: Start and end a landscape ride session with a swipeable preview and persistent action rail.
REQUIREMENT IDS: M1-RIDE-001, M1-DESIGN-001
IN SCOPE: Dedicated Ride Activity per architecture; landscape and immersive window while active; screen-awake only while resumed; three clearly labeled preview pages; horizontal swiping; page indicator; End Ride; visible Maps and assistant placeholders; large accessible controls.
OUT OF SCOPE: Page editor and persistence, dashboard widget renderer, live Maps, assistant execution, OLED saver, new permissions or dependencies.
FILES EXPECTED: `MainActivity.kt`, `core/navigation/RideApp.kt`, `feature/home/HomeScreen.kt`, `feature/ride/*`, AndroidManifest.xml, strings, shared theme tokens if needed, README and docs.
ACCEPTANCE CRITERIA: Home starts Ride; Ride opens in landscape with hidden system bars; page swipes update the indicator; rail remains visible; Maps and assistant are labeled unavailable; End Ride returns to Home and restores prior orientation; screen-awake flag exists only while Ride is resumed; no sensitive permission added.
VERIFICATION: docs validator; Gradle unit/lint/debug APK; physical-device launch, orientation, swipe, rail, exit and window flag checks; accessibility hierarchy. TalkBack remains a separate foundation gate.
DEPENDENCIES / APPROVALS: Existing M1 foundation shell and planned Ride Activity architecture. User requested this next development task. Full foundation acceptance remains open for TalkBack/dark appearance.
RISKS: Static preview pages do not yet represent persisted enabled pages; do not mark the complete M1-RIDE-001 requirement DONE until page integration and full device acceptance.

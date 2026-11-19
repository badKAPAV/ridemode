# M1 editor, part 1: grid geometry

TASK ID: M1-EDITOR-PART1
MILESTONE: M1
OBJECTIVE: Establish tested 12-column geometry rules for future widget editing.
REQUIREMENT IDS: M1-PAGE-001
IN SCOPE: Pure Kotlin grid rectangle model; 12-column bounds checks; edge-touching and overlapping behavior; clamped move/resize proposals; meaningful JVM unit tests. No widget instances are created in this step.
OUT OF SCOPE: Compose editor UI, drag handles, collision resolution UI, widget registry/rendering, Room/DataStore, row-height visual setting, saved layouts, native integrations.
FILES EXPECTED: `core/model/GridLayout.kt`, `app/src/test/.../GridLayoutTest.kt`, task file, README and milestone docs.
ACCEPTANCE CRITERIA: Invalid sizes and out-of-bounds rectangles are rejected; adjacent rectangles are accepted; overlapping proposals are rejected; move/resize stay within 12 columns and nonnegative rows; tests pass.
VERIFICATION: `bash scripts/verify-docs.sh`; `./gradlew --no-daemon :app:testDebugUnitTest :app:lintDebug :app:assembleDebug`.
DEPENDENCIES / APPROVALS: User requested the next M1 development step; existing PRD/architecture specify a 12-column normalized grid. No new dependency, permission or architecture choice.
RISKS: This is geometry groundwork only; M1-PAGE-001 remains IN_PROGRESS until editor UI, snapping, collision resolution and persistence are implemented and device-tested.

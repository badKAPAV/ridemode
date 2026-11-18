# M1 foundation, part 2

TASK ID: M1-FOUNDATION-PART2
MILESTONE: M1
OBJECTIVE: Navigable Home, Settings, onboarding and permission explanation screens.
REQUIREMENT IDS: M1-APP-001, M1-DESIGN-001
IN SCOPE: Navigation Compose graph; four screens; reusable screen scaffold and card actions; Phosphor icons; copy that distinguishes future features and permissions; accessible labels and 56 dp minimum controls.
OUT OF SCOPE: Ride activity, editor, persistence, permission requests, native integrations, dashboard widget registry, settings toggles that imply saved state.
FILES EXPECTED: `MainActivity.kt`, `core/ui/components`, `feature/home`, `feature/settings`, `feature/onboarding`, `feature/permissions`, `core/navigation`, strings, Gradle catalog/build, README and docs.
ACCEPTANCE CRITERIA: Home opens on launch; Settings, onboarding and permission explanations are reachable; system Back returns to the prior screen; no sensitive permission is declared or requested; unavailable work is labeled planned; screen controls use shared expressive components and Phosphor icons.
VERIFICATION: docs validator, Gradle unit/lint/debug APK; device navigation and TalkBack checks when a device is available.
DEPENDENCIES / APPROVALS: Part 1; user requested the next foundation step; Navigation Compose 2.9.8 on the existing API 36 toolchain.
RISKS: IDE sync after new dependency, device behavior and accessibility remain unverified without a device.

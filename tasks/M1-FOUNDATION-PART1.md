# M1 foundation, part 1

TASK ID: M1-FOUNDATION-PART1
MILESTONE: M1
OBJECTIVE: Launchable Compose shell and reusable Material 3 Expressive visual foundation.
REQUIREMENT IDS: M1-APP-001, M1-DESIGN-001
IN SCOPE: Compose setup; MainActivity; dynamic system color theme with API 24 fallback; Roboto Flex wide titles and normal body; Phosphor icons; reusable action, surface, and morphing loader components; honest foundation screen.
OUT OF SCOPE: navigation, Settings, onboarding, permissions screen, Ride, editor, persistence, native integrations, widget registry.
FILES EXPECTED: Gradle catalog/build files, manifest, app source/resources, design documentation, roadmap/session/traceability/README.
ACCEPTANCE CRITERIA: App compiles and launches to the shell; MaterialExpressiveTheme is used; wallpaper palette on Android 12+; fallback palette on older releases; reusable controls have at least 56 dp touch targets and accessible labels; no sensitive permissions.
VERIFICATION: docs validator; Gradle unit test, lint and debug assemble; device appearance and TalkBack remain unverified without device.
DEPENDENCIES / APPROVALS: User explicitly requested expressive UI, Phosphor pack, Roboto Flex, dynamic colors, and first-part scope. Existing namespace follows architecture.
RISKS: New dependency resolution; expressive RC API changes; device color and typography appearance need physical-device checks.

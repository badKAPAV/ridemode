# Ride continuity through PiP and notification

TASK ID: M1-RIDE-CONTINUITY
MILESTONE: M1
OBJECTIVE: Keep an active Ride reachable after leaving full screen and closing picture-in-picture.
REQUIREMENT IDS: M1-RIDE-001, M1-RIDE-002
IN SCOPE: Enter PiP on Home/Recents while Ride is active; compact PiP UI; ongoing Ride notification with Open and End Ride actions; request Android notification permission at Ride start; request Live Update promotion where supported; remove notification on End Ride.
OUT OF SCOPE: Foreground service, background route tracking, Maps integration, saved pages, editor UI, widget integrations.
FILES EXPECTED: Ride Activity/UI, notification controller/receiver, Home launch flow, manifest, strings, PRD addendum and repository docs.
ACCEPTANCE CRITERIA: On supported Android, minimizing Ride opens PiP; closing PiP leaves a notification when notifications are allowed; Open restores Ride; End Ride stops the session and removes the notification; screen-awake flag is absent outside full-screen Ride; denied notifications degrade clearly.
VERIFICATION: Gradle unit/lint/APK and docs checks; real-device Home/PiP close/notification Open/End actions; inspect promotion status and permission state.
DEPENDENCIES / APPROVALS: User explicitly requested PiP and Live Update behavior. ADR-006 approved separately for editor persistence. Android controls actual Live Update promotion.
RISKS: PiP entry and notification promotion depend on OS/device state. Notification permission denial prevents the shade fallback; no foreground work is performed after PiP closes.

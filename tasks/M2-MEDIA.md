# M2 music session integration

TASK ID: M2-MEDIA
REQUIREMENTS: M2-MEDIA-001, M2-QA-001; saver transition M1-OLED-001 and M1-QA-001.
STATUS: IN_PROGRESS. User directed this M2 slice before the still-open M1 device acceptance gate.
SCOPE: MediaSessionManager active sessions through an explicitly granted NotificationListenerService component; metadata, optional artwork and bounded playback progress; supported previous/play-pause/next actions; clear on session disappearance/revocation; permission/no-session/unavailable UI in music widget and both OLED styles. Smooth saver in/out and Reduce motion. No notification-feed or weather integration.
FILES: `data/MediaSessionRepository.kt`, `service/RideMediaListenerService.kt`, `feature/widgets/MusicWidget.kt`, shared Ride/editor/OLED UI and view model, Settings/Access screens, manifest, tests, tokens and tracking docs.
DEPENDENCIES: User grants Android notification access; a supported media app exposes an active MediaSession. Existing Hilt, Compose and widget registry suffice. No new dependency, Room schema or `uses-permission` entry.
RISKS: Android's grant exposes notifications at OS level, though this service reads only sessions; third-party apps may omit art/duration/actions; session and access can disappear; fade must consume wake touch through the exit animation.
ACCEPTANCE: On a real device, grant access and confirm title, artist, available art and advancing progress from at least two supported apps; previous/play/pause/next work only where advertised; paused/resumed UI matches source; app switch and session removal clear stale data; revocation shows access-needed and disables controls; editor preview cannot control playback; Classic and Row song areas match; saver fades both directions without tapping underlying controls, with immediate Reduce motion behavior. Unit, lint, APK, docs validator and diff checks pass.
VERIFICATION: `./gradlew --no-daemon :app:testDebugUnitTest :app:lintDebug :app:assembleDebug`; `bash scripts/verify-docs.sh`; `git diff --check`; user real-device integration checks. Until device scenarios pass, M2-MEDIA remains IN_PROGRESS.

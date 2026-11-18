# Android capability and permission boundaries

All are planned, not requested by the current manifest. Grant state may change at any time. Device/Android-version and Play policy behavior requires testing before claims of support.

| Capability | Grant/use | Restriction and fallback | Milestone |
|---|---|---|---|
| NotificationListenerService | Explicit user special-access grant via Settings; observe revocation | Needed for feed, media sessions and experimental Maps parsing. Missing/partial/redacted content shows permission or unavailable state; no invented cue. | M2 |
| MediaSessionManager / MediaController | Active sessions via listener authorization | Control only supported actions; session may disappear. No private library access. | M2 |
| AudioManager | Standard stream/route APIs | Clamp to actual range; handle Bluetooth route change. | M2 |
| Contacts | System picker where sufficient; READ_CONTACTS only at selection if needed | Denial yields manual/dial fallback; local references only. | M2 |
| ACTION_DIAL / CALL_PHONE | Dial needs no direct-call grant; ACTION_CALL requires runtime permission and confirmation | Emergency numbers through system dialer; OS owns incoming calls. | M2 |
| READ_CALL_LOG | Only after distribution and Play eligibility decision | Sensitive/restricted permission; default to favorites instead of recents. | M2 |
| SpeechRecognizer / RECORD_AUDIO | M3 tap-to-talk; ask microphone grant at use | Check on-device recognition and installed offline model; unsupported shows unavailable, no silent cloud fallback. | M3 |
| Bluetooth audio routing | Test device/intercom mic and call competition | Route may change or be unavailable; fail safely. Android-version-specific API behavior to verify. | M3 |
| SYSTEM_ALERT_WINDOW | Optional Maps return bubble, separately approved by user | Special access and background-start limits; task history/system navigation remains baseline. | M2 optional |
| FLAG_KEEP_SCREEN_ON / window brightness | Implemented in Ride Activity `onResume`/`onPause`; brightness change planned | Pixel 8a showed the flag on Ride and absent on Home. No global power or brightness change; no system-wide screen-off tap-to-wake. | M1 |
| Orientation / immersive | Ride Activity requests sensor-landscape and hidden system bars | Pixel 8a entered landscape and returned to portrait Home. Active call kept status bar visible during test; no-call immersive and broader inset behavior pending. | M1 |
| Google Maps Intent | Explicit package/URI where installed | Catch missing app; network routing not guaranteed; task history returns. | M2 |
| Location / network | Only if a later optional feature needs them | Core dashboard works offline; weather and online Maps/third-party messages depend on providers. | M2 optional |

See [PRD](PRD.md) and [architecture](ARCHITECTURE.md). Android/Play eligibility and device behavior must be checked against current official guidance when implementation begins.

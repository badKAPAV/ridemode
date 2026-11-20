# M2-WIDGET-REFINEMENTS

- Scope: center the Assistant icon and wrapping two-line Open Assistant label; pin notification-derived arrival time bottom-center; retain timed Maps handoff; make Music artwork/details request the shared timed confirmation before opening the tapped source app when playing, and launch the device "Open with" chooser sheet for all music player apps when no music is playing.
- Requirements: M2-ASSIST-001, M2-TURN-001, M2-MEDIA-001, M1-WIDGET-001, M1-DESIGN-001, M1-QA-001.
- Files: AssistantWidget, TurnGuidanceWidget/Presentation, MusicWidget, MediaSessionRepository, ExternalMusicApp, AndroidManifest.xml, presentation/media tests and reconciled docs.
- Dependencies/authorization: explicit user request; existing MediaSession access, platform app intents and RideConfirmationDialog. Targeted <queries> for music intents added for Android 11+ visibility; no broad package queries, new permission, service, schema or architectural owner.
- Risks: missing/cancelled provider session intent or absent launcher; source changes during confirmation; compact layouts and font scaling. Capture the tapped source when playing, handle unavailable launch with feedback, keep editor inert and transport controls separate.
- Acceptance: centered wrapping Assistant label; actual arrival time at bottom center without stale/fabricated data; guidance tap still confirms Maps; playing music targets confirm source app; no-music tap immediately opens system music chooser sheet; dismiss/Back/expiry never launch. Existing playback controls unaffected.
- Verification: unit tests, lint, debug APK, docs validator and diff whitespace; visual/provider handoff device acceptance remains open.
- State: IN_PROGRESS pending user visual/provider device acceptance.

Final checks (2026-11-20): JDK 17 full unit/lint/debug APK exit 0, 62 tasks/1m 30s, 52 tests/0 failures/errors. Docs validator and diff whitespace pass. Real-provider handoffs, timeout/Back interactions and visual/font-scale device review remain NOT VERIFIED this run.

## Music metadata marquee addendum — 2026-11-20 (ADR-069)

- Scope/files: MusicWidget, dedicated MusicMetadataText, RideUiTokens, proposed music tokens and tracking/design/catalog docs; M2-MEDIA-001 and M1-WIDGET-001/M1-DESIGN-001/M1-QA-001. Existing Compose marquee; no new dependency or permission.
- Acceptance: title and artist keep the same typography for short/long strings at each widget size; short text stays still; each overflowing line fades at both edges and holds at its original position for 1000 ms before its first and every subsequent pass. A new metadata value restarts the hold.
- Risk/device checks: inspect 5×4, 8×4 and large square/landscape widgets, short/long song and artist independently, metadata changes of equal width, 1×/1.5× font scale and multiple complete loops; check resting glyph visibility, unchanged controls/handoffs and no clipping.
- State: IN_PROGRESS pending physical-device visual acceptance. Non-UI check results recorded in SESSION_STATE.

Marquee verification (2026-11-20): JBR 21 app-only clean + full unit/lint/debug APK command exit 0 (63 tasks, 2m 42s), 52 tests/0 failures/errors. Docs validator and whitespace check exit 0. Physical-device marquee acceptance remains NOT VERIFIED.

Playback gate follow-up (2026-11-20): Pass current MusicSessionState.playing to both MusicMetadataText calls. Compose marquee uses zero iterations while paused and infinite iterations while playing. Acceptance: pause mid-scroll resets both labels to origin without changing typography; resume holds one second before scrolling. Existing fades/initial/repeat delays remain. Device check pending; verification recorded in SESSION_STATE.

Playback gate verification (2026-11-20): JBR 21 full unit/lint/debug APK command exit 0 (62 tasks, 1m 46s), 52 tests/0 failures/errors; docs validator and whitespace exit 0. Physical-device pause/resume remains NOT VERIFIED.

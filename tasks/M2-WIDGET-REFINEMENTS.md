# M2-WIDGET-REFINEMENTS

- Scope: center the Assistant icon and wrapping two-line Open Assistant label; pin notification-derived arrival time bottom-center; retain timed Maps handoff; make Music artwork/details request the shared timed confirmation before opening the tapped source app.
- Requirements: M2-ASSIST-001, M2-TURN-001, M2-MEDIA-001, M1-WIDGET-001, M1-DESIGN-001, M1-QA-001.
- Files: AssistantWidget, TurnGuidanceWidget/Presentation, MusicWidget, MediaSessionRepository, new source-app launcher, presentation/media tests and reconciled docs.
- Dependencies/authorization: explicit user request; existing MediaSession access, platform app intents and RideConfirmationDialog. No new dependency, permission, service, schema or architectural owner.
- Risks: missing/cancelled provider session intent or absent launcher; source changes during confirmation; compact layouts and font scaling. Capture the tapped source, handle unavailable launch with feedback, keep editor inert and transport controls separate.
- Acceptance: centered wrapping Assistant label; actual arrival time at bottom center without stale/fabricated data; guidance tap still confirms Maps; both music targets confirm source app, dismiss/Back/expiry never launch. Existing playback controls unaffected.
- Verification: unit tests, lint, debug APK, docs validator and diff whitespace; visual/provider handoff device acceptance remains open.
- State: IN_PROGRESS pending user visual/provider device acceptance.

Final checks (2026-11-20): JDK 17 full unit/lint/debug APK exit 0, 62 tasks/1m 15s, 51 tests/0 failures/errors. Docs validator and diff whitespace pass. Real-provider handoffs, timeout/Back interactions and visual/font-scale device review remain NOT VERIFIED this run.

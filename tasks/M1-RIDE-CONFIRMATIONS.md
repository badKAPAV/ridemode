# M1-RIDE-CONFIRMATIONS

2026-11-20 — IN_PROGRESS pending user device acceptance. Related M2 call/navigation integrations remain open. ADR-059 records user-delegated presentation/timing decisions.

Scope/requirements: M1-RIDE-001, M1-DESIGN-001, M1-QA-001; integration coverage M2-CONTACT-001, M2-CALLS-001, M2-NAV-001, M2-QA-001. Shared RideConfirmation controls replace small call actions and separate Ride/navigation countdown labels. Files: new core/ui/components/RideConfirmation.kt; timer/tokens; SOS/Calls, RideDashboard, GoogleMapsWidget/controller; timer policy tests; design JSON and docs.

Dependencies/risks: existing Compose Material 3, Phosphor vectors, monotonic deadline helper and existing call/navigation behavior. No new dependencies/permissions/services/schema. Preserve all prior uncommitted work. Small map widgets and large font scaling require device review; gloves/touch reliability cannot be asserted from JVM tests.

Acceptance: SOS contact or Recents call opens a large call/dismiss dialog; no action autoexecutes. No confirm within five seconds dismisses. Dismiss/Keep button fill drains continuously and seconds match the deadline. Both actions show bold titleMedium labels/icons with large targets. Call confirmation is high-contrast primary; end actions high-contrast error. Compact/map and full-grid views fit controls. End Ride/navigation keep their existing coverage and return to active mode on timeout/Back/Keep. Confirm before timeout works once; delayed confirm rejects. Permission prompt after a confirmed call is independent of the removed countdown. Default dialer, opted-in direct call, denial/emergency handling remain correct. Device tests require a real device; use a consenting non-emergency call target.

Verification: unit/lint/debug APK and docs/diff checks recorded in SESSION_STATE. User device tests remain NOT VERIFIED.

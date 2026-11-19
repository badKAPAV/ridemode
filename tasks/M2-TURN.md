# M2-TURN — Experimental notification-derived Turn guidance

- Scope: M2-TURN-001, M2-QA-001 and shared M1-WIDGET-001 rendering. Implement the concrete widget from current Google Maps notification data; leave real-device testing to the next task as requested.
- Files: one in-memory Maps cue repository, existing listener, Ride ViewModel/renderer/editor wiring, dedicated Compose card, UI tokens and affected documentation.
- Dependencies: user-granted notification-listener access and an installed Google Maps app actively posting usable navigation text. No new permission, dependency, service or persisted route history.
- Risks: Maps notification schema, text, locale and update cadence are not guaranteed; an old maneuver must not remain current. Compact widget sizes cannot display every detail at once.
- Acceptance: a current ongoing Maps notification with a recognizable maneuver shows the source instruction, directional arrow and available ETA/distance/route details; cue expires after two minutes, clears on removal/revocation/disconnect, and has explicit access/listening/inactive/stale states. Tap opens its notification intent or Maps. Ride and editor preview share state. Kotlin compile, APK packaging, docs validation and diff check pass; device tests remain IN_PROGRESS.

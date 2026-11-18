# Motorcycle Ride Companion — Codex Repository Bootstrap Contract

Version: 1.0 | Purpose: Paste this file into a new Android repository and instruct Codex to implement it **before application feature work**.

## 0. Bootstrap command

> Read `BOOTSTRAP_SPEC.md` completely and inspect the existing repository and `Motorcycle_Ride_Companion_MVP_PRD.docx` (if present). Implement the repository governance, specifications, configuration contracts, documentation maintenance, and three Codex workflows described here. Do not implement product features in this bootstrap task. Do not overwrite existing work. Report all files created, any assumptions or discrepancies, and verification results.

## 1. Product invariants

- Product: Motorcycle Ride Companion, Android-first motorcycle dashboard; Kotlin + Jetpack Compose, MVVM, StateFlow, Hilt, Room, DataStore.
- No backend, authentication, cloud storage, mandatory paid API or subscription. Core dashboard works offline. Weather, Maps and incoming third-party messages depend on external providers/apps; state this clearly.
- Ride mode: landscape, swipeable configurable pages, user-defined draggable/resizable widgets, persistent assistant button, screen-awake while activity visible, user-configurable OLED presentation mode with inactivity timeout; first touch wakes UI and is consumed.
- Widgets: music, volume, clock/date, weather (offline fallback), SOS favorites (up to six), contacts/calls, notifications, Google Maps launch, experimental Google Maps notification-derived maneuver display. Support predefined and user-created pages.
- Do NOT promise system-wide double-tap-to-wake, intercept incoming call UI, guaranteed Google Maps notification parsing, or unrestricted call-log permissions. Native dialer handles incoming calls. Calls history subject to Android permissions and Play distribution policy.
- NotificationListenerService requires explicit user grant; extracted Google Maps directions are best-effort, stale-safe, clearly labeled experimental. Never invent a maneuver.
- Assistant: M1 UI only; M3 on-device recognition, deterministic local commands, Bluetooth mic tests. Offline recognition must be capability-tested, with no silent cloud fallback.
- Milestones: M1 UI, local state/persistence, editor, saver and mock integrations; M2 native Android media/volume/contacts/notifications/Maps integrations; M3 on-device assistant, Bluetooth and stabilization. Maintain exact acceptance criteria from PRD.
- Safety: large touch targets, low-distraction UI, avoid complex interactions while moving, prevent accidental SOS calls, keep OS-managed emergency/call behavior.

## 2. Files Codex MUST create and maintain

```
AGENTS.md
README.md
docs/PRD.md
docs/ARCHITECTURE.md
docs/DESIGN_SYSTEM.md
docs/WIDGET_CATALOG.md
docs/ANDROID_CAPABILITIES.md
docs/DECISIONS.md
docs/ROADMAP.md
docs/SESSION_STATE.md
docs/TRACEABILITY.md
config/design-tokens.json
config/widget-catalog.json
config/ride-defaults.json
config/feature-flags.json
prompts/START.md
prompts/IMPLEMENT.md
prompts/AUDIT.md
prompts/TASK_TEMPLATE.md
scripts/codex-workflow.sh
scripts/verify-docs.sh
tasks/.gitkeep
```

`BOOTSTRAP_SPEC.md` and `COMMANDS.md` remain version-controlled references. The actual app source, Gradle files and tests are added separately when implementing the Android project.

### Canonical ownership / no drift

| Concern | Canonical source | Derived/description |
|---|---|---|
| Requirements and IDs | `docs/PRD.md` | Roadmap and traceability refer to IDs |
| Architecture and module boundaries | `docs/ARCHITECTURE.md` | README summary, code reflects it |
| Visual token values | `config/design-tokens.json` **only if** validated/generated into Kotlin | Compose theme and DESIGN_SYSTEM describe usage |
| Widget IDs, size constraints, config keys | `config/widget-catalog.json` **only if** validated against Kotlin registry | WIDGET_CATALOG documents behavior |
| Default app preferences | `config/ride-defaults.json` | DataStore stores user overrides |
| Feature rollout | `config/feature-flags.json` | ROADMAP tracks readiness |
| What exists and how to run it | **Actual code and verified commands** | README is reconciled against code |
| Architecture tradeoffs | `docs/DECISIONS.md` | Immutable ADR-style entries; supersede, do not erase |
| Work completion | Tests/build evidence + Git diff | ROADMAP and SESSION_STATE updated from evidence |

If no token generation/validation exists yet, **Kotlin Compose tokens are canonical**, and JSON is explicitly marked `proposed` and not treated as executable truth. Never maintain two independent sources of truth.

## 3. Mandatory AGENTS.md instructions

Codex must put these enforceable rules in root `AGENTS.md`:

1. On every run, inspect Git status/diff, read AGENTS.md, README.md, ROADMAP.md, SESSION_STATE.md, DECISIONS.md, relevant PRD IDs, architecture and config. Do not assume previous chat context.
2. Never overwrite user changes, discard uncommitted work, force push, alter dependencies, change architecture or grant new permissions without explicit authorization. If a task needs a new architectural decision, document a proposal and request approval **before** implementing unless the task explicitly delegates that decision.
3. Every task starts with scope, impacted files, requirement IDs, dependencies, risks, and acceptance criteria. Changes must stay within approved milestone.
4. Reuse Compose components and tokens. Widget types must be registered and validated. Avoid magic design values, redundant components, unnecessary services and unneeded permissions.
5. After **every Codex run that modifies repository files**, update README.md, DECISIONS.md, ROADMAP.md, SESSION_STATE.md and TRACEABILITY.md **as applicable**. For mandatory files with no substantive change, append/update a concise dated reconciliation entry in SESSION_STATE.md indicating each was checked and unchanged; do not fabricate decisions or add meaningless entries to DECISIONS.md.
6. README.md must accurately describe features actually implemented, prerequisites, setup/build/test commands, permissions, configuration, architecture links, known limitations and last verified build. Distinguish implemented vs planned.
7. DECISIONS.md: record every *new substantive autonomous* technical/design choice (ADR ID, date, status, context, options, decision, rationale, tradeoffs, consequences, affected files, PRD IDs). Do not rewrite prior decisions; supersede by new ADR. Minor mechanical changes do not warrant an ADR; log them in SESSION_STATE.
8. ROADMAP.md: requirement/task IDs, milestone, status `TODO|IN_PROGRESS|BLOCKED|DONE`, dependencies, verification evidence. Never mark DONE on UI mocks if native integration is pending.
9. SESSION_STATE.md: current milestone, current task, Git branch/head, changed files, verified behavior, exact checks and outcomes, blockers, next action, timestamp; preserve brief history, prune stale narrative into a dated archive if large.
10. TRACEABILITY.md maps PRD requirement IDs -> code paths -> tests -> milestone/status; update with each relevant code change.
11. Update DESIGN_SYSTEM, WIDGET_CATALOG, ANDROID_CAPABILITIES and structured config **when the underlying contract changes**. Keep docs factual, not aspirational.
12. Run the strongest feasible verification (format/lint/tests/build/config validation/docs checks), and report commands and exit status. If blocked, say `NOT VERIFIED` and leave task not DONE. Do not claim device tests without actual device execution.
13. Finish every run with: summary; changed files; decisions made; checks/results; docs reconciled; limitations; next suggested task. No automatic commit/push without explicit request.
14. For read-only planning sessions, do not alter files; report which docs are stale and propose updates. Audit sessions may update docs and in-scope fixes only when explicitly authorized.

## 4. Document content requirements

- **README.md:** concise project overview; current milestone; implemented/planned matrix; supported Android versions and device test matrix (unknown until verified); architecture map; developer prerequisites; build/run/test commands actually validated; permission rationale and setup; offline behavior; design/widget config ownership; navigation limitations; links to all docs; troubleshooting; verification status.
- **PRD.md:** convert existing PRD faithfully; uniquely label every requirement `M1-...`, `M2-...`, `M3-...`; include functional requirements, non-functional requirements, edge cases, privacy, acceptance criteria, out-of-scope, external dependencies, risks and milestones. Do not silently invent product requirements; flag gaps.
- **ARCHITECTURE.md:** module/package tree; Compose/ViewModel/repository flows; Room entities; DataStore keys; widget registry and renderer; notification parsing and stale-state pipeline; permissions boundaries; service lifecycles; navigation handoff; offline assistant pipeline; diagrams in Mermaid if useful.
- **DESIGN_SYSTEM.md:** typography, color, spacing, iconography, minimum tap areas, accessibility, landscape constraints, OLED stroke text behavior, safe touch-to-wake behavior, layouts for home/settings/editor/ride/notifications/calls/assistant.
- **WIDGET_CATALOG.md:** one record per widget: stable ID, purpose, minimum/default/max grid size, config keys, normal/OLED behavior, empty/loading/error states, permissions, milestone, tests.
- **ANDROID_CAPABILITIES.md:** each sensitive permission/API, user-grant flow, Android version restrictions, Play policy risk, failure/fallback behavior. Explicitly cover NotificationListenerService, MediaSession, contacts, calls/call logs, SpeechRecognizer, audio routing, overlay, screen flags and Maps Intents.
- **DECISIONS.md:** ADR index and template; only actual decisions with tradeoffs; separate accepted, proposed, superseded.
- **ROADMAP.md:** milestone task backlog with dependencies and objective acceptance gates. M1 assistant UI only; M2 native integrations; M3 voice.
- **SESSION_STATE.md:** succinct machine-readable front matter or fixed headings, current state, latest run report, last verification and next action.
- **TRACEABILITY.md:** requirement ID, milestone, implementation, test evidence, state.

## 5. Configuration schemas / constraints

- `design-tokens.json`: `schemaVersion`, palette for normal/OLED, typography scale (sp), spacing/radius (dp), touch-target minimum, responsive landscape breakpoints. Include explicit `sourceOfTruth` and validation approach.
- `widget-catalog.json`: stable type IDs, titles, size constraints (grid columns/rows), supported settings, permission dependencies, OLED support, milestone. Reject duplicate IDs and invalid min/default/max relationships.
- `ride-defaults.json`: default page list, orientation, keep-awake, OLED enable/timeout/outline/brightness, notification popup settings, privacy mode. Persist user overrides separately via DataStore/Room; do not reset them on app update.
- `feature-flags.json`: milestone-gated flags; disabled features should render deliberate UI placeholders, not fake functionality.
- Validate JSON syntax and referential integrity in `scripts/verify-docs.sh` or a small standard-library Python script. Do not require a paid service or backend.

## 6. Prompting automation

Generate `prompts/START.md`, `prompts/IMPLEMENT.md`, `prompts/AUDIT.md`, `prompts/TASK_TEMPLATE.md`, and `scripts/codex-workflow.sh` based on `COMMANDS.md`. Three commands: `start` (read-only plan), `implement <task-file>` (approved bounded task), `audit` (read-only unless separately authorized). Use Codex CLI `codex exec` with prompt input from stdin, verify the CLI exists, and fail fast on missing files or nonzero exit. Never use unsafe bypass flags, auto-commit, auto-push or silent destructive actions. Keep CLI compatibility configurable; document version-dependent syntax. Agent cannot be guaranteed to obey a prompt: scripts and CI must verify outcomes.

## 7. Automated checks and completion gate

`verify-docs.sh` must check mandatory files exist, JSON parses, milestone IDs/status are valid, references resolve where practical, README links point to existing files, and mandatory reconciliation records exist for the latest **mutating** task. Provide a separate project Gradle verification command after Gradle is initialized. CI should run docs validation + Gradle checks when available.

Task DONE requires: implementation matches acceptance criteria; relevant tests pass; docs reconciled; no unresolved blocker; evidence logged. If any fails, mark IN_PROGRESS/BLOCKED and report.

## 8. Bootstrap acceptance criteria

- All required files exist and are cross-linked.
- Existing PRD is accurately transcribed into Markdown with stable requirement IDs, or missing input explicitly flagged as a blocker.
- All three prompts and CLI workflow script exist; command syntax documented.
- `verify-docs.sh` succeeds for scaffold-only checks.
- No Android feature work, destructive Git operations, paid services or hidden dependencies introduced.
- Codex reports precisely which decisions it made and what was actually verified.

# Repository operating rules

1. Every run: inspect Git status/diff when Git exists; read this file, README, ROADMAP, SESSION_STATE, DECISIONS, relevant PRD IDs, architecture and config. Never rely on chat memory. If Git is absent, record that limitation.
2. Preserve user changes. Do not discard uncommitted work, force push, commit/push automatically, alter dependencies, permissions or architecture without explicit authorization. For a new substantive decision, propose an ADR and seek approval before implementation unless the task delegates that choice.
3. Start each task with scope, files, requirement IDs, dependencies, risks and acceptance criteria. Stay inside its approved milestone.
4. Reuse Compose components and tokens. Register and validate widget types. Avoid magic visual values, redundant services and unneeded permissions.
5. After every mutating Codex run, reconcile README, DECISIONS, ROADMAP, SESSION_STATE and TRACEABILITY as applicable. If any of the first four have no substantive change, record a dated check for each in SESSION_STATE. Do not invent ADRs.
6. README must distinguish implemented from planned and state prerequisites, verified commands, permissions, config ownership, limitations and last build result.
7. Record every substantive autonomous choice in an immutable ADR: ID/date/status/context/options/decision/rationale/tradeoffs/consequences/files/PRD IDs. Supersede with a new ADR; log mechanical work in SESSION_STATE.
8. ROADMAP uses requirement/task IDs, milestone, TODO|IN_PROGRESS|BLOCKED|DONE, dependencies and evidence. A UI mock never completes a native integration.
9. SESSION_STATE records milestone/task, Git branch/head, changed files, verified behavior, exact checks and outcomes, blockers, next action and timestamp. Keep concise history; archive stale narrative with a date.
10. TRACEABILITY maps each PRD ID to code, tests, milestone and state. Update with each relevant code change.
11. Update DESIGN_SYSTEM, WIDGET_CATALOG, ANDROID_CAPABILITIES and config when their underlying contracts change. Keep claims factual.
12. Run strongest feasible verification and report command/exit status. If blocked, say NOT VERIFIED and leave task open. Device tests require a real device.
13. End every run with summary, changed files, decisions, checks/results, docs reconciled, limitations and next task. Do not commit/push without request.
14. START and AUDIT are read-only. Report stale docs and proposed fixes. Audit may edit only with explicit authorization.

Read [bootstrap contract](docs/BOOTSTRAP_SPEC.md) and [workflow commands](docs/COMMANDS.md) for details.

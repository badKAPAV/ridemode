# Codex Workflow Commands — Motorcycle Ride Companion

These are the three repeatable operations. Run from the repository root after the bootstrap agent has generated `AGENTS.md`, `prompts/*` and `scripts/codex-workflow.sh`. Requires a configured Codex CLI (`codex --version`). Review proposed changes before approving execution.

## 1. START — read-only planning

```bash
bash scripts/codex-workflow.sh start
```

Codex reads repository instructions, roadmap, session state, decisions, PRD and relevant source. It reports current milestone, status, drift, blockers and the next bounded task. It must not modify files.

**Direct prompt fallback:**

```text
START SESSION. Follow AGENTS.md. Inspect git status and read README.md, docs/ROADMAP.md, docs/SESSION_STATE.md, docs/DECISIONS.md, docs/PRD.md and relevant architecture/config. Do not modify any files. Report current verified state, doc/code discrepancies, blockers, and propose one next task with requirement IDs, scope, exclusions, acceptance criteria and verification plan. Do not assume conversation memory.
```

## 2. IMPLEMENT — bounded task

First create `tasks/M1-RIDE-001.md` using `prompts/TASK_TEMPLATE.md`, then:

```bash
bash scripts/codex-workflow.sh implement tasks/M1-RIDE-001.md
```

Codex must read the task and repository rules, inspect code, implement only approved scope, verify, reconcile README/DECISIONS/ROADMAP/SESSION_STATE/TRACEABILITY and other affected docs, then report. It must not commit/push.

**Direct prompt fallback:**

```text
IMPLEMENT APPROVED TASK: tasks/M1-RIDE-001.md. Follow AGENTS.md. Read required docs, inspect git status and existing code, and honor task scope and exclusions. Implement the smallest complete change; do not add dependencies, permissions, or autonomous architecture changes without approval. Run relevant checks. Reconcile README.md, docs/DECISIONS.md, docs/ROADMAP.md, docs/SESSION_STATE.md, docs/TRACEABILITY.md, plus affected specs/config. Mark DONE only with verification evidence. Report changes, decisions, checks, blockers and next step. Do not commit/push.
```

## 3. AUDIT — read-only consistency review

```bash
bash scripts/codex-workflow.sh audit
```

Checks Git diff, tests, documentation drift, stale status, unsafe Android permissions, unregistered widgets, hardcoded UI tokens and milestone scope. Produces findings; do not silently fix anything.

**Direct prompt fallback:**

```text
AUDIT ONLY. Follow AGENTS.md. Inspect git diff and compare implementation against PRD, ROADMAP, TRACEABILITY, DESIGN_SYSTEM, WIDGET_CATALOG, ARCHITECTURE and configuration. Check README accuracy, ADR coverage, tests, Android permissions, offline constraints and milestone scope. Run non-mutating checks if available. Do not modify files. Report findings by severity with file paths, proposed fixes and unverified areas.
```

## First-time bootstrap prompt

```text
Read BOOTSTRAP_SPEC.md and COMMANDS.md fully. Inspect repository and attached PRD if present. Create the governance files, specs, structured configuration, verification scripts, and three Codex prompts required by the bootstrap contract. Do not implement app features. Preserve existing work. Run scaffold checks and report what is verified, blocked, or assumed.
```

## Task template

```text
TASK ID: M1-RIDE-001
MILESTONE: M1
OBJECTIVE: [one clear deliverable]
REQUIREMENT IDS: [IDs from docs/PRD.md]
IN SCOPE: [explicit items]
OUT OF SCOPE: [explicit exclusions]
FILES EXPECTED: [anticipated paths]
ACCEPTANCE CRITERIA: [testable statements]
VERIFICATION: [commands + device checks if applicable]
DEPENDENCIES / APPROVALS: [none or list]
```

## Important behavior

- **Every mutating Codex run:** README, ROADMAP, SESSION_STATE and TRACEABILITY must be checked and reconciled. DECISIONS must be checked; append an ADR only for a real decision. `SESSION_STATE.md` records the documentation reconciliation result even if a file needed no change.
- **START/AUDIT:** intentionally read-only, so no documentation updates; otherwise the commands would violate their own contract.
- **Tooling:** Codex CLI flags change over time. The generated wrapper should use `codex exec` conservatively, provide a `--help`/version check and avoid bypassing approvals or sandbox restrictions. If the installed CLI cannot enforce read-only mode, STOP rather than pretend the mode is read-only.
- **Review:** no prompt alone guarantees documentation consistency; automated validation and diff review are required.

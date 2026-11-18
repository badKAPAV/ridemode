#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
if ! command -v codex >/dev/null 2>&1; then echo 'codex CLI missing' >&2; exit 127; fi
if ! codex --version >/dev/null; then echo 'codex CLI unavailable' >&2; exit 1; fi
help_text="$(codex exec --help)"
for needed in '--sandbox' '--skip-git-repo-check'; do
  if ! grep -q -- "$needed" <<< "$help_text"; then echo "codex exec lacks $needed; inspect CLI compatibility" >&2; exit 2; fi
done
mode="${1:-}"
case "$mode" in
  start|audit)
    if [ "$#" -ne 1 ]; then echo "usage: $0 $mode" >&2; exit 2; fi
    if [ "$mode" = start ]; then prompt_file="prompts/START.md"; else prompt_file="prompts/AUDIT.md"; fi
    sandbox='read-only'
    ;;
  implement)
    if [ "$#" -ne 2 ] || [ ! -f "$2" ]; then echo "usage: $0 implement tasks/TASK.md (existing file)" >&2; exit 2; fi
    case "$2" in tasks/*.md) ;; *) echo 'task must be a Markdown file under tasks/' >&2; exit 2;; esac
    prompt_file='prompts/IMPLEMENT.md'
    sandbox='workspace-write'
    ;;
  *) echo "usage: $0 start | implement tasks/TASK.md | audit" >&2; exit 2;;
esac
for required in AGENTS.md "$prompt_file" docs/PRD.md; do
  if [ ! -f "$required" ]; then echo "missing $required" >&2; exit 2; fi
done
# The installed CLI supports stdin prompt, explicit sandbox and --skip-git-repo-check.
# Read-only modes use CLI sandbox enforcement, not prompt wording alone.
if [ "$mode" = implement ]; then
  { cat "$prompt_file"; printf '\nApproved task file: %s\n' "$2"; cat "$2"; } | codex exec --sandbox "$sandbox" --skip-git-repo-check -
else
  cat "$prompt_file" | codex exec --sandbox "$sandbox" --skip-git-repo-check -
fi

#!/usr/bin/env python3
"""Standard-library validation of repository documentation and proposed JSON contracts."""
import json
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
REQUIRED = [
    'AGENTS.md','README.md','docs/PRD.md','docs/ARCHITECTURE.md','docs/DESIGN_SYSTEM.md',
    'docs/WIDGET_CATALOG.md','docs/ANDROID_CAPABILITIES.md','docs/DECISIONS.md',
    'docs/ROADMAP.md','docs/SESSION_STATE.md','docs/TRACEABILITY.md',
    'config/design-tokens.json','config/widget-catalog.json','config/ride-defaults.json',
    'config/feature-flags.json','prompts/START.md','prompts/IMPLEMENT.md','prompts/AUDIT.md',
    'prompts/TASK_TEMPLATE.md','scripts/codex-workflow.sh','scripts/verify-docs.sh','tasks/.gitkeep',
    'docs/BOOTSTRAP_SPEC.md','docs/COMMANDS.md'
]
errors = []
for name in REQUIRED:
    if not (ROOT/name).is_file(): errors.append(f'missing {name}')

def load(name):
    try: return json.loads((ROOT/name).read_text())
    except (OSError, json.JSONDecodeError) as exc:
        errors.append(f'{name}: {exc}')
        return {}

tokens = load('config/design-tokens.json')
catalog = load('config/widget-catalog.json')
defaults = load('config/ride-defaults.json')
flags = load('config/feature-flags.json')
for name, data in [('tokens',tokens),('catalog',catalog),('defaults',defaults),('flags',flags)]:
    if data.get('schemaVersion') != 1: errors.append(f'{name}: schemaVersion must be 1')
    if data.get('sourceOfTruth') != 'proposed': errors.append(f'{name}: sourceOfTruth must be proposed until code parity exists')
if tokens:
    if tokens.get('touchTargetDp',{}).get('minimum',0) < 56: errors.append('touch minimum below PRD')
    if tokens.get('palette',{}).get('oled',{}).get('background') != '#000000': errors.append('OLED background must be true black')
widgets = catalog.get('widgets', [])
ids = [w.get('id') for w in widgets]
if len(ids) != len(set(ids)): errors.append('duplicate widget IDs')
for w in widgets:
    wid = w.get('id','<missing>')
    size = w.get('size',{})
    try:
        lo, mid, hi = (size[k] for k in ('min','default','max'))
        if any(len(v) != 2 or any(not isinstance(n,int) or n < 1 for n in v) for v in (lo,mid,hi)):
            raise ValueError('sizes must be positive integer pairs')
        if any(not lo[i] <= mid[i] <= hi[i] for i in (0,1)) or hi[0] > catalog['gridColumns']:
            raise ValueError('invalid min/default/max or grid width')
    except (KeyError,ValueError,TypeError) as exc: errors.append(f'widget {wid}: {exc}')
    if w.get('milestone') not in ('M1','M2','M3'): errors.append(f'widget {wid}: bad milestone')
    if not isinstance(w.get('settings'),list) or not isinstance(w.get('permissions'),list): errors.append(f'widget {wid}: settings/permissions must be lists')
for page in defaults.get('defaultPages',[]):
    for wid in page.get('widgetTypes',[]):
        if wid not in ids: errors.append(f'page {page.get("id")}: unknown widget {wid}')
oled = defaults.get('oled',{})
if oled.get('timeoutSeconds') not in oled.get('allowedTimeoutSeconds',[]): errors.append('OLED timeout not allowed')
flag_ids = [f.get('id') for f in flags.get('flags',[])]
if len(flag_ids) != len(set(flag_ids)): errors.append('duplicate feature flag IDs')
for flag in flags.get('flags',[]):
    if flag.get('milestone') not in ('M1','M2','M3') or not isinstance(flag.get('enabled'),bool): errors.append(f'invalid flag {flag.get("id")}')

prd = (ROOT/'docs/PRD.md').read_text() if (ROOT/'docs/PRD.md').exists() else ''
prd_ids = set(re.findall(r'\bM[123]-[A-Z]+-\d{3}\b', prd))
if not prd_ids: errors.append('PRD has no requirement IDs')
trace = (ROOT/'docs/TRACEABILITY.md').read_text() if (ROOT/'docs/TRACEABILITY.md').exists() else ''
trace_ids = set(re.findall(r'\bM[123]-[A-Z]+-\d{3}\b', trace))
if prd_ids != trace_ids: errors.append(f'traceability mismatch: missing {sorted(prd_ids-trace_ids)}, extra {sorted(trace_ids-prd_ids)}')
roadmap = (ROOT/'docs/ROADMAP.md').read_text() if (ROOT/'docs/ROADMAP.md').exists() else ''
for line in roadmap.splitlines():
    if line.startswith('| M') and not line.startswith('| Task'):
        cells = [x.strip() for x in line.strip('|').split('|')]
        if len(cells) < 6 or cells[2] not in ('M1','M2','M3') or cells[3] not in ('TODO','IN_PROGRESS','BLOCKED','DONE'):
            errors.append(f'invalid roadmap row: {line}')
        for rid in re.findall(r'\bM[123]-[A-Z]+-\d{3}\b', cells[1] if len(cells)>1 else ''):
            if rid not in prd_ids: errors.append(f'roadmap unknown requirement {rid}')
for line in trace.splitlines():
    if line.startswith('| M'):
        cells = [x.strip() for x in line.strip('|').split('|')]
        if len(cells) < 5 or cells[-1] not in ('TODO','IN_PROGRESS','BLOCKED','DONE'):
            errors.append(f'invalid traceability row: {line}')
readme = ROOT/'README.md'
if readme.exists():
    for target in re.findall(r'\[[^]]+\]\(([^)]+)\)', readme.read_text()):
        target = target.split('#',1)[0]
        if target and not target.startswith(('http:','https:','mailto:')) and not (ROOT/target).exists(): errors.append(f'broken README link {target}')
state = (ROOT/'docs/SESSION_STATE.md').read_text() if (ROOT/'docs/SESSION_STATE.md').exists() else ''
if 'Latest mutating run:' not in state: errors.append('missing latest mutating run marker')
for label in ('README.md','docs/DECISIONS.md','docs/ROADMAP.md','docs/SESSION_STATE.md','docs/TRACEABILITY.md'):
    if f'- {label}: ' not in state: errors.append(f'missing reconciliation record: {label}')
if errors:
    for error in errors: print('ERROR:',error,file=sys.stderr)
    sys.exit(1)
print(f'OK: {len(REQUIRED)} required files, {len(prd_ids)} requirement IDs, {len(widgets)} widget types, JSON and links')

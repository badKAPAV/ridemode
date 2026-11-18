# Widget catalog

[JSON catalog](../config/widget-catalog.json) proposes stable IDs, dimensions and settings; it is not executable until a Kotlin registry parity check exists (ADR-001). Dimensions below are columns × rows on the proposed 12-column grid. All UIs are M1 mock/placeholder work unless stated; permissions and native behavior belong to M2/M3. Shared renderer and OLED presentation are required for every widget. Each widget needs size validation, editor/ride rendering, persistence, accessibility and normal/OLED visual tests at M1, plus integration/failure tests at its native milestone.

| ID | Purpose; min/default/max | Config keys | Normal / OLED and states | Permission; integration |
|---|---|---|---|---|
| music | Session controls; 3×2 / 8×3 / 12×6 | showArtwork, showText, showTransport | Metadata/controls; monochrome text/controls. Empty no session, loading session, error grant/revocation. | Listener; M2 |
| volume | Device volume; 2×1 / 4×2 / 12×4 | showSlider, showButtons, increment | Slider/buttons; monochrome bounds. Empty route unknown, loading route, error unavailable. | None; M2 |
| clock | Local time/date; 2×1 / 4×2 / 12×4 | hourFormat, dateFormat, fontStyle | Large time; outline with solid fallback. Empty never expected, loading initial tick, error time unavailable. | None; M1 |
| weather | Condition/freshness; 2×1 / 4×2 / 12×4 | units, displayMode | Forecast/cache; monochrome freshness. Empty offline uncached, loading optional provider, error provider. | Optional provider; M2 |
| sos_contacts | Up to six favorites; 2×2 / 6×3 / 12×6 | contactIds, order, dialAction | Large tiles; monochrome names. Empty no contacts, loading selection, error denied/missing. | Contacts optional; M2 |
| calls | Recents/favorites; 3×2 / 6×3 / 12×6 | showRecent, favoritesFallback | Recent ten if eligible; monochrome favorites. Empty no entries, loading log, error policy/permission fallback. | Call log conditional; M2 |
| notifications | Filtered feed; 3×2 / 6×3 / 12×6 | priorityFilter, displayMode | Cards; monochrome priority labels. Empty no events, loading listener, error denied/redacted. | Listener; M2 |
| maps_shortcut | Maps handoff; 2×1 / 3×2 / 6×3 | destination, label | Launch affordance; monochrome. Empty no destination still launches Maps, loading app resolution, error Maps absent. | External Maps app; M2 |
| turn_guidance | Experimental cue; 3×2 / 6×3 / 12×5 | showDistance, showLabel | Cue/freshness; monochrome stale marker. Empty INACTIVE, loading LISTENING, error STALE/UNAVAILABLE. | Listener; M2 |
| battery | Local percentage/charge; 2×1 / 3×1 / 6×3 | showCharging | Percentage; monochrome. Empty/loader only during first read; error unavailable. | None; M1 |
| assistant | Assistant entry; 2×1 / 3×2 / 6×3 | buttonPlacement | M1 simulated states; monochrome state labels. Empty idle, loading simulated listening, error simulated error. | Microphone M3; M3 |

M1 tests must never interpret mock data as live native integration. See [PRD](PRD.md), [design](DESIGN_SYSTEM.md) and [capabilities](ANDROID_CAPABILITIES.md).

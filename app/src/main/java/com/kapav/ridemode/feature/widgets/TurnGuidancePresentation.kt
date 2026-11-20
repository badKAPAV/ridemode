package com.kapav.ridemode.feature.widgets

import com.kapav.ridemode.data.GuidanceAccess
import com.kapav.ridemode.data.TurnGuidanceState

internal const val TURN_GUIDANCE_IDLE_PROMPT = "Where do you wanna go?"
internal data class TurnGuidancePresentation(val active: Boolean, val instruction: String,
    val distance: String? = null, val arrivalTime: String? = null, val details: List<String> = emptyList())

/** Only current, recognizable directions may render as guidance. Never synthesize route metrics. */
internal fun turnGuidancePresentation(state: TurnGuidanceState): TurnGuidancePresentation {
    if (state.access != GuidanceAccess.CURRENT || state.maneuver == null || state.instruction.isBlank())
        return TurnGuidancePresentation(false, TURN_GUIDANCE_IDLE_PROMPT)
    val instruction = state.instruction.trim()
    val distance = state.distance?.trim()?.takeIf { it.isNotEmpty() }
    val arrivalTime = state.eta?.trim()?.takeIf { it.isNotEmpty() }
    val details = (listOfNotNull(state.remaining, state.destination) + state.details)
        .flatMap { it.split('·', '|') }.map(String::trim)
        .filterNot { part -> arrivalTime != null &&
            part.replace(Regex("^ETA[:\\s]+", RegexOption.IGNORE_CASE), "").equals(arrivalTime, true) }
        .filter { it.isNotEmpty() && !it.equals(instruction, true) && !it.equals(distance, true) }
        .distinctBy { it.lowercase(java.util.Locale.ROOT) }
    val concise = details.filter { line -> details.none { other ->
        other.length > line.length && Regex(
            "(?<![\\p{L}\\p{N}])" + Regex.escape(line) + "(?![\\p{L}\\p{N}])",
            RegexOption.IGNORE_CASE).containsMatchIn(other)
    } }
    return TurnGuidancePresentation(true, instruction, distance, arrivalTime, concise)
}

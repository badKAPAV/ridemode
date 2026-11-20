package com.kapav.ridemode.core.util

const val CONFIRMATION_TIMEOUT_SECONDS = 5

/** Monotonic deadlines stay expired even when UI/coroutine ticks are delayed. */
fun confirmationSecondsRemaining(deadlineMillis: Long, nowMillis: Long): Int =
    ((deadlineMillis - nowMillis + 999).coerceAtLeast(0) / 1_000).toInt()

fun confirmationFractionRemaining(deadlineMillis: Long, nowMillis: Long): Float =
    ((deadlineMillis - nowMillis).toFloat() / (CONFIRMATION_TIMEOUT_SECONDS * 1_000)).coerceIn(0f, 1f)

package com.elxvro.scan.scanner

data class LowLightState(
    val isLowLight: Boolean = false,
    val darkStreak: Int = 0,
    val brightStreak: Int = 0
)

object LowLightPolicy {
    private const val ENTER_THRESHOLD = 45
    private const val EXIT_THRESHOLD = 68
    private const val REQUIRED_STREAK = 3

    fun update(state: LowLightState, luma: Int): LowLightState {
        val value = luma.coerceIn(0, 255)
        return if (!state.isLowLight) {
            when {
                value <= ENTER_THRESHOLD -> {
                    val streak = state.darkStreak + 1
                    LowLightState(
                        isLowLight = streak >= REQUIRED_STREAK,
                        darkStreak = if (streak >= REQUIRED_STREAK) 0 else streak,
                        brightStreak = 0
                    )
                }
                else -> state.copy(darkStreak = 0, brightStreak = 0)
            }
        } else {
            when {
                value >= EXIT_THRESHOLD -> {
                    val streak = state.brightStreak + 1
                    LowLightState(
                        isLowLight = streak < REQUIRED_STREAK,
                        darkStreak = 0,
                        brightStreak = if (streak >= REQUIRED_STREAK) 0 else streak
                    )
                }
                else -> state.copy(darkStreak = 0, brightStreak = 0)
            }
        }
    }
}

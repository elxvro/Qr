package com.elxvro.scan

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

class ScanFeedback(private val context: Context) : AutoCloseable {
    private var tone: ToneGenerator? = null

    fun play(settings: ScanSettings) {
        if (settings.sound) {
            if (tone == null) tone = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 65)
            tone?.startTone(ToneGenerator.TONE_PROP_BEEP, 80)
        }
        if (settings.vibrate) {
            val vibrator = context.getSystemService(Vibrator::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(55, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(55)
            }
        }
    }

    override fun close() {
        tone?.release()
        tone = null
    }
}

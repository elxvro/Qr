package com.elxvro.scan.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.LoadAdError

class InterstitialAdController(context: Context) {
    private val appContext = context.applicationContext
    private val prefs = appContext.getSharedPreferences("elxvro_scan_ads", Context.MODE_PRIVATE)

    private var interstitialAd: InterstitialAd? = null
    private var loading = false
    private var showing = false

    fun preload() {
        if (loading || interstitialAd != null || showing) return
        loading = true
        InterstitialAd.load(
            appContext,
            AdConfig.INTERSTITIAL_ID,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    loading = false
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    loading = false
                    interstitialAd = null
                }
            }
        )
    }

    fun onEligibleAction(activity: Activity) {
        val count = prefs.getInt(KEY_ACTION_COUNT, 0) + 1
        prefs.edit().putInt(KEY_ACTION_COUNT, count).apply()

        val now = System.currentTimeMillis()
        val lastShown = prefs.getLong(KEY_LAST_SHOWN_AT, 0L)
        if (!AdFrequencyPolicy.shouldShow(count, lastShown, now)) {
            preload()
            return
        }

        val ad = interstitialAd
        if (ad == null || showing) {
            preload()
            return
        }

        showing = true
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                prefs.edit()
                    .putInt(KEY_ACTION_COUNT, 0)
                    .putLong(KEY_LAST_SHOWN_AT, System.currentTimeMillis())
                    .apply()
                interstitialAd = null
            }

            override fun onAdDismissedFullScreenContent() {
                showing = false
                interstitialAd = null
                preload()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                showing = false
                interstitialAd = null
                preload()
            }
        }
        ad.show(activity)
    }

    fun clear() {
        interstitialAd = null
        loading = false
        showing = false
    }

    private companion object {
        const val KEY_ACTION_COUNT = "eligible_action_count"
        const val KEY_LAST_SHOWN_AT = "last_interstitial_at"
    }
}

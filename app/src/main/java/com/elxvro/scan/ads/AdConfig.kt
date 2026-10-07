package com.elxvro.scan.ads

import com.elxvro.scan.BuildConfig

object AdConfig {
    private const val TEST_BANNER_ID = "ca-app-pub-3940256099942544/9214589741"
    private const val TEST_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712"

    private const val PRODUCTION_BANNER_ID = "ca-app-pub-1306515175084399/6105367552"

    val BANNER_ID: String
        get() = if (BuildConfig.DEBUG) TEST_BANNER_ID else PRODUCTION_BANNER_ID

    // Real interstitial unit ID has not been supplied yet, so both builds keep Google's demo interstitial.
    const val INTERSTITIAL_ID = TEST_INTERSTITIAL_ID
}

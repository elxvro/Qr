package com.elxvro.scan.ads

import com.elxvro.scan.BuildConfig

object AdConfig {
    val BANNER_ID: String
        get() = BuildConfig.ADMOB_BANNER_ID

    val INTERSTITIAL_ID: String
        get() = BuildConfig.ADMOB_INTERSTITIAL_ID
}

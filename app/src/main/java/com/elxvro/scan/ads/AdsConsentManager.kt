package com.elxvro.scan.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AdsConsentManager private constructor(context: Context) {
    private val appContext = context.applicationContext
    private val consentInformation = UserMessagingPlatform.getConsentInformation(appContext)
    private val gatheringStarted = AtomicBoolean(false)
    private val mobileAdsInitialized = AtomicBoolean(false)

    private val _canRequestAds = MutableStateFlow(false)
    val canRequestAds: StateFlow<Boolean> = _canRequestAds.asStateFlow()

    private val _privacyOptionsRequired = MutableStateFlow(false)
    val privacyOptionsRequired: StateFlow<Boolean> = _privacyOptionsRequired.asStateFlow()

    fun gatherConsent(activity: Activity) {
        if (gatheringStarted.getAndSet(true)) {
            publishState()
            initializeAdsIfAllowed()
            return
        }

        val params = ConsentRequestParameters.Builder().build()
        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                publishState()
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) {
                    publishState()
                    initializeAdsIfAllowed()
                }
            },
            {
                publishState()
                initializeAdsIfAllowed()
            }
        )
    }

    fun showPrivacyOptionsForm(activity: Activity) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity) {
            publishState()
            initializeAdsIfAllowed()
        }
    }

    private fun publishState() {
        _canRequestAds.value = consentInformation.canRequestAds()
        _privacyOptionsRequired.value =
            consentInformation.privacyOptionsRequirementStatus ==
                ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
    }

    private fun initializeAdsIfAllowed() {
        if (!_canRequestAds.value) return
        if (mobileAdsInitialized.getAndSet(true)) return
        MobileAds.initialize(appContext) {}
    }

    companion object {
        @Volatile
        private var instance: AdsConsentManager? = null

        fun getInstance(context: Context): AdsConsentManager =
            instance ?: synchronized(this) {
                instance ?: AdsConsentManager(context).also { instance = it }
            }
    }
}

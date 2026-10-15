package com.elxvro.scan.ads

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.elxvro.scan.BuildConfig
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import kotlinx.coroutines.delay

@Composable
fun AdBanner(modifier: Modifier = Modifier) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var failedToLoad by remember { mutableStateOf(false) }
    var retryAttempt by remember { mutableIntStateOf(0) }

    val adView = remember(context) {
        AdView(context).apply {
            adUnitId = AdConfig.BANNER_ID
            setAdSize(AdSize.BANNER)
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    failedToLoad = false
                    retryAttempt = 0
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    failedToLoad = true
                }
            }
            loadAd(AdRequest.Builder().build())
        }
    }

    DisposableEffect(adView) {
        onDispose { adView.destroy() }
    }

    LaunchedEffect(failedToLoad, retryAttempt, adView) {
        if (!failedToLoad) return@LaunchedEffect
        val waitMillis = AdRetryPolicy.delayForAttempt(retryAttempt) ?: return@LaunchedEffect
        delay(waitMillis)
        failedToLoad = false
        retryAttempt += 1
        adView.loadAd(AdRequest.Builder().build())
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        if (BuildConfig.DEBUG && failedToLoad) {
            Text(
                text = "Test reklamı yüklenemedi • ağ/VPN kontrolü",
                color = Color(0xFF667085),
                style = MaterialTheme.typography.labelSmall
            )
        } else {
            AndroidView(factory = { adView })
        }
    }
}

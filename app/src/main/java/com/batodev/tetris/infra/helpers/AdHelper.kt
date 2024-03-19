package com.batodev.tetris.infra.helpers

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.core.content.edit
import com.batodev.tetris.infra.settings.SettingsData
import com.batodev.tetris.infra.settings.SettingsHelper

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

private const val AD_ID = "ca-app-pub-9667420067790140/4371571769"
var ad: InterstitialAd? = null
const val WHEN_TO_SHOW_ADS_DEFAULT = 2

object AdHelper {
    fun showAddIfNeeded(activity: Activity) {
        val settings = SettingsHelper.load(activity)
        showAddIfNeeded(activity, settings)
        SettingsHelper.save(activity, settings)
    }

    fun showAddIfNeeded(
        activity: Activity,
        settings: SettingsData
    ) {
        var adCounter = settings.adCounter
        val whenToShowAds = settings.whenToShowAds
        Log.d(AdHelper.javaClass.simpleName, "loaded AD_COUNTER: $adCounter")
        Log.d(AdHelper.javaClass.simpleName, "loaded WHEN_TO_SHOW_ADS: $whenToShowAds")
        if (adCounter++ > whenToShowAds) {
            ad?.show(activity)
            Log.d(AdHelper.javaClass.simpleName, "showing ad: $ad")
            loadAd(activity)
            adCounter = 0
        }
        settings.adCounter = adCounter
    }

    fun loadAd(activity: Activity) {
        val adRequest: AdRequest = AdRequest.Builder().build()

        InterstitialAd.load(activity, AD_ID, adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    // The mInterstitialAd reference will be null until
                    // an ad is loaded.
                    Log.i(AdHelper::class.simpleName, "onAdLoaded: $interstitialAd")
                    ad = interstitialAd
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Handle the error
                    Log.w(AdHelper::class.simpleName, "onAdFailedToLoad: $loadAdError")
                }
            })
    }
}
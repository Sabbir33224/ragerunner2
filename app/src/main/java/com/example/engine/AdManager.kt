package com.example.engine

class AdManager {
    fun showInterstitialAd() {
        // Production stub for AdMob interstitial
    }

    fun showRewardedAd(onReward: () -> Unit) {
        // Production stub for AdMob rewarded ad
        onReward()
    }
}

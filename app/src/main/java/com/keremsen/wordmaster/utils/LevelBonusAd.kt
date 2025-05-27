import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.AdError
import android.util.Log
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback

fun loadAndShowRewardedInterstitialAd(
    context: Context,
    activity: Activity,
    adUnitId: String = "ca-app-pub-3940256099942544/5354046379", // Ödüllü geçiş test ID
    onRewardEarned: (rewardAmount: Int, rewardType: String) -> Unit,
    onAdDismissed: () -> Unit
) {
    val adRequest = AdRequest.Builder().build()

    RewardedInterstitialAd.load(context, adUnitId, adRequest,
        object : RewardedInterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedInterstitialAd) {
                ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        onAdDismissed()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        Log.e("AdMob", "Reklam gösterilemedi: ${adError.message}")
                        onAdDismissed()
                    }
                }

                // Reklamı göster ve ödül callback'ini ayarla
                ad.show(activity) { rewardItem ->
                    onRewardEarned(rewardItem.amount, rewardItem.type)
                }
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.e("AdMob", "Reklam yüklenemedi: ${adError.message}")
                onAdDismissed()
            }
        }
    )
}

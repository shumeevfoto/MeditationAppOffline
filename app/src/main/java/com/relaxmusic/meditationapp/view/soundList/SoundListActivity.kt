package com.relaxmusic.meditationapp.view.soundList

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.relaxmusic.meditationapp.*
import com.relaxmusic.meditationapp.background.sound.SoundService
import com.relaxmusic.meditationapp.base.BaseActivity
import com.relaxmusic.meditationapp.view.onboarding.OnboardingActivity
import com.relaxmusic.meditationapp.view.settings.SettingsActivity
import com.relaxmusic.meditationapp.view.soundCombination.SoundCombinationActivity
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.activity_sound_list.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class SoundListActivity : BaseActivity() {


    private var _initialAdWatched = false

    lateinit var _soundInterstitalAd: InterstitialAd
    lateinit var _soundAdapter: SoundListAdapter

    // milisec * sec * min * hours * days
    val TIME_TO_NEXT_RATE_MESSAGE = 1000 * 60 * 60 * 24 * 3
    val NUMBER_OF_FREE_ADS = 1


    fun isLocked(soundId: String): Boolean {
        if (preferences().isPurchased()) return false
        val sounds = _soundAdapter.soundsIds
        if (sounds.indexOf(soundId) < NUMBER_OF_FREE_ADS) return false
        return !preferences().getUnlockedSounds().contains(soundId)
    }

    fun openLockedSound(soundId: String) {
        _soundInterstitalAd.adListener = object : AdListener() {
            override fun onAdLoaded() {
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                _soundInterstitalAd.loadAd(AdRequest.Builder().build())
            }

            override fun onAdOpened() {
            }

            override fun onAdLeftApplication() {
            }

            override fun onAdClosed() {
                preferences().addUnlockedSound(soundId)
                toast(preferences().getUnlockedSounds().size.toString())
                _soundAdapter.notifyItemChanged(_soundAdapter.soundsIds.indexOf(soundId))
                _soundInterstitalAd.loadAd(AdRequest.Builder().build())
                openUnlockedSound(soundId)
            }
        }

        if (_soundInterstitalAd.isLoaded) {
            _soundInterstitalAd.show()
        } else {
            toast("Ad not loaded")
        }

    }

    fun openUnlockedSound(soundId: String) {
        startActivity<SoundCombinationActivity>("soundId" to soundId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setLanguage(preferences().getLanguage())
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sound_list)


        MobileAds.initialize(
            this,
            getString(R.string.admob_account_id)
        )


        _soundInterstitalAd = InterstitialAd(this)
        val interstitialAd = InterstitialAd(this)
        if (!preferences().isPurchased()) {
            _soundInterstitalAd.adUnitId = getString(R.string.admob_initaial_sound_id)
            _soundInterstitalAd.loadAd(
                AdRequest.Builder()
                    .addTestDevice("0DC86204419E0F6C995FC85EB3744EAF").build()
            )
            interstitialAd.adUnitId = getString(R.string.admob_initaial_interstitial_id)
            interstitialAd.loadAd(
                AdRequest.Builder()
                    .addTestDevice("0DC86204419E0F6C995FC85EB3744EAF").build()
            )

            val adRequest = AdRequest.Builder().addTestDevice("0DC86204419E0F6C995FC85EB3744EAF").build()
            banner.loadAd(adRequest)
        }

        if (!preferences().getPolicyAccepted()) {
            startActivity<OnboardingActivity>()
            preferences().setTimeToShowRate(System.currentTimeMillis() + TIME_TO_NEXT_RATE_MESSAGE)
        } else {
            interstitialAd.adListener = object : AdListener() {
                override fun onAdLoaded() {
                    if (!_initialAdWatched) {
                        interstitialAd.show()
                    }
                }

                override fun onAdFailedToLoad(errorCode: Int) {
                    interstitialAd.loadAd(AdRequest.Builder().build())
                }

                override fun onAdOpened() {
                }

                override fun onAdLeftApplication() {
                }

                override fun onAdClosed() {
                    _initialAdWatched = true
                    interstitialAd.loadAd(AdRequest.Builder().build())
                }
            }
        }

        if (preferences().getPolicyAccepted() && preferences().getTimeToShowRate() < System.currentTimeMillis()) {
            RateDialog(this).show()
            preferences().setTimeToShowRate(System.currentTimeMillis() + TIME_TO_NEXT_RATE_MESSAGE)
        }


        if (isServiceRunning(SoundService::class.java)) {
            startActivity<SoundCombinationActivity>("already_running" to true)
        }

        _soundAdapter = SoundListAdapter(assets, this)
        rv_sounds.adapter = _soundAdapter
        iv_settings.setOnClickListener {
            startActivity<SettingsActivity>()
        }

        tv_go_play_market.setOnClickListener {
            val appPackageName = getString(R.string.play_market_developer_id)
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://developer?id=$appPackageName")))
            } catch (anfe: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/developer?id=$appPackageName")
                    )
                )
            }

        }
    }

    override fun onResume() {
        super.onResume()
        if (preferences().isPurchased()) {
            banner.gone()
        } else {
            banner.visible()
        }
    }

}

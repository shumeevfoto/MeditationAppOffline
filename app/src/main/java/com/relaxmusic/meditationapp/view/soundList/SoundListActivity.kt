package com.relaxmusic.meditationapp.view.soundList

import android.os.Bundle
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.relaxmusic.meditationapp.*
import com.relaxmusic.meditationapp.background.sound.SoundService
import com.relaxmusic.meditationapp.base.BaseActivity
import com.relaxmusic.meditationapp.view.settings.SettingsActivity
import com.relaxmusic.meditationapp.view.soundCombination.SoundCombinationActivity
import kotlinx.android.synthetic.main.activity_sound_list.*
import org.jetbrains.anko.startActivity

class SoundListActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setLanguage(preferences().getLanguage())
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sound_list)


        MobileAds.initialize(
            this,
            getString(R.string.admob_account_id)
        )


        if (preferences().isPurchased()) {
        } else {
            val adRequest = AdRequest.Builder().addTestDevice("0DC86204419E0F6C995FC85EB3744EAF").build()
            banner.loadAd(adRequest)
        }

        if (isServiceRunning(SoundService::class.java)) {
            startActivity<SoundCombinationActivity>("already_running" to true)
        }

        rv_sounds.adapter = SoundListAdapter(assets)
        iv_settings.setOnClickListener {
            startActivity<SettingsActivity>()
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
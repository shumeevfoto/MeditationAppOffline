package com.relaxmusic.meditationapp.view.soundCombination.combineViews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.relaxmusic.meditationapp.PlayerInfo
import com.relaxmusic.meditationapp.R
import com.relaxmusic.meditationapp.getSoundIcon
import com.relaxmusic.meditationapp.view.soundCombination.SoundCombinationActivity
import com.relaxmusic.meditationapp.view.soundCombination.volumeManager.VolumeManagerDialog
import kotlinx.android.synthetic.main.additional_sound_view_layout.view.*

class AdditionalSoundView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.additional_sound_view_layout, this, true)

        setOnClickListener {
            VolumeManagerDialog(context as SoundCombinationActivity).show()
        }

    }

    fun setPlayerInfo(soundId: PlayerInfo) {
        val drawable = context.getSoundIcon(soundId.soundId + "_white")
        iv_sound.setImageDrawable(drawable)
        tv_volume.text = (soundId.volume * 100).toInt().toString()
    }

    fun setVolume(volume: Float) {
        tv_volume.text = (volume * 100).toInt().toString()
    }

}
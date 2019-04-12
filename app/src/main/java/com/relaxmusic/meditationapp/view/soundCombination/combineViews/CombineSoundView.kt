package com.relaxmusic.meditationapp.view.soundCombination.combineViews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.relaxmusic.meditationapp.PlayerInfo
import com.relaxmusic.meditationapp.R
import kotlinx.android.synthetic.main.combine_sound_layout.view.*


class CombineSoundView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var soundIds = mutableListOf<PlayerInfo>()

    init {
        LayoutInflater.from(context).inflate(R.layout.combine_sound_layout, this, true)
    }

    fun addSound(soundId: String) {
        soundIds.add(PlayerInfo(soundId, 0.5f))
        setAdditionalSounds(soundIds)
    }

    fun removeSound(soundId: String) {
        soundIds.remove(soundIds.find { it.soundId == soundId })
        setAdditionalSounds(soundIds)
    }

    fun setAdditionalSounds(sounds: MutableList<PlayerInfo>) {
        soundIds = sounds
        root.removeAllViews()
        if (soundIds.size <= 1) {
            val addSoundView = AddSoundView(context)
            root.addView(addSoundView)
        } else if (soundIds.size == 4) {
            soundIds.drop(1).forEach {
                val additionalSoundView = AdditionalSoundView(context)
                additionalSoundView.setPlayerInfo(it)
                root.addView(additionalSoundView)
            }
        } else {
            soundIds.drop(1).forEach {
                val additionalSoundView = AdditionalSoundView(context)
                additionalSoundView.setPlayerInfo(it)
                root.addView(additionalSoundView)
            }
            val addSoundView = AddSoundView(context)
            root.addView(addSoundView)
        }


    }

    fun setVolume(soundId: String, volume: Float) {
        val playerInfo = soundIds.find { it.soundId == soundId }
        val index = soundIds.indexOf(playerInfo)
        playerInfo?.volume = volume
        (root.getChildAt(index - 1) as AdditionalSoundView?)?.setVolume(volume)
    }

}
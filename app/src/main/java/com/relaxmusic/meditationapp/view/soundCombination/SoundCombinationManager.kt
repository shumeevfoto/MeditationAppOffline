package com.relaxmusic.meditationapp.view.soundCombination

import android.content.Context
import com.relaxmusic.meditationapp.PlayerInfo

interface SoundCombinationManager {

    fun getAllSoundIds() : List<String>

    fun getAllPlayerInfo() : List<PlayerInfo>

    fun setVolumeForPlayer(soundId: String, volume: Float)

    fun removeSound(soundId: String)

    fun playSound(categoryId: String, soundId: String)

    fun getContext(): Context

}
package com.relaxmusic.meditationapp.view.soundCombination.volumeManager

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.SeekBar
import com.relaxmusic.meditationapp.PlayerInfo
import com.relaxmusic.meditationapp.R
import com.relaxmusic.meditationapp.getSoundIcon
import com.relaxmusic.meditationapp.view.soundCombination.SoundCombinationManager
import kotlinx.android.synthetic.main.additional_volume_view_layout.view.*

class AdditionalVolumeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.additional_volume_view_layout, this, true)
    }

    fun setup(soundId: PlayerInfo, soundCombinationManager: SoundCombinationManager) {
        iv_icon.setImageDrawable(context.getSoundIcon(soundId.soundId))

        seekBar.max = 100
        seekBar.progress = (soundId.volume * 100).toInt()
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(arg0: SeekBar) {}

            override fun onStartTrackingTouch(arg0: SeekBar) {}

            override fun onProgressChanged(arg0: SeekBar, progress: Int, arg2: Boolean) {
                soundCombinationManager.setVolumeForPlayer(soundId.soundId, progress / 100.toFloat())
            }
        })

    }

}
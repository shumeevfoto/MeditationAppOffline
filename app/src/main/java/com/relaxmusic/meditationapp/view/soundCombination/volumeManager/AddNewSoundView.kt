package com.relaxmusic.meditationapp.view.soundCombination.volumeManager

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.relaxmusic.meditationapp.R

class AddNewSoundView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.add_new_sound_layout, this, true)
    }


}
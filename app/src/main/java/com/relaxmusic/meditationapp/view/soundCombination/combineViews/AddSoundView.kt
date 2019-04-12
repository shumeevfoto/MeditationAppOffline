package com.relaxmusic.meditationapp.view.soundCombination.combineViews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.relaxmusic.meditationapp.R
import com.relaxmusic.meditationapp.view.soundCombination.SoundCombinationActivity
import com.relaxmusic.meditationapp.view.soundCombination.SoundListDialog

class AddSoundView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.add_sound_view_layout, this, true)

        setOnClickListener { SoundListDialog(context as SoundCombinationActivity).show() }

    }

}
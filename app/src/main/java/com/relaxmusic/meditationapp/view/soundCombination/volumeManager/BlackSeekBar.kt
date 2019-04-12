package com.relaxmusic.meditationapp.view.soundCombination.volumeManager

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.widget.SeekBar

class BlackSeekBar : SeekBar {

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {

        progressDrawable.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        thumb.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);

    }

}

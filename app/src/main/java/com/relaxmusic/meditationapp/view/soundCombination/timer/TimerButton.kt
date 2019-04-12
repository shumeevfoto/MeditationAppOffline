package com.relaxmusic.meditationapp.view.soundCombination.timer

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.relaxmusic.meditationapp.R
import com.relaxmusic.meditationapp.gone
import com.relaxmusic.meditationapp.visible
import kotlinx.android.synthetic.main.timer_button_layout.view.*

class TimerButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.timer_button_layout, this, true)
    }

    fun setTimer(time : String) {
        tv_timer.visible()
        iv_timer.gone()
        tv_timer.text = time
    }

    fun stopTimer() {
        tv_timer.gone()
        iv_timer.visible()
        tv_timer.text = ""
    }

}
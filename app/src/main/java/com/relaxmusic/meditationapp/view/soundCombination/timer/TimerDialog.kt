package com.relaxmusic.meditationapp.view.soundCombination.timer

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.widget.RadioButton
import com.relaxmusic.meditationapp.R
import kotlinx.android.synthetic.main.dialog_timer_layout.*


class TimerDialog(val timeResultListener: TimeResultListener) : Dialog(timeResultListener.getContext()) {

    val map = mapOf(
        R.string.no_timer to -1,
        R.string.t_5_min to 60 * 5,
        R.string.t_10_min to 60 * 10,
        R.string.t_15_min to 60 * 15,
        R.string.t_20_min to 60 * 20,
        R.string.t_30_min to 60 * 30,
        R.string.t_40_min to 60 * 40,
        R.string.t_1_hour to 60 * 60 * 1,
        R.string.t_2_hour to 60 * 60 * 2,
        R.string.t_4_hour to 60 * 60 * 3,
        R.string.t_8_hour to 60 * 60 * 4
    )

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_timer_layout)

        map.forEach {
            val radioButton = LayoutInflater.from(context).inflate(R.layout.timer_radio_button, null) as RadioButton
            radioButton.text = context.getString(it.key)
            radioButton.setOnClickListener { view ->
                timeResultListener.onTimerDialogResult(it)
                dismiss()
            }
            rg_holder.addView(radioButton)
        }

    }
}
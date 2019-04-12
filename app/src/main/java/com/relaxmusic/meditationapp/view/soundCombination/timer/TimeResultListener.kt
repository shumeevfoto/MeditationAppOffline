package com.relaxmusic.meditationapp.view.soundCombination.timer

import android.content.Context

interface TimeResultListener {

    fun onTimerDialogResult(result: Map.Entry<Int, Int>)

    fun getContext() : Context

}
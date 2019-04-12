package com.relaxmusic.meditationapp.base

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.relaxmusic.meditationapp.getLanguage
import com.relaxmusic.meditationapp.preferences
import org.jetbrains.anko.AnkoLogger


@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity(), AnkoLogger {

    private var mCurrentLocale: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mCurrentLocale = preferences().getLanguage()
    }

    override fun onResume() {
        super.onResume()
        val locale = preferences().getLanguage()
        if (locale != mCurrentLocale) {

            mCurrentLocale = locale
            recreate()
        }
    }
}
package com.relaxmusic.meditationapp.view.settings

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.RadioButton
import com.relaxmusic.meditationapp.*
import com.relaxmusic.meditationapp.base.BaseActivity
import kotlinx.android.synthetic.main.language_dialog_layout.*


class LanguageDialog(val context: BaseActivity) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.language_dialog_layout)

        val language = context.preferences().getLanguage()

        LangUtils.langs.forEach {
            val radioButton = LayoutInflater.from(context).inflate(R.layout.timer_radio_button, null) as RadioButton
            radioButton.text = context.getString(it.key)
            if (it.value == language) {
                radioButton.isChecked = true
            }
            radioButton.setOnClickListener { view ->
                context.setLanguage(it.value)
                context.preferences().setLanguage(it.value)
                context.recreate()
                dismiss()
            }
            rg_holder.addView(radioButton)
        }

    }

}
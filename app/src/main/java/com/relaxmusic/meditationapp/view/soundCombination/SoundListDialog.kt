package com.relaxmusic.meditationapp.view.soundCombination

import android.app.Dialog
import android.os.Bundle
import com.relaxmusic.meditationapp.R
import com.relaxmusic.meditationapp.getCategories
import kotlinx.android.synthetic.main.sound_list_dialog_layout.*

class SoundListDialog(val soundCombinationManager: SoundCombinationManager) : Dialog(soundCombinationManager.getContext()) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sound_list_dialog_layout)

        tv_close.setOnClickListener {
            dismiss()
        }

        context.assets.getCategories()?.forEach {
            val categoryView = CategoryView(context)
            categoryView.setCategory(it, soundCombinationManager, this)
            ll_category_holder.addView(categoryView)
        }

    }



}
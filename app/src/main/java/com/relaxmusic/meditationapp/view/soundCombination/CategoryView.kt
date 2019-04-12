package com.relaxmusic.meditationapp.view.soundCombination

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import com.relaxmusic.meditationapp.R
import com.relaxmusic.meditationapp.getCategorySoundsList
import com.relaxmusic.meditationapp.getSoundIcon
import com.relaxmusic.meditationapp.getSoundName
import com.relaxmusic.meditationapp.view.soundCombination.volumeManager.VolumeManagerDialog
import kotlinx.android.synthetic.main.category_view_layout.view.*

class CategoryView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var allSoundIds: List<String> = emptyList()

    init {
        LayoutInflater.from(context).inflate(R.layout.category_view_layout, this)
    }

    @SuppressLint("InflateParams")
    fun setCategory(
        categoryId: String,
        soundCombinationManager: SoundCombinationManager,
        soundListDialog: SoundListDialog
    ) {
        tv_category.text = context.getSoundName(categoryId)
        allSoundIds = soundCombinationManager.getAllSoundIds()
        context.assets.getCategorySoundsList(categoryId)?.forEach { soundId ->
            if (!allSoundIds.contains(soundId)) {
                val imageView = LayoutInflater.from(context).inflate(R.layout.additional_sound_list_view_layout, null) as ImageView
                imageView.setImageDrawable(context.getSoundIcon(soundId))
                imageView.setOnClickListener {
                    soundCombinationManager.playSound(categoryId, soundId)
                    gl_holder.removeView(it)
                    allSoundIds = soundCombinationManager.getAllSoundIds()
                    soundListDialog.dismiss()
                    VolumeManagerDialog(soundCombinationManager, soundId).show()
                }
                gl_holder.addView(imageView)
            }
        }
    }

}
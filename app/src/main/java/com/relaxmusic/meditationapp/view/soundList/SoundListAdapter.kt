package com.relaxmusic.meditationapp.view.soundList

import android.content.res.AssetManager
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.relaxmusic.meditationapp.*
import com.relaxmusic.meditationapp.view.soundCombination.SoundCombinationActivity
import kotlinx.android.synthetic.main.sound_view_holder_layout.view.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast


class SoundListAdapter(private val assetManager: AssetManager, val activity: SoundListActivity) :
    RecyclerView.Adapter<SoundListAdapter.SoundViewHolder>() {

    val soundsIds = assetManager.getSounds() ?: emptyArray()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoundViewHolder {
        return SoundViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.sound_view_holder_layout,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = soundsIds.size

    override fun onBindViewHolder(holder: SoundViewHolder, position: Int) {
        holder.bind(soundsIds[position])
    }

    inner class SoundViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(soundId: String) {
            itemView.apply {
                val background = root.background as GradientDrawable
                background.color = ColorStateList(
                    arrayOf(intArrayOf()),
                    intArrayOf(context.getSoundBackgroundColor(soundId))
                )

                tv_sound_text.text = context.getSoundName(soundId)
                tv_sound_text.textColor = context.getSoundTextColor(soundId)

                iv_sound_image.borderColor = context.getSoundBorderColor(soundId)
                iv_sound_image.setImageBitmap(assetManager.getSoundImage(soundId))

                if (activity.isLocked(soundId)) {
                    holder_music_lock.visible()
                } else {
                    holder_music_lock.gone()
                }

                setOnClickListener {
                    if (activity.isLocked(soundId)) {
                        activity.openLockedSound(soundId)
                    } else {
                        activity.openUnlockedSound(soundId)
                    }
                }

            }
        }

    }

}
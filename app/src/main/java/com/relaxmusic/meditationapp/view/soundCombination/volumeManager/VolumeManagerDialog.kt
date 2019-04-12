package com.relaxmusic.meditationapp.view.soundCombination.volumeManager

import android.app.Dialog
import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.widget.SeekBar
import com.relaxmusic.meditationapp.*
import com.relaxmusic.meditationapp.view.soundCombination.SoundCombinationManager
import com.relaxmusic.meditationapp.view.soundCombination.SoundListDialog
import kotlinx.android.synthetic.main.additional_volume_view_layout.view.*
import kotlinx.android.synthetic.main.volume_manager_dialog_layout.*

class VolumeManagerDialog(val soundCombinationManager: SoundCombinationManager, val newSoundId: String?) :
    Dialog(soundCombinationManager.getContext()) {

    constructor(soundCombinationManager: SoundCombinationManager) : this(soundCombinationManager, null) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.volume_manager_dialog_layout)


        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        //max = 15
        sb_volume_general.max = audioManager
            .getStreamMaxVolume(AudioManager.STREAM_MUSIC)

        sb_volume_general.progress = audioManager
            .getStreamVolume(AudioManager.STREAM_MUSIC)

        sb_volume_general.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(arg0: SeekBar) {}

            override fun onStartTrackingTouch(arg0: SeekBar) {}

            override fun onProgressChanged(arg0: SeekBar, progress: Int, arg2: Boolean) {
                soundCombinationManager.setVolumeForPlayer("general", progress.toFloat())
            }
        })
        val allSoundIds = soundCombinationManager.getAllPlayerInfo().toMutableList()

        newSoundId?.let {
            allSoundIds.add(PlayerInfo(it, 0.5f))
        }

        val generalSoundId = allSoundIds[0]

        sb_volume_sound.max = 100
        sb_volume_sound.progress = 50
        iv_sound_image.borderColor = context.getSoundBorderColor(generalSoundId.soundId)
        iv_sound_image.setImageBitmap(context.assets.getSoundImage(generalSoundId.soundId))
        sb_volume_sound.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(arg0: SeekBar) {}

            override fun onStartTrackingTouch(arg0: SeekBar) {}

            override fun onProgressChanged(arg0: SeekBar, progress: Int, arg2: Boolean) {
                soundCombinationManager.setVolumeForPlayer(generalSoundId.soundId, progress / 100.toFloat())
            }
        })

        if (allSoundIds.size == 4) {
            allSoundIds.drop(1).forEach {
                createAdditionalVolumeView(it)
            }
        } else {
            allSoundIds.drop(1).forEach {
                createAdditionalVolumeView(it)
            }
            createAddNewSoundView()
        }

    }

    private fun createAdditionalVolumeView(soundId: PlayerInfo) {
        val additionalVolumeView = AdditionalVolumeView(context)
        additionalVolumeView.setup(soundId, soundCombinationManager)
        additionalVolumeView.iv_remove.setOnClickListener {
            soundCombinationManager.removeSound(soundId.soundId)
            if (ll_additional_holder.children.filter { it is AdditionalVolumeView }.count() == 3) {
                createAddNewSoundView()
            }
            ll_additional_holder.removeView(additionalVolumeView)
        }
        ll_additional_holder.addView(additionalVolumeView)
    }

    private fun createAddNewSoundView() {
        val addNewSoundView = AddNewSoundView(context)
        addNewSoundView.setOnClickListener {
            dismiss()
            SoundListDialog(soundCombinationManager).show()
        }
        ll_additional_holder.addView(addNewSoundView, 0)
    }

}
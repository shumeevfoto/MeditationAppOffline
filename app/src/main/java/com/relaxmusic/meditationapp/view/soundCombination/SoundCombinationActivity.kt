package com.relaxmusic.meditationapp.view.soundCombination

import android.content.*
import android.media.AudioManager
import android.os.Bundle
import android.os.IBinder
import android.view.KeyEvent
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import com.google.android.gms.ads.AdRequest
import com.relaxmusic.meditationapp.*
import com.relaxmusic.meditationapp.background.sound.SoundService
import com.relaxmusic.meditationapp.base.BaseActivity
import com.relaxmusic.meditationapp.view.soundCombination.timer.TimeResultListener
import com.relaxmusic.meditationapp.view.soundCombination.timer.TimerDialog
import kotlinx.android.synthetic.main.activity_sound_combination.*
import org.jetbrains.anko.info


class SoundCombinationActivity : BaseActivity(),
    TimeResultListener, SoundCombinationManager {
    override fun getAllPlayerInfo(): List<PlayerInfo> {
        return serviceConnection.playerPool?.getAllSoundIds() ?: emptyList()
    }

    override fun getAllSoundIds(): List<String> {
        return serviceConnection.playerPool?.getAllSoundIds()?.map { it.soundId } ?: emptyList()
    }

    override fun setVolumeForPlayer(soundId: String, volume: Float) {
        if (soundId == "general") {
            sb_volume.progress = volume.toInt()
        } else {
            serviceConnection.playerPool?.setVolumeForPlayer(soundId, volume)
            combine_sounds.setVolume(soundId, volume)
        }
    }

    override fun removeSound(soundId: String) {
        serviceConnection.playerPool?.removeSound(soundId)
        preferences().removeSound(soundId)
        combine_sounds.removeSound(soundId)
    }

    override fun playSound(categoryId: String, soundId: String) {
        if (isServiceRunning(SoundService::class.java)) {
            sendAdditionalSound(categoryId, soundId)
            combine_sounds.addSound(soundId)
        } else {
            startService(categoryId, soundId)
            preferences().newSound(getSoundId())
            preferences().addSound(soundId)
            combine_sounds.setAdditionalSounds(mutableListOf(PlayerInfo(getSoundId(), 0.5f), PlayerInfo(soundId, 0.5f)))
        }
    }


    private var playing = true

    override fun onTimerDialogResult(result: Map.Entry<Int, Int>) {
        if (isServiceRunning(SoundService::class.java)) {
            startTimer(result.value)
        } else {
            startService(result.value)
        }
    }

    private val timerUpdateReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val time = intent.getStringExtra("countdown")
            if (time == SoundService.SERVICE_STOPPED) {
                playing = false
                updateStopSoundUI()
            } else if (time == SoundService.SERVICE_STARTED) {
                playing = true
                updateStartSoundUI()
            } else if (time == SoundService.COUNTDOWN_STOPPED) {
                b_timer.stopTimer()
            } else {
                b_timer.setTimer(time)
            }

        }
    }


    private val serviceConnection = object : ServiceConnection {

        var playerPool: MediaPlayerPool? = null

        override fun onServiceDisconnected(name: ComponentName?) {
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as SoundService.SoundServiceBinder
            playerPool = binder.getPlayerPool()
            combine_sounds.setAdditionalSounds(playerPool?.getAllSoundIds()?.toMutableList() ?: mutableListOf())
        }

    }

    fun updateStartSoundUI() {
        b_stop.setImageResource(R.drawable.ic_pause_white_32dp)
    }

    fun updateStopSoundUI() {
        b_stop.setImageResource(R.drawable.ic_play_arrow_white_32dp)
    }

    override fun getContext(): Context = this

    private lateinit var audioManager: AudioManager


    override fun onStop() {
        super.onStop()
        unregisterReceiver(timerUpdateReceiver)
        unbindService(serviceConnection)
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(timerUpdateReceiver, IntentFilter(SoundService.COUNTDOWN_UPDATED))
        Intent(this, SoundService::class.java).also { intent ->
            bindService(intent, serviceConnection, 0)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        setLanguage(preferences().getLanguage())
        super.onCreate(savedInstanceState)
        volumeControlStream = AudioManager.STREAM_MUSIC;
        setContentView(R.layout.activity_sound_combination)


        if (preferences().isPurchased()) {
        } else {
            val adRequest = AdRequest.Builder().addTestDevice("0DC86204419E0F6C995FC85EB3744EAF").build()
            banner.loadAd(adRequest)
        }

        if (preferences().isPurchased()) {
            banner.gone()
        } else {
            banner.visible()
        }

        if (BuildConfig.DEBUG) {
            info { preferences().getGeneralSound() }
            info { preferences().getAdditionalSounds() }
        }

        if (isAlreadyRunning()) {
            intent.putExtra("soundId", preferences().getGeneralSound())
        }
        if (intent?.action != SoundService.ACTION_FROM_PUSH && !isAlreadyRunning()) {
            startService()
        }

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        iv_background.setImageBitmap(assets.getSoundImage(getSoundId()))
        tv_title.text = getSoundName(getSoundId())
        //max = 15
        sb_volume.max = audioManager
            .getStreamMaxVolume(AudioManager.STREAM_MUSIC)

        sb_volume.progress = audioManager
            .getStreamVolume(AudioManager.STREAM_MUSIC)

        sb_volume.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStopTrackingTouch(arg0: SeekBar) {}

            override fun onStartTrackingTouch(arg0: SeekBar) {}

            override fun onProgressChanged(arg0: SeekBar, progress: Int, arg2: Boolean) {
                audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    progress, 0
                )
            }
        })


        b_timer.setOnClickListener {
            TimerDialog(this).show()
        }

        b_stop.setOnClickListener {
            playing = if (playing) {
                b_stop.setImageResource(R.drawable.ic_play_arrow_white_32dp)
                stopService()
                false
            } else {
                b_stop.setImageResource(R.drawable.ic_pause_white_32dp)
                startService()
                true
            }
        }

    }

    private fun startService(categoryId: String, soundId: String) {
        startService(-1, categoryId, soundId)
    }

    private fun startService() {
        startService(-1, null, null)
    }

    private fun startService(time: Int) {
        startService(time, null, null)
    }

    private fun startService(time: Int, categoryId: String?, soundId: String?) {
        val intent = Intent(this, SoundService::class.java)
        intent.action = SoundService.ACTION_START_FOREGROUND_SERVICE
        intent.putExtra("soundId", getSoundId())
        preferences().newSound(getSoundId())
        if (soundId != null && categoryId != null) {
            intent.putExtra("additionalSound", soundId)
            intent.putExtra("additionalCategory", categoryId)
            preferences().addSound(soundId)
        }
        intent.putExtra("time", time)
        startService(intent)

        Intent(this, SoundService::class.java).also {
            bindService(it, serviceConnection, 0)
        }


        playing = true
    }

    private fun sendAdditionalSound(categoryId: String, soundId: String) {
        val intent = Intent(this, SoundService::class.java)
        intent.action = SoundService.ACTION_START_ADDITIONAL_SOUND
        intent.putExtra("additionalSound", soundId)
        intent.putExtra("additionalCategory", categoryId)
        startService(intent)

        preferences().addSound(soundId)
        playing = true
    }


    private fun startTimer(time: Int) {
        val intent = Intent(this, SoundService::class.java)
        intent.action = SoundService.ACTION_START_TIMER
        intent.putExtra("soundId", getSoundId())
        intent.putExtra("time", time)
        startService(intent)

    }

    private fun stopService() {
        val intent = Intent(this, SoundService::class.java)
        intent.action = SoundService.ACTION_STOP_FOREGROUND_SERVICE
        startService(intent)

        playing = false
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        val keyCode = event.keyCode
        val progress = sb_volume.progress
        return when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> {
                sb_volume.progress = progress + 1
                true
            }
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                sb_volume.progress = progress - 1
                true
            }
            else -> super.dispatchKeyEvent(event)
        }
    }

    private fun getSoundId() = intent.getStringExtra("soundId")

    private fun isAlreadyRunning() = intent.getBooleanExtra("already_running", false)

    override fun onBackPressed() {
        super.onBackPressed()
        if (isServiceRunning(SoundService::class.java)) {
            stopService()
        }
    }

}

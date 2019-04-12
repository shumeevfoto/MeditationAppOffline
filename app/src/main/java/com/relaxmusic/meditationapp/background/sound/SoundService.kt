package com.relaxmusic.meditationapp.background.sound

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Binder
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.relaxmusic.meditationapp.*
import com.relaxmusic.meditationapp.base.BaseService
import com.relaxmusic.meditationapp.view.soundCombination.SoundCombinationActivity


class SoundService : BaseService() {

    private var remoteViews: RemoteViews? = null
    private var builder: NotificationCompat.Builder? = null
    private var playerPool: MediaPlayerPool? = null
    private var timer: CountDownTimer? = null
    private var manager: NotificationManager? = null

    inner class SoundServiceBinder : Binder() {

        fun getPlayerPool() = playerPool

    }

    override fun onBind(intent: Intent): IBinder {
        return SoundServiceBinder()
    }

    override fun onCreate() {
        super.onCreate()
        playerPool = MediaPlayerPool(this, 4)
        manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onDestroy() {
        playerPool?.release()
        super.onDestroy()
    }

    fun startTimer(time: Int) {
        timer?.cancel()
        timer = object : CountDownTimer(time.toLong() * 1000, 1000) {
            override fun onFinish() {
                sendStopTimer()
                stopForegroundService()
            }

            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                val formattedTime = String.format("%02d:%02d", seconds % 3600 / 60, seconds % 60)
                val i = Intent(COUNTDOWN_UPDATED)
                i.putExtra("countdown", formattedTime)
                sendBroadcast(i)

                remoteViews?.setTextViewText(R.id.tv_timer, formattedTime)
                manager?.notify(NOTIFICATION_ID, builder?.build())
            }
        }.start()
    }

    fun sendStopTimer() {
        val i = Intent(COUNTDOWN_UPDATED)
        i.putExtra("countdown", COUNTDOWN_STOPPED)
        sendBroadcast(i)

        remoteViews?.setTextViewText(R.id.tv_timer, "")
        manager?.notify(NOTIFICATION_ID, builder?.build())
    }

    fun sendStartSound() {
        val i = Intent(COUNTDOWN_UPDATED)
        i.putExtra("countdown", SERVICE_STARTED)
        sendBroadcast(i)
    }
    fun sendStopSound() {
        val i = Intent(COUNTDOWN_UPDATED)
        i.putExtra("countdown", SERVICE_STOPPED)
        sendBroadcast(i)
    }

    private fun Intent?.getSoundId() = this?.getStringExtra("soundId") ?: ""

    private fun Intent?.getTime() = this?.getIntExtra("time", -1) ?: -1

    private fun Intent?.getAdditionalSound() = this?.getStringExtra("additionalSound")
    private fun Intent?.getAdditionalCategory() = this?.getStringExtra("additionalCategory")

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        when (action) {
            ACTION_START_FOREGROUND_SERVICE -> {
                startForegroundService(intent.getSoundId())
                val sound = assets.getSound(intent.getSoundId())
                sound?.let {
                    playerPool?.playSound(intent.getSoundId(), it)
                    sendStartSound()
                }
                val time = intent.getTime()
                if (time != -1) {
                    startTimer(time)
                } else {
                    timer?.cancel()
                    sendStopTimer()
                }

                val category = intent.getAdditionalCategory()
                val additionalSound = intent.getAdditionalSound()

                if (category != null && additionalSound != null) {
                    val categorySound = assets.getCategorySound(category, additionalSound)
                    categorySound?.let {
                        playerPool?.playSound(additionalSound, it)
                    }
                }


            }
            ACTION_START_ADDITIONAL_SOUND -> {
                val category = intent.getAdditionalCategory()
                val additionalSound = intent.getAdditionalSound()

                if (category != null && additionalSound != null) {
                    val categorySound = assets.getCategorySound(category, additionalSound)
                    categorySound?.let {
                        playerPool?.playSound(additionalSound, it)
                    }
                }
            }
            ACTION_STOP_FOREGROUND_SERVICE -> {
                stopForegroundService()
            }
            ACTION_PLAY -> {
                playerPool?.start()
                remoteViews?.setViewVisibility(R.id.iv_play, View.GONE)
                remoteViews?.setViewVisibility(R.id.iv_pause, View.VISIBLE)
                manager?.notify(NOTIFICATION_ID, builder?.build())
            }
            ACTION_PAUSE -> {
                playerPool?.pause()
                remoteViews?.setViewVisibility(R.id.iv_play, View.VISIBLE)
                remoteViews?.setViewVisibility(R.id.iv_pause, View.GONE)
                manager?.notify(NOTIFICATION_ID, builder?.build())
            }
            ACTION_START_TIMER -> {
                val time = intent.getTime()
                if (time != -1) {
                    startTimer(time)
                } else {
                    timer?.cancel()
                    sendStopTimer()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundService(soundId: String) {

        val intent = Intent(this, SoundCombinationActivity::class.java)
        intent.action = ACTION_FROM_PUSH
        intent.putExtra("soundId", soundId)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createNotificationChannel("sound_service", "Meditation Sound Service") else ""

        val playIntent = Intent(this, SoundService::class.java)
        playIntent.action = ACTION_PLAY
        val pendingPlayIntent = PendingIntent.getService(this, 0, playIntent, 0)

        val pauseIntent = Intent(this, SoundService::class.java)
        pauseIntent.action = ACTION_PAUSE
        val pendingPauseIntent = PendingIntent.getService(this, 0, pauseIntent, 0)

        val stopIntent = Intent(this, SoundService::class.java)
        stopIntent.action = ACTION_STOP_FOREGROUND_SERVICE
        val pendingStopIntent = PendingIntent.getService(this, 0, stopIntent, 0)

        remoteViews = RemoteViews(packageName, R.layout.sound_service_push_layout)
        remoteViews?.setOnClickPendingIntent(R.id.iv_play, pendingPlayIntent)
        remoteViews?.setOnClickPendingIntent(R.id.iv_pause, pendingPauseIntent)
        remoteViews?.setOnClickPendingIntent(R.id.iv_stop, pendingStopIntent)
        remoteViews?.setTextViewText(R.id.tv_status, getSoundName(soundId))
        builder = NotificationCompat.Builder(this, channelId)
            .setWhen(System.currentTimeMillis())
            .setContentTitle("Meditation Sounds")
            .setSmallIcon(R.drawable.ic_settings_black_24dp)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_settings_black_24dp))
            .setPriority(Notification.PRIORITY_MAX)
            .setFullScreenIntent(pendingIntent, true)
            .setContent(remoteViews)

        startForeground(
            NOTIFICATION_ID, builder?.build()
        )
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    private fun stopForegroundService() {
        timer?.cancel()
        sendStopTimer()
        playerPool?.reset()
        sendStopSound()
        stopForeground(true)
        stopSelf()
    }

    companion object {

        val COUNTDOWN_UPDATED = "COUNTDOWN_UPDATED"

        val COUNTDOWN_STOPPED = "COUNTDOWN_STOPPED"

        val SERVICE_STOPPED = "SERVICE_STOPPED"

        val SERVICE_STARTED = "SERVICE_STARTED"

        val NOTIFICATION_ID = 1312

        val ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE"

        val ACTION_START_ADDITIONAL_SOUND = "ACTION_START_ADDITIONAL_SOUND"

        val ACTION_START_TIMER = "ACTION_START_TIMER"

        val ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE"

        val ACTION_PAUSE = "ACTION_PAUSE"

        val ACTION_FROM_PUSH = "ACTION_FROM_PUSH"

        val ACTION_PLAY = "ACTION_PLAY"
    }
}

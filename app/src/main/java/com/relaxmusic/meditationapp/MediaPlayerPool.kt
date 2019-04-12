package com.relaxmusic.meditationapp

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer

class PlayerInfo(var soundId: String, var volume: Float)

class MediaPlayerPool(context: Context, maxStreams: Int) {
    private val context: Context = context.applicationContext

    private val mediaPlayerPool = mutableListOf<MediaPlayer>().also {
        for (i in 0..maxStreams) it += buildPlayer()
    }
    private var playersInUse = mutableListOf<Pair<PlayerInfo, MediaPlayer>>()

    private fun buildPlayer() = MediaPlayer().apply {
        setOnPreparedListener { start() }
        isLooping = true
    }

    /**
     * Returns a [MediaPlayer] if one is available,
     * otherwise null.
     */
    private fun requestPlayer(soundId: String): MediaPlayer? {
        return if (!mediaPlayerPool.isEmpty()) {
            mediaPlayerPool.removeAt(0).also {
                it.isLooping = true
                playersInUse.add(Pair(PlayerInfo(soundId, 0.5f), it))
            }
        } else null
    }

    private fun recyclePlayer(pair: Pair<PlayerInfo, MediaPlayer>) {
        pair.second.reset()
        playersInUse.remove(pair)
        mediaPlayerPool += pair.second
    }


    fun release() {
        playersInUse.forEach { it.second.release() }
    }

    fun reset() {
        val clone = playersInUse.toMutableList()
        clone.forEach { recyclePlayer(it) }
        playersInUse.clear()
    }

    fun pause() {
        playersInUse.forEach { it.second.pause() }
    }

    fun start() {
        playersInUse.forEach { it.second.start() }
    }

    fun getAllSoundIds() = playersInUse.map { it.first }

    fun getAllPlayers() = playersInUse.map { it.second }

    fun setVolumeForPlayer(soundId: String, volume: Float) {
        val pair = playersInUse.find { it.first.soundId == soundId }
        val player = pair?.second ?: return

        pair.first.volume = volume

        player.setVolume(volume, volume)
    }

    fun removeSound(soundId: String) {
        recyclePlayer(playersInUse.find { it.first.soundId == soundId } ?: return)
    }

    fun playSound(soundId: String, sound: AssetFileDescriptor) {
        val mediaPlayer = requestPlayer(soundId) ?: return

        mediaPlayer.run {
            setDataSource(
                sound.fileDescriptor, sound.startOffset,
                sound.declaredLength
            )
            prepareAsync()
        }
    }
}
package com.relaxmusic.meditationapp

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

fun Context.preferences() = PreferenceManager.getDefaultSharedPreferences(applicationContext)

fun SharedPreferences.setLanguage(locale: String) = edit().putString("lang", locale).apply()

fun SharedPreferences.setPurchased(purchased: Boolean) = edit().putBoolean("purchased", purchased).apply()

fun SharedPreferences.setPolicyAccepted(accepted: Boolean) = edit().putBoolean("policyAccepted", accepted).apply()

fun SharedPreferences.setTimeToShowRate(time: Long) = edit().putLong("timeToShowRate", time).apply()


fun SharedPreferences.newSound(soundId: String) {
    edit().putString("generalSound", soundId).apply()
    edit().putStringSet("sounds", setOf()).apply()
}

fun SharedPreferences.addSound(soundId: String) {
    val sounds = getAdditionalSounds()
    sounds.add(soundId)
    edit().putStringSet("sounds", sounds).apply()
}

fun SharedPreferences.removeSound(soundId: String) {
    val sounds = getAdditionalSounds()
    sounds.remove(soundId)
    edit().putStringSet("sounds", sounds).apply()
}

fun SharedPreferences.getLanguage() = getString("lang", "en")

fun SharedPreferences.isPurchased() = getBoolean("purchased", false)

fun SharedPreferences.getAdditionalSounds() = getStringSet("sounds", setOf())

fun SharedPreferences.getGeneralSound() = getString("generalSound", "")

fun SharedPreferences.getPolicyAccepted() = getBoolean("policyAccepted", false)
fun SharedPreferences.getTimeToShowRate() = getLong("timeToShowRate", -1)

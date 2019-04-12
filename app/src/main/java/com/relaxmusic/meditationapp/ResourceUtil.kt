package com.relaxmusic.meditationapp

import android.app.ActivityManager
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import java.util.*


fun Context.getSoundName(soundId: String): String {
    val resId = resources.getIdentifier(soundId, "string", packageName)
    return getString(resId)
}

fun Context.getSoundBackgroundColor(soundId: String): Int {
    val resId = resources.getIdentifier(soundId, "color", packageName)
    return ContextCompat.getColor(this, resId)
}

fun Context.getSoundIcon(soundId: String): Drawable? {
    val resId = resources.getIdentifier(soundId, "drawable", packageName)
    return ContextCompat.getDrawable(this, resId)
}

fun Context.getSoundTextColor(soundId: String): Int {
    val resId = resources.getIdentifier(soundId + "_text", "color", packageName)
    return ContextCompat.getColor(this, resId)
}

fun Context.getSoundBorderColor(soundId: String): Int {
    val resId = resources.getIdentifier(soundId + "_text", "color", packageName)
    return ContextCompat.getColor(this, resId)
}

fun Context.setLanguage(locale: String) {
    val dm = resources.displayMetrics
    val conf = resources.configuration
    conf.setLocale(Locale(locale))
    resources.updateConfiguration(conf, dm)
}

fun Context.isServiceRunning(serviceClass: Class<*>): Boolean {
    val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
        if (serviceClass.name == service.service.className) {
            return true
        }
    }
    return false
}
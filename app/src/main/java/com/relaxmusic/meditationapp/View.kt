package com.relaxmusic.meditationapp

import android.view.View

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.enabled() {
    isEnabled = true
}

fun View.focusable() {
    isFocusable = true
}

fun View.nofocusable() {
    isFocusable = false
}

fun View.disabled() {
    isEnabled = false
}
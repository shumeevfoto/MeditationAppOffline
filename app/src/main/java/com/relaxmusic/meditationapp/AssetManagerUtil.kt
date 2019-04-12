package com.relaxmusic.meditationapp

import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory


const val soundsFolderName = "meditation_sounds"
const val categoriesFolderName = "meditation_categories"

fun AssetManager.getSoundsAmount() = getSounds()?.size ?: 0



fun AssetManager.getSounds(): Array<out String>? {
    return list(soundsFolderName)
}

fun AssetManager.getCategories(): Array<out String>? {
    return list(categoriesFolderName)
}

fun AssetManager.getCategorySoundsList(categoryId: String): Array<out String>? {
    return list("$categoriesFolderName/$categoryId")
}


fun AssetManager.getSoundImage(soundId: String): Bitmap? {
    return getImageFromFolder("$soundsFolderName/$soundId")
}


fun AssetManager.getSound(soundId: String): AssetFileDescriptor? {
    return getSoundFromFolder("$soundsFolderName/$soundId")
}

fun AssetManager.getCategorySound(categoryId: String, soundId: String): AssetFileDescriptor? {
    return getSoundFromFolder("$categoriesFolderName/$categoryId/$soundId")
}


fun AssetManager.getImageFromFolder(folder: String): Bitmap? {
    val imageName = list(folder)
        ?.firstOrNull {
            it.isImage()
        }
    return if (imageName != null) BitmapFactory.decodeStream(open("$folder/$imageName")) else null
}

fun AssetManager.getSoundFromFolder(folder: String): AssetFileDescriptor? {
    val soundName = list(folder)
        ?.firstOrNull {
            it.isSound()
        }
    return if (soundName != null) openFd("$folder/$soundName") else null
}

fun String.isImage() = endsWith(".png") || endsWith(".jpg") || endsWith(".jpeg")

fun String.isSound() = endsWith(".mp3")


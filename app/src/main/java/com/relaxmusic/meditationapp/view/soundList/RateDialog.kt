package com.relaxmusic.meditationapp.view.soundList

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.relaxmusic.meditationapp.R
import kotlinx.android.synthetic.main.rate_dialog_layout.*
import org.jetbrains.anko.toast

class RateDialog(context: Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rate_dialog_layout)


        tv_rate.setOnClickListener {
            val appPackageName = context.getString(R.string.play_market_app_id)
            try {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
            } catch (anfe: ActivityNotFoundException) {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                    )
                )
            }

            context.toast(context.getString(R.string.rate_toast_thanks))

            dismiss()

        }

    }

}
package com.relaxmusic.meditationapp.view.onboarding


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.relaxmusic.meditationapp.base.BaseActivity
import com.relaxmusic.meditationapp.*
import com.relaxmusic.meditationapp.view.settings.LanguageDialog
import kotlinx.android.synthetic.main.activity_onboarding.*

class OnboardingActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)


        val language = preferences().getLanguage()
        LangUtils.langs.forEach {
            if (it.value == language) {
                tv_language.text = getString(it.key)
            }
        }

        tv_terms_of_use.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(getString(R.string.terms_of_use_link))
            startActivity(i)
        }

        tv_next.setOnClickListener {
            preferences().setPolicyAccepted(true)
            finish()
        }

        tv_language.setOnClickListener {
            LanguageDialog(this).show()
        }
    }

    override fun onBackPressed() {

    }

}

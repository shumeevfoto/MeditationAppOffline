package com.relaxmusic.meditationapp.view.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponse
import com.relaxmusic.meditationapp.*
import kotlinx.android.synthetic.main.activity_settings.*
import org.jetbrains.anko.toast


class SettingsActivity : AppCompatActivity(), PurchasesUpdatedListener {


    lateinit var skuList: List<String>

    override fun onPurchasesUpdated(responseCode: Int, purchases: MutableList<Purchase>?) {
        if (responseCode == BillingResponse.OK) {
            preferences().setPurchased(true)
            tv_disable_ads.text = getString(R.string.already_bought)
            tv_disable_ads.setOnClickListener {

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setLanguage(preferences().getLanguage())
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        skuList = listOf(getString(R.string.disable_ads_google_pay_id))


        if (preferences().isPurchased()) {
            tv_disable_ads.text = getString(R.string.already_bought)
        } else {
            setupBillingClient()
        }

        val language = preferences().getLanguage()
        LangUtils.langs.forEach {
            if (it.value == language) {
                tv_language.text = getString(it.key)
            }
        }


        tv_language.setOnClickListener {
            LanguageDialog(this).show()
        }

        tv_terms_of_use.setOnClickListener {
            val i = Intent(android.content.Intent.ACTION_VIEW)
            i.data = Uri.parse(getString(R.string.terms_of_use_link))
            startActivity(i)
        }

        tv_play_market.setOnClickListener {
            val appPackageName = getString(R.string.play_market_developer_id)
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://developer?id=$appPackageName")))
            } catch (anfe: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/developer?id=$appPackageName")
                    )
                )
            }

        }
        tv_rate_us.setOnClickListener {
            val appPackageName = getString(R.string.play_market_app_id)
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
            } catch (anfe: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                    )
                )
            }

            toast(getString(R.string.rate_toast_thanks))

        }

    }

    lateinit var billingClient: BillingClient

    fun loadProducts() {
        if (billingClient.isReady) {
            val params = SkuDetailsParams
                .newBuilder()
                .setSkusList(skuList)
                .setType(BillingClient.SkuType.INAPP)
                .build()
            billingClient.querySkuDetailsAsync(params) { responseCode, skuDetailsList ->
                if (responseCode == BillingClient.BillingResponse.OK) {

                    try {
                        tv_disable_ads.text = getString(R.string.activity_settings_disable_ads).format(skuDetailsList[0].price)
                        println("querySkuDetailsAsync, responseCode: $responseCode")
                        tv_disable_ads.setOnClickListener {
                            val billingFlowParams = BillingFlowParams
                                .newBuilder()
                                .setSkuDetails(skuDetailsList[0])
                                .build()
                            billingClient.launchBillingFlow(this, billingFlowParams)
                        }

                    } catch (e: IndexOutOfBoundsException) {
                        tv_disable_ads.text = getString(R.string.activity_settings_disable_ads).format("1.00 USD")
                        toast("No purchasable items yet. Checkout your developer console!")
                    }
                } else {
                    println("Can't querySkuDetailsAsync, responseCode: $responseCode")
                }
            }
        } else {
            println("Billing Client not ready")
        }
    }

    private fun setupBillingClient() {
        billingClient = BillingClient
            .newBuilder(this)
            .setListener(this)
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(@BillingClient.BillingResponse billingResponseCode: Int) {
                if (billingResponseCode == BillingClient.BillingResponse.OK) {
                    println("BILLING | startConnection | RESULT OK")
                    loadProducts()
                } else {
                    println("BILLING | startConnection | RESULT: $billingResponseCode")
                }
            }

            override fun onBillingServiceDisconnected() {
                println("BILLING | onBillingServiceDisconnected | DISCONNECTED")
            }
        })
    }
}

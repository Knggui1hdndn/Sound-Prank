package com.pranksound.fartsound.trollandjoke.funnyapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.access.pro.callBack.OnShowInterstitialListener
import com.access.pro.config.ConfigModel
import com.pranksound.fartsound.trollandjoke.funnyapp.R
import com.pranksound.fartsound.trollandjoke.funnyapp.application.BaseActivity

class Splash : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupBilling { p, po -> }
        getConfigData(true)
        Handler(Looper.myLooper()!!).postDelayed({
            if (!proApplication.isSubVip && ConfigModel.showSub) {
                showAds(true, object : OnShowInterstitialListener {
                    override fun onCloseAds(hasAds: Boolean) {
                        startActivity(Intent(this@Splash, SubVipActivity::class.java))
                        finish()
                    }
                })
            } else {
                showAds(true, object : OnShowInterstitialListener {
                    override fun onCloseAds(hasAds: Boolean) {
                        startActivity(Intent(this@Splash, Home::class.java))
                        finish()
                    }
                })
            }
        }, 3000)
    }
}
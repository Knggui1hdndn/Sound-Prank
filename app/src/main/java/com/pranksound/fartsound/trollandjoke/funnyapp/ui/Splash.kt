package com.pranksound.fartsound.trollandjoke.funnyapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.pranksound.fartsound.trollandjoke.funnyapp.R
import com.pranksound.fartsound.trollandjoke.funnyapp.application.BaseActivity

class Splash : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupBilling { p, po -> }
        getConfigData(true)
        Handler(Looper.myLooper()!!).postDelayed({
            startActivity(Intent(this@Splash, Home::class.java))
            finish()
        }, 3000)
    }
}
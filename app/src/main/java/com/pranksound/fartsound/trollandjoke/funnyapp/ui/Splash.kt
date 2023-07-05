package com.pranksound.fartsound.trollandjoke.funnyapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.pranksound.fartsound.trollandjoke.funnyapp.R

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Handler(Looper.myLooper()!!).postDelayed(Runnable {
            startActivity(Intent(this@Splash, Home::class.java))
        }, 3000)
    }
}
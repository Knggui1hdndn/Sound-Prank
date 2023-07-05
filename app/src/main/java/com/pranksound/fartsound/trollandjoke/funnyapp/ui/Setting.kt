package com.pranksound.fartsound.trollandjoke.funnyapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pranksound.fartsound.trollandjoke.funnyapp.R
import com.pranksound.fartsound.trollandjoke.funnyapp.databinding.ActivitySettingBinding

class Setting : AppCompatActivity() {
    private lateinit var bindingSetting: ActivitySettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingSetting= ActivitySettingBinding.inflate(layoutInflater)
        setContentView(bindingSetting.root)
        bindingSetting.imgBack.setOnClickListener { finish() }
    }
}
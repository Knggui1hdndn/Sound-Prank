package com.pranksound.fartsound.trollandjoke.funnyapp.application
import androidx.appcompat.app.AppCompatDelegate
import com.access.pro.application.ProApplication
import com.access.pro.config.AdsConfigModel
import com.pranksound.fartsound.trollandjoke.funnyapp.BuildConfig

class MyApplication:ProApplication() {
    override fun onCreate() {
        AdsConfigModel.GG_APP_OPEN = BuildConfig.GG_APP_OPEN
        AdsConfigModel.GG_BANNER = BuildConfig.GG_BANNER
        AdsConfigModel.GG_NATIVE = BuildConfig.GG_NATIVE
        AdsConfigModel.GG_FULL = BuildConfig.GG_FULL
        AdsConfigModel.GG_REWARDED = BuildConfig.GG_REWARDED
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate()
    }
}
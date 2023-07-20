package com.pranksound.fartsound.trollandjoke.funnyapp.ui

import android.app.Dialog
import android.app.Notification.Action
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import com.access.pro.config.ConfigModel
import com.pranksound.fartsound.trollandjoke.funnyapp.R
import com.pranksound.fartsound.trollandjoke.funnyapp.application.BaseActivity
import com.pranksound.fartsound.trollandjoke.funnyapp.databinding.ActivitySettingBinding
import com.pranksound.fartsound.trollandjoke.funnyapp.databinding.DialogRateBinding

class Setting : BaseActivity() {
    private lateinit var bindingSetting: ActivitySettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingSetting = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(bindingSetting.root)
        bindingSetting.imgBack.setOnClickListener { finish() }
        if (ConfigModel.showSub && !proApplication.isSubVip) {
            bindingSetting.mLinearPremium.visibility = View.VISIBLE
        } else {
            bindingSetting.mLinearPremium.visibility = View.GONE
        }
        bindingSetting.mLinearPremium.setOnClickListener {
            startActivity(Intent(this@Setting, SubVipActivity::class.java))
        }

        bindingSetting.mLinearPolicy.setOnClickListener {
            val intent = Intent(this@Setting, ShowWebActitvity::class.java)
            intent.putExtra(
                "link",
                "https://sites.google.com/view/privacypolicytosforhaircutpran/"
            )
            startActivity(intent)
        }

        bindingSetting.mLinearInviteFriend.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "https://play.google.com/store/apps/details?id=com.pranksound.fartsound.trollandjoke.funnyapp"
            )
            intent.type="text/plain"
            startActivity(Intent.createChooser(intent, "Share "))


        }
        bindingSetting.mLinearRate.setOnClickListener {
            getDialogRate().show()
        }
    }
    fun getDialogRate(): Dialog {
        val dialogBinding = DialogRateBinding.inflate(LayoutInflater.from(this))
        val dialog = Dialog(this).apply {
            window?.setBackgroundDrawableResource(android.R.color.transparent);
            window?.setGravity(Gravity.TOP)
            setContentView(dialogBinding.root)
            window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            dialogBinding.btnRate.setOnClickListener {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=com.pranksound.fartsound.trollandjoke.funnyapp")
                )
                startActivity(intent)
            }

            dialogBinding.mLinearStar.setOnClickListener {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=com.pranksound.fartsound.trollandjoke.funnyapp")
                )
                startActivity(intent)
            }
        }
        return dialog
    }
}
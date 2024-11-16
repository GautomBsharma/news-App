package com.mksolution.newsshort

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.mksolution.newsshort.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding
    private lateinit var auth: FirebaseAuth
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = R.color.time_back
        auth = FirebaseAuth.getInstance()
        val sharedPref = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val nightModeEnabled = sharedPref.getBoolean("NightMode", false)
        val notificationsEnabled = sharedPref.getBoolean("NotificationsEnabled", true)
        binding.imageView11.setOnClickListener {
            finish()
        }
        binding.switchNotification.isChecked = notificationsEnabled

        binding.switchNotification.setOnCheckedChangeListener { _, isChecked ->
            with(sharedPref.edit()) {
                putBoolean("NotificationsEnabled", isChecked)
                apply()
            }

        }
        binding.signOut.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }
        binding.signIn.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }

        binding.signUp.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
        }
        binding.goPolicy.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/muslimly-mk-solution/home"))
            startActivity(intent)
        }

    }
}
package com.mksolution.newsshort

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.firebase.messaging.FirebaseMessaging
import com.mksolution.newsshort.Fragments.HomeFragment
import com.mksolution.newsshort.Fragments.JobFragment
import com.mksolution.newsshort.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var selectedFragmentId: Int = R.id.home
    private var backPressedOnce = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomeFragment())

        FirebaseMessaging.getInstance().subscribeToTopic("test")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                }
                else {

                }
            }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false) // Ensures the content extends under the system bars
            val controller = window.insetsController
            if (controller != null) {
                controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                // Ensure the status bar is transparent
                window.statusBarColor = Color.TRANSPARENT
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
            window.statusBarColor = Color.TRANSPARENT
        }

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> replaceFragment(HomeFragment())
                R.id.jobNews -> replaceFragment(JobFragment())
                R.id.others -> replaceFragment(OthersFragment())
                else ->{

                }
            }
            true
        }
    }
    private fun replaceFragment(fragment: Fragment)
    {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container,fragment)
        //fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
    override fun onBackPressed() {
        if (binding.bottomNavigationView.selectedItemId != R.id.home) {
            binding.bottomNavigationView.selectedItemId = R.id.home
            replaceFragment(HomeFragment())
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        } else {

            super.onBackPressed()
        }
    }
}
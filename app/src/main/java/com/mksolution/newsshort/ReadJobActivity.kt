package com.mksolution.newsshort

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.mksolution.newsshort.databinding.ActivityReadJobBinding

class ReadJobActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReadJobBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadJobBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val jobid = intent.getStringExtra("JOB_ID").toString()
        val jobabout = intent.getStringExtra("JOB_ABOUT").toString()
        val jobDeadLine = intent.getStringExtra("JOB_DEADLINE").toString()
        val jobexp = intent.getStringExtra("JOB_EXP").toString()
        val joblocation = intent.getStringExtra("JOB_LOCATION").toString()
        val jobprovider = intent.getStringExtra("JOB_PROVIDER").toString()
        val jobslot = intent.getStringExtra("JOB_SLOAT").toString()
        val jobtitle = intent.getStringExtra("JOB_TITLE").toString()
        val joburl = intent.getStringExtra("JOB_URL").toString()
        val jobprovilink = intent.getStringExtra("JOB_PROLINK").toString()
        val jobsalary = intent.getStringExtra("JOB_SALARY").toString()
        if (isNetworkAvailable(this)) {

        } else {

            showNoInternetDialog()  // Show dialog if offline
                // Load default quotes
        }
        binding.backimg.setOnClickListener {
            finish()
        }
        binding.jTitle.text = jobtitle
        binding.jProvider.text =jobprovider
        binding.jDeadline.text = jobDeadLine
        binding.jSlot.text= jobslot
        binding.jSalary.text = jobsalary
        binding.jLocation.text = joblocation
        binding.jExperi.text = jobexp
        binding.jAbout.text = jobabout
        if (joburl.isNotEmpty()){
            Glide.with(this).load(joburl).into(binding.jImage)
        }
        else{
            binding.jImage.setImageResource(R.drawable.jobhint)
        }
    }
    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }

    private fun showNoInternetDialog() {
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("No Internet Connection")
            .setIcon(R.drawable.round_signal_wifi_connected_no_internet_4_24)
            .setMessage("Please check your internet connection and try again.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                binding.constraintLayout7.visibility = View.VISIBLE
                binding.animNoInternet.playAnimation()
            }
            .setCancelable(false)
            .create()

        dialog.show()
    }
}
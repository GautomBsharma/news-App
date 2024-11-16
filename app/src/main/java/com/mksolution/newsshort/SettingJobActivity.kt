package com.mksolution.newsshort

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.mksolution.newsshort.databinding.ActivitySettingJobBinding

class SettingJobActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingJobBinding
    private var selectedItem :Any = ""
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingJobBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = R.color.gray2
        val sharedPreferences = getSharedPreferences("JobPreferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()


        val jobType = resources.getStringArray(R.array.job_list)
        val arrayAdapter = ArrayAdapter(this, R.layout.drop_doun_item, jobType)
        binding.autoCompleteTextView.setAdapter(arrayAdapter)

        binding.autoCompleteTextView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            selectedItem = adapterView.getItemAtPosition(i) as String
            editor.putString("SELECTED_JOB", selectedItem as String)
            editor.apply()
        }

        val savedJob = sharedPreferences.getString("SELECTED_JOB", "Default")
        binding.tvShowType.text = savedJob

        binding.autoCompleteTextView.setText(savedJob)

        val sharedPref = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val nightModeEnabled = sharedPref.getBoolean("NightMode", false)
        val notificationsEnabled = sharedPref.getBoolean("NotificationsEnabled", true)

        binding.switchNotificationJob.isChecked = notificationsEnabled
        binding.switchNotificationJob.setOnCheckedChangeListener { _, isChecked ->
            with(sharedPref.edit()) {
                putBoolean("NotificationsEnabled", isChecked)
                apply()
            }

        }

    }
}
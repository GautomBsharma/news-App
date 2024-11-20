package com.mksolution.newsshort

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateUtils
import com.bumptech.glide.Glide
import com.mksolution.newsshort.databinding.ActivityReadNewsBinding

class ReadNewsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReadNewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val imgg = intent.getStringExtra("IMG")
        val title = intent.getStringExtra("TITLE")
        val news = intent.getStringExtra("NEWS")
        val source = intent.getStringExtra("SOURCE")
        val time = intent.getLongExtra("TIME",0L)

        binding.tvNews.text= news
        binding.newsTitle.text = title
        binding.tvSorce.text = source
        if (imgg != null) {
            if (imgg.isNotEmpty()){
                Glide.with(this).load(imgg).into(binding.newsImg)
            }
            else{
                binding.newsImg.setImageResource(R.drawable.newsshorthint1)
            }

        }

        val relativeTime = DateUtils.getRelativeTimeSpanString(
            time,
            System.currentTimeMillis(),
            DateUtils.MINUTE_IN_MILLIS
        )
        binding.tvTime.text = relativeTime
    }
}
package com.mksolution.newsshort

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mksolution.newsshort.databinding.ActivityReadNewsBinding

class ReadNewsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReadNewsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}
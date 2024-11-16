package com.mksolution.newsshort

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.mksolution.newsshort.databinding.ActivityForgetBinding

class ForgetActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgetBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.reset.setOnClickListener {
            val resetEmail = binding.resetEmail.text.toString()
            auth.sendPasswordResetEmail(resetEmail)
                .addOnSuccessListener {
                    Toast.makeText(this, "Please Check Your Email", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                }

        }

    }
}
package com.mksolution.newsshort

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.mksolution.newsshort.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var dialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        dialog = Dialog(this)
        dialog.setContentView(R.layout.progress_layout)
        dialog.setCancelable(true)
        binding.goSignup.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
        }
        binding.iBLogin.setOnClickListener {
            if (isNetworkAvailable(this)) {
                // Internet is available, retrieve data
                validatedata()
            } else {
                // No internet connection, show dialog
                showNoInternetDialog()
            }

        }

        binding.forget.setOnClickListener {
            startActivity(Intent(this,ForgetActivity::class.java))
        }
    }


    private fun validatedata() {
        if (binding.logEmail.text.toString().isEmpty()){
            binding.logEmail.error = "Enter Email A/C"
        }
        else if (binding.logPassword.text!!.length<6){
            binding.logPassword.error = "Enter 6 or more!"
        }
        else if (binding.logPassword.text.toString().isEmpty()){
            binding.logPassword.error = "Enter password"
        }
        else{
            login()
            dialog.show()
        }
    }

    private fun login() {
        val email = binding.logEmail.text.toString()
        val password = binding.logPassword.text.toString()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val verification = auth.currentUser?.isEmailVerified
                    if (verification==true){
                        dialog.dismiss()
                        //startActivity(Intent(this,MainActivity::class.java))
                        finish()
                    }
                    else{
                        Toast.makeText(this, "Please Verify Your Email", Toast.LENGTH_SHORT)
                            .show()
                    }

                } else {

                    dialog.dismiss()
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()

                }
            }
    }
    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
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
            }
            .setCancelable(true)
            .create()

        dialog.show()
    }
}
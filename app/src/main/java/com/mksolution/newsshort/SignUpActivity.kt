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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.mksolution.newsshort.databinding.ActivitySignUpBinding
import java.util.*
import kotlin.collections.HashMap

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        dialog = Dialog(this)
        dialog.setContentView(R.layout.progress_layout)
        dialog.setCancelable(true)


        binding.iBSignUp.setOnClickListener {
            validateData()
        }
        binding.goLogin.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }
    }
    private fun validateData() {
        if (binding.sinName.text.toString().isEmpty()){
            binding.sinName.error = "Enter Name"
        }
        else if (binding.sinEmail.text.toString().isEmpty()){
            binding.sinEmail.error = "Enter Email A/C"
        }
        else if (binding.sinPass.text!!.length<6){
            binding.sinPass.error = "Enter 6 or more!"
        }

        else{
            if (isNetworkAvailable(this)) {
                // Internet is available, retrieve data
                creatAcount()
                dialog.show()
            } else {
                // No internet connection, show dialog
                showNoInternetDialog()
            }

        }
    }


    private fun creatAcount() {
        val email = binding.sinEmail.text.toString()
        val password = binding.sinPass.text.toString()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful)
                {
                    auth.currentUser?.sendEmailVerification()
                        ?.addOnSuccessListener {
                            Toast.makeText(this, "Please Verify Your Email", Toast.LENGTH_SHORT)
                                .show()
                            saveUser()
                        }
                        ?.addOnFailureListener {
                            Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT)
                                .show()
                        }


                } else
                {
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
    }

    private fun saveUser() {
        val userRef = FirebaseDatabase.getInstance().reference.child("Users")
        val userMap = HashMap<String, Any>()
        val UserId = auth.currentUser!!.uid
        userMap["UserImageUrl"] = ""
        userMap["UserId"] = UserId
        userMap["UserName"] = binding.sinName.text.toString()
        userMap["UserPhone"] = ""
        userMap["UserInstitute"] = ""
        userMap["UserEmail"] = binding.sinEmail.text.toString()
        userRef.child(UserId).setValue(userMap).addOnCompleteListener {
            //startActivity(Intent(this, MainActivity::class.java))
            dialog.dismiss()
            finish()
        }.addOnFailureListener {
            Toast.makeText(
                baseContext, "Save data fail",
                Toast.LENGTH_SHORT
            ).show()
            dialog.dismiss()
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
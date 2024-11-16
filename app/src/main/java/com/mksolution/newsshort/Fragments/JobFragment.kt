package com.mksolution.newsshort.Fragments


import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mksolution.newsshort.Adapters.JobAdapter
import com.mksolution.newsshort.Models.Job
import com.mksolution.newsshort.R
import com.mksolution.newsshort.databinding.FragmentJobBinding


class JobFragment : Fragment() {
    private lateinit var binding: FragmentJobBinding
    private lateinit var adapter: JobAdapter
    private var jobList: ArrayList<Job>?=null
    private var connectivityManager: ConnectivityManager? = null
    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private var isOnline = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentJobBinding.inflate(layoutInflater)
        binding.recyclerViewJob.layoutManager = LinearLayoutManager(requireContext())

        jobList = arrayListOf()
        adapter = JobAdapter(requireContext(), jobList!!)
        binding.recyclerViewJob.adapter = adapter

        setupNetworkCallback()
        if (isNetworkAvailable(requireContext())) {
            showLoading()
            isOnline = true
            getJob()
        } else {
            isOnline = false
            showNoInternetDialog()
        }

        binding.searchGro.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.searchGro.text.toString().isEmpty())
                {
                    getJob()
                }
                else {
                    searchJob(s.toString().lowercase())
                }
            }
        })
        return binding.root
    }

    private fun setupNetworkCallback() {
        connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                if (!isOnline) {
                    isOnline = true
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Connection restored", Toast.LENGTH_SHORT).show()
                        if (jobList?.isEmpty() == true){

                            showLoading()
                            getJob()
                        }

                    }
                }
            }

            override fun onLost(network: Network) {
                isOnline = false
                requireActivity().runOnUiThread {

                    Toast.makeText(requireContext(), "Connection lost", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        connectivityManager?.registerNetworkCallback(request, networkCallback!!)
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
        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("No Internet Connection")
            .setIcon(R.drawable.round_signal_wifi_connected_no_internet_4_24)
            .setMessage("Please check your internet connection and try again.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                showLoading()
            }
            .setCancelable(false)
            .create()

        dialog.show()
    }

    private fun getJob() {
        val reff = FirebaseDatabase.getInstance().reference.child("Job")
        reff.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    jobList?.clear()  // Clear the list before adding new items
                    for (snap in snapshot.children) {
                        val datt = snap.getValue(Job::class.java)
                        datt?.let { jobList?.add(it) }
                    }
                    jobList?.reverse()
                    adapter.notifyDataSetChanged()
                    binding.recyclerViewJob.visibility = View.VISIBLE
                    hideLoading()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun searchJob(input:String) {
        val query= FirebaseDatabase.getInstance().reference
            .child("Job")
            .orderByChild("jobTitle")
            .startAt(input)
            .endAt(input + "\uf8ff")
        query.addValueEventListener(object: ValueEventListener
        {
            override fun onCancelled(error: DatabaseError) {

            }
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(datasnapshot: DataSnapshot) {
                jobList?.clear()
                for(snapshot in datasnapshot.children)
                {
                    val user=snapshot.getValue(Job::class.java)
                    if(user!=null)
                    {
                        jobList?.add(user)

                    }
                }
                jobList?.reverse()
                adapter.notifyDataSetChanged()
            }
        })
    }

    private fun showLoading() {
        binding.shimmerFrameLayout.visibility = View.VISIBLE
        binding.shimmerFrameLayout.startShimmer()
        binding.recyclerViewJob.visibility = View.GONE
    }

    private fun hideLoading() {
        binding.shimmerFrameLayout.stopShimmer()
        binding.shimmerFrameLayout.visibility = View.GONE
        binding.recyclerViewJob.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        networkCallback?.let {
            connectivityManager?.unregisterNetworkCallback(it)
        }
    }

}
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
import com.mksolution.newsshort.Services.JobDatabaseHelper
import com.mksolution.newsshort.Services.NewsDatabaseHelper
import com.mksolution.newsshort.databinding.FragmentJobBinding


class JobFragment : Fragment() {
    private lateinit var binding: FragmentJobBinding
    private lateinit var adapter: JobAdapter
    private lateinit var databaseHelper: JobDatabaseHelper
    private var connectivityManager: ConnectivityManager? = null
    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private var isOnline = false
    private var isDataLoaded = false
    private val jobList = arrayListOf<Job>()
    private var isAppInForeground = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentJobBinding.inflate(layoutInflater)
        adapter = JobAdapter(requireContext(), jobList)
        binding.recyclerViewJob.adapter = adapter

        databaseHelper = JobDatabaseHelper(requireContext())
        val cachedJobs = databaseHelper.getSavedJobs()
        if (cachedJobs.isNotEmpty()) {
            jobList.addAll(cachedJobs)
            loadPreviousData()
        } else {
            showLoading()
        }

        setupRecyclerView()
        setupNetworkCallback()

        loadData()

        binding.searchGro.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val query = binding.searchGro.text.toString().lowercase()
                if (query.isEmpty()) {
                    loadData()
                } else {
                    searchJobs(query)
                }
            }
        })

        return binding.root
    }

    private fun loadData() {
        if (isNetworkAvailable(requireContext())) {
            if (!isDataLoaded) {
                showLoading()
                fetchJobsFromFirebase()
            } else {
                loadPreviousData()
            }
        } else {
            if (jobList.isEmpty()) {
                showNoInternetDialog()
            } else {
                loadPreviousData()
            }
        }
    }

    private fun loadPreviousData() {
        adapter.updateData(jobList)
        hideLoading()
    }

    private fun fetchJobsFromFirebase() {
        val reff = FirebaseDatabase.getInstance().reference.child("Job")
        reff.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val newDataList = arrayListOf<Job>()
                    for (snap in snapshot.children) {
                        snap.getValue(Job::class.java)?.let { newDataList.add(it) }
                    }
                    newDataList.reverse()
                    jobList.clear()
                    jobList.addAll(newDataList)
                    adapter.updateData(jobList)
                    hideLoading()
                    isDataLoaded = true
                    databaseHelper.saveJobs(jobList)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun searchJobs(query: String) {
        val searchQuery = FirebaseDatabase.getInstance().reference
            .child("Job")
            .orderByChild("jobTitle")
            .startAt(query)
            .endAt(query + "\uf8ff")

        searchQuery.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                val searchResults = arrayListOf<Job>()
                for (snap in snapshot.children) {
                    snap.getValue(Job::class.java)?.let { searchResults.add(it) }
                }
                searchResults.reverse()
                jobList.clear()
                jobList.addAll(searchResults)
                adapter.updateData(jobList)
            }
        })
    }

    private fun setupRecyclerView() {
        binding.recyclerViewJob.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupNetworkCallback() {
        connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                if (!isOnline) {
                    isOnline = true
                    requireActivity().runOnUiThread {
                        if (isAppInForeground) {
                            Toast.makeText(requireContext(), "Connection restored", Toast.LENGTH_SHORT).show()
                        }
                        if (jobList.isEmpty()) {
                            showLoading()
                            fetchJobsFromFirebase()
                        }
                    }
                }
            }

            override fun onLost(network: Network) {
                isOnline = false
                requireActivity().runOnUiThread {
                    if (isAppInForeground) {
                        Toast.makeText(requireContext(), "Connection lost", Toast.LENGTH_SHORT).show()
                    }
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
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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
                if (isDataLoaded) {
                    loadPreviousData()
                } else {
                    showLoading()
                }
            }
            .setCancelable(false)
            .create()
        dialog.show()
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

    override fun onResume() {
        super.onResume()
        isAppInForeground = true
    }

    override fun onPause() {
        super.onPause()
        isAppInForeground = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        networkCallback?.let {
            connectivityManager?.unregisterNetworkCallback(it)
        }
    }
}

package com.mksolution.newsshort.Fragments

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mksolution.newsshort.Adapters.NewsAdapter
import com.mksolution.newsshort.Models.News
import com.mksolution.newsshort.Models.NewsDefaults
import com.mksolution.newsshort.R
import com.mksolution.newsshort.Services.NewsDatabaseHelper
import com.mksolution.newsshort.SettingActivity
import com.mksolution.newsshort.ViewModel.HomeViewModel
import com.mksolution.newsshort.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private var adapter: NewsAdapter? = null
    private lateinit var databaseHelper: NewsDatabaseHelper
    private var connectivityManager: ConnectivityManager? = null
    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private var isOnline = false
    private var isDataLoaded = false
    private val newsList = arrayListOf<News>()

    private var isAppInForeground = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        adapter = NewsAdapter(requireContext(), newsList)
        binding.recyclerViewNews.adapter = adapter
        binding.appMenu.setOnClickListener {
            startActivity(Intent(requireContext(), SettingActivity::class.java))
        }

        databaseHelper = NewsDatabaseHelper(requireContext())
        val cachedNews = databaseHelper.getSavedNews()
        if (cachedNews.isNotEmpty()) {
            newsList.addAll(cachedNews)
            loadPreviousData() // Display cached data immediately
        } else {
            showLoading() // Show loading if no cache and proceed to fetch from Firebase
        }

        setupRecyclerView()
        setupNetworkCallback()

        // Load data based on network and cache availability
        loadData()

        return binding.root
    }

    private fun loadData() {
        if (isNetworkAvailable(requireContext())) {
            if (!isDataLoaded) {
                showLoading()
                fetchDataFromFirebase()
            } else {
                loadPreviousData()
            }
        } else {
            if (newsList.isEmpty()) {
                showNoInternetDialog()

            } else {
                loadPreviousData()
            }
        }
    }

    private fun loadPreviousData() {
        adapter?.updateData(newsList)
        hideLoading()
    }

    private fun fetchDataFromFirebase() {
        val reff = FirebaseDatabase.getInstance().reference.child("News")
        reff.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val newDataList = arrayListOf<News>()
                    for (snap in snapshot.children) {
                        snap.getValue(News::class.java)?.let { newDataList.add(it) }
                    }
                    newDataList.reverse()
                    newsList.clear()
                    newsList.addAll(newDataList)
                    adapter?.updateData(newsList)
                    hideLoading()
                    isDataLoaded = true // Set data as loaded
                    // Cache data in SQLite
                    databaseHelper.saveNews(newsList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error if needed
            }
        })
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewNews.layoutManager = layoutManager
        val pagerSnapHelper = PagerSnapHelper()
        pagerSnapHelper.attachToRecyclerView(binding.recyclerViewNews)
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
                        if (newsList.isEmpty()) {
                            showLoading()
                            fetchDataFromFirebase()
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
        binding.recyclerViewNews.visibility = View.GONE
    }

    private fun hideLoading() {
        binding.shimmerFrameLayout.stopShimmer()
        binding.shimmerFrameLayout.visibility = View.GONE
        binding.recyclerViewNews.visibility = View.VISIBLE
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
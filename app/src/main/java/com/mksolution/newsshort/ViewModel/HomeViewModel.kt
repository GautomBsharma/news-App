package com.mksolution.newsshort.ViewModel


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mksolution.newsshort.Models.News

class HomeViewModel : ViewModel() {
    // LiveData to hold the list of news items
    val newsList = MutableLiveData<ArrayList<News>>()

    // Private backing field for data loaded state
    private var _isDataLoaded = false

    // Public property to access data loaded state
    var isDataLoaded: Boolean
        get() = _isDataLoaded
        set(value) {
            _isDataLoaded = value
        }

    // Fetch data from Firebase
    fun getData() {
        val reff = FirebaseDatabase.getInstance().reference.child("News")
        reff.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val newDataList = arrayListOf<News>()
                    for (snap in snapshot.children) {
                        snap.getValue(News::class.java)?.let { newDataList.add(it) }
                    }
                    newDataList.reverse()
                    newsList.value = newDataList
                    _isDataLoaded = true // Set data as loaded
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error if needed
            }
        })
    }
}


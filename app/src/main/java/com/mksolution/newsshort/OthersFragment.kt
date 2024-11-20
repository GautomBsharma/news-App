package com.mksolution.newsshort


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mksolution.newsshort.Adapters.AllNewsAdapter
import com.mksolution.newsshort.Models.News
import com.mksolution.newsshort.databinding.FragmentOthersBinding


class OthersFragment : Fragment() {
    private lateinit var binding: FragmentOthersBinding

    private lateinit var adapter: AllNewsAdapter
    private lateinit var list: ArrayList<News>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentOthersBinding.inflate(layoutInflater)
        binding.recyclerViewALlNews.layoutManager = LinearLayoutManager(requireContext())
        list = arrayListOf()
        adapter = AllNewsAdapter(requireContext(), list as ArrayList<News>)
        binding.recyclerViewALlNews.adapter = adapter
        showLastAddedChart()
        getData()
        return binding.root
    }

    private fun showLastAddedChart() {
        val dbRef = FirebaseDatabase.getInstance().reference.child("Chart")

        dbRef.orderByChild("timestamp").limitToLast(1).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (chartSnapshot in snapshot.children) {
                        val chartUrl = chartSnapshot.child("chartUrl").value.toString()
                        val chartTitle = chartSnapshot.child("chartTitle").value.toString()
                        val chartTag = chartSnapshot.child("chartTag").value.toString()
                        binding.chartTag.text = chartTag
                        binding.chartTitle.text = chartTitle

                        Glide.with(requireContext())
                            .load(chartUrl)
                            .placeholder(R.drawable.chat_backi6)
                            .into(binding.imChart)
                    }
                } else {
                    Toast.makeText(requireContext(), "No chart found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load chart: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getData() {
        val reff = FirebaseDatabase.getInstance().reference.child("News")
        reff.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    list.clear()
                    for (snap in snapshot.children) {
                        val datt = snap.getValue(News::class.java)
                        datt?.let { list.add(it) }
                    }
                    list.reverse()
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

}

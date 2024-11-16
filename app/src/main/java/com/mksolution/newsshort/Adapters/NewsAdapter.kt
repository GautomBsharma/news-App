package com.mksolution.newsshort.Adapters

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mksolution.newsshort.Models.News
import com.mksolution.newsshort.Models.NewsDefaults
import com.mksolution.newsshort.R

class NewsAdapter (private var context: Context,var newsList: List<News>):RecyclerView.Adapter<NewsAdapter.NewsHolder>() {


    inner class NewsHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val image = itemView.findViewById<ImageView>(R.id.newsImg)
        val title = itemView.findViewById<TextView>(R.id.newsTitle)
        val news = itemView.findViewById<TextView>(R.id.tvNews)
        val uploadTime = itemView.findViewById<TextView>(R.id.tvTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.news_item,parent,false)
        return NewsHolder(view)
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    override fun onBindViewHolder(holder: NewsHolder, position: Int) {
        val  data = newsList[position]

            holder.title.text = data.newsTitle
            holder.news.text = data.news
            if (data.newsUrl.isNotEmpty()){
                Glide.with(context).load(data.newsUrl).into(holder.image)
            }

            val relativeTime = DateUtils.getRelativeTimeSpanString(
                data.newsTime,
                System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS
            )
            holder.uploadTime.text = relativeTime
    }
    fun updateData(newData: ArrayList<News>) {
        newsList = newData
        notifyDataSetChanged()
    }

}
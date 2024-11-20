package com.mksolution.newsshort.Adapters

import android.content.Context
import android.content.Intent
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mksolution.newsshort.Models.News
import com.mksolution.newsshort.R
import com.mksolution.newsshort.ReadNewsActivity

class AllNewsAdapter(private var context: Context, var list: List<News>):RecyclerView.Adapter<AllNewsAdapter.AllNewsHolder>() {


    inner class AllNewsHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val image = itemView.findViewById<ImageView>(R.id.imNews)
        val title = itemView.findViewById<TextView>(R.id.tvTitleAllNews)
        val time = itemView.findViewById<TextView>(R.id.tvDateAllNews)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllNewsHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.all_news_item,parent,false)
        return AllNewsHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: AllNewsHolder, position: Int) {
        val data = list[position]

        holder.title.text = data.newsTitle
        val relativeTime = DateUtils.getRelativeTimeSpanString(
            data.newsTime,
            System.currentTimeMillis(),
            DateUtils.MINUTE_IN_MILLIS
        )
        holder.time.text = relativeTime

        if (data.newsUrl.isNotEmpty()){
            Glide.with(context).load(data.newsUrl).into(holder.image)
        }
        else{
            holder.image.setImageResource(R.drawable.newsshorthint1)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ReadNewsActivity::class.java)
            intent.putExtra("IMG",data.newsUrl)
            intent.putExtra("TITLE",data.newsTitle)
            intent.putExtra("NEWS",data.news)
            intent.putExtra("SOURCE",data.newsSource)
            intent.putExtra("TIME",data.newsTime)
            intent.putExtra("ID",data.newsId)

            context.startActivity(intent)
        }
    }
}
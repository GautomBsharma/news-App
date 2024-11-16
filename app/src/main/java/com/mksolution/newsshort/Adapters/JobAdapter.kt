package com.mksolution.newsshort.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mksolution.newsshort.Models.Job
import com.mksolution.newsshort.R
import com.mksolution.newsshort.ReadJobActivity

class JobAdapter (var context: Context,var jobList: ArrayList<Job>): RecyclerView.Adapter<JobAdapter.JobHolder>(){

    inner class JobHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val image = itemView.findViewById<ImageView>(R.id.jobImg)
        val jobtitle = itemView.findViewById<TextView>(R.id.jobTitle)
        val provider = itemView.findViewById<TextView>(R.id.tvProvider)
        val readJob = itemView.findViewById<ImageButton>(R.id.readJob)
        val about = itemView.findViewById<TextView>(R.id.jobDes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.job_item,parent,false)
        return JobHolder(view)
    }

    override fun getItemCount(): Int {
        return jobList.size
    }

    override fun onBindViewHolder(holder: JobHolder, position: Int) {
        val data = jobList[position]
        holder.jobtitle.text = data.jobTitle
        holder.provider.text = data.jobProvider
        holder.about.text = data.jobAbout
        if(data.jobUrl.isNotEmpty()){
            Glide.with(context).load(data.jobUrl).into(holder.image)
        }
        else{
            holder.image.setImageResource(R.drawable.jobhint)
        }

        holder.readJob.setOnClickListener {
            val intent = Intent(context,ReadJobActivity::class.java)
            intent.putExtra("JOB_ID",data.jobId)
            intent.putExtra("JOB_ABOUT",data.jobAbout)
            intent.putExtra("JOB_DEADLINE",data.jobDeadline)
            intent.putExtra("JOB_EXP",data.jobExperiance)
            intent.putExtra("JOB_LOCATION",data.jobLocation)
            intent.putExtra("JOB_PROVIDER",data.jobProvider)
            intent.putExtra("JOB_SLOAT",data.jobSloat)
            intent.putExtra("JOB_TITLE",data.jobTitle)
            intent.putExtra("JOB_URL",data.jobUrl)
            intent.putExtra("JOB_PROLINK",data.jobProviderLink)
            intent.putExtra("JOB_SALARY",data.jobSalary)

            context.startActivity(intent)
        }
    }
}
package com.example.analytics

import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.analytics.models.new_item


class adapter(private val contestList: ArrayList<new_item>) :
    RecyclerView.Adapter<adapter.FontestViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FontestViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.new_item, parent, false)
        return FontestViewHolder(view)
    }

    override fun onBindViewHolder(holder: FontestViewHolder, position: Int) {
        val currContest = contestList[position]
        holder.itemView.apply {
            holder.itemView.findViewById<TextView>(R.id.contest_name).text = currContest.contest_name
//            contest_name.text = currContest.contest_name
            holder.itemView.findViewById<TextView>(R.id.contest_name).text = currContest.start_date
//            start_date.text = currContest.start_date
        }
    }


    override fun getItemCount(): Int {
        return contestList.size
    }

    inner class FontestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
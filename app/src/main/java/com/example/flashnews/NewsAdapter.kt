package com.example.flashnews

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.news_card.view.*

class NewsAdapter(list:List<News>,private val listener: RecyclerViewItemClickListener): RecyclerView.Adapter<NewsAdapter.MyViewHolder>() {

    private val arrayList: List<News> = list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.news_card, parent, false)
        val viewHolder = MyViewHolder(view)
        view.setOnClickListener { listener.onItemClick(viewHolder.adapterPosition) }
        return viewHolder
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.titleTextView.text = arrayList[position].Title
        holder.dateTextView.text = arrayList[position].date
        holder.sourceTextView.text = arrayList[position].source
        var description = arrayList[position].description
        if (description== "null"){
            description = "No description available, tap to read news in detail."

        }
        holder.descriptionTextView.text = description

        val imageUrl = arrayList[position].ImageUrl
        if (imageUrl != "null") {
            if (imageUrl.contains("youtube.com")) {
                // Get the video ID from the URL
                val videoId = imageUrl.substring(imageUrl.lastIndexOf("/") + 1)
                // Construct the thumbnail URL
                val thumbnailUrl = "https://img.youtube.com/vi/$videoId/0.jpg"
                Glide.with(holder.itemView.context).load(thumbnailUrl).into(holder.imageView)
            } else {

                Glide.with(holder.itemView.context).load(imageUrl).into(holder.imageView)
            }
        } else {
            Glide.with(holder.itemView.context).load(R.drawable.no_image)
                .into(holder.imageView)

        }
    }


    override fun getItemCount(): Int = arrayList.size


    class MyViewHolder(itemView: View) : ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.title)
        val imageView: ImageView = itemView.findViewById(R.id.newsImage)
        val dateTextView : TextView = itemView.findViewById(R.id.date)
        val descriptionTextView :TextView = itemView.findViewById(R.id.desc)
        val sourceTextView :TextView = itemView.findViewById(R.id.source)
    }

}
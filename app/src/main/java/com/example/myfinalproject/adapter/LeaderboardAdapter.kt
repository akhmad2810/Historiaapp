package com.example.myfinalproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.myfinalproject.R
import com.example.myfinalproject.data.LeaderboardItem


class LeaderboardAdapter : RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {

    private var list: List<LeaderboardItem> = emptyList()

    fun setData(newList: List<LeaderboardItem>) {
        list = newList
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rank: TextView = view.findViewById(R.id.tvRank)
        val avatar: ImageView = itemView.findViewById(R.id.imgAvatar)
        val name: TextView = view.findViewById(R.id.tvName)
        val title: TextView = view.findViewById(R.id.tvTitle)
        val score: TextView = view.findViewById(R.id.tvScore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leaderboard, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.rank.text = (position + 1).toString()
        holder.name.text = item.name
        holder.title.text = item.title
        holder.score.text = item.score.toString()

        
        Glide.with(holder.avatar.context)
            .load(item.avatar)
            .placeholder(R.drawable.ic_profile)
            .circleCrop()
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(holder.avatar)
    }
}

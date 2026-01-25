package com.example.myfinalproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.myfinalproject.R

class AvatarAdapter(
    private val items: List<String>,
    private val onSelect: (String) -> Unit
) : RecyclerView.Adapter<AvatarAdapter.AvatarViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvatarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_avatar, parent, false)
        return AvatarViewHolder(view)
    }

    override fun onBindViewHolder(holder: AvatarViewHolder, position: Int) {
        val avatarName = items[position]
        val resId = holder.itemView.resources.getIdentifier(
            avatarName, "drawable", holder.itemView.context.packageName
        )

        holder.imageView.setImageResource(resId)

        holder.itemView.setOnClickListener {
            onSelect(avatarName)
        }
    }

    override fun getItemCount() = items.size

    class AvatarViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.gridAvatar)
    }
}

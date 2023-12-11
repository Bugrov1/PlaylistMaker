package com.example.playlistmaker.player.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.mediateka.domain.model.Playlist


class BottomAdapter (

) : RecyclerView.Adapter<BottomViewHolder>() {
    var playlists = ArrayList<Playlist>()
    var onItemClick:((Playlist)->Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_items, parent, false)
        return BottomViewHolder(view)
    }

    override fun onBindViewHolder(holder: BottomViewHolder, position: Int) {
        holder.bind(playlists[position])

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(playlists[position])
            this.notifyDataSetChanged()
        }
    }
    override fun getItemCount(): Int {
        return playlists.size
    }
}
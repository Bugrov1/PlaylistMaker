package com.example.playlistmaker.mediateka.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.mediateka.domain.model.Playlist
import com.example.playlistmaker.player.ui.TracksEndingCount

class PlaylistViewHolder (view: View): RecyclerView.ViewHolder(view) {

    private val cover: ImageView = itemView.findViewById(R.id.cover)
    private val playlistName: TextView = itemView.findViewById(R.id.playlistName)
    private val tracksNumber:TextView = itemView.findViewById(R.id.playlistLength)

    fun bind(playlist: Playlist) {
        playlistName.text = playlist.playlistName
        tracksNumber.text = TracksEndingCount().tracksString(playlist.length ?: 0)
        val roundingRadius = 8
        Glide.with(itemView)
            .load(playlist.filepath)
            .placeholder(R.drawable.placeholdermiddle)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .transform(RoundedCorners(roundingRadius))
            .into(cover)

    }
}
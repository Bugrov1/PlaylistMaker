package com.example.playlistmaker.player.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.mediateka.domain.model.Playlist

class BottomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val playlistName: TextView = itemView.findViewById(R.id.playlistNameItem)
    private val trackNumber: TextView = itemView.findViewById(R.id.tracksNumberItem)
    private val cover: ImageView = itemView.findViewById(R.id.playlistCoverItem)
    fun bind(model: Playlist) {
        playlistName.text = model.playlistName

        trackNumber.text = TracksEndingCount().tracksString(model.length ?: 0)


        val roundingRadius = 10

        Glide.with(itemView)
            .load(model.filepath)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .transform(RoundedCorners(roundingRadius))
            .into(cover)
    }


}
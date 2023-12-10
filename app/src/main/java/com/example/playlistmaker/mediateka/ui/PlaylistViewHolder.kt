package com.example.playlistmaker.mediateka.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.mediateka.domain.model.Playlist

class PlaylistViewHolder (view: View): RecyclerView.ViewHolder(view) {

    private val cover: ImageView = itemView.findViewById(R.id.cover)
    private val playlistName: TextView = itemView.findViewById(R.id.playlistName)
    private val tracksNumber:TextView = itemView.findViewById(R.id.playlistLength)

    fun bind(playlist: Playlist) {
        playlistName.text = playlist.playlistName
        if (playlist.length==null){tracksNumber.text = "no tracks added"}
//        if(playlist.length!! >1){
//            tracksNumber.text = playlist.length.toString() +" tracks"
//        } else {tracksNumber.text = playlist.length.toString() +" track"}
        val roundingRadius = 10
        Glide.with(itemView)
            .load(playlist.filepath)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(roundingRadius))
            .into(cover)

    }
}
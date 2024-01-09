package com.example.playlistmaker.mediateka.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class ViewHolderPlaylistTracks (itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val trackName: TextView = itemView.findViewById(R.id.trackName)
    private val artistName: TextView = itemView.findViewById(R.id.artistName)
    private val trackTime: TextView = itemView.findViewById(R.id.trackTime)
    private val imageViewArtwork: ImageView = itemView.findViewById(R.id.imageViewArtwork)


    fun bind(model: Track) {
        trackName.text = model.trackName
        artistName.text = model.artistName
        val formatTrackTime = model.trackTimeMillis
        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(formatTrackTime)
        val roundingRadius = 10
        val albumCoverUrl =
            model.artworkUrl100
                .replaceAfterLast('/', "60x60bb.jpg")//done
        println(albumCoverUrl)
        Glide.with(itemView)
            .load(albumCoverUrl)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(roundingRadius))
            .into(imageViewArtwork)
    }


}
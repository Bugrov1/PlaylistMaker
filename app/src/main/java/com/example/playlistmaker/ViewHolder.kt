package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import kotlinx.coroutines.NonDisposableHandle.parent

class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val trackName: TextView= itemView.findViewById(R.id.trackName)
    private val artistName: TextView= itemView.findViewById(R.id.artistName)
    private val trackTime: TextView= itemView.findViewById(R.id.trackTime)
    private val artWork: ImageView= itemView.findViewById(R.id.artWork)


    fun bind(model: Track) {
        trackName.text = model.trackName
        artistName.text = model.artistName
        trackTime.text = model.trackTime
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.baseline_person_24)
            .centerCrop()
            .transform(RoundedCorners(10))
            .into(artWork)
    }



}









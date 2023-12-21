package com.example.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class Adapter(

) : RecyclerView.Adapter<ViewHolderTrack>() {
    var tracks = ArrayList<Track>()
    var onItemClick:((Track)->Unit)? = null
    var onLongItemclick: ((Track)->Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderTrack {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return ViewHolderTrack(view)
    }

    override fun onBindViewHolder(holder: ViewHolderTrack, position: Int) {
        holder.bind(tracks[position])

        holder.itemView.setOnClickListener {

            onItemClick?.invoke(tracks[position])
            //clickListener.onClick(tracks[position])
            this.notifyDataSetChanged()

        }

        holder.itemView.setOnLongClickListener {
            onLongItemclick?.invoke(tracks[position])
            return@setOnLongClickListener true
        }

    }

    fun interface TrackClickListener {
        fun onClick(track: Track)
    }
    override fun getItemCount(): Int {
        return tracks.size
    }



}
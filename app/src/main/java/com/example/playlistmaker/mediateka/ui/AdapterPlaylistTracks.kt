package com.example.playlistmaker.mediateka.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track

class AdapterPlaylistTracks : RecyclerView.Adapter<ViewHolderPlaylistTracks>() {
        var tracks = ArrayList<Track>()
        var onItemClick:((Track)->Unit)? = null
        var onLongItemclick: ((Track)->Unit)? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPlaylistTracks {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
            return ViewHolderPlaylistTracks(view)
        }

        override fun onBindViewHolder(holder: ViewHolderPlaylistTracks, position: Int) {
            holder.bind(tracks[position])

            holder.itemView.setOnClickListener {
                onItemClick?.invoke(tracks[position])
                this.notifyDataSetChanged()

            }

            holder.itemView.setOnLongClickListener {
                onLongItemclick?.invoke(tracks[position])
                return@setOnLongClickListener true
            }

        }

        override fun getItemCount(): Int {
            return tracks.size
        }

    }
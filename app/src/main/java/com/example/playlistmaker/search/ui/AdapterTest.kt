package com.example.playlistmaker.search.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track

class AdapterTest(val clickListener: TrackClickListener

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
            Log.v("NAV","adapterclickedformAdapter ${tracks[position]}")
//            onItemClick?.invoke(tracks[position])
            clickListener.onClick(tracks[position])
            Log.v("NAV","adapterclickedformAdapter ${clickListener}")

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
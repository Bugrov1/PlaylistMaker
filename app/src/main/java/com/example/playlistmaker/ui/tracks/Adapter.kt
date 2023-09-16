package com.example.playlistmaker.ui.tracks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.Track

class Adapter(

) : RecyclerView.Adapter<ViewHolder>() {
    var tracks = ArrayList<Track>()
    var onItemClick:((Track)->Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(tracks[position])

        holder.itemView.setOnClickListener {

            onItemClick?.invoke(tracks[position])
            //clickListener.onClick(tracks[position])
            this.notifyDataSetChanged()

        }

    }

    fun interface TrackClickListener {
        fun onClick(track: Track)
    }
    override fun getItemCount(): Int {
        return tracks.size
    }



}
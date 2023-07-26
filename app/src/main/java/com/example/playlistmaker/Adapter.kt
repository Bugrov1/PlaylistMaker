package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

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
        //val context = holder.itemView.context
       //val sharedPreferences = context.getSharedPreferences(TRACK_SEARCH_HISTORY ,
        //    Context.MODE_PRIVATE
        //)
        //val history = SearchHistory(sharedPreferences)
        holder.itemView.setOnClickListener {
            //history.write(tracks[position])
            //notifyDataSetChanged()
            onItemClick?.invoke(tracks[position])
        }

    }
    override fun getItemCount(): Int {
        return tracks.size
    }

}
package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners


class AdapterHistory(

) : RecyclerView.Adapter<ViewHolder>() {
    var tracks = ArrayList<Track>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(tracks[position])
        var context = holder.itemView.context
        var sharedPreferences = context.getSharedPreferences(TRACK_SEARCH_HISTORY , MODE_PRIVATE)
        var history = SearchHistory(sharedPreferences)
        holder.itemView.setOnClickListener {
            history.write(tracks[position])
            var sharedPreferences = context .getSharedPreferences(TRACK_SEARCH_HISTORY , MODE_PRIVATE);
            var historyList = SearchHistory(sharedPreferences).read()?.toCollection(ArrayList())
            if (historyList != null) {
                tracks = historyList
            }
            notifyDataSetChanged()
        }

    }
    override fun getItemCount(): Int {
        return tracks.size
    }

}
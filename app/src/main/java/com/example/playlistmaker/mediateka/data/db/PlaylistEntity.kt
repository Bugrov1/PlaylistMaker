package com.example.playlistmaker.mediateka.data.db

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson

@Entity(tableName = "playlist_table")
data class PlaylistEntity(
@PrimaryKey(autoGenerate = true)
val id: Long?,
val playlistName: String,
val description: String?,
val filepath: String?,
val tracks: String?,
val length: Int?,


)

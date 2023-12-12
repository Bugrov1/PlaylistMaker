package com.example.playlistmaker.mediateka.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
@Database(version = 1, entities = [TrackInPlaylistEntity::class])
abstract class TracksInPlaylistsDatabase: RoomDatabase(){
    abstract fun trackInPlaylistDao(): TrackInPlaylistDao
}

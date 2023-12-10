package com.example.playlistmaker.mediateka.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
@Database(version = 1, entities = [PlaylistEntity::class])
abstract class PlaylistDatabase:RoomDatabase(){

    abstract fun PlaylistDao(): PlaylistDao
}
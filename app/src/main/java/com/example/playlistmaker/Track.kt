package com.example.playlistmaker

import android.os.Parcel
import android.os.Parcelable

data class Track(
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTimeMillis: Long, // Продолжительность трек
    val artworkUrl100: String,// Ссылка на изображение обложк
    val trackId: Int,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?

)
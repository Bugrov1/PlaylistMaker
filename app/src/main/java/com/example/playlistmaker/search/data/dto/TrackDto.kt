package com.example.playlistmaker.search.data.dto

data class TrackDto (
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTimeMillis: Long, // Продолжительность трек
    val artworkUrl100: String,// Ссылка на изображение обложк
    val trackId: Int,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?
)

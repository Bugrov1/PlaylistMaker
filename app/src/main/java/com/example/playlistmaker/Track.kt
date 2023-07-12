package com.example.playlistmaker

data class Track(
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTimeMillis: Long, // Продолжительность трек
    val artworkUrl100: String, // Ссылка на изображение обложки
)
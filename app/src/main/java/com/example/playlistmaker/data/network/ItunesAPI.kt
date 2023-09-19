package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.dto.TrackResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesAPI {

    @GET("/search?entity=song")
    fun search(@Query("term") text: String): Call<TrackResponse>

    @GET("/search?entity=song")
    fun getDetails(@Query("term") text: String): Call<TrackResponse>
}
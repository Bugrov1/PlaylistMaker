package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.TrackSearchRequest
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {

    private val baseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ItunesAPI::class.java)

    override fun doRequest(dto: Any):com.example.playlistmaker.data.dto.Response  {
        if (dto is TrackSearchRequest) {
            val resp = itunesService.search(dto.expression).execute()

            val body = resp.body() ?: com.example.playlistmaker.data.dto.Response()

            return body.apply { resultCode = resp.code() }
        } else {
            return com.example.playlistmaker.data.dto.Response().apply { resultCode = 400 }
        }
    }
}
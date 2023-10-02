package com.example.playlistmaker.search.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.TrackSearchRequest

class RetrofitNetworkClient(private val context: Context,
                            private val itunesService: ItunesAPI,) : NetworkClient {



    override fun doRequest(dto: Any): com.example.playlistmaker.search.data.dto.Response {
        if (isConnected() == false) {
            return com.example.playlistmaker.search.data.dto.Response().apply { resultCode = -1 }
        }
        if (dto !is TrackSearchRequest) {
            return com.example.playlistmaker.search.data.dto.Response().apply { resultCode = 400 }
        }

        val response = itunesService.search(dto.expression).execute()
        val body = response.body()
        return if (body != null) {
            body.apply { resultCode = response.code() }
        } else {
            com.example.playlistmaker.search.data.dto.Response().apply { resultCode = response.code() }
        }
    }

    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }
}
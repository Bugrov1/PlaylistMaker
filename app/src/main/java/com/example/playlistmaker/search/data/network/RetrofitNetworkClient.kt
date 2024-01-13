package com.example.playlistmaker.search.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.TrackResponse
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse

class RetrofitNetworkClient(private val context: Context,
                            private val itunesService: ItunesAPI,) : NetworkClient {



    override suspend fun doRequest(dto: Any):  TrackResponse {
        Log.v("SEARCH","dto is {$dto}")
        if (isConnected() == false) {
            return TrackResponse(results = emptyList()).apply { resultCode = -1 }
        }
        if (dto !is TrackSearchRequest) {
            return TrackResponse(results = emptyList()).apply { resultCode = 400 }
        }


        return withContext(Dispatchers.IO){
            val response= itunesService.search(dto.expression).awaitResponse()
            Log.v("SEARCH","response is ${response.code()}")
            when(response.isSuccessful){
                true->  response.body()
                    ?.let { TrackResponse(results = it.results).apply { resultCode =response.code() } }!!

                else -> {TrackResponse(results = emptyList()).apply { resultCode =response.code() }}
            }


//            try {
//                val response= itunesService.search(dto.expression)
//
//                response.apply { resultCode = 200 }
//            } catch (e: Throwable) {
//                Log.v("SEARCH","response  is {${e}}")
//                Response().apply { resultCode = 500 }
//
//            }

        }
    }
    suspend fun getResult(text:String){

        GlobalScope.launch(Dispatchers.IO) {
            val response = itunesService.search(text)
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
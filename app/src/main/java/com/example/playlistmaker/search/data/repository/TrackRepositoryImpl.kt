package com.example.playlistmaker.search.data.repository

import android.util.Log
import com.example.playlistmaker.mediateka.data.db.AppDatabase
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.TrackResponse
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TrackRepositoryImpl(private val networkClient: NetworkClient,
                          private val appDatabase: AppDatabase) : TrackRepository {



    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        Log.v("SEARCH","${response.resultCode}")
        when (response.resultCode) {
             -1 -> {
                emit(Resource.Error("Проверьте подключение к интернету"))
                 Log.v("SEARCH","repository answer Проверьте подключение к интернету")
            }

            200 -> {
                with(response as TrackResponse){
                    val data = results.map{
                        Track(
                            it.trackName,
                            it.artistName,
                            it.trackTimeMillis,
                            it.artworkUrl100,
                            it.trackId,
                            it.collectionName,
                            it.releaseDate,
                            it.primaryGenreName,
                            it.country,
                            it.previewUrl,
                            isFavorite = it.trackId  in appDatabase.trackDao().getTracksId()
                        )
                    }
                    emit(Resource.Success(data))
                    Log.v("SEARCH","repositoy answer Данные")
                }

            }

            else -> {
               emit(Resource.Error("Ошибка сервера"))
                Log.v("SEARCH","repository answer Ошибка сервера")
            }
        }
    }
}
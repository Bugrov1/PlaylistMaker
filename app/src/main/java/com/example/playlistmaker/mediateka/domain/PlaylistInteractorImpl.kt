package com.example.playlistmaker.mediateka.domain

import android.util.Log
import com.example.playlistmaker.mediateka.domain.db.PlaylistInteractor
import com.example.playlistmaker.mediateka.domain.db.PlaylistRepository
import com.example.playlistmaker.mediateka.domain.model.Playlist
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository):PlaylistInteractor {
    override suspend fun insertList(playlist: Playlist) {
        playlistRepository.insertList(playlist)
    }

    override suspend fun updateList(playlist: Playlist) {
        playlistRepository.updateList(playlist)
    }

    override suspend fun getTracksId(id: Long): String {
        return playlistRepository.getTracksId(id)

    }



    override fun getLists(): Flow<List<Playlist>> {
        return playlistRepository.getLists()
    }

    override suspend fun insertTrack(track: Track) {
        playlistRepository.insertTrack(track)
    }

    override suspend fun getPlaylist(id: Long):Playlist {
        return playlistRepository.getPlaylist(id)
    }

    override fun getTrackListFlow(ids: List<Int>): Flow<List<Track>> {
        return playlistRepository.getTrackListFlow(ids)
    }

    override suspend fun getTracksListIds():List<String> {
        return playlistRepository.getTracksListIds()
    }

    override suspend fun checkTrack(track: Track):Boolean {
        val allTracks = getTracksListIds()
        var allTracksInt = mutableListOf<Int>()
        for (tracklist in allTracks){
            val newlist = Gson().fromJson(tracklist,Array<Int>::class.java)?: emptyArray()
            allTracksInt.addAll(newlist)
        }
        return track.trackId in allTracksInt

    }

    override suspend fun deleteTrack(track: Track) {
        playlistRepository.deleteTrack(track)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistRepository.delete(playlist)

    }

    override suspend fun getIdsFromAddedTracks(): List<Int> {
        return playlistRepository.getIdsFromAddedTracks()
    }

    override suspend fun deleteById(id:Int){
        playlistRepository.deleteById(id)
    }

    override suspend fun updateTracksTable() {
        val tracksInTable = getIdsFromAddedTracks()
        Log.v("updateTracksTable","$tracksInTable")
        val allTracks = getTracksListIds()
        var allTracksInPlaylist = mutableListOf<Int>()
        for (tracklist in allTracks){
            val newlist = Gson().fromJson(tracklist,Array<Int>::class.java)?: emptyArray()
            allTracksInPlaylist.addAll(newlist)
        }
        Log.v("updateTracksTable","$allTracksInPlaylist")
        for(id in tracksInTable){ if(id !in allTracksInPlaylist){
            Log.v("updateTracksTable","deleted")
            deleteById(id)}
        }
    }


}
package com.example.playlistmaker.player.ui.viewmodel


import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.mediateka.domain.db.FavoritesInteractor
import com.example.playlistmaker.mediateka.domain.db.PlaylistInteractor
import com.example.playlistmaker.mediateka.domain.model.Playlist
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.models.State
import com.example.playlistmaker.player.ui.models.PlayerState
import com.example.playlistmaker.player.ui.models.PlaylistState
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class PlayerViewModel(
    track: Track,
    private val mediaPlayer: PlayerInteractor,
    private val favoritesInteractor: FavoritesInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private var trackInit = track

    private val _playerState =
        MutableStateFlow<PlayerState>(PlayerState.Default(trackInit.isFavorite))
    val playerState = _playerState.asStateFlow()


    private val playlistLiveData = MutableLiveData<List<Playlist>?>()
    fun observePlaylists(): MutableLiveData<List<Playlist>?> = playlistLiveData

    private val trackLiveData = MutableLiveData<PlaylistState>()
    fun observeTracks(): MutableLiveData<PlaylistState> = trackLiveData

    fun addToPlaylist(trackId:Int,playlist:Playlist){
        val jsonIds = playlist.tracks
        val trackList =  Gson().fromJson(jsonIds,Array<Int>::class.java)?: emptyArray()
        if(trackList.contains(trackId)) {
            trackLiveData.postValue(PlaylistState.inPlaylist("Данный трек уже есть в плейлисте ${playlist.playlistName}"))
        }
        else{
            val newtrackList =  trackList.plus(trackId)
            val tracksList = Gson().toJson(newtrackList)
            val playlistSize = newtrackList.size
            val updatedPlaylist =Playlist(
                id=playlist.id,
                playlistName = playlist.playlistName,
                description = playlist.description,
                filepath = playlist.filepath,
                length = playlistSize,
                tracks = tracksList
            )
            viewModelScope.launch {
                playlistInteractor.insertTrack(trackInit)
                playlistInteractor.updateList(updatedPlaylist)
                playlistInteractor.getLists()
                    .collect { playlists ->
                        processResult(playlists)
                    }
            }
            trackLiveData.postValue(PlaylistState.inPlaylist("Tрек добавлен в плейлист ${playlist.playlistName}"))
        }

    }
    fun getPlayLists() {
        viewModelScope.launch {
            playlistInteractor.getLists()
                .collect { playlists ->
                    processResult(playlists)
                }
        }
    }

    fun refreshBottomSheet() {
        getPlayLists()
    }

    private fun processResult(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            render(emptyList<Playlist>())
        } else {
            render(playlists)
        }
    }

    private fun render(playlists: List<Playlist>?) {
        playlistLiveData.postValue(playlists)
    }

    init {
        trackInit.previewUrl?.let { mediaPlayer.setDataSource(it) }
        playerInit()
        Log.v("State", "State ---${_playerState}")
        Log.v("Fav Track", "Fav Track ---${trackInit.isFavorite}")

        getPlayLists()
    }


    private var timerJob: Job? = null
    fun playerInit() {

        mediaPlayer.setOnPreparedListener {
            _playerState.value = PlayerState.Prepared(trackInit.isFavorite)

        }
        mediaPlayer.setOnCompletionListener {
            _playerState.value = PlayerState.Prepared(trackInit.isFavorite)
            timerJob?.cancel()
            Log.v("STATUS", "Isplaying ---${mediaPlayer.isPlaying()}")
            Log.v("STATUS", "endtime ---${getCurrentPlayerPosition()}")

        }
    }

    fun onFavoriteClicked() {
        if (!trackInit.isFavorite) {
            viewModelScope.launch {
                favoritesInteractor.addToFavorites(trackInit)
            }
            trackInit.isFavorite = true
            _playerState.value =
                PlayerState.Neutral(
                    buttonText = _playerState.value.buttonText,
                    progress = _playerState.value.progress,
                    inFavorites = trackInit.isFavorite
                )

        } else {
            viewModelScope.launch { favoritesInteractor.deleteFromFavorites(trackInit) }
            trackInit.isFavorite = false
            _playerState.value = PlayerState.Neutral(
                buttonText = _playerState.value.buttonText,
                progress = _playerState.value.progress,
                inFavorites = trackInit.isFavorite
            )

        }
    }

    fun playClickListen() {
        when (playerState.value) {
            is PlayerState.Playing -> {
                pausePlayer()
            }

            is PlayerState.Prepared, is PlayerState.Paused, is PlayerState.Neutral -> {
                startPlayer()
            }

            else -> {}
        }
        startTimer()

    }


    override fun onCleared() {
        super.onCleared()
        releasePlayer()

    }


    private fun startTimer() {

        timerJob = viewModelScope.launch {
            while (mediaPlayer.getState() == State.PLAYING) {
                delay(300L)
                Log.v("STATUS", "current time ---${getCurrentPlayerPosition()}")
                _playerState.value =
                    PlayerState.Playing(getCurrentPlayerPosition(), trackInit.isFavorite)
            }


        }
    }

    private fun getCurrentPlayerPosition(): String {
        return mediaPlayer.currentPosition()

    }


    private fun startPlayer() {
        mediaPlayer.startPlayer()
        _playerState.value = PlayerState.Playing(getCurrentPlayerPosition(), trackInit.isFavorite)

    }

    fun pausePlayer() {
        _playerState.value = PlayerState.Paused(getCurrentPlayerPosition(), trackInit.isFavorite)
        mediaPlayer.pausePlayer()
        timerJob?.cancel()


    }

    private fun releasePlayer() {
        mediaPlayer.stop()
        mediaPlayer.release()
        timerJob?.cancel()
        _playerState.value = PlayerState.Default(trackInit.isFavorite)
    }

    fun playerStop() {
        mediaPlayer.stop()
        timerJob?.cancel()
    }

}
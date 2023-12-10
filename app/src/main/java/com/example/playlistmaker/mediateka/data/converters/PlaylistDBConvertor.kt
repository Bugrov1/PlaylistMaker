package com.example.playlistmaker.mediateka.data.converters

import android.net.Uri
import com.example.playlistmaker.mediateka.data.db.PlaylistEntity
import com.example.playlistmaker.mediateka.data.db.TrackEntity
import com.example.playlistmaker.mediateka.domain.model.Playlist
import com.example.playlistmaker.search.domain.models.Track

class PlaylistDBConvertor {

    fun map(playlist: Playlist): PlaylistEntity {

        return PlaylistEntity(
            id=null,
            playlistName = playlist.playlistName,
            description = playlist.description,
            filepath = playlist.filepath.toString(),
            length = playlist.length,
            tracks = playlist.tracks
        )
    }

    fun map(entity: PlaylistEntity): Playlist {
        return Playlist(
            id = entity.id,
            playlistName = entity.playlistName,
            description = entity.description,
            filepath = Uri.parse(entity.filepath),
            length = entity.length,
            tracks = entity.tracks
        )
    }
}
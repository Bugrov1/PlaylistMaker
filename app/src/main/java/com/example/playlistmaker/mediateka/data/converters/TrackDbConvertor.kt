package com.example.playlistmaker.mediateka.data.converters

import com.example.playlistmaker.mediateka.data.db.TrackEntity
import com.example.playlistmaker.search.domain.models.Track

class TrackDbConvertor {
    fun map(track: Track): TrackEntity {
        return TrackEntity(
            trackId=track.trackId,
            trackName=track.trackName,
            artistName=track.artistName,
            trackTimeMillis=track.trackTimeMillis,
            artworkUrl100=track.artworkUrl100,
            collectionName=track.collectionName,
            releaseDate=track.releaseDate,
            primaryGenreName=track.primaryGenreName,
            country=track.country,
            previewUrl=track.previewUrl,
        )
    }

    fun map(entity: TrackEntity): Track {
        return Track(
            trackId=entity.trackId,
            trackName=entity.trackName,
            artistName=entity.artistName,
            trackTimeMillis=entity.trackTimeMillis,
            artworkUrl100=entity.artworkUrl100,
            collectionName=entity.collectionName,
            releaseDate=entity.releaseDate,
            primaryGenreName=entity.primaryGenreName,
            country=entity.country,
            previewUrl=entity.previewUrl,
            isFavorite = true)
    }
}
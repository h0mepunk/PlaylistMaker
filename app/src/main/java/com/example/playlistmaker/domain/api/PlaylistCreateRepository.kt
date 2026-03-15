package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Playlist

interface PlaylistCreateRepository{
    fun getCurrentCreatingPlaylist(): Playlist?

    fun saveCurrentCreatingPlaylist(playlist: Playlist?)

    fun getCurrentPlaylist() : Playlist?

    fun saveCurrentPlaylist(playlist: Playlist?)

    fun getIsEditedFlag(): Boolean

     fun setIsEditedFlag(isEdited: Boolean)
}
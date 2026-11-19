package com.example.playlist_maker.data.search.repository

import com.example.playlist_maker.data.search.mapper.toTrack
import com.example.playlist_maker.data.search.network.ITunesApi
import com.example.playlist_maker.data.search.network.NetworkStateProvider
import com.example.playlist_maker.domain.models.Track
import com.example.playlist_maker.domain.search.repository.TrackRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn


class TrackRepositoryImpl(
    private val api: ITunesApi,
    private val networkStateProvider: NetworkStateProvider
) : TrackRepository {

    override fun search(query: String): Flow<Pair<List<Track>?, String?>> = flow {
        if (!networkStateProvider.isConnected()){
            emit(Pair(null, "Нет соединения с интернетом"))
            return@flow
        }
        try{

            val response = api.search(query)
            val tracks = response.results.map { it.toTrack() }
            emit(Pair(tracks, null))
        }
        catch (e: Exception){
            emit(Pair(null, "Ошибка сервера"))
        }
    }.flowOn(Dispatchers.IO)
}
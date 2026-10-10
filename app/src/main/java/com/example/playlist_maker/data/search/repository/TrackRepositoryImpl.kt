package com.example.playlist_maker.data.search.repository

import com.example.playlist_maker.data.search.dto.TrackSearchResponse
import com.example.playlist_maker.data.search.mapper.toTrack
import com.example.playlist_maker.data.search.network.NetworkStateProvider
import com.example.playlist_maker.data.search.network.RetrofitClient
import com.example.playlist_maker.domain.search.repository.TrackRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TrackRepositoryImpl(
    private val networkStateProvider: NetworkStateProvider
) : TrackRepository {

    override fun search(query: String, consumer: TrackRepository.TracksConsumer) {
        if (!networkStateProvider.isConnected()) {
            consumer.consume(emptyList(), "Нет соединения с интернетом")
            return
        }

        RetrofitClient.iTunesApi.search(query).enqueue(object : Callback<TrackSearchResponse> {
            override fun onResponse(
                call: Call<TrackSearchResponse>,
                response: Response<TrackSearchResponse>
            ) {
                if (response.isSuccessful) {
                    val tracks = response.body()?.results?.map { it.toTrack() } ?: emptyList()
                    consumer.consume(tracks, null)
                } else {
                    consumer.consume(emptyList(), "Ошибка сервера")
                }
            }

            override fun onFailure(call: Call<TrackSearchResponse>, t: Throwable) {
                consumer.consume(emptyList(), "Нет соединения с интернетом")
            }
        })
    }
}
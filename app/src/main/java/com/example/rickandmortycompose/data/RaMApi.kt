package com.example.rickandmortycompose.data

import com.example.rickandmortycompose.data.characters.CharactersDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RaMApi {
    @GET("character")
    suspend fun getAllCharacters(
        @Query("count") count: Int,
        @Query("page") page: Int,
        @Query("status") status: String = "",
        @Query("gender") gender: String = ""
    ): CharactersDto

    @GET("episode/{episode_id}")
    suspend fun getEpisodeInfo(
        @Path("episode_id") episodeId: String
    ): List<EpisodeDto>
}
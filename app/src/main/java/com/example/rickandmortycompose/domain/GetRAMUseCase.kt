package com.example.rickandmortycompose.domain

import com.example.rickandmortycompose.data.EpisodeDto
import com.example.rickandmortycompose.data.RaMRepository
import com.example.rickandmortycompose.data.characters.CharactersDto
import javax.inject.Inject

class GetRAMUseCase @Inject constructor(
    private val ramRepository: RaMRepository
) {
    suspend fun executeCharacters(
        count: Int,
        pages: Int,
        status: String,
        gender: String
    ): CharactersDto {
        return ramRepository.getCharactersList(count, pages, status, gender)
    }

    suspend fun executeEpisodeInfo(episodeId: String): List<EpisodeDto> {
        return ramRepository.getEpisodeInfo(episodeId)
    }
}
package com.example.rickandmortycompose.data.characters

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.example.rickandmortycompose.entity.characters.Characters
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class CharactersDto(
    @Json(name = "info") override val info: InfoDto?,
    @Json(name = "results") override val results: List<ResultCharacterDto>
) : Characters, Parcelable
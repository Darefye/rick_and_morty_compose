package com.example.rickandmortycompose.presentation.charactersList

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.rickandmortycompose.data.characters.ResultCharacterDto
import com.example.rickandmortycompose.presentation.RaMViewModel

class CharactersListPagingSource(
    private val viewModel: RaMViewModel
) : PagingSource<Int, ResultCharacterDto>() {
    override fun getRefreshKey(state: PagingState<Int, ResultCharacterDto>): Int = FIRST_PAGE

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ResultCharacterDto> {
        val page = params.key ?: FIRST_PAGE
        return kotlin.runCatching {
            viewModel.loadCharacters(
                count = 10,
                page = page,
                status = viewModel.getFilter().value.paramStatus,
                gender = viewModel.getFilter().value.paramGender
            ).results
        }.fold(
            onSuccess = {
                LoadResult.Page(
                    data = it,
                    prevKey = null,
                    nextKey = if (it.isEmpty()) null else page + 1
                )
            },
            onFailure = { throwable -> LoadResult.Error(throwable) }
        )

    }

    companion object {
        private const val FIRST_PAGE = 1
        fun page(viewModel: RaMViewModel) =
            Pager(PagingConfig(pageSize = 15)) { CharactersListPagingSource(viewModel) }.flow
    }
}
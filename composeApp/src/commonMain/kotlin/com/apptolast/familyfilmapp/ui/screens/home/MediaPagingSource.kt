package com.apptolast.familyfilmapp.ui.screens.home

import app.cash.paging.PagingSource
import app.cash.paging.PagingState
import com.apptolast.familyfilmapp.model.local.Media
import com.apptolast.familyfilmapp.model.local.toDomain
import com.apptolast.familyfilmapp.repositories.datasources.TmdbDatasource

class MediaPagingSource(
    private val tmdbDatasource: TmdbDatasource,
    private val countryCode: String,
) : PagingSource<Int, Media>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Media> = try {
        val currentPage = params.key ?: 1
        val movies = tmdbDatasource.getPopularMovies(page = currentPage)

        LoadResult.Page(
            data = movies.distinct().map { it.toDomain(countryCode) },
            prevKey = if (currentPage == 1) null else currentPage - 1,
            nextKey = if (movies.isEmpty()) null else currentPage + 1,
        )
    } catch (e: Throwable) {
        LoadResult.Error(e)
    }

    override fun getRefreshKey(state: PagingState<Int, Media>): Int? = state.anchorPosition
}

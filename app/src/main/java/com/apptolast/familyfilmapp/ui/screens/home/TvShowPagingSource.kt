package com.apptolast.familyfilmapp.ui.screens.home

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apptolast.familyfilmapp.model.local.Media
import com.apptolast.familyfilmapp.model.local.toDomain
import com.apptolast.familyfilmapp.repositories.datasources.TmdbDatasource
import retrofit2.HttpException
import java.io.IOException

class TvShowPagingSource(private val tmdbDatasource: TmdbDatasource, private val countryCode: String) :
    PagingSource<Int, Media>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Media> {
        return try {
            val currentPage = params.key ?: 1
            val tvShows = tmdbDatasource.getPopularTvShows(page = currentPage)
            LoadResult.Page(
                data = tvShows.distinct().map { it.toDomain(countryCode) },
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = if (tvShows.isEmpty()) null else currentPage + 1,
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Media>): Int? = state.anchorPosition
}

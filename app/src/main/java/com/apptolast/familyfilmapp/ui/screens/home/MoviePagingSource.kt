package com.apptolast.familyfilmapp.ui.screens.home

import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadParams
import androidx.paging.PagingSource.LoadResult
import androidx.paging.PagingState
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.model.local.toDomain
import com.apptolast.familyfilmapp.repositories.datasources.TmdbDatasource
import java.io.IOException
import retrofit2.HttpException

class MoviePagingSource(private val tmdbDatasource: TmdbDatasource) : PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        return try {
            val currentPage = params.key ?: 1
            val movies = tmdbDatasource.getPopularMovies(
                page = currentPage,
            )

            LoadResult.Page(
                data = movies.map { it.toDomain() },
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = if (movies.isEmpty()) null else currentPage + 1,
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? = state.anchorPosition
}

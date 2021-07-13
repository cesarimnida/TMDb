package br.com.cesarimnida.tmdb.movie.service

import br.com.cesarimnida.tmdb.BuildConfig
import br.com.cesarimnida.tmdb.commons.service.BaseServiceInterface
import br.com.cesarimnida.tmdb.commons.service.lazyInterface
import br.com.cesarimnida.tmdb.movie.model.GenresResponse
import br.com.cesarimnida.tmdb.movie.model.RequestResponse
import io.reactivex.Single
import java.util.*

/**
 * ************************************************************
 * Autor : Cesar Augusto dos Santos
 * Data : 03/08/2019
 * ************************************************************
 */
open class MovieService : BaseServiceInterface {
    private val movieInterface: MovieInterface by lazyInterface()
    private val language = "${Locale.getDefault().language}-${Locale.getDefault().country}"
    open fun fetchUpcomingMovies(page: Int = 1): Single<RequestResponse> {
        return movieInterface.fetchUpcomingMovies(
            BuildConfig.API_KEY,
            language,
            page.toString()
        )
    }

    open fun queryMovie(query: String, page: Int = 1): Single<RequestResponse> {
        return movieInterface.queryMovie(
            BuildConfig.API_KEY,
            language,
            query,
            page.toString()
        )
    }

    open fun fetchGenres(): Single<GenresResponse> {
        return movieInterface.fetchGenres(
            BuildConfig.API_KEY,
            language
        )
    }
}
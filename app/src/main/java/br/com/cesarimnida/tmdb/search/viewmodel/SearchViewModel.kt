package br.com.cesarimnida.tmdb.search.viewmodel

import android.app.Application
import br.com.cesarimnida.tmdb.movie.model.RequestResponse
import br.com.cesarimnida.tmdb.movie.service.MovieService
import br.com.cesarimnida.tmdb.movie.viewmodel.MovieViewModel
import io.reactivex.Completable
import io.reactivex.Single

/**
 * ************************************************************
 * Autor : Cesar Augusto dos Santos
 * Data : 04/08/2019
 * ************************************************************
 */
class SearchViewModel(application: Application) : MovieViewModel(application) {
    constructor(
        application: Application,
        movieService: MovieService
    ) : this(application) {
        this.movieService = movieService
    }

    private var query = ""
    override fun fetchMoviesSingle(): Single<RequestResponse> {
        return checkForQuery()
            .andThen(movieService.queryMovie(query))
    }

    private fun checkForQuery(): Completable {
        return Completable.create {
            if (query.isBlank()) it.onError(IllegalStateException())
            else it.onComplete()
        }
    }

    override fun fetchMoreMoviesSingle(nextPage: Int): Single<RequestResponse> {
        return movieService.queryMovie(query, nextPage)
    }

    fun updateQuery(query: String) {
        this.query = query
        fetchMovies()
    }
}
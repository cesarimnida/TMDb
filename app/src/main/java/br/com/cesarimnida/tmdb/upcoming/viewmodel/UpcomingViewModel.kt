package br.com.cesarimnida.tmdb.upcoming.viewmodel

import android.app.Application
import androidx.collection.SparseArrayCompat
import br.com.cesarimnida.tmdb.App
import br.com.cesarimnida.tmdb.movie.model.Movie
import br.com.cesarimnida.tmdb.movie.model.MovieGenre
import br.com.cesarimnida.tmdb.movie.model.RequestResponse
import br.com.cesarimnida.tmdb.movie.model.dao.MovieDao
import br.com.cesarimnida.tmdb.movie.model.dao.MovieGenreDao
import br.com.cesarimnida.tmdb.movie.service.MovieService
import br.com.cesarimnida.tmdb.movie.viewmodel.MovieViewModel
import io.reactivex.Single

/**
 * ************************************************************
 * Autor : Cesar Augusto dos Santos
 * Data : 03/08/2019
 * ************************************************************
 */
class UpcomingViewModel(application: Application) : MovieViewModel(application) {
    constructor(
        application: Application,
        movieService: MovieService
    ) : this(application) {
        this.movieService = movieService
    }

    private val movieDao: MovieDao by lazy { (application as App).db.movieDao() }
    private val movieGenreDao: MovieGenreDao by lazy { (application as App).db.movieGenreDao() }
    override fun fetchMoviesSingle(): Single<RequestResponse> {
        return movieService
            .fetchUpcomingMovies()
            .flatMap {
                movieDao
                    .deleteAllMovies()
                    .andThen(movieDao.insertMovies(*it.results.toTypedArray()))
                    .andThen(createMovieGenres(it.results)
                        .flatMap { movieGenres ->
                            movieGenreDao
                                .deleteAllMovieGenres()
                                .andThen(movieGenreDao.insertMovieGenres(*movieGenres.toTypedArray()))
                                .toSingleDefault(it)
                        })
            }
            .onErrorResumeNext(getMoviesFromDb())
    }

    private fun createMovieGenres(movies: ArrayList<Movie>): Single<ArrayList<MovieGenre>> {
        return Single.create {
            val movieGenres = ArrayList<MovieGenre>()
            movies.forEach { movie ->
                movie.genreIds.forEach { genreId ->
                    movieGenres.add(MovieGenre(movie.id, genreId))
                }
            }
            it.onSuccess(movieGenres)
        }
    }

    private fun getMoviesFromDb(): Single<RequestResponse> {
        return movieDao
            .getAll()
            .flatMap {
                movieGenreDao
                    .getAll()
                    .flatMap { movieGenres ->
                        mapMovieGenresByMovie(movieGenres)
                    }
                    .flatMap { movieGenres ->
                        injectGenreIdsToMovies(it, movieGenres)
                    }
            }
            .flatMap {
                Single.just(RequestResponse(1, ArrayList(it), 1, it.size, true))
            }
    }

    private fun injectGenreIdsToMovies(
        movies: List<Movie>,
        movieGenres: SparseArrayCompat<ArrayList<Int>>
    ): Single<List<Movie>> {
        return Single.create {
            movies.forEach { movie ->
                movie.genreIds = movieGenres.get(movie.id) ?: return@forEach
            }
            it.onSuccess(movies)
        }
    }

    private fun mapMovieGenresByMovie(movieGenres: List<MovieGenre>): Single<SparseArrayCompat<ArrayList<Int>>> {
        return Single.create {
            val map = SparseArrayCompat<ArrayList<Int>>()
            movieGenres.forEach { movieGenre ->
                val mappedList = map[movieGenre.movieId] ?: ArrayList()
                mappedList.add(movieGenre.genreId)
                map.put(movieGenre.movieId, mappedList)
            }
            it.onSuccess(map)
        }
    }

    override fun fetchMoreMoviesSingle(nextPage: Int): Single<RequestResponse> {
        return movieService.fetchUpcomingMovies(nextPage)
            .flatMap {
                movieDao
                    .insertMovies(*it.results.toTypedArray())
                    .andThen(
                        createMovieGenres(it.results)
                            .flatMap { movieGenres ->
                                movieGenreDao.insertMovieGenres(*movieGenres.toTypedArray())
                                    .toSingleDefault(it)
                            })
            }
    }
}
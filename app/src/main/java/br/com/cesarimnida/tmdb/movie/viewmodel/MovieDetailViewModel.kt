package br.com.cesarimnida.tmdb.movie.viewmodel

import android.app.Application
import androidx.collection.SparseArrayCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.com.cesarimnida.tmdb.App
import br.com.cesarimnida.tmdb.R
import br.com.cesarimnida.tmdb.commons.viewmodel.BaseViewModel
import br.com.cesarimnida.tmdb.commons.viewmodel.StatusEvent
import br.com.cesarimnida.tmdb.movie.model.Genre
import br.com.cesarimnida.tmdb.movie.model.Movie
import br.com.cesarimnida.tmdb.movie.model.dao.GenreDao
import br.com.cesarimnida.tmdb.movie.service.MovieService
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.net.UnknownHostException

/**
 * ************************************************************
 * Autor : Cesar Augusto dos Santos
 * Data : 05/08/2019
 * ************************************************************
 */
class MovieDetailViewModel(application: Application) : BaseViewModel(application) {
    constructor(
        application: Application,
        movieService: MovieService
    ) : this(application) {
        this.movieService = movieService
    }

    private val genreDao: GenreDao by lazy { (application as App).db.genreDao() }
    private var movieService = MovieService()
    private val selectedMovie = MutableLiveData<Movie>()
    val selectedMovieLiveData: LiveData<Movie>
        get() = selectedMovie
    private val genres = MutableLiveData<StatusEvent<SparseArrayCompat<Genre>>>()
    val genresLiveData: LiveData<StatusEvent<SparseArrayCompat<Genre>>>
        get() = genres


    fun selectMovie(movie: Movie) {
        this.selectedMovie.value = movie
    }

    fun fetchGenres() {
        val observer = fetchGenresObserver()
        genreDao
            .getAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap {
                if (it.isNullOrEmpty()) {
                    fetchGenresOnline()
                } else {
                    Single.just(it)
                }
            }
            .flatMap {
                hashGenres(ArrayList(it))
            }
            .subscribe(observer)
    }

    private fun fetchGenresObserver(): SingleObserver<SparseArrayCompat<Genre>> {
        return object : SingleObserver<SparseArrayCompat<Genre>> {
            override fun onSuccess(genres: SparseArrayCompat<Genre>) {
                this@MovieDetailViewModel.genres.value = StatusEvent.success(genres)
            }

            override fun onSubscribe(d: Disposable) {
                genres.value = StatusEvent.loading()
            }

            override fun onError(e: Throwable) {
                val message = when (e) {
                    is UnknownHostException -> R.string.err_genre_no_connection
                    else -> R.string.err_genre_general_message
                }
                genres.value = StatusEvent.error(message)
            }
        }
    }

    private fun fetchGenresOnline(): Single<List<Genre>> {
        return movieService
            .fetchGenres()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap {
                insertGenres(it.genres)
                    .andThen(Single.just(it.genres.toList()))
            }
    }

    private fun insertGenres(genres: ArrayList<Genre>): Completable {
        return genreDao.insertGenres(*genres.toTypedArray())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private fun hashGenres(genres: ArrayList<Genre>): Single<SparseArrayCompat<Genre>> {
        return Single.create {
            val hash = SparseArrayCompat<Genre>()
            genres.forEach { genre ->
                hash.put(genre.id, genre)
            }
            it.onSuccess(hash)
        }
    }

    fun joinGenresToString(): String {
        val genresIds = selectedMovie.value?.genreIds ?: return ""
        val genres = (genres.value as? StatusEvent.Success)?.data ?: return ""
        val genresNames = ArrayList<String>()
        genresIds.forEach {
            genresNames.add(genres[it]!!.name)
        }
        return genresNames.joinToString()
    }

    fun buildOriginalMoviePoster(): String? {
        val movie = selectedMovie.value ?: return null
        return if (movie.posterPath == null) {
            null
        } else {
            "https://image.tmdb.org/t/p/original${movie.posterPath}"
        }
    }
}
package br.com.cesarimnida.tmdb.movie.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.com.cesarimnida.tmdb.R
import br.com.cesarimnida.tmdb.commons.viewmodel.BaseViewModel
import br.com.cesarimnida.tmdb.commons.viewmodel.StatusEvent
import br.com.cesarimnida.tmdb.movie.model.Movie
import br.com.cesarimnida.tmdb.movie.model.RequestResponse
import br.com.cesarimnida.tmdb.movie.service.MovieService
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.net.UnknownHostException

/**
 * ************************************************************
 * Autor : Cesar Augusto dos Santos
 * Data : 04/08/2019
 * ************************************************************
 */
abstract class MovieViewModel(application: Application) : BaseViewModel(application) {
    protected var movieService = MovieService()
    protected val movies = ArrayList<Movie>()
    protected val requestResponse = MutableLiveData<StatusEvent<RequestResponse>>()
    private var currentRequestResponse: RequestResponse? = null
    val responseLiveData: LiveData<StatusEvent<RequestResponse>>
        get() = requestResponse

    private fun fetchMoviesObserver(): SingleObserver<RequestResponse> {
        return object : SingleObserver<RequestResponse> {
            override fun onSuccess(requestResponse: RequestResponse) {
                movies.addAll(requestResponse.results)
                when {
                    requestResponse.results.isNullOrEmpty() -> {
                        this@MovieViewModel.requestResponse.value = StatusEvent.emptyList()
                        currentRequestResponse = null
                    }
                    requestResponse.isOffline -> {
                        this@MovieViewModel.requestResponse.value = StatusEvent.successOffline(requestResponse)
                        currentRequestResponse = requestResponse
                    }
                    else -> {
                        this@MovieViewModel.requestResponse.value = StatusEvent.success(requestResponse)
                        currentRequestResponse = requestResponse
                    }
                }
            }

            override fun onSubscribe(d: Disposable) {
                requestResponse.value = StatusEvent.loading()
            }

            override fun onError(e: Throwable) {
                val message = when (e) {
                    is UnknownHostException -> R.string.err_no_connection
                    is IllegalStateException -> {
                        requestResponse.value = StatusEvent.refreshList()
                        return
                    }
                    else -> R.string.err_general_message
                }
                requestResponse.value = StatusEvent.error(message)
            }
        }
    }

    fun fetchMovies() {
        val observer = fetchMoviesObserver()
        fetchMoviesSingle()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)
    }

    abstract fun fetchMoviesSingle(): Single<RequestResponse>

    fun fetchMoreMovies(lastVisibleItem: Int) {
        if (!shouldRefresh(lastVisibleItem)) return
        val observer = fetchMoviesObserver()
        val page = currentRequestResponse!!.page
        fetchMoreMoviesSingle(page + 1)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)
    }

    private fun shouldRefresh(lastVisibleItem: Int): Boolean {
        val itemCount = movies.size
        return when (val requestResponse = this.requestResponse.value) {
            is StatusEvent.Success -> {
                val page = requestResponse.data.page
                val totalPage = requestResponse.data.totalPages
                itemCount < (lastVisibleItem + 5) && page < totalPage
            }
            is StatusEvent.Error -> {
                currentRequestResponse != null && itemCount < (lastVisibleItem + 5)
            }
            else -> {
                false
            }
        }
    }

    abstract fun fetchMoreMoviesSingle(nextPage: Int): Single<RequestResponse>

    fun moviesSize(): Int {
        return movies.size
    }

    fun movieItem(position: Int): Movie {
        return movies[position]
    }

    fun resetMovies() {
        movies.clear()
        requestResponse.value = StatusEvent.refreshList()
    }

    fun buildMoviePoster(movie: Movie): String? {
        return if (movie.posterPath == null) null else "https://image.tmdb.org/t/p/w185${movie.posterPath}"
    }
}
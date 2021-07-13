package br.com.cesarimnida.tmdb.upcoming.viewmodel

import androidx.lifecycle.Observer
import br.com.cesarimnida.tmdb.R
import br.com.cesarimnida.tmdb.commons.viewmodel.BaseViewModelTest
import br.com.cesarimnida.tmdb.commons.viewmodel.StatusEvent
import br.com.cesarimnida.tmdb.movie.model.*
import br.com.cesarimnida.tmdb.movie.model.dao.MovieDao
import br.com.cesarimnida.tmdb.movie.model.dao.MovieGenreDao
import br.com.cesarimnida.tmdb.movie.service.MovieService
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import io.reactivex.Completable
import io.reactivex.Single
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import java.net.UnknownHostException

/**
 * ************************************************************
 * Autor : Cesar Augusto dos Santos
 * Data : 07/08/2019
 * ************************************************************
 */
@Suppress("UNCHECKED_CAST")
class UpcomingViewModelTest : BaseViewModelTest() {
    private lateinit var viewModel: UpcomingViewModel
    @Mock
    var service = MovieService()
    @Mock
    lateinit var movieDao: MovieDao
    @Mock
    lateinit var movieGenreDao: MovieGenreDao
    private val movieMockFactory = MovieMockFactory()
    private val movieGenreMockFactory = MovieGenreMockFactory()

    @Before
    fun before() {
        viewModel = UpcomingViewModel(application, service)
        doReturn(movieDao).`when`(db).movieDao()
        doReturn(movieGenreDao).`when`(db).movieGenreDao()
        doReturn(Single.just(ArrayList<MovieGenre>()))
            .`when`(movieGenreDao)
            .getAll()
        doReturn(Single.just(ArrayList<Movie>()))
            .`when`(movieDao)
            .getAll()
        doReturn(Completable.complete())
            .`when`(movieGenreDao)
            .deleteAllMovieGenres()
        doReturn(Completable.complete())
            .`when`(movieDao)
            .deleteAllMovies()
        doReturn(Completable.complete())
            .`when`(movieGenreDao)
            .insertMovieGenres(any())
        doReturn(Completable.complete())
            .`when`(movieDao)
            .insertMovies(any())
    }

    @Test
    fun fetchMovies_success() {
        val movies = movieMockFactory.nList(15)
        doReturn(Single.just(RequestResponse(1, movies, 1, movies.size)))
            .`when`(service)
            .fetchUpcomingMovies()
        val observer = Mockito.mock(Observer::class.java) as Observer<StatusEvent<RequestResponse>>
        val responseLiveData = viewModel.responseLiveData
        responseLiveData.observeForever(observer)
        viewModel.fetchMovies()
        assert(responseLiveData.value is StatusEvent.Success)
        assertEquals(movies, (responseLiveData.value as StatusEvent.Success).data.results)
        assertEquals(movies.size, viewModel.moviesSize())
    }

    @Test
    fun fetchMovies_networkError_emptyDb() {
        doReturn(Single.error<UnknownHostException>(UnknownHostException()))
            .`when`(service)
            .fetchUpcomingMovies()
        val observer = Mockito.mock(Observer::class.java) as Observer<StatusEvent<RequestResponse>>
        val responseLiveData = viewModel.responseLiveData
        responseLiveData.observeForever(observer)
        viewModel.fetchMovies()
        assert(responseLiveData.value is StatusEvent.EmptyList)
        assertEquals(0, viewModel.moviesSize())
    }

    @Test
    fun fetchMovies_networkError_filledDb_withoutMovieGenres() {
        doReturn(Single.error<UnknownHostException>(UnknownHostException()))
            .`when`(service)
            .fetchUpcomingMovies()
        val movies = movieMockFactory.nList(10)
        doReturn(Single.just(movies))
            .`when`(movieDao)
            .getAll()
        val observer = Mockito.mock(Observer::class.java) as Observer<StatusEvent<RequestResponse>>
        val responseLiveData = viewModel.responseLiveData
        responseLiveData.observeForever(observer)
        viewModel.fetchMovies()
        assert(responseLiveData.value is StatusEvent.SuccessOffline)
        assertEquals(movies, (responseLiveData.value as StatusEvent.SuccessOffline).data.results)
        assertTrue((responseLiveData.value as StatusEvent.SuccessOffline).data.isOffline)
        assertEquals(movies.size, viewModel.moviesSize())
    }

    @Test
    fun fetchMovies_networkError_filledDb_withMovieGenres() {
        doReturn(Single.error<UnknownHostException>(UnknownHostException()))
            .`when`(service)
            .fetchUpcomingMovies()
        val movies = movieMockFactory.nList(10)
        val movieGenres = movieGenreMockFactory.nList(30)
        doReturn(Single.just(movies))
            .`when`(movieDao)
            .getAll()
        doReturn(Single.just(movieGenres))
            .`when`(movieGenreDao)
            .getAll()
        val observer = Mockito.mock(Observer::class.java) as Observer<StatusEvent<RequestResponse>>
        val responseLiveData = viewModel.responseLiveData
        responseLiveData.observeForever(observer)
        viewModel.fetchMovies()
        assert(responseLiveData.value is StatusEvent.SuccessOffline)
        assertEquals(movies, (responseLiveData.value as StatusEvent.SuccessOffline).data.results)
        assertTrue((responseLiveData.value as StatusEvent.SuccessOffline).data.isOffline)
        assertEquals(movies.size, viewModel.moviesSize())
    }

    @Test
    fun fetchMovies_generalError() {
        doReturn(Single.error<Exception>(Exception()))
            .`when`(service)
            .fetchUpcomingMovies()
        doReturn(Single.error<Exception>(Exception()))
            .`when`(movieDao)
            .getAll()
        val observer = Mockito.mock(Observer::class.java) as Observer<StatusEvent<RequestResponse>>
        val responseLiveData = viewModel.responseLiveData
        responseLiveData.observeForever(observer)
        viewModel.fetchMovies()
        assert(responseLiveData.value is StatusEvent.Error)
        assertEquals(R.string.err_general_message, (responseLiveData.value as StatusEvent.Error).errorMessage)
        assertEquals(0, viewModel.moviesSize())
    }


    @Test
    fun fetchMovies_emptyList() {
        doReturn(Single.just(RequestResponse(1, ArrayList(), 1, 0)))
            .`when`(service)
            .fetchUpcomingMovies()
        val observer = Mockito.mock(Observer::class.java) as Observer<StatusEvent<RequestResponse>>
        val responseLiveData = viewModel.responseLiveData
        responseLiveData.observeForever(observer)
        viewModel.fetchMovies()
        assert(responseLiveData.value is StatusEvent.EmptyList)
        assertEquals(0, viewModel.moviesSize())
    }

    @Test
    fun fetchMoreMovies_success() {
        val firstMovies = movieMockFactory.nList(15)
        val secondMovies = movieMockFactory.nList(15)
        val allMovies = ArrayList(firstMovies)
        allMovies.addAll(secondMovies)
        doReturn(Single.just(RequestResponse(1, firstMovies, 2, allMovies.size)))
            .`when`(service)
            .fetchUpcomingMovies()
        doReturn(Single.just(RequestResponse(2, secondMovies, 2, firstMovies.size + secondMovies.size)))
            .`when`(service)
            .fetchUpcomingMovies(2)
        val observer = Mockito.mock(Observer::class.java) as Observer<StatusEvent<RequestResponse>>
        val responseLiveData = viewModel.responseLiveData
        responseLiveData.observeForever(observer)
        viewModel.fetchMovies()
        assert(responseLiveData.value is StatusEvent.Success)
        assertEquals(firstMovies, (responseLiveData.value as StatusEvent.Success).data.results)
        viewModel.fetchMoreMovies(firstMovies.size - 2)
        assert(responseLiveData.value is StatusEvent.Success)
        assertEquals(secondMovies, (responseLiveData.value as StatusEvent.Success).data.results)
        assertEquals(allMovies.size, viewModel.moviesSize())
        assertEquals(allMovies[0], viewModel.movieItem(0))
        assertEquals(allMovies[allMovies.size - 1], viewModel.movieItem(allMovies.size - 1))
        assertEquals(allMovies[allMovies.size / 2], viewModel.movieItem(allMovies.size / 2))
    }

    @Test
    fun fetchMoreMovies_networkError() {
        val firstMovies = movieMockFactory.nList(15)
        val secondMovies = movieMockFactory.nList(15)
        val allMovies = ArrayList(firstMovies)
        allMovies.addAll(secondMovies)
        doReturn(Single.just(RequestResponse(1, firstMovies, 2, allMovies.size)))
            .`when`(service)
            .fetchUpcomingMovies()
        doReturn(Single.error<UnknownHostException>(UnknownHostException()))
            .`when`(service)
            .fetchUpcomingMovies(2)
        val observer = Mockito.mock(Observer::class.java) as Observer<StatusEvent<RequestResponse>>
        val responseLiveData = viewModel.responseLiveData
        responseLiveData.observeForever(observer)
        viewModel.fetchMovies()
        assert(responseLiveData.value is StatusEvent.Success)
        assertEquals(firstMovies, (responseLiveData.value as StatusEvent.Success).data.results)
        assertEquals(firstMovies.size, viewModel.moviesSize())
        viewModel.fetchMoreMovies(firstMovies.size - 2)
        assert(responseLiveData.value is StatusEvent.Error)
        assertEquals(R.string.err_no_connection, (responseLiveData.value as StatusEvent.Error).errorMessage)
        assertEquals(firstMovies.size, viewModel.moviesSize())
    }

    @Test
    fun fetchMoreMovies_generalError() {
        val firstMovies = movieMockFactory.nList(15)
        val secondMovies = movieMockFactory.nList(15)
        val allMovies = ArrayList(firstMovies)
        allMovies.addAll(secondMovies)
        doReturn(Single.just(RequestResponse(1, firstMovies, 2, allMovies.size)))
            .`when`(service)
            .fetchUpcomingMovies()
        doReturn(Single.error<Exception>(Exception()))
            .`when`(service)
            .fetchUpcomingMovies(2)
        val observer = Mockito.mock(Observer::class.java) as Observer<StatusEvent<RequestResponse>>
        val responseLiveData = viewModel.responseLiveData
        responseLiveData.observeForever(observer)
        viewModel.fetchMovies()
        assert(responseLiveData.value is StatusEvent.Success)
        assertEquals(firstMovies, (responseLiveData.value as StatusEvent.Success).data.results)
        assertEquals(firstMovies.size, viewModel.moviesSize())
        viewModel.fetchMoreMovies(firstMovies.size - 2)
        assert(responseLiveData.value is StatusEvent.Error)
        assertEquals(R.string.err_general_message, (responseLiveData.value as StatusEvent.Error).errorMessage)
        assertEquals(firstMovies.size, viewModel.moviesSize())
    }

    @Test
    fun fetchMoreMovies_noNeedToUpdate_noScroll() {
        val movies = movieMockFactory.nList(15)
        doReturn(Single.just(RequestResponse(1, movies, 2, movies.size)))
            .`when`(service)
            .fetchUpcomingMovies()
        val observer = Mockito.mock(Observer::class.java) as Observer<StatusEvent<RequestResponse>>
        val responseLiveData = viewModel.responseLiveData
        responseLiveData.observeForever(observer)
        viewModel.fetchMovies()
        assert(responseLiveData.value is StatusEvent.Success)
        assertEquals(movies, (responseLiveData.value as StatusEvent.Success).data.results)
        assertEquals(movies.size, viewModel.moviesSize())
        viewModel.fetchMoreMovies(0)
        assert(responseLiveData.value is StatusEvent.Success)
        assertEquals(movies, (responseLiveData.value as StatusEvent.Success).data.results)
        assertEquals(movies.size, viewModel.moviesSize())
    }

    @Test
    fun fetchMoreMovies_noNeedToUpdate_errorHappened() {
        doReturn(Single.error<Exception>(Exception()))
            .`when`(service)
            .fetchUpcomingMovies()
        doReturn(Single.error<Exception>(Exception()))
            .`when`(movieDao)
            .getAll()
        val observer = Mockito.mock(Observer::class.java) as Observer<StatusEvent<RequestResponse>>
        val responseLiveData = viewModel.responseLiveData
        responseLiveData.observeForever(observer)
        viewModel.fetchMovies()
        assert(responseLiveData.value is StatusEvent.Error)
        assertEquals(R.string.err_general_message, (responseLiveData.value as StatusEvent.Error).errorMessage)
        assertEquals(0, viewModel.moviesSize())
        viewModel.fetchMoreMovies(0)
        assert(responseLiveData.value is StatusEvent.Error)
        assertEquals(R.string.err_general_message, (responseLiveData.value as StatusEvent.Error).errorMessage)
        assertEquals(0, viewModel.moviesSize())
    }

    @Test
    fun fetchMoreMovies_noNeedToUpdate_noMorePages() {
        val movies = movieMockFactory.nList(15)
        doReturn(Single.just(RequestResponse(1, movies, 1, movies.size)))
            .`when`(service)
            .fetchUpcomingMovies()
        val observer = Mockito.mock(Observer::class.java) as Observer<StatusEvent<RequestResponse>>
        val responseLiveData = viewModel.responseLiveData
        responseLiveData.observeForever(observer)
        viewModel.fetchMovies()
        assert(responseLiveData.value is StatusEvent.Success)
        assertEquals(movies, (responseLiveData.value as StatusEvent.Success).data.results)
        assertEquals(movies.size, viewModel.moviesSize())
        viewModel.fetchMoreMovies(movies.size - 2)
        assert(responseLiveData.value is StatusEvent.Success)
        assertEquals(movies, (responseLiveData.value as StatusEvent.Success).data.results)
        assertEquals(movies.size, viewModel.moviesSize())
    }

    @Test
    fun fetchMoreMovies_noNeedToUpdate_networkErrorAndRetry() {
        val firstMovies = movieMockFactory.nList(15)
        val secondMovies = movieMockFactory.nList(15)
        val allMovies = ArrayList(firstMovies)
        allMovies.addAll(secondMovies)
        doReturn(Single.just(RequestResponse(1, firstMovies, 2, allMovies.size)))
            .`when`(service)
            .fetchUpcomingMovies()
        doReturn(Single.error<UnknownHostException>(UnknownHostException()))
            .`when`(service)
            .fetchUpcomingMovies(2)
        val observer = Mockito.mock(Observer::class.java) as Observer<StatusEvent<RequestResponse>>
        val responseLiveData = viewModel.responseLiveData
        responseLiveData.observeForever(observer)
        viewModel.fetchMovies()

        assert(responseLiveData.value is StatusEvent.Success)
        assertEquals(firstMovies, (responseLiveData.value as StatusEvent.Success).data.results)
        assertEquals(firstMovies.size, viewModel.moviesSize())
        viewModel.fetchMoreMovies(firstMovies.size - 2)
        assert(responseLiveData.value is StatusEvent.Error)
        assertEquals(R.string.err_no_connection, (responseLiveData.value as StatusEvent.Error).errorMessage)
        assertEquals(firstMovies.size, viewModel.moviesSize())

        doReturn(Single.just(RequestResponse(2, secondMovies, 2, allMovies.size)))
            .`when`(service)
            .fetchUpcomingMovies(2)
        viewModel.fetchMoreMovies(firstMovies.size - 2)
        assert(responseLiveData.value is StatusEvent.Success)
        assertEquals(secondMovies, (responseLiveData.value as StatusEvent.Success).data.results)
        assertEquals(allMovies.size, viewModel.moviesSize())
    }

    @Test
    fun fetchMoreMovies_noNeedToUpdate_listWasReset() {
        val movies = movieMockFactory.nList(15)
        doReturn(Single.just(RequestResponse(1, movies, 2, movies.size)))
            .`when`(service)
            .fetchUpcomingMovies()
        val observer = Mockito.mock(Observer::class.java) as Observer<StatusEvent<RequestResponse>>
        val responseLiveData = viewModel.responseLiveData
        responseLiveData.observeForever(observer)
        viewModel.fetchMovies()
        assert(responseLiveData.value is StatusEvent.Success)
        assertEquals(movies, (responseLiveData.value as StatusEvent.Success).data.results)
        assertEquals(movies.size, viewModel.moviesSize())

        viewModel.resetMovies()
        assert(responseLiveData.value is StatusEvent.RefreshList)
        assertEquals(0, viewModel.moviesSize())

        viewModel.fetchMoreMovies(movies.size - 2)
        assert(responseLiveData.value is StatusEvent.RefreshList)
        assertEquals(0, viewModel.moviesSize())
    }
}
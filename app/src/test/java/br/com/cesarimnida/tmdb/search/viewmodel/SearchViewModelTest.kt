package br.com.cesarimnida.tmdb.search.viewmodel

import androidx.lifecycle.Observer
import br.com.cesarimnida.tmdb.R
import br.com.cesarimnida.tmdb.commons.viewmodel.BaseViewModelTest
import br.com.cesarimnida.tmdb.commons.viewmodel.StatusEvent
import br.com.cesarimnida.tmdb.movie.model.MovieMockFactory
import br.com.cesarimnida.tmdb.movie.model.RequestResponse
import br.com.cesarimnida.tmdb.movie.service.MovieService
import com.nhaarman.mockito_kotlin.doReturn
import io.reactivex.Single
import junit.framework.TestCase.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import java.net.UnknownHostException

/**
 * ************************************************************
 * Autor : Cesar Augusto dos Santos
 * Data : 06/08/2019
 * ************************************************************
 */
@Suppress("UNCHECKED_CAST")
class SearchViewModelTest : BaseViewModelTest() {
    private lateinit var viewModel: SearchViewModel
    @Mock
    var service = MovieService()
    private val movieMockFactory = MovieMockFactory()

    @Before
    fun before() {
        viewModel = SearchViewModel(application, service)
    }

    @Test
    fun fetchMovies_emptyQuery() {
        doReturn(Single.just(RequestResponse(1, ArrayList(), 1, 0)))
            .`when`(service)
            .queryMovie("")
        val observer = Mockito.mock(Observer::class.java) as Observer<StatusEvent<RequestResponse>>
        val responseLiveData = viewModel.responseLiveData
        responseLiveData.observeForever(observer)
        viewModel.fetchMovies()
        assert(responseLiveData.value is StatusEvent.RefreshList)
        assertEquals(0, viewModel.moviesSize())
    }

    @Test
    fun fetchMovies_success() {
        val query = "anyQuery"
        val movies = movieMockFactory.nList(15)
        doReturn(Single.just(RequestResponse(1, movies, 1, movies.size)))
            .`when`(service)
            .queryMovie(query)
        val observer = Mockito.mock(Observer::class.java) as Observer<StatusEvent<RequestResponse>>
        val responseLiveData = viewModel.responseLiveData
        responseLiveData.observeForever(observer)
        viewModel.updateQuery(query)
        assert(responseLiveData.value is StatusEvent.Success)
        assertEquals(movies, (responseLiveData.value as StatusEvent.Success).data.results)
        assertEquals(movies.size, viewModel.moviesSize())
    }

    @Test
    fun fetchMovies_networkError() {
        val query = "anyQuery"
        doReturn(Single.error<UnknownHostException>(UnknownHostException()))
            .`when`(service)
            .queryMovie(query)
        val observer = Mockito.mock(Observer::class.java) as Observer<StatusEvent<RequestResponse>>
        val responseLiveData = viewModel.responseLiveData
        responseLiveData.observeForever(observer)
        viewModel.updateQuery(query)
        assert(responseLiveData.value is StatusEvent.Error)
        assertEquals(R.string.err_no_connection, (responseLiveData.value as StatusEvent.Error).errorMessage)
        assertEquals(0, viewModel.moviesSize())
    }

    @Test
    fun fetchMovies_generalError() {
        val query = "anyQuery"
        doReturn(Single.error<Exception>(Exception()))
            .`when`(service)
            .queryMovie(query)
        val observer = Mockito.mock(Observer::class.java) as Observer<StatusEvent<RequestResponse>>
        val responseLiveData = viewModel.responseLiveData
        responseLiveData.observeForever(observer)
        viewModel.updateQuery(query)
        assert(responseLiveData.value is StatusEvent.Error)
        assertEquals(R.string.err_general_message, (responseLiveData.value as StatusEvent.Error).errorMessage)
        assertEquals(0, viewModel.moviesSize())
    }


    @Test
    fun fetchMovies_emptyList() {
        val query = "anyQuery"
        doReturn(Single.just(RequestResponse(1, ArrayList(), 1, 0)))
            .`when`(service)
            .queryMovie(query)
        val observer = Mockito.mock(Observer::class.java) as Observer<StatusEvent<RequestResponse>>
        val responseLiveData = viewModel.responseLiveData
        responseLiveData.observeForever(observer)
        viewModel.updateQuery(query)
        assert(responseLiveData.value is StatusEvent.EmptyList)
        assertEquals(0, viewModel.moviesSize())
    }

    @Test
    fun fetchMoreMovies_success() {
        val query = "anyQuery"
        val firstMovies = movieMockFactory.nList(15)
        val secondMovies = movieMockFactory.nList(15)
        val allMovies = ArrayList(firstMovies)
        allMovies.addAll(secondMovies)
        doReturn(Single.just(RequestResponse(1, firstMovies, 2, allMovies.size)))
            .`when`(service)
            .queryMovie(query)
        doReturn(Single.just(RequestResponse(2, secondMovies, 2, firstMovies.size + secondMovies.size)))
            .`when`(service)
            .queryMovie(query, 2)
        val observer = Mockito.mock(Observer::class.java) as Observer<StatusEvent<RequestResponse>>
        val responseLiveData = viewModel.responseLiveData
        responseLiveData.observeForever(observer)
        viewModel.updateQuery(query)
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
        val query = "anyQuery"
        val firstMovies = movieMockFactory.nList(15)
        val secondMovies = movieMockFactory.nList(15)
        val allMovies = ArrayList(firstMovies)
        allMovies.addAll(secondMovies)
        doReturn(Single.just(RequestResponse(1, firstMovies, 2, allMovies.size)))
            .`when`(service)
            .queryMovie(query)
        doReturn(Single.error<UnknownHostException>(UnknownHostException()))
            .`when`(service)
            .queryMovie(query, 2)
        val observer = Mockito.mock(Observer::class.java) as Observer<StatusEvent<RequestResponse>>
        val responseLiveData = viewModel.responseLiveData
        responseLiveData.observeForever(observer)
        viewModel.updateQuery(query)
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
        val query = "anyQuery"
        val firstMovies = movieMockFactory.nList(15)
        val secondMovies = movieMockFactory.nList(15)
        val allMovies = ArrayList(firstMovies)
        allMovies.addAll(secondMovies)
        doReturn(Single.just(RequestResponse(1, firstMovies, 2, allMovies.size)))
            .`when`(service)
            .queryMovie(query)
        doReturn(Single.error<Exception>(Exception()))
            .`when`(service)
            .queryMovie(query, 2)
        val observer = Mockito.mock(Observer::class.java) as Observer<StatusEvent<RequestResponse>>
        val responseLiveData = viewModel.responseLiveData
        responseLiveData.observeForever(observer)
        viewModel.updateQuery(query)
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
        val query = "anyQuery"
        val movies = movieMockFactory.nList(15)
        doReturn(Single.just(RequestResponse(1, movies, 2, movies.size)))
            .`when`(service)
            .queryMovie(query)
        val observer = Mockito.mock(Observer::class.java) as Observer<StatusEvent<RequestResponse>>
        val responseLiveData = viewModel.responseLiveData
        responseLiveData.observeForever(observer)
        viewModel.updateQuery(query)
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
        val query = "anyQuery"
        doReturn(Single.error<Exception>(Exception()))
            .`when`(service)
            .queryMovie(query)
        val observer = Mockito.mock(Observer::class.java) as Observer<StatusEvent<RequestResponse>>
        val responseLiveData = viewModel.responseLiveData
        responseLiveData.observeForever(observer)
        viewModel.updateQuery(query)
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
        val query = "anyQuery"
        val movies = movieMockFactory.nList(15)
        doReturn(Single.just(RequestResponse(1, movies, 1, movies.size)))
            .`when`(service)
            .queryMovie(query)
        val observer = Mockito.mock(Observer::class.java) as Observer<StatusEvent<RequestResponse>>
        val responseLiveData = viewModel.responseLiveData
        responseLiveData.observeForever(observer)
        viewModel.updateQuery(query)
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
        val query = "anyQuery"
        val firstMovies = movieMockFactory.nList(15)
        val secondMovies = movieMockFactory.nList(15)
        val allMovies = ArrayList(firstMovies)
        allMovies.addAll(secondMovies)
        doReturn(Single.just(RequestResponse(1, firstMovies, 2, allMovies.size)))
            .`when`(service)
            .queryMovie(query)
        doReturn(Single.error<UnknownHostException>(UnknownHostException()))
            .`when`(service)
            .queryMovie(query, 2)
        val observer = Mockito.mock(Observer::class.java) as Observer<StatusEvent<RequestResponse>>
        val responseLiveData = viewModel.responseLiveData
        responseLiveData.observeForever(observer)
        viewModel.updateQuery(query)

        assert(responseLiveData.value is StatusEvent.Success)
        assertEquals(firstMovies, (responseLiveData.value as StatusEvent.Success).data.results)
        assertEquals(firstMovies.size, viewModel.moviesSize())
        viewModel.fetchMoreMovies(firstMovies.size - 2)
        assert(responseLiveData.value is StatusEvent.Error)
        assertEquals(R.string.err_no_connection, (responseLiveData.value as StatusEvent.Error).errorMessage)
        assertEquals(firstMovies.size, viewModel.moviesSize())

        doReturn(Single.just(RequestResponse(2, secondMovies, 2, allMovies.size)))
            .`when`(service)
            .queryMovie(query, 2)
        viewModel.fetchMoreMovies(firstMovies.size - 2)
        assert(responseLiveData.value is StatusEvent.Success)
        assertEquals(secondMovies, (responseLiveData.value as StatusEvent.Success).data.results)
        assertEquals(allMovies.size, viewModel.moviesSize())
    }

    @Test
    fun fetchMoreMovies_noNeedToUpdate_listWasReset() {
        val query = "anyQuery"
        val movies = movieMockFactory.nList(15)
        doReturn(Single.just(RequestResponse(1, movies, 2, movies.size)))
            .`when`(service)
            .queryMovie(query)
        val observer = Mockito.mock(Observer::class.java) as Observer<StatusEvent<RequestResponse>>
        val responseLiveData = viewModel.responseLiveData
        responseLiveData.observeForever(observer)
        viewModel.updateQuery(query)
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

    @Test
    fun buildPoster_containsPoster() {
        val movie = movieMockFactory.anyOne()
        val posterPath = viewModel.buildMoviePoster(movie)
        assertNotNull(posterPath)
        assert(posterPath!!.contains(movie.posterPath!!))
        assert(posterPath.contains("w185"))
    }

    @Test
    fun buildPoster_nullPoster() {
        val movie = movieMockFactory.anyOne().copy(posterPath = null)
        val posterPath = viewModel.buildMoviePoster(movie)
        assertNull(posterPath)
    }
}
package br.com.cesarimnida.tmdb.movie.viewmodel

import androidx.collection.SparseArrayCompat
import androidx.lifecycle.Observer
import br.com.cesarimnida.tmdb.R
import br.com.cesarimnida.tmdb.commons.viewmodel.BaseViewModelTest
import br.com.cesarimnida.tmdb.commons.viewmodel.StatusEvent
import br.com.cesarimnida.tmdb.movie.model.*
import br.com.cesarimnida.tmdb.movie.model.dao.GenreDao
import br.com.cesarimnida.tmdb.movie.service.MovieService
import io.reactivex.Completable
import io.reactivex.Single
import junit.framework.TestCase.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import java.net.UnknownHostException

/**
 * ************************************************************
 * Autor : Cesar Augusto dos Santos
 * Data : 06/08/2019
 * ************************************************************
 */
@Suppress("UNCHECKED_CAST")
open class MovieDetailViewModelTest : BaseViewModelTest() {
    private lateinit var viewModel: MovieDetailViewModel
    @Mock
    var service = MovieService()
    @Mock
    lateinit var genreDao: GenreDao
    private val genreMockFactory = GenreMockFactory()
    private val movieMockFactory = MovieMockFactory()

    @Before
    fun before() {
        viewModel = MovieDetailViewModel(application, service)
        doReturn(genreDao).`when`(db).genreDao()
        doReturn(Single.just(ArrayList<Genre>()))
            .`when`(genreDao)
            .getAll()
    }

    @Test
    fun fetchGenres_firstAccess_success() {
        val genres = genreMockFactory.nList(5)
        doReturn(Single.just(GenresResponse(genres)))
            .`when`(service)
            .fetchGenres()
        doReturn(Completable.complete())
            .`when`(genreDao)
            .insertGenres(*genres.toTypedArray())
        val observer = mock(Observer::class.java) as Observer<StatusEvent<SparseArrayCompat<Genre>>>
        val genresLiveData = viewModel.genresLiveData
        genresLiveData.observeForever(observer)
        viewModel.fetchGenres()
        assert(genresLiveData.value is StatusEvent.Success)
        assertEquals(genres.size, (viewModel.genresLiveData.value as StatusEvent.Success).data.size())
    }

    @Test
    fun fetchGenres_firstAccess_networkError() {
        doReturn(Single.error<UnknownHostException>(UnknownHostException()))
            .`when`(service)
            .fetchGenres()
        val observer = mock(Observer::class.java) as Observer<StatusEvent<SparseArrayCompat<Genre>>>
        val genresLiveData = viewModel.genresLiveData
        genresLiveData.observeForever(observer)
        viewModel.fetchGenres()
        assert(genresLiveData.value is StatusEvent.Error)
        assertEquals(
            R.string.err_genre_no_connection,
            (viewModel.genresLiveData.value as StatusEvent.Error).errorMessage
        )
    }

    @Test
    fun fetchGenres_firstAccess_generalError() {
        doReturn(Single.error<Exception>(Exception()))
            .`when`(service)
            .fetchGenres()
        val observer = mock(Observer::class.java) as Observer<StatusEvent<SparseArrayCompat<Genre>>>
        val genresLiveData = viewModel.genresLiveData
        genresLiveData.observeForever(observer)
        viewModel.fetchGenres()
        assert(genresLiveData.value is StatusEvent.Error)
        assertEquals(
            R.string.err_genre_general_message,
            (viewModel.genresLiveData.value as StatusEvent.Error).errorMessage
        )
    }

    @Test
    fun fetchGenres_offline_success() {
        val genres = genreMockFactory.nList(5)
        doReturn(Single.just(genres))
            .`when`(genreDao)
            .getAll()
        val observer = mock(Observer::class.java) as Observer<StatusEvent<SparseArrayCompat<Genre>>>
        val genresLiveData = viewModel.genresLiveData
        genresLiveData.observeForever(observer)
        viewModel.fetchGenres()
        assert(genresLiveData.value is StatusEvent.Success)
        assertEquals(genres.size, (genresLiveData.value as StatusEvent.Success).data.size())
    }

    @Test
    fun selectMovieAndGenerateGenreString_withGenres() {
        val genres = genreMockFactory.nList(5)
        doReturn(Single.just(genres))
            .`when`(genreDao)
            .getAll()

        val genreObserver = mock(Observer::class.java) as Observer<StatusEvent<SparseArrayCompat<Genre>>>
        val genresLiveData = viewModel.genresLiveData
        genresLiveData.observeForever(genreObserver)
        viewModel.fetchGenres()

        val genreIds = ArrayList<Int>()
        genreIds.add(genres[0].id)
        genreIds.add(genres[1].id)
        val movie = movieMockFactory.anyOne().copy(genreIds = genreIds)
        val movieObserver = mock(Observer::class.java) as Observer<Movie>
        val movieLiveData = viewModel.selectedMovieLiveData
        movieLiveData.observeForever(movieObserver)
        viewModel.selectMovie(movie)

        assertEquals(movie, movieLiveData.value)
        val genreNamesJoined = "${genres[0].name}, ${genres[1].name}"
        assertEquals(genreNamesJoined, viewModel.joinGenresToString())
    }

    @Test
    fun selectMovieAndGenerateGenreString_noGenres() {
        val genres = genreMockFactory.nList(5)
        doReturn(Single.just(genres))
            .`when`(genreDao)
            .getAll()

        val genreObserver = mock(Observer::class.java) as Observer<StatusEvent<SparseArrayCompat<Genre>>>
        val genresLiveData = viewModel.genresLiveData
        genresLiveData.observeForever(genreObserver)
        viewModel.fetchGenres()

        val movie = movieMockFactory.anyOne().copy(genreIds = ArrayList())
        val movieObserver = mock(Observer::class.java) as Observer<Movie>
        val movieLiveData = viewModel.selectedMovieLiveData
        movieLiveData.observeForever(movieObserver)
        viewModel.selectMovie(movie)

        assertEquals(movie, movieLiveData.value)
        assertEquals("", viewModel.joinGenresToString())
    }

    @Test
    fun selectMovieAndGenerateGenreString_failedGenresRequest() {
        doReturn(Single.error<UnknownHostException>(UnknownHostException()))
            .`when`(service)
            .fetchGenres()
        val observer = mock(Observer::class.java) as Observer<StatusEvent<SparseArrayCompat<Genre>>>
        val genresLiveData = viewModel.genresLiveData
        genresLiveData.observeForever(observer)
        viewModel.fetchGenres()

        val genreIds = ArrayList<Int>()
        genreIds.add(1)
        genreIds.add(2)
        val movie = movieMockFactory.anyOne().copy(genreIds = genreIds)
        val movieObserver = mock(Observer::class.java) as Observer<Movie>
        val movieLiveData = viewModel.selectedMovieLiveData
        movieLiveData.observeForever(movieObserver)
        viewModel.selectMovie(movie)

        assertEquals(movie, movieLiveData.value)
        assertEquals("", viewModel.joinGenresToString())
    }

    @Test
    fun buildOriginalMoviePoster_containsPoster() {
        val movie = movieMockFactory.anyOne()
        val movieObserver = mock(Observer::class.java) as Observer<Movie>
        val movieLiveData = viewModel.selectedMovieLiveData
        movieLiveData.observeForever(movieObserver)
        viewModel.selectMovie(movie)
        val posterPath = viewModel.buildOriginalMoviePoster()
        assertNotNull(posterPath)
        assert(posterPath!!.contains(movie.posterPath!!))
    }

    @Test
    fun buildOriginalMoviePoster_nullPoster() {
        val movie = movieMockFactory.anyOne().copy(posterPath = null)
        val movieObserver = mock(Observer::class.java) as Observer<Movie>
        val movieLiveData = viewModel.selectedMovieLiveData
        movieLiveData.observeForever(movieObserver)
        viewModel.selectMovie(movie)
        val posterPath = viewModel.buildOriginalMoviePoster()
        assertNull(posterPath)
    }

    @Test
    fun buildOriginalMoviePoster_nullMovie() {
        val posterPath = viewModel.buildOriginalMoviePoster()
        assertNull(posterPath)
    }

    @Test
    fun formatDate_success() {
        val date = "2010-01-10"
        val formattedDate = "10/01/2010"
        assertEquals(formattedDate, viewModel.formatDate(date))
    }

    @Test
    fun formatDate_emptyDate() {
        val date = ""
        assertEquals(date, viewModel.formatDate(date))
    }
}
package br.com.cesarimnida.tmdb.movie.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.collection.SparseArrayCompat
import androidx.lifecycle.Observer
import br.com.cesarimnida.tmdb.R
import br.com.cesarimnida.tmdb.commons.view.BaseActivity
import br.com.cesarimnida.tmdb.commons.viewmodel.StatusEvent
import br.com.cesarimnida.tmdb.movie.model.Genre
import br.com.cesarimnida.tmdb.movie.model.Movie
import br.com.cesarimnida.tmdb.movie.viewmodel.MovieDetailViewModel
import com.koushikdutta.ion.Ion
import kotlinx.android.synthetic.main.activity_movie_detail.*

/**
 * ************************************************************
 * Autor : Cesar Augusto dos Santos
 * Data : 04/08/2019
 * ************************************************************
 */
class MovieDetailActivity : BaseActivity() {
    private val viewModel: MovieDetailViewModel by viewModel()

    override fun layoutId(): Int {
        return R.layout.activity_movie_detail
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        viewModel.selectedMovieLiveData.observe(this, Observer { selectedMovie -> handleSelectedMovie(selectedMovie) })
        viewModel.genresLiveData.observe(this, Observer { genres -> handleGenres(genres) })
        viewModel.selectMovie(loadMovieFromIntent())
    }

    private fun handleGenres(genres: StatusEvent<SparseArrayCompat<Genre>>?) {
        if (genres == null) return
        when (genres) {
            is StatusEvent.Loading -> {
                startLoadingGenres()
            }
            is StatusEvent.Success -> {
                stopLoadingGenres()
                tv_genres_movie_detail.text = viewModel.joinGenresToString()
            }
            is StatusEvent.Error -> {
                stopLoadingGenres()
                showError(genres.errorMessage)
            }
            else -> {
                stopLoadingGenres()
            }
        }
    }

    private fun startLoadingGenres() {
        pb_genres_movie_detail.visibility = View.VISIBLE
    }

    private fun stopLoadingGenres() {
        pb_genres_movie_detail.visibility = View.GONE
    }

    private fun handleSelectedMovie(selectedMovie: Movie?) {
        if (selectedMovie == null) return
        val poster = viewModel.buildOriginalMoviePoster()
        Ion.with(iv_poster_movie_detail)
            .placeholder(R.drawable.ic_image_black_24dp)
            .error(R.drawable.ic_broken_image_black_24dp)
            .load(poster)
        tv_synopsis_movie_detail.text = selectedMovie.overview
        tv_release_date_movie_detail.text = viewModel.formatDate(selectedMovie.releaseDate)
        viewModel.fetchGenres()
        title = selectedMovie.title
        tv_title_movie_detail.text = selectedMovie.title
    }

    private fun loadMovieFromIntent(): Movie {
        return intent?.getSerializableExtra(MOVIE) as Movie
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_right_from, R.anim.slide_right_to)
    }

    companion object {
        private const val MOVIE = "br.com.cesarimnida.tmdb.movie.view.MovieDetailActivity.MOVIE"
        fun startActivity(activity: Activity, movie: Movie) {
            val intent = Intent(activity, MovieDetailActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(MOVIE, movie)
            intent.putExtras(bundle)
            activity.startActivity(intent)
        }
    }
}
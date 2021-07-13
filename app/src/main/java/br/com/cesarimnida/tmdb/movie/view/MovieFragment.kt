package br.com.cesarimnida.tmdb.movie.view

import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.cesarimnida.tmdb.R
import br.com.cesarimnida.tmdb.commons.view.BaseFragment
import br.com.cesarimnida.tmdb.commons.viewmodel.StatusEvent
import br.com.cesarimnida.tmdb.movie.model.RequestResponse
import br.com.cesarimnida.tmdb.movie.viewmodel.MovieViewModel
import kotlinx.android.synthetic.main.content_empty_movie.view.*
import kotlinx.android.synthetic.main.fragment_movie.*

/**
 * ************************************************************
 * Autor : Cesar Augusto dos Santos
 * Data : 04/08/2019
 * ************************************************************
 */
abstract class MovieFragment : BaseFragment() {
    abstract override val viewModel: MovieViewModel
    private val adapter: MovieAdapter by lazy { MovieAdapter(viewModel) }
    private val layoutManager: LinearLayoutManager by lazy { LinearLayoutManager(context) }

    override fun layoutId(): Int {
        return R.layout.fragment_movie
    }

    @StringRes
    abstract fun emptyListMessage(): Int

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) viewModel.fetchMovies()
        hideViews()
        rv_movie.layoutManager = layoutManager
        rv_movie.adapter = adapter
        rv_movie.addOnScrollListener(scrollListener())
        cl_empty_movie.tv_empty_movie.text = getString(emptyListMessage())
        viewModel.responseLiveData.observe(this, Observer { response -> handleResponse(response) })
        srl_movie.setOnRefreshListener {
            viewModel.resetMovies()
            viewModel.fetchMovies()
            srl_movie.isRefreshing = false
        }
    }

    private fun hideViews() {
        rv_movie.visibility = View.GONE
        cl_empty_movie.visibility = View.GONE
        pb_movie.visibility = View.GONE
        cv_offline_movie.visibility = View.GONE
    }

    private fun handleResponse(response: StatusEvent<RequestResponse>?) {
        if (response == null) return
        rv_movie.post { adapter.notifyDataSetChanged() }
        when (response) {
            is StatusEvent.Loading -> {
                startLoading()
            }
            is StatusEvent.Success -> {
                stopLoading()
                showList()
            }
            is StatusEvent.SuccessOffline -> {
                stopLoading()
                showList()
                showOfflineWarning()
            }
            is StatusEvent.EmptyList -> {
                stopLoading()
                showEmptyListMessage()
            }
            is StatusEvent.RefreshList -> {
                hideViews()
            }
            is StatusEvent.Error -> {
                stopLoading()
                showError(response.errorMessage)
            }
        }
    }

    private fun showOfflineWarning() {
        cv_offline_movie.visibility = View.VISIBLE
    }

    private fun startLoading() {
        pb_movie.visibility = View.VISIBLE
    }

    private fun stopLoading() {
        pb_movie.visibility = View.GONE
    }

    private fun showEmptyListMessage() {
        rv_movie.visibility = View.GONE
        cl_empty_movie.visibility = View.VISIBLE
    }

    private fun showList() {
        rv_movie.visibility = View.VISIBLE
        cl_empty_movie.visibility = View.GONE
    }

    private fun scrollListener(): RecyclerView.OnScrollListener {
        return object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                viewModel.fetchMoreMovies(lastVisibleItem)
            }
        }
    }
}
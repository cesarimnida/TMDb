package br.com.cesarimnida.tmdb.upcoming.view

import br.com.cesarimnida.tmdb.R
import br.com.cesarimnida.tmdb.movie.view.MovieFragment
import br.com.cesarimnida.tmdb.upcoming.viewmodel.UpcomingViewModel

/**
 * ************************************************************
 * Autor : Cesar Augusto dos Santos
 * Data : 03/08/2019
 * ************************************************************
 */
class UpcomingFragment : MovieFragment() {
    override val viewModel: UpcomingViewModel by viewModel()

    override fun emptyListMessage(): Int {
        return R.string.tv_empty_upcoming
    }

    companion object {
        fun newInstance(): UpcomingFragment {
            return UpcomingFragment()
        }
    }
}
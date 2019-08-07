package br.com.cesarimnida.tmdb.search.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.widget.SearchView
import br.com.cesarimnida.tmdb.R
import br.com.cesarimnida.tmdb.movie.view.MovieFragment
import br.com.cesarimnida.tmdb.search.viewmodel.SearchViewModel

/**
 * ************************************************************
 * Autor : Cesar Augusto dos Santos
 * Data : 04/08/2019
 * ************************************************************
 */
class SearchFragment : MovieFragment() {
    override val viewModel: SearchViewModel by viewModel()

    override fun emptyListMessage(): Int {
        return R.string.tv_empty_search
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.menu_search, menu) ?: return
        val searchItem = menu?.findItem(R.id.action_search) ?: return
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.updateQuery(query!!)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    companion object {
        fun newInstance(): SearchFragment {
            return SearchFragment()
        }
    }
}
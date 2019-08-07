package br.com.cesarimnida.tmdb.movie.view

import android.os.Bundle
import br.com.cesarimnida.tmdb.R
import br.com.cesarimnida.tmdb.commons.view.BaseActivity
import kotlinx.android.synthetic.main.activity_movie.*

class MovieActivity : BaseActivity() {
    private val adapter: MoviePagerAdapter by lazy { MoviePagerAdapter(supportFragmentManager) }

    override fun layoutId(): Int {
        return R.layout.activity_movie
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view_pager.adapter = adapter
        nav_view.setupWithViewPager(view_pager)
    }
}

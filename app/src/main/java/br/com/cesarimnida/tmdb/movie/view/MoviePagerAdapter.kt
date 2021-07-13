package br.com.cesarimnida.tmdb.movie.view

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import br.com.cesarimnida.tmdb.search.view.SearchFragment
import br.com.cesarimnida.tmdb.upcoming.view.UpcomingFragment
import java.util.*

/**
 * ************************************************************
 * Autor : Cesar Augusto dos Santos
 * Data : 04/08/2019
 * ************************************************************
 */
class MoviePagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
    private val fragments: ArrayList<Fragment> by lazy {
        val fragments = ArrayList<Fragment>()
        fragments.add(UpcomingFragment.newInstance())
        fragments.add(SearchFragment.newInstance())
        fragments
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Upcoming"
            else -> "Search"
        }
    }
}
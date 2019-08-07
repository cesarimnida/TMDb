package br.com.cesarimnida.tmdb.movie.view

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.cesarimnida.tmdb.R
import br.com.cesarimnida.tmdb.movie.viewmodel.MovieViewModel
import com.koushikdutta.ion.Ion
import kotlinx.android.synthetic.main.item_movie.view.*

/**
 * ************************************************************
 * Autor : Cesar Augusto dos Santos
 * Data : 04/08/2019
 * ************************************************************
 */
class MovieAdapter(private val viewModel: MovieViewModel) : RecyclerView.Adapter<MovieAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false))
    }

    override fun getItemCount(): Int {
        return viewModel.moviesSize()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = viewModel.movieItem(position)
        holder.title.text = movie.title
        holder.releaseDate.text = viewModel.formatDate(movie.releaseDate)
        val poster = viewModel.buildMoviePoster(movie)
        Ion.with(holder.poster)
            .placeholder(R.drawable.ic_image_black_24dp)
            .error(R.drawable.ic_broken_image_black_24dp)
            .load(poster)
        holder.cardView.setOnClickListener {
            MovieDetailActivity.startActivity(
                holder.cardView.context as Activity,
                movie
            )
        }
    }

    open class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView = itemView.cv_movie!!
        val poster = itemView.iv_poster_movie!!
        val title = itemView.tv_title_movie!!
        val releaseDate = itemView.tv_release_date_movie!!
    }
}
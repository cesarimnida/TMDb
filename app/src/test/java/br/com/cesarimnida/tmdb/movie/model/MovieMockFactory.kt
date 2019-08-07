package br.com.cesarimnida.tmdb.movie.model

import br.com.cesarimnida.tmdb.commons.model.MockFactory

/**
 * ************************************************************
 * Autor : Cesar Augusto dos Santos
 * Data : 06/08/2019
 * ************************************************************
 */
class MovieMockFactory : MockFactory<Movie> {
    override fun createOne(variance: Int): Movie {
        val genreIds = ArrayList<Int>()
        genreIds.add(1)
        genreIds.add(2)
        genreIds.add(3)
        return Movie(
            variance,
            "/posterPath$variance.jpg",
            false,
            "Overview $variance",
            "01-01-2010",
            genreIds,
            "Og Title $variance",
            "pt-BR",
            "Title $variance",
            null,
            variance / 5.0,
            variance * 10,
            false,
            variance * 5.0
        )
    }
}
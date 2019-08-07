package br.com.cesarimnida.tmdb.movie.model

import br.com.cesarimnida.tmdb.commons.model.MockFactory

/**
 * ************************************************************
 * Autor : Cesar Augusto dos Santos
 * Data : 06/08/2019
 * ************************************************************
 */
class GenreMockFactory : MockFactory<Genre> {
    override fun createOne(variance: Int): Genre {
        return Genre(
            variance,
            "Genre$variance"
        )
    }
}
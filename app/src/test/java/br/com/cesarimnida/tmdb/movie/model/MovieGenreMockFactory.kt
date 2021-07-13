package br.com.cesarimnida.tmdb.movie.model

import br.com.cesarimnida.tmdb.commons.model.MockFactory

/**
 * ************************************************************
 * Autor : Cesar Augusto dos Santos
 * Data : 07/08/2019
 * ************************************************************
 */
class MovieGenreMockFactory : MockFactory<MovieGenre> {
    override fun createOne(variance: Int): MovieGenre {
        return MovieGenre(variance % 9, variance % 7)
    }
}
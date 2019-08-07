package br.com.cesarimnida.tmdb.movie.model

import java.io.Serializable

/**
 * ************************************************************
 * Autor : Cesar Augusto dos Santos
 * Data : 05/08/2019
 * ************************************************************
 */
data class GenresResponse(val genres: ArrayList<Genre>) : Serializable
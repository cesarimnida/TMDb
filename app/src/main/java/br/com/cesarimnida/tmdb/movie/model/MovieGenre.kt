package br.com.cesarimnida.tmdb.movie.model

import androidx.room.Entity
import androidx.room.Index
import java.io.Serializable

/**
 * ************************************************************
 * Autor : Cesar Augusto dos Santos
 * Data : 06/08/2019
 * ************************************************************
 */
@Entity(
    tableName = "movieGenre",
    primaryKeys = ["movieId", "genreId"],
    indices = [Index(value = ["genreId"])]
)
data class MovieGenre(
    val movieId: Int,
    val genreId: Int
) : Serializable


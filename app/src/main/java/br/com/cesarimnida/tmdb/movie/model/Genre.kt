package br.com.cesarimnida.tmdb.movie.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * ************************************************************
 * Autor : Cesar Augusto dos Santos
 * Data : 05/08/2019
 * ************************************************************
 */
@Entity
data class Genre(
    @PrimaryKey val id: Int,
    val name: String
) : Serializable
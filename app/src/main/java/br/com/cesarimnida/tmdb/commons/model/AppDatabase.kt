package br.com.cesarimnida.tmdb.commons.model

import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.cesarimnida.tmdb.movie.model.Genre
import br.com.cesarimnida.tmdb.movie.model.Movie
import br.com.cesarimnida.tmdb.movie.model.MovieGenre
import br.com.cesarimnida.tmdb.movie.model.dao.GenreDao
import br.com.cesarimnida.tmdb.movie.model.dao.MovieDao
import br.com.cesarimnida.tmdb.movie.model.dao.MovieGenreDao

/**
 * ************************************************************
 * Autor : Cesar Augusto dos Santos
 * Data : 06/08/2019
 * ************************************************************
 */
@Database(entities = [Movie::class, Genre::class, MovieGenre::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun genreDao(): GenreDao
    abstract fun movieGenreDao(): MovieGenreDao
}
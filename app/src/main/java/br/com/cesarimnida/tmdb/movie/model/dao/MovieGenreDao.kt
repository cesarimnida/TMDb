package br.com.cesarimnida.tmdb.movie.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.cesarimnida.tmdb.movie.model.MovieGenre
import io.reactivex.Completable
import io.reactivex.Single

/**
 * ************************************************************
 * Autor : Cesar Augusto dos Santos
 * Data : 06/08/2019
 * ************************************************************
 */
@Dao
interface MovieGenreDao {
    @Query("SELECT * FROM movieGenre")
    fun getAll(): Single<List<MovieGenre>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovieGenres(vararg movies: MovieGenre): Completable

    @Query("DELETE FROM movieGenre")
    fun deleteAllMovieGenres(): Completable
}
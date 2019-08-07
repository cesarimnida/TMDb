package br.com.cesarimnida.tmdb.movie.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.cesarimnida.tmdb.movie.model.Movie
import io.reactivex.Completable
import io.reactivex.Single

/**
 * ************************************************************
 * Autor : Cesar Augusto dos Santos
 * Data : 06/08/2019
 * ************************************************************
 */
@Dao
interface MovieDao {
    @Query("SELECT * FROM movie")
    fun getAll(): Single<List<Movie>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovies(vararg movies: Movie): Completable

    @Query("DELETE FROM movie")
    fun deleteAllMovies(): Completable
}
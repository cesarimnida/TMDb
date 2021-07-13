package br.com.cesarimnida.tmdb.movie.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.cesarimnida.tmdb.movie.model.Genre
import io.reactivex.Completable
import io.reactivex.Single

/**
 * ************************************************************
 * Autor : Cesar Augusto dos Santos
 * Data : 06/08/2019
 * ************************************************************
 */
@Dao
interface GenreDao {
    @Query("SELECT * FROM genre")
    fun getAll(): Single<List<Genre>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGenres(vararg genres: Genre): Completable
}
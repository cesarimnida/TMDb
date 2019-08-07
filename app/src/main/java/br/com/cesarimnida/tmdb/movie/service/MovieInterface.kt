package br.com.cesarimnida.tmdb.movie.service

import br.com.cesarimnida.tmdb.movie.model.GenresResponse
import br.com.cesarimnida.tmdb.movie.model.RequestResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * ************************************************************
 * Autor : Cesar Augusto dos Santos
 * Data : 03/08/2019
 * ************************************************************
 */
interface MovieInterface {
    @GET("/3/movie/upcoming")
    fun fetchUpcomingMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("page") page: String
    ): Single<RequestResponse>

    @GET("/3/search/movie")
    fun queryMovie(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("query") query: String,
        @Query("page") page: String
    ): Single<RequestResponse>

    @GET("/3/genre/movie/list")
    fun fetchGenres(
        @Query("api_key") apiKey: String,
        @Query("language") language: String
    ): Single<GenresResponse>
}
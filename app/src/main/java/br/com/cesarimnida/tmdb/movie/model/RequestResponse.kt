package br.com.cesarimnida.tmdb.movie.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * ************************************************************
 * Autor : Cesar Augusto dos Santos
 * Data : 04/08/2019
 * ************************************************************
 */
data class RequestResponse(
    val page: Int,
    val results: ArrayList<Movie>,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int,
    val isOffline: Boolean = false
) : Serializable
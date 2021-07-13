package br.com.cesarimnida.tmdb.movie.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * ************************************************************
 * Autor : Cesar Augusto dos Santos
 * Data : 03/08/2019
 * ************************************************************
 */
@Entity
data class Movie(
    @PrimaryKey var id: Int = 0,
    @SerializedName("poster_path") var posterPath: String? = null,
    var adult: Boolean = false,
    var overview: String = "",
    @SerializedName("release_date") var releaseDate: String = "",
    @Ignore @SerializedName("genre_ids") var genreIds: ArrayList<Int> = ArrayList(),
    @SerializedName("original_title") var originalTitle: String = "",
    @SerializedName("original_language") var originalLanguage: String = "",
    var title: String = "",
    @SerializedName("backdrop_path") var backdropPath: String? = null,
    var popularity: Double = 0.0,
    @SerializedName("vote_count") var voteCount: Int = 0,
    var video: Boolean = false,
    @SerializedName("vote_average") var voteAverage: Double = 0.0
) : Serializable
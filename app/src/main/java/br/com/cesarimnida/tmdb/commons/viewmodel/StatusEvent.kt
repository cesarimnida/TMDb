package br.com.cesarimnida.tmdb.commons.viewmodel

import androidx.annotation.StringRes

/**
 * ************************************************************
 * Autor : Cesar Augusto dos Santos
 * Data : 04/08/2019
 * ************************************************************
 */
sealed class StatusEvent<out T> {
    data class Success<T>(val data: T) : StatusEvent<T>()
    data class SuccessOffline<T>(val data: T) : StatusEvent<T>()
    class Loading<T> : StatusEvent<T>()
    class EmptyList<T> : StatusEvent<T>()
    class RefreshList<T> : StatusEvent<T>()
    data class Error<T>(@StringRes val errorMessage: Int) : StatusEvent<T>()

    companion object {
        fun <T> success(data: T) = Success(data)
        fun <T> successOffline(data: T) = SuccessOffline(data)
        fun <T> loading() = Loading<T>()
        fun <T> emptyList() = EmptyList<T>()
        fun <T> refreshList() = RefreshList<T>()
        fun <T> error(@StringRes message: Int) = Error<T>(message)
    }
}
package br.com.cesarimnida.tmdb

import android.app.Application
import androidx.room.Room
import br.com.cesarimnida.tmdb.commons.model.AppDatabase
import timber.log.Timber

/**
 * ************************************************************
 * Autor : Cesar Augusto dos Santos
 * Data : 03/08/2019
 * ************************************************************
 */
open class App : Application() {
    open val db: AppDatabase by lazy {
        Room.databaseBuilder(applicationContext, AppDatabase::class.java, "tmdb").build()
    }

    override fun onCreate() {
        super.onCreate()
        if (!BuildConfig.DEBUG) return
        Timber.plant(Timber.DebugTree())
    }
}
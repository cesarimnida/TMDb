package br.com.cesarimnida.tmdb.commons.service

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * ************************************************************
 * Autor : Cesar Augusto dos Santos
 * Data : 03/08/2019
 * ************************************************************
 */
interface BaseServiceInterface {
    fun okHttpClient(
        okHttpClientBuilder: OkHttpClient.Builder = OkHttpClient.Builder(),
        headerInterceptor: Interceptor? = null
    ): OkHttpClient {
        val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        if (headerInterceptor != null) okHttpClientBuilder.addInterceptor(headerInterceptor)
        okHttpClientBuilder.addInterceptor(interceptor)
        okHttpClientBuilder.readTimeout(2, TimeUnit.MINUTES)
        okHttpClientBuilder.connectTimeout(2, TimeUnit.MINUTES)
        okHttpClientBuilder.writeTimeout(2, TimeUnit.MINUTES)
        return okHttpClientBuilder.build()
    }

    fun headerInterceptor(): Interceptor? {
        return null
    }

    fun baseUrl(): String {
        return "https://api.themoviedb.org/"
    }
}

inline fun <reified S, T> T.lazyInterface(): Lazy<S> where T : BaseServiceInterface {
    return lazy {
        val builder = Retrofit.Builder()
            .baseUrl(baseUrl())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        val retrofit = builder.client(okHttpClient(headerInterceptor = headerInterceptor())).build()
        retrofit.create(S::class.java)
    }
}
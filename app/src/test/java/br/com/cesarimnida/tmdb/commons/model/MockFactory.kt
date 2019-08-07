package br.com.cesarimnida.tmdb.commons.model

import kotlin.random.Random

/**
 * ************************************************************
 * Autor : Cesar Augusto dos Santos
 * Data : 06/08/2019
 * ************************************************************
 */
interface MockFactory<T> {

    fun createOne(variance: Int): T

    fun nList(min: Int): ArrayList<T> {
        val items = ArrayList<T>()
        val max = Random.nextInt(10, 30) + min
        for (i in 0..max) {
            items.add(createOne(i))
        }
        return items
    }

    fun nList(): ArrayList<T> {
        return nList(0)
    }

    fun anyOne(): T {
        return nList(1)[0]
    }
}
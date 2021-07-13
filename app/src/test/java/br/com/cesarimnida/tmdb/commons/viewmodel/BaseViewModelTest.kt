package br.com.cesarimnida.tmdb.commons.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.cesarimnida.tmdb.App
import br.com.cesarimnida.tmdb.commons.rule.RxImmediateSchedulerRule
import br.com.cesarimnida.tmdb.commons.model.AppDatabase
import org.junit.Before
import org.junit.Rule
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

/**
 * ************************************************************
 * Autor : Cesar Augusto dos Santos
 * Data : 06/08/2019
 * ************************************************************
 */
abstract class BaseViewModelTest {
    @Mock
    lateinit var application: App
    @get:Rule
    val rule = InstantTaskExecutorRule()
    @get:Rule
    var testSchedulerRule = RxImmediateSchedulerRule()
    @Mock
    lateinit var db: AppDatabase

    @Before
    fun baseBefore() {
        MockitoAnnotations.initMocks(this)
        `when`(application.db).thenReturn(db)
    }
}
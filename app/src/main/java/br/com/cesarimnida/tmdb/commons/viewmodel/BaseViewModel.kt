package br.com.cesarimnida.tmdb.commons.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * ************************************************************
 * Autor : Cesar Augusto dos Santos
 * Data : 03/08/2019
 * ************************************************************
 */
open class BaseViewModel(application: Application) :
    AndroidViewModel(application) {

    fun formatDate(date: String): String {
        if (date.isBlank()) return ""
        val inPattern = "yyyy-MM-dd"
        val outPattern = "dd/MM/yyyy"
        val inFormatter = SimpleDateFormat(inPattern, Locale.US)
        val outFormatter = SimpleDateFormat(outPattern, Locale.US)
        return outFormatter.format(inFormatter.parse(date))
    }
}
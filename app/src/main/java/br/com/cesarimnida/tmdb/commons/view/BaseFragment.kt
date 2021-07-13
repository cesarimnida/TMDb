package br.com.cesarimnida.tmdb.commons.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import br.com.cesarimnida.tmdb.commons.viewmodel.BaseViewModel

/**
 * ************************************************************
 * Autor : Cesar Augusto dos Santos
 * Data : 03/08/2019
 * ************************************************************
 */
abstract class BaseFragment : Fragment() {
    protected open val viewModel: BaseViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutId(), container, false)
    }

    @LayoutRes
    abstract fun layoutId(): Int

    protected fun showError(@StringRes message: Int) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    inline fun <reified VM : BaseViewModel, T> T.viewModel(): Lazy<VM> where T : Fragment {
        return lazy { ViewModelProviders.of(context as FragmentActivity).get(VM::class.java) }
    }
}
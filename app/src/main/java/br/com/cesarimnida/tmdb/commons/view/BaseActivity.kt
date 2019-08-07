package br.com.cesarimnida.tmdb.commons.view

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import br.com.cesarimnida.tmdb.R
import br.com.cesarimnida.tmdb.commons.viewmodel.BaseViewModel

/**
 * ************************************************************
 * Autor : Cesar Augusto dos Santos
 * Data : 05/08/2019
 * ************************************************************
 */
abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId())
    }

    @LayoutRes
    abstract fun layoutId(): Int

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    protected fun showError(@StringRes message: Int) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun startActivityForResult(intent: Intent?, requestCode: Int) {
        super.startActivityForResult(intent, requestCode)
        overridePendingTransition(R.anim.slide_left_from, R.anim.slide_left_to)
    }

    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
        overridePendingTransition(R.anim.slide_left_from, R.anim.slide_left_to)
    }

    inline fun <reified VM : BaseViewModel, T> T.viewModel(): Lazy<VM> where T : FragmentActivity {
        return lazy { ViewModelProviders.of(this).get(VM::class.java) }
    }
}
package me.dm7.barcodescanner.zbar.sample.base

import android.R
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

open class BaseScannerActivity : AppCompatActivity() {

    fun setupToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        if (item.itemId == R.id.home) {
            finish()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
}

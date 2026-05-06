package me.dm7.barcodescanner.zbar.sample

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import me.dm7.barcodescanner.zbar.sample.databinding.ActivityMainBinding

private const val ZBAR_CAMERA_PERMISSION = 1

class MainActivity : AppCompatActivity() {

    private var clss: Class<*>? = null
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
    }

    fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
    }

    fun launchSimpleActivity(view: View) {
        launchActivity(SimpleScannerActivity::class.java)
    }

    fun launchSimpleFragmentActivity(view: View) {
        launchActivity(SimpleScannerFragmentActivity::class.java)
    }

    fun launchFullActivity(view: View) {
        launchActivity(FullScannerActivity::class.java)
    }

    fun launchFullFragmentActivity(view: View) {
        launchActivity(FullScannerFragmentActivity::class.java)
    }

    fun launchActivity(clss: Class<*>) {
        if (
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            this.clss = clss
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                ZBAR_CAMERA_PERMISSION
            )
        } else {
            val intent = Intent(this, clss)
            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            ZBAR_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    clss?.let {
                        val intent = Intent(this, it)
                        startActivity(intent)
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Please grant camera permission to use the QR Scanner",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}

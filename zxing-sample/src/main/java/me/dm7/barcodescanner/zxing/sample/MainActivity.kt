package me.dm7.barcodescanner.zxing.sample

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import me.dm7.barcodescanner.zxing.sample.customviewfinder.CustomViewFinderScannerActivity
import me.dm7.barcodescanner.zxing.sample.databinding.ActivityMainBinding
import me.dm7.barcodescanner.zxing.sample.fullscanner.FullScannerActivity
import me.dm7.barcodescanner.zxing.sample.fullscanner.FullScannerFragmentActivity
import me.dm7.barcodescanner.zxing.sample.fullscanner.FullScreenScannerFragmentActivity
import me.dm7.barcodescanner.zxing.sample.scalingscanner.ScalingScannerActivity
import me.dm7.barcodescanner.zxing.sample.simplescanner.SimpleScannerActivity
import me.dm7.barcodescanner.zxing.sample.simplescanner.SimpleScannerFragmentActivity

private const val ZXING_CAMERA_PERMISSION = 1

class MainActivity : AppCompatActivity() {

    private var clss: Class<*>? = null

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

    fun launchFullScreenScannerFragmentActivity(view: View) {
        launchActivity(FullScreenScannerFragmentActivity::class.java)
    }

    fun launchCustomViewFinderScannerActivity(view: View) {
        launchActivity(CustomViewFinderScannerActivity::class.java)
    }

    fun launchScalingScannerActivity(view: View) {
        launchActivity(ScalingScannerActivity::class.java)
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
                ZXING_CAMERA_PERMISSION
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
            ZXING_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    clss?.let { selectedClass ->
                        val intent = Intent(this, selectedClass)
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

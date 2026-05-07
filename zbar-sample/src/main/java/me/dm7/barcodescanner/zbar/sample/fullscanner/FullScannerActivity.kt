package me.dm7.barcodescanner.zbar.sample.fullscanner

import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import me.dm7.barcodescanner.zbar.BarcodeFormat
import me.dm7.barcodescanner.zbar.Result
import me.dm7.barcodescanner.zbar.ZBarScannerView
import me.dm7.barcodescanner.zbar.sample.base.BaseScannerActivity
import me.dm7.barcodescanner.zbar.sample.fullscanner.dialog.MessageDialogFragment
import me.dm7.barcodescanner.zbar.sample.R
import me.dm7.barcodescanner.zbar.sample.databinding.ActivityFullScannerBinding
import me.dm7.barcodescanner.zbar.sample.fullscanner.dialog.CameraSelectorDialogFragment
import me.dm7.barcodescanner.zbar.sample.fullscanner.dialog.FormatSelectorDialogFragment
import me.dm7.barcodescanner.zbar.sample.fullscanner.scannerlistener.CameraSelectorDialogListener
import me.dm7.barcodescanner.zbar.sample.fullscanner.scannerlistener.FormatSelectorDialogListener
import me.dm7.barcodescanner.zbar.sample.fullscanner.scannerlistener.MessageDialogListener

private const val FLASH_STATE = "FLASH_STATE"
private const val AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE"
private const val SELECTED_FORMATS = "SELECTED_FORMATS"
private const val CAMERA_ID = "CAMERA_ID"

class FullScannerActivity :
    BaseScannerActivity(),
    MessageDialogListener,
    ZBarScannerView.ResultHandler,
    FormatSelectorDialogListener,
    CameraSelectorDialogListener {

    private var scannerView: ZBarScannerView? = null
    private var flash = false
    private var autoFocus = true
    private var selectedIndices: ArrayList<Int>? = null
    private var cameraId = -1

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)

        if (state != null) {
            flash = state.getBoolean(FLASH_STATE, false)
            autoFocus = state.getBoolean(AUTO_FOCUS_STATE, true)
            selectedIndices = state.getIntegerArrayList(SELECTED_FORMATS)
            cameraId = state.getInt(CAMERA_ID, -1)
        } else {
            flash = false
            autoFocus = true
            selectedIndices = null
            cameraId = -1
        }

        val binding = ActivityFullScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar(binding.toolbar)

        scannerView = ZBarScannerView(this)
        setupFormats()
        binding.contentFrame.addView(scannerView)
    }

    override fun onResume() {
        super.onResume()
        scannerView?.setResultHandler(this)
        scannerView?.startCamera(cameraId)
        scannerView?.setFlash(flash)
        scannerView?.setAutoFocus(autoFocus)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(FLASH_STATE, flash)
        outState.putBoolean(AUTO_FOCUS_STATE, autoFocus)
        outState.putIntegerArrayList(SELECTED_FORMATS, selectedIndices)
        outState.putInt(CAMERA_ID, cameraId)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        var menuItem: MenuItem = if (flash) {
            menu.add(Menu.NONE, R.id.menu_flash, 0, R.string.flash_on)
        } else {
            menu.add(Menu.NONE, R.id.menu_flash, 0, R.string.flash_off)
        }
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER)

        menuItem = if (autoFocus) {
            menu.add(Menu.NONE, R.id.menu_auto_focus, 0, R.string.auto_focus_on)
        } else {
            menu.add(Menu.NONE, R.id.menu_auto_focus, 0, R.string.auto_focus_off)
        }
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER)

        menuItem = menu.add(Menu.NONE, R.id.menu_formats, 0, R.string.formats)
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER)

        menuItem = menu.add(Menu.NONE, R.id.menu_camera_selector, 0, R.string.select_camera)
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_flash -> {
                flash = !flash
                item.setTitle(if (flash) R.string.flash_on else R.string.flash_off)
                scannerView?.setFlash(flash)
                true
            }

            R.id.menu_auto_focus -> {
                autoFocus = !autoFocus
                item.setTitle(if (autoFocus) R.string.auto_focus_on else R.string.auto_focus_off)
                scannerView?.setAutoFocus(autoFocus)
                true
            }

            R.id.menu_formats -> {
                val fragment =
                    FormatSelectorDialogFragment.Companion.newInstance(this, selectedIndices)
                fragment.show(supportFragmentManager, "format_selector")
                true
            }

            R.id.menu_camera_selector -> {
                scannerView?.stopCamera()
                val fragment = CameraSelectorDialogFragment.Companion.newInstance(this, cameraId)
                fragment.show(supportFragmentManager, "camera_selector")
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun handleResult(rawResult: Result) {
        try {
            val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val ringtone = RingtoneManager.getRingtone(applicationContext, notification)
            ringtone.play()
        } catch (_: Exception) {
        }

        showMessageDialog(
            "Contents = ${rawResult.contents}, Format = ${rawResult.barcodeFormat.name}"
        )
    }

    fun showMessageDialog(message: String) {
        val fragment = MessageDialogFragment.Companion.newInstance("Scan Results", message, this)
        fragment.show(supportFragmentManager, "scan_results")
    }

    fun closeMessageDialog() {
        closeDialog("scan_results")
    }

    fun closeFormatsDialog() {
        closeDialog("format_selector")
    }

    fun closeDialog(dialogName: String) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragment = fragmentManager.findFragmentByTag(dialogName) as? DialogFragment
        fragment?.dismiss()
    }

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        scannerView?.resumeCameraPreview(this)
    }

    override fun onFormatsSaved(selectedIndices: ArrayList<Int>) {
        this.selectedIndices = selectedIndices
        setupFormats()
    }

    override fun onCameraSelected(cameraId: Int) {
        this.cameraId = cameraId
        scannerView?.startCamera(this.cameraId)
        scannerView?.setFlash(flash)
        scannerView?.setAutoFocus(autoFocus)
    }

    fun setupFormats() {
        val formats = ArrayList<BarcodeFormat>()

        if (selectedIndices == null || selectedIndices?.isEmpty() == true) {
            selectedIndices = ArrayList()
            for (i in BarcodeFormat.ALL_FORMATS.indices) {
                selectedIndices?.add(i)
            }
        }

        if (selectedIndices != null) {
            for (index in selectedIndices) {
                formats.add(BarcodeFormat.ALL_FORMATS[index])
            }
        }

        scannerView?.setFormats(formats)
    }

    override fun onPause() {
        super.onPause()
        scannerView?.stopCamera()
        closeMessageDialog()
        closeFormatsDialog()
    }
}

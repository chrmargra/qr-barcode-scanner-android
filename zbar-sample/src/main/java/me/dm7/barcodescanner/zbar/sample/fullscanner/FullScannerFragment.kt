package me.dm7.barcodescanner.zbar.sample.fullscanner

import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import me.dm7.barcodescanner.zbar.BarcodeFormat
import me.dm7.barcodescanner.zbar.Result
import me.dm7.barcodescanner.zbar.ResultHandler
import me.dm7.barcodescanner.zbar.ZBarScannerView
import me.dm7.barcodescanner.zbar.sample.fullscanner.dialog.MessageDialogFragment
import me.dm7.barcodescanner.zbar.sample.R
import me.dm7.barcodescanner.zbar.sample.fullscanner.dialog.CameraSelectorDialogFragment
import me.dm7.barcodescanner.zbar.sample.fullscanner.dialog.FormatSelectorDialogFragment
import me.dm7.barcodescanner.zbar.sample.fullscanner.scannerlistener.CameraSelectorDialogListener
import me.dm7.barcodescanner.zbar.sample.fullscanner.scannerlistener.FormatSelectorDialogListener
import me.dm7.barcodescanner.zbar.sample.fullscanner.scannerlistener.MessageDialogListener

private const val FLASH_STATE = "FLASH_STATE"
private const val AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE"
private const val SELECTED_FORMATS = "SELECTED_FORMATS"
private const val CAMERA_ID = "CAMERA_ID"

class FullScannerFragment :
    Fragment(),
    MessageDialogListener,
    ResultHandler,
    FormatSelectorDialogListener,
    CameraSelectorDialogListener {

    private var scannerView: ZBarScannerView? = null
    private var flash = false
    private var autoFocus = true
    private var selectedIndices: ArrayList<Int>? = null
    private var cameraId = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        state: Bundle?
    ): View? {
        scannerView = ZBarScannerView(context = requireActivity())

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

        setupFormats()
        return scannerView
    }

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        var menuItem: MenuItem

        menuItem = if (flash) {
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
                val fragment = FormatSelectorDialogFragment.newInstance(
                    listener = this,
                    selectedIndices = selectedIndices
                )
                fragment.show(requireActivity().supportFragmentManager, "format_selector")
                true
            }

            R.id.menu_camera_selector -> {
                scannerView?.stopCamera()
                val fragment = CameraSelectorDialogFragment.newInstance(
                    listener = this,
                    cameraId = cameraId
                )
                fragment.show(requireActivity().supportFragmentManager, "camera_selector")
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        scannerView?.setResultHandler(resultHandler = this)
        scannerView?.startCamera(cameraId = cameraId)
        scannerView?.setFlash(isEnabled = flash)
        scannerView?.setAutoFocus(isEnabled = autoFocus)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(FLASH_STATE, flash)
        outState.putBoolean(AUTO_FOCUS_STATE, autoFocus)
        outState.putIntegerArrayList(SELECTED_FORMATS, selectedIndices)
        outState.putInt(CAMERA_ID, cameraId)
    }

    override fun handleResult(rawResult: Result) {
        try {
            val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val ringtone = RingtoneManager.getRingtone(
                requireActivity().applicationContext,
                notification
            )
            ringtone.play()
        } catch (_: Exception) {
        }

        showMessageDialog(
            message = "Contents = ${rawResult.contents}, Format = ${rawResult.barcodeFormat?.name}"
        )
    }

    private fun showMessageDialog(message: String) {
        val fragment = MessageDialogFragment.newInstance(
            title = "Scan Results",
            message = message,
            listener = this
        )
        fragment.show(requireActivity().supportFragmentManager, "scan_results")
    }

    private fun closeMessageDialog() {
        closeDialog(dialogName = "scan_results")
    }

    private fun closeFormatsDialog() {
        closeDialog(dialogName = "format_selector")
    }

    private fun closeDialog(dialogName: String) {
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val fragment = fragmentManager.findFragmentByTag(dialogName) as? DialogFragment
        fragment?.dismiss()
    }

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        scannerView?.resumeCameraPreview(resultHandler = this)
    }

    override fun onFormatsSaved(selectedIndices: ArrayList<Int>) {
        this.selectedIndices = selectedIndices
        setupFormats()
    }

    override fun onCameraSelected(cameraId: Int) {
        this.cameraId = cameraId
        scannerView?.startCamera(cameraId = this.cameraId)
        scannerView?.setFlash(isEnabled = flash)
        scannerView?.setAutoFocus(isEnabled = autoFocus)
    }

    private fun setupFormats() {
        val formats = ArrayList<BarcodeFormat>()

        if (selectedIndices == null || selectedIndices?.isEmpty() == true) {
            selectedIndices = ArrayList()
            for (index in BarcodeFormat.ALL_FORMATS.indices) {
                selectedIndices?.add(index)
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

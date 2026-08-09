package me.dm7.barcodescanner.zbar.sample.fullscanner.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.hardware.Camera
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import me.dm7.barcodescanner.zbar.sample.R
import me.dm7.barcodescanner.zbar.sample.fullscanner.scannerlistener.CameraSelectorDialogListener

class CameraSelectorDialogFragment : DialogFragment() {

    private var cameraId = -1
    private var listener: CameraSelectorDialogListener? = null

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        retainInstance = true
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val safeListener = listener
        if (safeListener == null) {
            dismiss()
            return Dialog(requireContext())
        }

        val numberOfCameras = Camera.getNumberOfCameras()
        val cameraNames = Array(size = numberOfCameras) { index ->
            val info = Camera.CameraInfo()
            Camera.getCameraInfo(index, info)

            when (info.facing) {
                Camera.CameraInfo.CAMERA_FACING_FRONT -> "Front Facing"
                Camera.CameraInfo.CAMERA_FACING_BACK -> "Rear Facing"
                else -> "Camera ID: $index"
            }
        }

        var checkedIndex = 0
        for (index in 0 until numberOfCameras) {
            if (index == cameraId) {
                checkedIndex = index
            }
        }

        return AlertDialog.Builder(requireActivity())
            .setTitle(R.string.select_camera)
            .setSingleChoiceItems(cameraNames, checkedIndex) { _, which ->
                cameraId = which
            }
            .setPositiveButton(R.string.ok_button) { _, _ ->
                safeListener.onCameraSelected(cameraId)
            }
            .setNegativeButton(R.string.cancel_button) { _, _ ->
            }
            .create()
    }

    companion object {
        fun newInstance(
            listener: CameraSelectorDialogListener,
            cameraId: Int
        ): CameraSelectorDialogFragment = CameraSelectorDialogFragment().apply {
            this.cameraId = cameraId
            this.listener = listener
        }
    }
}

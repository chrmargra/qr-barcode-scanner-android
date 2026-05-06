package me.dm7.barcodescanner.zbar.sample

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import me.dm7.barcodescanner.zbar.BarcodeFormat

class FormatSelectorDialogFragment : DialogFragment() {

    interface FormatSelectorDialogListener {
        fun onFormatsSaved(selectedIndices: ArrayList<Int>)
    }

    private var selectedIndices: ArrayList<Int>? = null
    private var listener: FormatSelectorDialogListener? = null

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        retainInstance = true
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val safeSelectedIndices = selectedIndices
        val safeListener = listener

        if (safeSelectedIndices == null || safeListener == null) {
            dismiss()
            return Dialog(requireContext())
        }

        val formats = Array(BarcodeFormat.ALL_FORMATS.size) { index ->
            BarcodeFormat.ALL_FORMATS[index].name
        }

        val checkedIndices = BooleanArray(BarcodeFormat.ALL_FORMATS.size) { index ->
            safeSelectedIndices.contains(index)
        }

        return AlertDialog.Builder(requireActivity())
            .setTitle(R.string.choose_formats)
            .setMultiChoiceItems(formats, checkedIndices) { _, which, isChecked ->
                if (isChecked) {
                    safeSelectedIndices.add(which)
                } else if (safeSelectedIndices.contains(which)) {
                    safeSelectedIndices.remove(which)
                }
            }
            .setPositiveButton(R.string.ok_button) { _, _ ->
                safeListener.onFormatsSaved(safeSelectedIndices)
            }
            .setNegativeButton(R.string.cancel_button) { _, _ ->
            }
            .create()
    }

    companion object {
        fun newInstance(
            listener: FormatSelectorDialogListener,
            selectedIndices: ArrayList<Int>?
        ): FormatSelectorDialogFragment {
            return FormatSelectorDialogFragment().apply {
                this.selectedIndices = ArrayList(selectedIndices ?: emptyList())
                this.listener = listener
            }
        }
    }
}

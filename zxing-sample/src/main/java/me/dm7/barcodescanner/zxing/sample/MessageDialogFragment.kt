package me.dm7.barcodescanner.zxing.sample

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class MessageDialogFragment : DialogFragment() {

    interface MessageDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
    }

    private var title: String? = null
    private var message: String? = null
    private var listener: MessageDialogListener? = null

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        retainInstance = true
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireActivity())
            .setMessage(message)
            .setTitle(title)
            .setPositiveButton("OK") { _, _ ->
                listener?.onDialogPositiveClick(this)
            }
            .create()
    }

    companion object {
        fun newInstance(
            title: String,
            message: String,
            listener: MessageDialogListener
        ): MessageDialogFragment {
            return MessageDialogFragment().apply {
                this.title = title
                this.message = message
                this.listener = listener
            }
        }
    }
}

package me.dm7.barcodescanner.zxing.sample.qrcodegenerator

import android.os.Bundle
import android.view.View
import androidx.core.view.SoftwareKeyboardControllerCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import me.dm7.barcodescanner.zxing.encoder.QRCodeEncoder
import me.dm7.barcodescanner.zxing.sample.base.BaseScannerActivity
import me.dm7.barcodescanner.zxing.sample.databinding.ActivityQrCodeGeneratorBinding

class QRCodeGeneratorActivity : BaseScannerActivity() {

    private var binding: ActivityQrCodeGeneratorBinding? = null
    private var qrText: String? = null

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)

        binding = ActivityQrCodeGeneratorBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupToolbar(toolbar = binding?.toolbar)

        setupView()
    }

    private fun setupView() {
        binding?.apply {
            editQr.doOnTextChanged { text, _, _, _ ->
                if (text.isNullOrEmpty()) {
                    buttonGenerateQr.isEnabled = false
                    qrText = null
                } else {
                    buttonGenerateQr.isEnabled = true
                    qrText = text.toString()
                }
            }

            buttonGenerateQr.setOnClickListener {
                if (qrText.isNullOrEmpty()) return@setOnClickListener
                editQr.hideKeyboard()
                loader.isVisible = true
                val bitmap = QRCodeEncoder.encodeQRCode(value = qrText.toString())
                imageResult.setImageBitmap(bitmap)
                loader.isVisible = false
                buttonGenerateQr.isEnabled = true
            }
        }
    }

    private fun View.hideKeyboard() {
        SoftwareKeyboardControllerCompat(this).hide()
        clearFocus()
    }
}

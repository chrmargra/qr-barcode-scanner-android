package me.dm7.barcodescanner.zxing.sample.customviewfinder

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.Toast
import com.google.zxing.Result
import me.dm7.barcodescanner.core.IViewFinder
import me.dm7.barcodescanner.core.ViewFinderView
import me.dm7.barcodescanner.zxing.ZXingScannerView
import me.dm7.barcodescanner.zxing.sample.base.BaseScannerActivity
import me.dm7.barcodescanner.zxing.sample.databinding.ActivityCustomViewFinderScannerBinding

private const val TRADE_MARK_TEXT = "ZXing"
private const val TRADE_MARK_TEXT_SIZE_SP = 40

class CustomViewFinderScannerActivity : BaseScannerActivity(), ZXingScannerView.ResultHandler {

    private var scannerView: ZXingScannerView? = null

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)

        val binding = ActivityCustomViewFinderScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar(binding.toolbar)

        val newScannerView = object : ZXingScannerView(this) {
            override fun createViewFinderView(context: Context): IViewFinder =
                CustomViewFinderView(context)
        }

        scannerView = newScannerView
        binding.contentFrame.addView(newScannerView)
    }

    override fun onResume() {
        super.onResume()

        scannerView?.setResultHandler(this)
        scannerView?.startCamera()
    }

    override fun onPause() {
        super.onPause()

        scannerView?.stopCamera()
    }

    override fun handleResult(rawResult: Result) {
        Toast.makeText(
            this,
            "Contents = ${rawResult.text}, Format = ${rawResult.barcodeFormat}",
            Toast.LENGTH_SHORT
        ).show()

        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
        Handler(Looper.getMainLooper()).postDelayed(
            {
                scannerView?.resumeCameraPreview(this)
            },
            2000
        )
    }

    private class CustomViewFinderView : ViewFinderView {

        private val paint = Paint()

        constructor(context: Context) : super(context) {
            init()
        }

        constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
            init()
        }

        private fun init() {
            paint.color = Color.WHITE
            paint.isAntiAlias = true

            val textPixelSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                TRADE_MARK_TEXT_SIZE_SP.toFloat(),
                resources.displayMetrics
            )

            paint.textSize = textPixelSize
            setSquareViewFinder(true)
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            drawTradeMark(canvas)
        }

        private fun drawTradeMark(canvas: Canvas) {
            val framingRect: Rect? = framingRect

            val tradeMarkTop: Float
            val tradeMarkLeft: Float

            if (framingRect != null) {
                tradeMarkTop = framingRect.bottom + paint.textSize + 10
                tradeMarkLeft = framingRect.left.toFloat()
            } else {
                tradeMarkTop = 10f
                tradeMarkLeft = canvas.height - paint.textSize - 10
            }

            canvas.drawText(TRADE_MARK_TEXT, tradeMarkLeft, tradeMarkTop, paint)
        }
    }
}

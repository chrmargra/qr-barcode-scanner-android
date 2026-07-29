package me.dm7.barcodescanner.zxing.sample.customviewfinder

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.TypedValue
import me.dm7.barcodescanner.core.ViewFinderView

private const val TRADE_MARK_TEXT = "ZXing"
private const val TRADE_MARK_TEXT_SIZE_SP = 40

class CustomViewFinderView : ViewFinderView {

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
        setSquareViewFinder(isSquareViewFinder = true)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawTradeMark(canvas = canvas)
    }

    private fun drawTradeMark(canvas: Canvas) {
        val framingRect: Rect? = getFramingRect()

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

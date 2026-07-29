package me.dm7.barcodescanner.core

import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.CornerPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

private const val PORTRAIT_WIDTH_RATIO = 6f / 8f
private const val PORTRAIT_WIDTH_HEIGHT_RATIO = 0.75f

private const val LANDSCAPE_HEIGHT_RATIO = 5f / 8f
private const val LANDSCAPE_WIDTH_HEIGHT_RATIO = 1.4f
private const val MIN_DIMENSION_DIFF = 50

private const val DEFAULT_SQUARE_DIMENSION_RATIO = 5f / 8f

private const val POINT_SIZE = 10
private const val ANIMATION_DELAY = 80L

private const val MAX_ALPHA = 255

private const val CENTER_DIVISOR = 2

open class ViewFinderView : View, ViewFinder {

    companion object {
        private val SCANNER_ALPHA = intArrayOf(
            0,
            64,
            128,
            192,
            255,
            192,
            128,
            64,
        )
    }

    private var storedFramingRect: Rect? = null
    private var scannerAlpha = 0

    private val defaultLaserColor = ContextCompat.getColor(context, R.color.viewfinder_laser)

    private val defaultMaskColor = ContextCompat.getColor(context, R.color.viewfinder_mask)

    private val defaultBorderColor = ContextCompat.getColor(context, R.color.viewfinder_border)

    private val defaultBorderStrokeWidth = resources.getInteger(R.integer.viewfinder_border_width)

    private val defaultBorderLineLength = resources.getInteger(R.integer.viewfinder_border_length)

    // Set up laser paint
    protected var laserPaint: Paint = Paint().apply {
        color = defaultLaserColor
        style = Paint.Style.FILL
    }

    // Finder mask paint
    protected var finderMaskPaint: Paint = Paint().apply {
        color = defaultMaskColor
    }

    // Border paint
    protected var borderPaint: Paint = Paint().apply {
        color = defaultBorderColor
        style = Paint.Style.STROKE
        strokeWidth = defaultBorderStrokeWidth.toFloat()
        isAntiAlias = true
    }

    @JvmField
    protected var borderLineLength: Int = defaultBorderLineLength

    @JvmField
    protected var squareViewFinder: Boolean = false

    private var laserEnabledState = false
    private var finderOffset = 0

    constructor(context: Context) : super(context)

    constructor(
        context: Context,
        attributeSet: AttributeSet?,
    ) : super(context, attributeSet)

    override fun setLaserColor(laserColor: Int) {
        laserPaint.color = laserColor
    }

    override fun setMaskColor(maskColor: Int) {
        finderMaskPaint.color = maskColor
    }

    override fun setBorderColor(borderColor: Int) {
        borderPaint.color = borderColor
    }

    override fun setBorderStrokeWidth(borderStrokeWidth: Int) {
        borderPaint.strokeWidth = borderStrokeWidth.toFloat()
    }

    override fun setBorderLineLength(borderLineLength: Int) {
        this.borderLineLength = borderLineLength
    }

    override fun setLaserEnabled(isEnabled: Boolean) {
        laserEnabledState = isEnabled
    }

    override fun setBorderCornerRounded(isBorderCornersRounded: Boolean) {
        if (isBorderCornersRounded) {
            borderPaint.strokeJoin = Paint.Join.ROUND
        } else {
            borderPaint.strokeJoin = Paint.Join.BEVEL
        }
    }

    override fun setBorderAlpha(alpha: Float) {
        val colorAlpha = (MAX_ALPHA * alpha).toInt()
        borderPaint.alpha = colorAlpha
    }

    override fun setBorderCornerRadius(borderCornersRadius: Int) {
        borderPaint.pathEffect = CornerPathEffect(borderCornersRadius.toFloat())
    }

    override fun setViewFinderOffset(offset: Int) {
        finderOffset = offset
    }

    // TODO: Need a better way to configure this. Revisit when working on 2.0
    override fun setSquareViewFinder(isSquareViewFinder: Boolean) {
        squareViewFinder = isSquareViewFinder
    }

    override fun setupViewFinder() {
        updateFramingRect()
        invalidate()
    }

    override fun getFramingRect(): Rect? = storedFramingRect

    public override fun onDraw(canvas: Canvas) {
        if (getFramingRect() == null) return

        drawViewFinderMask(canvas)
        drawViewFinderBorder(canvas)

        if (laserEnabledState) {
            drawLaser(canvas)
        }
    }

    open fun drawViewFinderMask(canvas: Canvas) {
        val width = canvas.width
        val height = canvas.height
        val framingRect = getFramingRect() ?: throw NullPointerException()

        canvas.drawRect(
            0f,
            0f,
            width.toFloat(),
            framingRect.top.toFloat(),
            finderMaskPaint,
        )
        canvas.drawRect(
            0f,
            framingRect.top.toFloat(),
            framingRect.left.toFloat(),
            (framingRect.bottom + 1).toFloat(),
            finderMaskPaint,
        )
        canvas.drawRect(
            (framingRect.right + 1).toFloat(),
            framingRect.top.toFloat(),
            width.toFloat(),
            (framingRect.bottom + 1).toFloat(),
            finderMaskPaint,
        )
        canvas.drawRect(
            0f,
            (framingRect.bottom + 1).toFloat(),
            width.toFloat(),
            height.toFloat(),
            finderMaskPaint
        )
    }

    open fun drawViewFinderBorder(canvas: Canvas) {
        val framingRect = getFramingRect()

        // Top-left corner
        val path = Path()
        val activeFramingRect = framingRect ?: throw NullPointerException()

        path.moveTo(
            activeFramingRect.left.toFloat(),
            (activeFramingRect.top + borderLineLength).toFloat(),
        )
        path.lineTo(
            activeFramingRect.left.toFloat(),
            activeFramingRect.top.toFloat(),
        )
        path.lineTo(
            (activeFramingRect.left + borderLineLength).toFloat(),
            activeFramingRect.top.toFloat(),
        )
        canvas.drawPath(path, borderPaint)

        // Top-right corner
        path.moveTo(
            activeFramingRect.right.toFloat(),
            (activeFramingRect.top + borderLineLength).toFloat(),
        )
        path.lineTo(
            activeFramingRect.right.toFloat(),
            activeFramingRect.top.toFloat(),
        )
        path.lineTo(
            (activeFramingRect.right - borderLineLength).toFloat(),
            activeFramingRect.top.toFloat(),
        )
        canvas.drawPath(path, borderPaint)

        // Bottom-right corner
        path.moveTo(
            activeFramingRect.right.toFloat(),
            (activeFramingRect.bottom - borderLineLength).toFloat(),
        )
        path.lineTo(
            activeFramingRect.right.toFloat(),
            activeFramingRect.bottom.toFloat(),
        )
        path.lineTo(
            (activeFramingRect.right - borderLineLength).toFloat(),
            activeFramingRect.bottom.toFloat(),
        )
        canvas.drawPath(path, borderPaint)

        // Bottom-left corner
        path.moveTo(
            activeFramingRect.left.toFloat(),
            (activeFramingRect.bottom - borderLineLength).toFloat(),
        )
        path.lineTo(
            activeFramingRect.left.toFloat(),
            activeFramingRect.bottom.toFloat(),
        )
        path.lineTo(
            (activeFramingRect.left + borderLineLength).toFloat(),
            activeFramingRect.bottom.toFloat(),
        )
        canvas.drawPath(path, borderPaint)
    }

    open fun drawLaser(canvas: Canvas) {
        val framingRect = getFramingRect()

        // Draw a red "laser scanner" line through the middle to show decoding is active
        laserPaint.alpha = SCANNER_ALPHA[scannerAlpha]
        scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.size

        val activeFramingRect = framingRect ?: throw NullPointerException()
        val middle =
            activeFramingRect.height() / CENTER_DIVISOR + activeFramingRect.top

        canvas.drawRect(
            (activeFramingRect.left + 2).toFloat(),
            (middle - 1).toFloat(),
            (activeFramingRect.right - 1).toFloat(),
            (middle + 2).toFloat(),
            laserPaint,
        )

        postInvalidateDelayed(
            ANIMATION_DELAY,
            activeFramingRect.left - POINT_SIZE,
            activeFramingRect.top - POINT_SIZE,
            activeFramingRect.right + POINT_SIZE,
            activeFramingRect.bottom + POINT_SIZE,
        )
    }

    protected override fun onSizeChanged(
        xNew: Int,
        yNew: Int,
        xOld: Int,
        yOld: Int,
    ) {
        updateFramingRect()
    }

    @Synchronized
    open fun updateFramingRect() {
        val viewResolution = Point(width, height)
        var framingWidth: Int
        var framingHeight: Int
        val orientation = DisplayUtils.getScreenOrientation(context)

        if (squareViewFinder) {
            if (orientation != Configuration.ORIENTATION_PORTRAIT) {
                framingHeight = (height * DEFAULT_SQUARE_DIMENSION_RATIO).toInt()
                framingWidth = framingHeight
            } else {
                framingWidth = (width * DEFAULT_SQUARE_DIMENSION_RATIO).toInt()
                framingHeight = framingWidth
            }
        } else {
            if (orientation != Configuration.ORIENTATION_PORTRAIT) {
                framingHeight = (height * LANDSCAPE_HEIGHT_RATIO).toInt()
                framingWidth =
                    (LANDSCAPE_WIDTH_HEIGHT_RATIO * framingHeight).toInt()
            } else {
                framingWidth = (width * PORTRAIT_WIDTH_RATIO).toInt()
                framingHeight =
                    (PORTRAIT_WIDTH_HEIGHT_RATIO * framingWidth).toInt()
            }
        }

        if (framingWidth > width) {
            framingWidth = width - MIN_DIMENSION_DIFF
        }

        if (framingHeight > height) {
            framingHeight = height - MIN_DIMENSION_DIFF
        }

        val leftOffset =
            (viewResolution.x - framingWidth) / CENTER_DIVISOR
        val topOffset =
            (viewResolution.y - framingHeight) / CENTER_DIVISOR

        storedFramingRect = Rect(
            leftOffset + finderOffset,
            topOffset + finderOffset,
            leftOffset + framingWidth - finderOffset,
            topOffset + framingHeight - finderOffset,
        )
    }
}

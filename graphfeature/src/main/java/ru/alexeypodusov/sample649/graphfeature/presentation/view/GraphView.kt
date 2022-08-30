package ru.alexeypodusov.sample649.graphfeature.presentation.view

import android.animation.ArgbEvaluator
import android.animation.FloatArrayEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import ru.alexeypodusov.sample649.graphfeature.R
import ru.alexeypodusov.sample649.graphfeature.presentation.GraphItem
import ru.alexeypodusov.sample649.graphfeature.util.CalculateUtils
import java.lang.Float.max
import kotlin.properties.Delegates

private const val ROW_TEXT_SIZE_SP = 16
private const val ITEM_TEXT_SIZE_SP = 12
private const val LINES_MARGIN_START_DP = 16
private const val MIN_ITEM_HORIZONTAL_MARGIN_DP = 8
private const val ANIMATION_DURATION = 1000L

internal class GraphView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,

    ) : View(context, attrs, defStyleAttr, defStyleRes) {


    private var colorBackground by Delegates.notNull<Int>()
    private var minItemColor by Delegates.notNull<Int>()
    private var maxItemColor by Delegates.notNull<Int>()
    private var textColor by Delegates.notNull<Int>()
    private var lineColor by Delegates.notNull<Int>()

    init {
        initAttrs(attrs, defStyleAttr, defStyleRes)
    }

    private fun initAttrs(
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) {
        if (attrs == null) return

        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.GraphView,
            defStyleAttr,
            defStyleRes
        )

        colorBackground = typedArray.getColor(R.styleable.GraphView_backgroundColor, Color.WHITE)
        minItemColor = typedArray.getColor(R.styleable.GraphView_minItemColor, Color.GRAY)
        maxItemColor = typedArray.getColor(R.styleable.GraphView_maxItemColor, Color.BLACK)
        textColor = typedArray.getColor(R.styleable.GraphView_textColor, Color.BLACK)
        lineColor = typedArray.getColor(R.styleable.GraphView_lineColor, Color.BLACK)

        typedArray.recycle()
    }


    private var items: List<GraphItem> = emptyList()


    private var currentWidth = 0f
    private var currentHeight = 0f

    private var rowHeadersStartX = 0f
    private var itemsStartX = 0f
    private var linesStep = 0f
    private var rowHeaders = emptyList<String>()
    private var linesEndY = 0f

    private var itemHorizontalMargin = 0f
    private var itemWidth = 0f

    private var itemsDrawData = emptyList<GraphItemDrawData>()

    private var displayContentWidth = 0f
    private var fullContentwidth = 0f

    private var contentTranslationX = 0f
    private var minContentTranslationX = 0f

    private val lastPoint = PointF()
    private var lastPointerId = 0

    private var animator: ValueAnimator? = null

    private val linesMarginStartPx = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        LINES_MARGIN_START_DP.toFloat(),
        resources.displayMetrics
    )

    private val rowTextSize = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        ROW_TEXT_SIZE_SP.toFloat(),
        resources.displayMetrics
    )

    private val itemTextSize = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        ITEM_TEXT_SIZE_SP.toFloat(),
        resources.displayMetrics
    )

    private val minItemHorizontalMarginPx = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        MIN_ITEM_HORIZONTAL_MARGIN_DP.toFloat(),
        resources.displayMetrics
    )

    private val rowHeaderTextPaint = Paint().apply {
        textSize = this@GraphView.rowTextSize
        color = textColor
        textAlign = Paint.Align.RIGHT
    }

    private val itemTextPaint = Paint().apply {
        textSize = this@GraphView.itemTextSize
        color = textColor
        textAlign = Paint.Align.CENTER
    }

    private val linePaint = Paint().apply {
        color = lineColor
        strokeWidth = 2f
    }

    private val backgroundPaint = Paint().apply {
        color = colorBackground
    }

    private val itemPaint = Paint()

    private val textBounds = Rect()

    fun setItems(data: List<GraphItem>) {
        this.items = data
        calculate()
        startAnimation()
    }

    private fun Canvas.drawTextYCentred(text: String, x: Float, y: Float, paint: Paint) {
        paint.getTextBounds(text, 0, text.length, textBounds)
        drawText(text, x, y - textBounds.exactCenterY(), paint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        currentWidth = w.toFloat()
        currentHeight = h.toFloat()
        calculate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        with(canvas) {
            drawColor(colorBackground)
            drawLines()
            drawItems()
            drawRowHeaders()

            drawRect(currentWidth - paddingRight, 0f, currentWidth, currentHeight, backgroundPaint)
        }
    }

    private fun Canvas.drawItems() {
        var x = itemsStartX + itemHorizontalMargin + contentTranslationX
        itemsDrawData.forEach {
            itemPaint.color = it.color
            drawRect(
                x,
                it.currentTop,
                x + itemWidth,
                linesEndY,
                itemPaint
            )
            drawText(it.name, x + itemWidth / 2, linesEndY + itemTextSize * 2, itemTextPaint)

            x += itemWidth + itemHorizontalMargin * 2
        }
    }

    private fun Canvas.drawLines() {
        var y = linesEndY
        repeat(rowHeaders.size) {
            drawLine(rowHeadersStartX + linesMarginStartPx, y, currentWidth, y, linePaint)
            y -= linesStep
        }
    }

    private fun Canvas.drawRowHeaders() {
        drawRect(0f, 0f, itemsStartX, currentHeight, backgroundPaint)
        var y = linesEndY
        rowHeaders.forEach {
            drawTextYCentred(it, rowHeadersStartX, y, rowHeaderTextPaint)
            y -= linesStep
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event ?: return false

        if (event.pointerCount != 1) {
            return false
        }

        if (displayContentWidth >= fullContentwidth) {
            return false
        }

        return when (event.action) {

            MotionEvent.ACTION_DOWN -> {
                lastPoint.set(event.x, event.y)
                lastPointerId = event.getPointerId(0)
                true
            }

            MotionEvent.ACTION_MOVE -> {
                val pointerId = event.getPointerId(0)
                if (lastPointerId == pointerId && event.x >= itemsStartX) {
                    contentTranslationX = (contentTranslationX + (event.x - lastPoint.x)).coerceIn(
                        minContentTranslationX,
                        0f)
                    invalidate()
                }

                lastPoint.set(event.x, event.y)
                lastPointerId = event.getPointerId(0)

                true
            }
            else -> false
        }
    }

    private fun calculate() {
        if (items.isEmpty()) return

        val maxValue = items.maxBy { it.value }.value
        rowHeaders = CalculateUtils.rowValues(maxValue)
        rowHeadersStartX = paddingLeft + rowHeaderTextPaint.measureText(maxValue.toString())
        itemsStartX = rowHeadersStartX + linesMarginStartPx
        linesEndY = currentHeight + rowTextSize / 2 - itemTextSize * 2 - paddingBottom
        linesStep = (linesEndY - rowTextSize / 2 - paddingTop) / (rowHeaders.size - 1)

        displayContentWidth = (currentWidth - paddingRight - itemsStartX)
        itemWidth = displayContentWidth / 7
        itemHorizontalMargin = max(
            minItemHorizontalMarginPx,
            (displayContentWidth - items.size * itemWidth) / items.size / 2
        )

        fullContentwidth = (itemHorizontalMargin * 2 + itemWidth) * items.size
        minContentTranslationX = (displayContentWidth - fullContentwidth).coerceAtMost(0f)

        itemsDrawData = items.mapIndexed { i, item ->
            val top =
                linesEndY - item.value * ((linesEndY - rowTextSize / 2 - paddingTop) / rowHeaders.last()
                    .toInt())
            GraphItemDrawData(
                name = item.name,
                maxTop = top,
                currentTop = top,
                color = ArgbEvaluator()
                    .evaluate(100 / items.size.toFloat() / 100 * i, minItemColor, maxItemColor) as Int
            )
        }
    }

    private fun startAnimation() {
        animator?.cancel()
        val startValues = FloatArray(itemsDrawData.size)
        val endValues = FloatArray(itemsDrawData.size)
        itemsDrawData.forEachIndexed { i, it ->
            startValues[i] = linesEndY
            endValues[i] = it.maxTop
        }

        animator = ValueAnimator.ofObject(
            FloatArrayEvaluator(),
            startValues,
            endValues
        ).apply {
            duration = ANIMATION_DURATION
            interpolator = FastOutSlowInInterpolator()
            addUpdateListener {
                (it.animatedValue as FloatArray).forEachIndexed { i, value ->
                    itemsDrawData[i].currentTop = value
                }
                invalidate()
            }
            start()
        }
    }

    override fun onDetachedFromWindow() {
        animator?.cancel()
        super.onDetachedFromWindow()
    }

    override fun onSaveInstanceState(): Parcelable {
        return super.onSaveInstanceState().let {
            SavedState(it).apply {
                items = this@GraphView.items
            }
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        with(state as SavedState) {
            super.onRestoreInstanceState(this.superState)
            this@GraphView.items = this.items ?: listOf()
        }
    }

    class SavedState : BaseSavedState {
        var items: List<GraphItem>? = null

        constructor(superState: Parcelable?) : super(superState)
        constructor(source: Parcel) : super(source) {
            val mutableItems = mutableListOf<GraphItem>()
            source.readTypedList(mutableItems, GraphItem.CREATOR)
            items = mutableItems
        }


        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeTypedList(items ?: emptyList<GraphItem>())
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(parcel: Parcel): SavedState {
                    return SavedState(parcel)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return Array(size) { null }
                }
            }
        }
    }
}

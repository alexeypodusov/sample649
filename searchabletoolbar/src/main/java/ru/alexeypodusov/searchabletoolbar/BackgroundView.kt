package ru.alexeypodusov.searchabletoolbar

import android.animation.FloatArrayEvaluator
import android.animation.ValueAnimator
import android.content.Context

import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator

private const val MAX_RADIUS_DIP = 30f
private const val DURATION = 100L

internal class BackgroundView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0

) : View(context, attrs, defStyleAttr, defStyleRes) {

    private var state = State.BUTTON

    private var paint = Paint()

    private val rect = RectF()
    private var animator: ValueAnimator? = null

    private val maxRadiusPx = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        MAX_RADIUS_DIP,
        resources.displayMetrics
    )
    private var currentRadiusPx = maxRadiusPx

    private var currentWidth = 0f

    private var currentHeight = 0f

    private val maxAnimValues = floatArrayOf(
        maxRadiusPx,
        paddingLeft.toFloat(),
        paddingTop.toFloat(),
        paddingRight.toFloat(),
        paddingBottom.toFloat()
    )

    private val minAnimValues = floatArrayOf(
        0f,
        0f,
        0f,
        0f,
        0f
    )

    fun setColor(color: Int) {
        paint.color = color
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        currentWidth = w.toFloat()
        currentHeight = h.toFloat()
        calculateRect()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRoundRect(rect, currentRadiusPx, currentRadiusPx, paint)
    }

    fun setState(state: State, withAnimation: Boolean = false) {
        this.state = state
        when (withAnimation) {
            true -> startAnimation()
            false -> {
                calculateRect()
                invalidate()
            }
        }
    }

    private fun calculateRect() {
        when (state) {
            State.BUTTON -> {
                currentRadiusPx = maxRadiusPx
                rect.set(
                    paddingLeft.toFloat(),
                    paddingTop.toFloat(),
                    currentWidth - paddingRight,
                    currentHeight - paddingBottom
                )
            }
            State.INPUT -> {
                currentRadiusPx = 0f
                rect.set(0.toFloat(), 0.toFloat(), currentWidth, currentHeight)
            }
        }
    }

    private fun startAnimation() {
        animator?.cancel()
        animator = when (state) {
            State.INPUT -> createValueAnimator(maxAnimValues, minAnimValues)
            State.BUTTON -> createValueAnimator(minAnimValues, maxAnimValues)
        }.apply {
            duration = DURATION
            interpolator = LinearOutSlowInInterpolator()
            addUpdateListener {
                with(it.animatedValue as FloatArray) {
                    currentRadiusPx = this[0]
                    rect.set(
                        this[1],
                        this[2],
                        currentWidth - this[3],
                        currentHeight - this[4]
                    )
                }
                invalidate()
            }

            start()
        }

    }

    private fun createValueAnimator(startValue: FloatArray, endValue: FloatArray): ValueAnimator {
        return ValueAnimator.ofObject(
            FloatArrayEvaluator(),
            startValue,
            endValue
        )
    }

    override fun onDetachedFromWindow() {
        animator?.cancel()
        super.onDetachedFromWindow()
    }
}
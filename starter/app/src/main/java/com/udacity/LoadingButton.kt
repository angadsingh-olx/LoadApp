package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.core.animation.doOnEnd
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var centerX = 0
    private var centerY = 0

    private var textSize = 32f

    private var loadingText: String = ""
    private var defaultText: String = ""

    private val tempRect: Rect = Rect()

    @FloatRange(from = 0.0, to = 100.0)
    private var progress = 0f

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create( "", Typeface.BOLD)
    }

    private val valueAnimator = ValueAnimator.ofFloat(0f, 100f).apply {
        duration = 5000
        interpolator = DecelerateInterpolator()
        addUpdateListener {
            progress = (it.animatedValue as Float)
            invalidate()
        }
        doOnEnd {
            isEnabled = true
            progress = 0f
            invalidate()
        }
    }

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { property, oldState, newState ->
        when(newState) {
            ButtonState.Loading -> {
                isEnabled = false
            }

            ButtonState.Completed -> {
                isEnabled = true
            }

            ButtonState.Clicked -> {
                isEnabled = false
                valueAnimator.start()
            }
        }
    }

    init {
        isClickable = true

        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            loadingText = getString(R.styleable.LoadingButton_loadingText) ?: resources.getString(R.string.button_loading)
            defaultText = getString(R.styleable.LoadingButton_defaultText) ?: resources.getString(R.string.label_button_download)
            defaultText = getString(R.styleable.LoadingButton_defaultText) ?: resources.getString(R.string.label_button_download)
            textSize = getDimension(R.styleable.LoadingButton_android_textSize, textSize)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // Paint default background
        canvas?.drawColor(resources.getColor(R.color.colorPrimary))

        val progressRatio = progress / 100f

        paint.color = resources.getColor(R.color.download_icon_background)
        // Paint progress background
        canvas?.drawRect(
            0f,
            0f,
            widthSize * progressRatio,
            widthSize.toFloat(),
            paint
        )

        paint.textSize = textSize
        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.CENTER

        val drawText = if (progressRatio == 0f || progressRatio == 1.0f) {
            defaultText
        } else {
            loadingText
        }
        paint.getTextBounds(drawText, 0, drawText.length, tempRect)

        canvas?.drawText(
            drawText,
            centerX.toFloat(),
            centerY.toFloat() - tempRect.exactCenterY(),
            paint
        )

        paint.color = Color.YELLOW

        val circleDiameter = tempRect.height()
        val startCoordinate = centerX + (tempRect.right.toFloat() / 2f) + 16

        canvas?.drawArc(
            startCoordinate,
            centerY - (circleDiameter / 2f),
            startCoordinate + circleDiameter,
            centerY + (circleDiameter / 2f),
        0f,
            (360 * progressRatio) % 360,
            true,
            paint
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h

        centerX = (widthSize / 2f).toInt()
        centerY = (heightSize / 2f).toInt()
        setMeasuredDimension(w, h)
    }
}
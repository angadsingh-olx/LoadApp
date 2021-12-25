package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.IntRange
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

    @IntRange(from = 0, to = 100)
    private var progress = 50

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create( "", Typeface.BOLD)
    }

    private val valueAnimator = ValueAnimator()

    var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { p, old, new ->

    }

    init {
        isClickable = true

        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            loadingText = getString(R.styleable.LoadingButton_loadingText) ?: resources.getString(R.string.label_button_loading)
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
        paint.getTextBounds(defaultText, 0, defaultText.length, tempRect)

        canvas?.drawText(
            defaultText,
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
            360 * progressRatio,
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

    fun startAnimation() {
        val animator = ValueAnimator.ofInt(0, 100)
        animator.duration = 5000
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.addUpdateListener {
            progress = (it.animatedValue as Int)
            invalidate()
        }
        animator.start()
    }

}
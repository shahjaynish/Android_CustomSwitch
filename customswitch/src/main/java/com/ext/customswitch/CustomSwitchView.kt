package com.ext.customswitch

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class CustomSwitchView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var isOn = false
    private var thumbPosition = 0f // 0 = OFF, 1 = ON

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bgRect = RectF()

    private var activeColor = Color.GREEN
    private var inactiveColor = Color.GRAY
    private var thumbColor = Color.WHITE

    private var animationDuration = 250L

    private var listener: ((Boolean) -> Unit)? = null

    init {
        isClickable = true

        // Read XML attributes
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.CustomSwitchView)

            activeColor = typedArray.getColor(
                R.styleable.CustomSwitchView_activeColor,
                Color.GREEN
            )

            inactiveColor = typedArray.getColor(
                R.styleable.CustomSwitchView_inactiveColor,
                Color.GRAY
            )

            thumbColor = typedArray.getColor(
                R.styleable.CustomSwitchView_thumbColor,
                Color.WHITE
            )

            isOn = typedArray.getBoolean(
                R.styleable.CustomSwitchView_isChecked,
                false
            )

            animationDuration = typedArray.getInt(
                R.styleable.CustomSwitchView_animationDuration,
                250
            ).toLong()

            typedArray.recycle()
        }

        // Set initial thumb position
        thumbPosition = if (isOn) 1f else 0f

        setOnClickListener {
            toggle()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Background
        bgRect.set(0f, 0f, width.toFloat(), height.toFloat())
        paint.color = if (thumbPosition > 0.5f) activeColor else inactiveColor
        canvas.drawRoundRect(bgRect, height / 2f, height / 2f, paint)

        // Thumb position (animated)
        val cx = height / 2f + (width - height) * thumbPosition
        val cy = height / 2f
        val radius = height / 2f - 8

        paint.color = thumbColor
        canvas.drawCircle(cx, cy, radius, paint)
    }

    private fun toggle() {
        val start = thumbPosition
        val end = if (isOn) 0f else 1f

        val animator = ValueAnimator.ofFloat(start, end)
        animator.duration = animationDuration

        animator.addUpdateListener {
            thumbPosition = it.animatedValue as Float
            invalidate()
        }

        animator.start()

        isOn = !isOn
        listener?.invoke(isOn)
    }

    // Public API
    fun setOnCheckedChangeListener(l: (Boolean) -> Unit) {
        listener = l
    }

    fun setChecked(value: Boolean) {
        isOn = value
        thumbPosition = if (value) 1f else 0f
        invalidate()
    }

    fun isChecked(): Boolean = isOn

    override fun performClick(): Boolean {
        return super.performClick()
    }
}
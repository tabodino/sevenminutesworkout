package fr.wesy.sevenminutesworkout.presentation.animation

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import fr.wesy.sevenminutesworkout.R

class BreathingAnimationView (context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val circleColors = listOf(
        ContextCompat.getColor(context, R.color.circle_blue1),
        ContextCompat.getColor(context, R.color.circle_blue2),
        ContextCompat.getColor(context, R.color.circle_blue3),
        ContextCompat.getColor(context, R.color.circle_blue4),
        ContextCompat.getColor(context, R.color.circle_blue5)
    )

    private val numCircles = circleColors.size

    private val paints = List(numCircles) { i ->
        Paint().apply {
            color = circleColors[i]
            alpha = (255 - (i * 40))
            style = Paint.Style.FILL
        }
    }

    private val radii = FloatArray(numCircles)

    private val maxRadius = 200f
    private val circleSpacing = 30f

    private val animator = ValueAnimator.ofFloat(100f, maxRadius).apply {
        duration = 3000
        repeatMode = ValueAnimator.REVERSE
        repeatCount = ValueAnimator.INFINITE
        addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Float
            for (i in 0 until numCircles) {
                radii[i] = animatedValue - (i * circleSpacing)
            }
            invalidate()
        }
    }

    init {
        animator.start()
    }

    fun pauseAnimation() {
        if (animator.isRunning) {
            animator.pause()
        }
    }

    fun resumeAnimation() {
        if (animator.isPaused) {
            animator.resume()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (i in 0 until numCircles) {
            if (radii[i] > 0) {
                canvas.drawCircle(width / 2f, height / 2f, radii[i], paints[i])
            }
        }
    }
}
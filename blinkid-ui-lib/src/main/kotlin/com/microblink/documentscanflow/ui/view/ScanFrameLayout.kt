package com.microblink.documentscanflow.ui.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.support.v4.content.ContextCompat
import com.microblink.documentscanflow.R
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.FrameLayout
import com.microblink.documentscanflow.getFloatValue
import com.microblink.documentscanflow.pxToDp
import com.microblink.documentscanflow.ui.utils.AccelerateDecelerateReverseInterpolator
import kotlinx.android.synthetic.main.mb_view_scan_frame.view.*

class ScanFrameLayout(context : Context, attrs : AttributeSet?, styleAttrs : Int) : FrameLayout(context, attrs, styleAttrs) {

    private val scanRectAspectRatio = resources.getFloatValue(R.dimen.mb_scan_rect_aspect_ratio)
    private lateinit var scanRect : RectF

    private var scanLineInitialY: Float = 0f
    private var scanAnimation = createLineAnimation()

    init {
        inflate(getContext(), R.layout.mb_view_scan_frame, this)
        scanLineImg.drawable.mutate().setColorFilter(ContextCompat.getColor(context, R.color.mbIconScanLine), PorterDuff.Mode.MULTIPLY)
    }

    constructor(context : Context, attrs : AttributeSet) : this(context, attrs, 0)

    constructor(context : Context) : this(context, null, 0)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val rectWidthPercent = 0.90f
        val scanRectWidth = w * rectWidthPercent
        val scanRectHeight = scanRectWidth / scanRectAspectRatio

        val sideOffset = ((1 - rectWidthPercent) / 2f)
        val rectLeft = w * sideOffset
        val rectRight = w * (rectWidthPercent + sideOffset)
        val rectTop = (h - scanRectHeight) / 2.0f
        val rectBottom = rectTop + scanRectHeight
        scanRect = RectF(rectLeft, rectTop, rectRight, rectBottom)

        cameraOverlay.setScanRect(scanRect)

        val params = FrameLayout.LayoutParams(flipCardView.layoutParams)
        params.width = (w * 0.75f).toInt()
        params.height = (params.width / scanRectAspectRatio).toInt()
        params.gravity = Gravity.CENTER
        flipCardView.layoutParams = params
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        // scan line starts centered in parent so move it up for simpler animation
        scanLineImg.y = height / 2 - scanRect.height() / 2 - scanLineImg.height / 2
        scanLineInitialY = scanLineImg.y

        // instructions also start centered so move them up
        scanInstructionsTv.y = height / 2 - scanRect.height() / 2 - scanInstructionsTv.height
    }

    fun startLineAnimation() {
        if (!this::scanRect.isInitialized) {
            return
        }

        if (scanAnimation.hasStarted()) {
            scanAnimation.cancel()
            scanAnimation = createLineAnimation()
        }

        scanLineImg.visibility = View.VISIBLE
        val previewHeightDp = pxToDp(scanRect.height())
        val scanLineAnimDuration = 4.167f * previewHeightDp + 1250

        scanAnimation.apply {
            duration = scanLineAnimDuration.toLong()
            interpolator = AccelerateDecelerateReverseInterpolator()
            repeatCount = Animation.INFINITE
        }

        scanLineImg.startAnimation(scanAnimation)
    }

    fun cancelLineAnimation() {
        scanAnimation.cancel()
        scanLineImg.visibility = View.GONE
    }

    private fun createLineAnimation(): Animation {
        return object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                val translateY = scanRect.height() * interpolatedTime
                scanLineImg.y = scanLineInitialY + translateY
            }
        }
    }

}
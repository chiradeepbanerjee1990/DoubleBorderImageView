package com.chiradeep.countryflag.imageview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.ViewTreeObserver
import androidx.appcompat.widget.AppCompatImageView


/**
 * Author : Chiradeep Banerjee
 * This class going to define circular country flag imageview
 */
class CircularImageView: AppCompatImageView, ViewTreeObserver.OnPreDrawListener {

    lateinit var mCirclePaint : Paint
    lateinit var mCircleBorderPaint : Paint
    var outerRadius: Float = 0.0f
    var innerradius : Float = 0.0f

    private var TAG : String = CircularImageView::class.java.name

    private val STICKY_WIDTH_UNDEFINED = -1
    private val BREAK_HEIGHT = 1950
    private val ARBITRARY_WIDTH_LESSER = 200
    private val ARBITRARY_WIDTH_GREATER = 800
    private var mStickyWidth = STICKY_WIDTH_UNDEFINED

    constructor(context: Context?) : super(context,null) {
       init(null)
    }

    constructor(context: Context?, attr: AttributeSet) : super(context,attr) {
        init(attr)
    }

    constructor(context: Context?, attr: AttributeSet, defStyle: Int) : super(context,attr, defStyle) {
        init(attr)
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //Getting the viewHeight and viewWidth number to set it as center
        var vieHeight = height/2
        var viewWidth = width/2

        canvas!!.drawCircle(viewWidth.toFloat(),
            vieHeight.toFloat(),
            outerRadius,
            mCirclePaint)


        canvas!!.drawCircle(viewWidth.toFloat(),
            vieHeight.toFloat(),
            innerradius,
            mCircleBorderPaint)

    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val w = right - left
        val h = bottom - top

        super.onLayout(changed, left, top, right, bottom)
        if (h <= BREAK_HEIGHT && w < ARBITRARY_WIDTH_GREATER
            && mStickyWidth === STICKY_WIDTH_UNDEFINED
        ) {
            mStickyWidth = ARBITRARY_WIDTH_GREATER
            viewTreeObserver.addOnPreDrawListener(this)
        } else {
            mStickyWidth = STICKY_WIDTH_UNDEFINED
        }
    }

    override  fun onPreDraw(): Boolean {
        viewTreeObserver.removeOnPreDrawListener(this)
        if (mStickyWidth === STICKY_WIDTH_UNDEFINED) { // Happy with the selected width.
            return true
        }

        Log.d(TAG, ">>>>onPreDraw() requesting new layout")
        requestLayout()
        return false
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        logMeasureSpecs(widthMeasureSpec,heightMeasureSpec)

        // some value that is too high for the screen
        val desiredHeight = 10000
        val desiredWidth: Int

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val getSuggestedHeight = suggestedMinimumHeight
        val getSuggestedWidth = suggestedMinimumWidth

        val width: Int
        val height: Int

        // Height
        height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> Math.min(desiredHeight, heightSize)
            else -> getSuggestedHeight
        }

        // Width
        desiredWidth = when {
            mStickyWidth !== STICKY_WIDTH_UNDEFINED ->
                mStickyWidth
            height > BREAK_HEIGHT ->
                ARBITRARY_WIDTH_LESSER
            else -> ARBITRARY_WIDTH_GREATER
        }

        width = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> Math.min(desiredWidth, widthSize)
            else -> suggestedMinimumWidth
        }

        Log.d(TAG, "suggested height and suggested width ($getSuggestedHeight, $getSuggestedWidth)")
        Log.d(TAG, "setMeasuredDimension($width, $height)")
        setMeasuredDimension(width, height)


    }

    /**
     * @param attr
     * Getting the attribute from xml file to render
     */
    private fun init(attr: AttributeSet?){
        mCirclePaint = Paint()


        mCirclePaint.color = context.obtainStyledAttributes(attr, R.styleable.CircularImageView).
            getColor(R.styleable.CircularImageView_outerciclecolor, Color.RED)
        mCirclePaint.strokeWidth = 5F
        mCirclePaint.isAntiAlias = true
        mCirclePaint.style = Paint.Style.STROKE

        mCircleBorderPaint = Paint()
        mCircleBorderPaint.color = context.obtainStyledAttributes(attr, R.styleable.CircularImageView).
            getColor(R.styleable.CircularImageView_innercirclecolor, Color.GREEN)
        mCircleBorderPaint.strokeWidth = 3.5F
        mCircleBorderPaint.style = Paint.Style.STROKE

        outerRadius = context.obtainStyledAttributes(attr,R.styleable.CircularImageView).
            getInteger(R.styleable.CircularImageView_outerradius,100).toFloat()
        innerradius = context.obtainStyledAttributes(attr,R.styleable.CircularImageView).
            getInteger(R.styleable.CircularImageView_innerradius,90).toFloat()




    }


    private fun logMeasureSpecs(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val measureSpecHeight: String
        val measureSpecWidth: String

        measureSpecHeight = when (heightMode) {
            MeasureSpec.EXACTLY -> "EXACTLY"
            MeasureSpec.AT_MOST -> "AT_MOST"
            else -> "UNSPECIFIED"
        }

        measureSpecWidth = when (widthMode) {
            MeasureSpec.EXACTLY -> "EXACTLY"
            MeasureSpec.AT_MOST -> "AT_MOST"
            else -> "UNSPECIFIED"
        }

        Log.d(
            TAG, "Width: " + measureSpecWidth + ", " + widthSize + " Height: "
                    + measureSpecHeight + ", " + heightSize
        )
    }

}
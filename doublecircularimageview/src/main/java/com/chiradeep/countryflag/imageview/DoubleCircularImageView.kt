package com.chiradeep.countryflag.imageview

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.ViewTreeObserver
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.min


/**
 * Author : Chiradeep Banerjee
 * This class going to define circular country flag imageview
 */
class DoubleCircularImageView: AppCompatImageView, ViewTreeObserver.OnPreDrawListener {

    lateinit var mCirclePaint : Paint
    lateinit var mCircleBorderPaint : Paint
    var outerRadius: Float = 0.0f
    var innerradius : Float = 0.0f

    private var TAG : String = DoubleCircularImageView::class.java.name

    private val STICKY_WIDTH_UNDEFINED = -1
    private val BREAK_HEIGHT = 1950
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


        var mBitmap = drawableToBitmap(this.background)
        val smallest = min(mBitmap.width, mBitmap.height)
        val factor = smallest / innerradius
        val bwidth = mBitmap.width.toFloat()
        val bheight = mBitmap.height.toFloat()


        val newbitMap = Bitmap.createScaledBitmap(mBitmap,
            (bwidth/factor).toInt(), (bheight/factor).toInt(), true)

        this.background = BitmapDrawable(resources,getRoundedShape(newbitMap,innerradius.toInt()))


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
        if (mStickyWidth === STICKY_WIDTH_UNDEFINED) {
            return true
        }
        requestLayout()
        return false
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        logMeasureSpecs(widthMeasureSpec,heightMeasureSpec)

        val desiredWidth = outerRadius.toInt() * 2
        val desiredHeight = outerRadius.toInt() * 2

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width: Int
        val height: Int

        //Measure Width
        width = when (widthMode) {
            MeasureSpec.EXACTLY -> //Must be this size
                widthSize
            MeasureSpec.AT_MOST -> //Can't be bigger than...
                min(desiredWidth, widthSize)
            else ->
                desiredWidth
        }

        //Measure Height
        height = when (heightMode) {
            MeasureSpec.EXACTLY -> //Must be this size
                heightSize
            MeasureSpec.AT_MOST -> //Can't be bigger than...
                min(desiredHeight, heightSize)
            else ->
                desiredHeight
        }

        setMeasuredDimension(width, height)
    }

    fun getRoundedShape(scaleBitmapImage: Bitmap,innerRadius: Int): Bitmap {

        val targetBitmap = Bitmap.createBitmap(
            innerRadius,
            innerRadius, Bitmap.Config.ARGB_8888
        )
        var canvas = Canvas(targetBitmap)
        val path = Path()
        path.addCircle(
            (innerRadius.toFloat() - 1) /2,
            (innerRadius.toFloat() - 1) /2,
            min(innerRadius.toFloat(), innerRadius.toFloat())/2 ,
            Path.Direction.CW
        )

        canvas.clipPath(path)
        canvas.drawBitmap(
            scaleBitmapImage,
            Rect(
                0, 0, scaleBitmapImage.width,
                scaleBitmapImage.height
            ),
            Rect(
                0, 0, innerRadius,
                innerRadius
            ), null
        )
        return targetBitmap
    }


    /**
     * @param attr
     * Getting the attribute from xml file to render
     */
    private fun init(attr: AttributeSet?){

        outerRadius = context.obtainStyledAttributes(attr,R.styleable.DoubleCircularImageView).
            getInteger(R.styleable.DoubleCircularImageView_outerradius,100).toFloat()
        innerradius = context.obtainStyledAttributes(attr,R.styleable.DoubleCircularImageView).
            getInteger(R.styleable.DoubleCircularImageView_innerradius,90).toFloat()

        mCirclePaint = Paint()


        mCirclePaint.color = context.obtainStyledAttributes(attr, R.styleable.DoubleCircularImageView).
            getColor(R.styleable.DoubleCircularImageView_outerciclecolor, Color.RED)
        mCirclePaint.strokeWidth = 5F
        mCirclePaint.isAntiAlias = true
        mCirclePaint.style = Paint.Style.STROKE

        mCircleBorderPaint = Paint()
        mCircleBorderPaint.color = context.obtainStyledAttributes(attr, R.styleable.DoubleCircularImageView).
            getColor(R.styleable.DoubleCircularImageView_innercirclecolor, Color.GREEN)
        mCircleBorderPaint.strokeWidth = 3.5F
        mCircleBorderPaint.style = Paint.Style.STROKE



    }

    fun drawableToBitmap(drawable: Drawable): Bitmap {
        var bitmap: Bitmap? = null

        if (drawable is BitmapDrawable) {
            if (drawable.bitmap != null) {
                return drawable.bitmap
            }
        }

        bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            Bitmap.createBitmap(
                1,
                1,
                Bitmap.Config.ARGB_8888
            ) // Single color bitmap will be created of 1x1 pixel
        } else {
            Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
        }

        val canvas = Canvas(bitmap!!)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
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
package com.chiradeep.countryflag.imageview

import android.animation.*
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.ViewTreeObserver
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.min
import android.graphics.Bitmap
import com.chiradeep.doublecircular.imageview.R


/**
 * Author : Chiradeep Banerjee
 * This class going to define Double Circular ImageView
 */
class DoubleCircularImageView: AppCompatImageView, ViewTreeObserver.OnPreDrawListener {

    private var TAG : String = DoubleCircularImageView::class.java.name

    lateinit var mCirclePaint : Paint
    lateinit var mCircleBorderPaint : Paint
    var outerRadius: Float = 0.0f
    var innerradius : Float = 0.0f
    var clip_type = CLIP_TYPE.CENTER
    private val STICKY_WIDTH_UNDEFINED = -1
    private val BREAK_HEIGHT = 1950
    private val ARBITRARY_WIDTH_GREATER = 800
    private var mStickyWidth = STICKY_WIDTH_UNDEFINED


    enum class CLIP_TYPE(val flag:Boolean){
        ALL(true),
        CENTER(false),
        LEFT(false),
        RIGHT(true)
    }

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

    fun showOuterCircularAnimation(){
        val  mAnimator = ObjectAnimator.ofObject(mCirclePaint, "color",  ArgbEvaluator(),
            mCirclePaint.color,mCircleBorderPaint.color)
        mAnimator.duration = 200
        mAnimator.repeatMode = ValueAnimator.REVERSE
        mAnimator.repeatCount = ValueAnimator.INFINITE

        mAnimator.addUpdateListener {
            invalidate()
        }
        mAnimator.start()

    }

    fun showInnerCircularAnimation() {
        val  mAnimator = ObjectAnimator.ofObject(mCircleBorderPaint, "color",  ArgbEvaluator(),
            mCircleBorderPaint.color,mCirclePaint.color)
        mAnimator.duration = 200
        mAnimator.repeatMode = ValueAnimator.REVERSE
        mAnimator.repeatCount = ValueAnimator.INFINITE

        mAnimator.addUpdateListener {
            invalidate()
        }
        mAnimator.start()
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

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        invalidate()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        logMeasureSpecs(widthMeasureSpec,heightMeasureSpec)

        val desiredWidth = (outerRadius.toInt()+5) * 2
        val desiredHeight = (outerRadius.toInt()+5) * 2

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

        if(this.background != null) {
            var mBtmp = getRoundedShape(getBitmapBasedOnClipData(),width)

            if(clip_type == CLIP_TYPE.RIGHT) {
                var rotateFinalMatrix = Matrix()
                rotateFinalMatrix.postRotate(180F)

                var myBm = Bitmap.createBitmap(
                    mBtmp, 0, 0,
                    mBtmp.width,
                    mBtmp.height,
                    rotateFinalMatrix, true
                )
                this.background =
                    BitmapDrawable(resources, myBm)
            }else {
                this.background =
                    BitmapDrawable(resources, mBtmp)
            }
        }

        Log.e(TAG,"Measured Dimension : ${width}, ${height}")
        setMeasuredDimension(width, height)
    }

    fun getBitmapBasedOnClipData():Bitmap {
        when(clip_type){
            CLIP_TYPE.LEFT -> {
                var mBitmap = drawableToBitmap(this.background)
                val smallest = min(mBitmap.width, mBitmap.height)
                val factor = smallest / innerradius
                val bwidth = mBitmap.width.toFloat()
                val bheight = mBitmap.height.toFloat()
                return Bitmap.createScaledBitmap(
                    mBitmap,
                    (bwidth / factor).toInt(), (bheight / factor).toInt(), true
                )
            }
            CLIP_TYPE.CENTER -> {
                var mBitmap = drawableToBitmap(this.background)
                val factor = mBitmap.width / innerradius
                val bwidth = mBitmap.width.toFloat()
                var bheight = if(bwidth > mBitmap.height.toFloat()){
                    bwidth
                }else{
                    mBitmap.height.toFloat()
                }

                Log.e(TAG, "bwidth is : ${bwidth} and bheight is : ${bheight}")
                return Bitmap.createScaledBitmap(
                    mBitmap,
                    (bwidth / factor).toInt(), (bheight / factor).toInt(), true
                )
            }
            CLIP_TYPE.RIGHT -> {
                var matrix = Matrix()
                matrix.postRotate(180F)

               var rotatedBm =   Bitmap.createBitmap((this.background as BitmapDrawable).bitmap, 0, 0,
                   ((this.background as BitmapDrawable).bitmap.width),
                   ((this.background as BitmapDrawable).bitmap.height),
                    matrix, true)


                val bwidth = rotatedBm.width.toFloat()
                val bheight = rotatedBm.height.toFloat()

                val factor = rotatedBm.width.toFloat() / rotatedBm.height.toFloat()

                Log.e(TAG, "bwidth is : $bwidth and bheight is : $bheight")

               return  Bitmap.createScaledBitmap(
                    rotatedBm,
                    (bwidth *factor).toInt(), (bheight*factor ).toInt(), true
                )
            }
            else -> {
                // Default case to return from left clip
                var mBitmap = drawableToBitmap(this.background)
                val factor = mBitmap.width / innerradius
                val bwidth = mBitmap.width.toFloat()
                var bheight = mBitmap.height.toFloat()
                return Bitmap.createScaledBitmap(
                    mBitmap,
                    (bwidth / factor).toInt(), (bheight / factor).toInt(), true
                )
            }
        }
    }

    fun getRoundedShape(scaleBitmapImage: Bitmap, measuredwidth : Int): Bitmap {

        val targetBitmap = Bitmap.createBitmap(
            innerradius.toInt(),
            innerradius.toInt(),
            Bitmap.Config.ARGB_8888
        )

        var shadder = BitmapShader(scaleBitmapImage,Shader.TileMode.CLAMP,Shader.TileMode.CLAMP)
        var canvas = Canvas(targetBitmap)
        var radius = min(canvas.width, canvas.height/2)
        var paint = Paint()
        paint.isAntiAlias = true
        paint.shader = shadder
        canvas.drawCircle(
            canvas.width.toFloat()/2,
            canvas.height.toFloat()/2,
            radius.toFloat() - (outerRadius/2-innerradius/2) ,
            paint
        )


        return targetBitmap
    }




    /**
     * @param attr
     * Getting the attribute from xml file to render
     */
    private fun init(attr: AttributeSet?){

        outerRadius = context.obtainStyledAttributes(attr, R.styleable.DoubleCircularImageView).
            getInteger(R.styleable.DoubleCircularImageView_outerradius,100).toFloat()
        innerradius = context.obtainStyledAttributes(attr,R.styleable.DoubleCircularImageView).
            getInteger(R.styleable.DoubleCircularImageView_innerradius,90).toFloat()

        mCirclePaint = Paint()


        mCirclePaint.color = context.obtainStyledAttributes(attr, R.styleable.DoubleCircularImageView).
            getColor(R.styleable.DoubleCircularImageView_outerciclecolor, Color.RED)
        mCirclePaint.strokeWidth = context.obtainStyledAttributes(attr, R.styleable.DoubleCircularImageView).
            getInteger(R.styleable.DoubleCircularImageView_outerradiuswidth,5).toFloat()
        mCirclePaint.isAntiAlias = true
        mCirclePaint.style = Paint.Style.STROKE

        mCircleBorderPaint = Paint()
        mCircleBorderPaint.color = context.obtainStyledAttributes(attr, R.styleable.DoubleCircularImageView).
            getColor(R.styleable.DoubleCircularImageView_innercirclecolor, Color.GREEN)
        mCircleBorderPaint.strokeWidth = context.obtainStyledAttributes(attr, R.styleable.DoubleCircularImageView).
            getInteger(R.styleable.DoubleCircularImageView_innerrardiuswidth,3).toFloat()
        mCircleBorderPaint.style = Paint.Style.STROKE

        val clipType = context.obtainStyledAttributes(attr, R.styleable.DoubleCircularImageView).
            getInteger(R.styleable.DoubleCircularImageView_cliptype,0)

        Log.d(TAG,"Clip type value is ${clipType}")

        clip_type = when(clipType){
            0 -> CLIP_TYPE.CENTER
            1 -> CLIP_TYPE.LEFT
            2-> CLIP_TYPE.RIGHT
            3-> CLIP_TYPE.ALL
            else -> CLIP_TYPE.CENTER
        }

        Log.d(TAG,"Clip type value is ${clip_type}")

        if(context.obtainStyledAttributes(attr,R.styleable.DoubleCircularImageView).getBoolean(R.styleable.DoubleCircularImageView_borderanim, true)) {
            showOuterCircularAnimation()
            showInnerCircularAnimation()
        }

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
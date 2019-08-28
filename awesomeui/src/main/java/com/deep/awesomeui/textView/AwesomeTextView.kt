package com.deep.awesomeui.textView

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Handler
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.AttributeSet
import android.util.TypedValue
import android.view.*
import android.view.accessibility.AccessibilityEvent
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Checkable
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatTextView
import com.deep.awesomeui.R


class awesometext_View @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    AppCompatTextView(context, attrs), Checkable{

    companion object {

        private val COLOR_ACCENT_INDEX = 2
        private val INVALID_END_INDEX = -1
        private val CHECKED_STATE_SET = intArrayOf(android.R.attr.state_checked)
        private val LEFT = 0
        private val RIGHT = 1
    }

    private var actionTextColor: Int = 0
    private var mBackgroundColor: Int = 0
    private var mBorderColor: Int = 0
    private var checkMarkDrawableTint: Int = 0
    private var padding: Int = 0
    private var leftPadding: Int = 0
    private var topPadding: Int = 0
    private var rightPadding: Int = 0
    private var bottomPadding: Int = 0
    private var lineEndIndex: Int = 0
    private var trimLines: Int = 0
    private var shape: Int = 0
    private var collapsedHeight: Int = 0
    private var checkBoxPosition: Int = 0
    private var expandedHeight = 0
    private var checkBoxDrawablePadding: Int = 0
    private var baseCheckBoxPadding: Int = 0
    private var checkBoxWidth: Int = 0
    private var animationDuration = 300L
    private var isExpandableText: Boolean = false
    private var expanded: Boolean = false
    private var needTint: Boolean = false
    private var mChecked: Boolean = false
    private var isStrikeText: Boolean = false
    private var readMore = true
    private var isClick = false
    private var DEFAULT_SHOW_EXPANDED_TEXT: Boolean = false
    private var isUnderLine: Boolean = false
    private var isBorderView: Boolean = false
    private var isCheckedText: Boolean = false
    private var radius: Float = 0.toFloat()
    private var strokeWidth: Float = 0.toFloat()
    private var fontName: String? = null
    private val defValue = arrayOf("more", "less")
    private var textData : CharSequence? = null
    private var checkBoxDrawable: Drawable? = null
    private var checked: Drawable? = null
    private var unChecked: Drawable? = null
    private var expandInterpolator: TimeInterpolator? = null
    private var collapseInterpolator: TimeInterpolator? = null
    private var bufferType: BufferType? = null
    private var viewMoreSpan: ReadMoreClickableSpan? = null
    private var listener: OnCheckedChangeListener? = null

    private val measuredHeightOfTextView: Int
        get() {
            val textView = TextView(this.getContext())
            textView.setPadding(
                getPaddingLeft(),
                getPaddingTop(),
                getPaddingRight(),
                getPaddingBottom()
            )
            textView.setTypeface(getTypeface())
            textView.setText(text, BufferType.SPANNABLE)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (getTextSize() as Int).toFloat())
            textView.measure(
                MeasureSpec.makeMeasureSpec(this.getMeasuredWidth(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            )
            return textView.measuredHeight
        }

    init {
        init(attrs)
    }


    @SuppressLint("ResourceType")
    private fun init(attrs: AttributeSet?) {
        val a = getContext().obtainStyledAttributes(attrs, R.styleable.awesometext_View)
        isBorderView = a.getBoolean(R.styleable.awesometext_View_awesometext_setRoundedView, false)
        mBorderColor = a.getColor(
            R.styleable.awesometext_View_awesometext_setRoundedBorderColor,
            Color.parseColor("#B6B6B6")
        )
        padding = a.getDimensionPixelSize(R.styleable.awesometext_View_android_padding, -1)
        leftPadding = a.getDimensionPixelSize(R.styleable.awesometext_View_android_paddingLeft, 5)
        topPadding = a.getDimensionPixelSize(R.styleable.awesometext_View_android_paddingTop, 5)
        rightPadding = a.getDimensionPixelSize(R.styleable.awesometext_View_android_paddingRight, 5)
        bottomPadding = a.getDimensionPixelSize(R.styleable.awesometext_View_android_paddingBottom, 5)
        isClick = a.hasValue(R.styleable.awesometext_View_android_onClick)
        radius = a.getDimension(R.styleable.awesometext_View_awesometext_setRadius, 1f)
        mBackgroundColor =
            a.getColor(R.styleable.awesometext_View_awesometext_setRoundedBGColor, Color.TRANSPARENT)
        strokeWidth = a.getDimension(R.styleable.awesometext_View_awesometext_setStrokeWidth, 1f)
        shape = a.getInt(R.styleable.awesometext_View_awesometext_setShape, 0)
        isExpandableText = a.getBoolean(R.styleable.awesometext_View_awesometext_setExpandableText, false)
        isUnderLine = a.getBoolean(R.styleable.awesometext_View_awesometext_setUnderLineText, false)
        isStrikeText = a.getBoolean(R.styleable.awesometext_View_awesometext_setStrikeText, false)
        DEFAULT_SHOW_EXPANDED_TEXT =
            a.getBoolean(R.styleable.awesometext_View_awesometext_setActionTextVisible, true)
        fontName = a.getString(R.styleable.awesometext_View_awesometext_setFont)
        if (a.hasValue(R.styleable.awesometext_View_awesometext_setExpandHint))
            defValue[0] = a.getString(R.styleable.awesometext_View_awesometext_setExpandHint)!!
        if (a.hasValue(R.styleable.awesometext_View_awesometext_setCollapseHint))
            defValue[1] = a.getString(R.styleable.awesometext_View_awesometext_setCollapseHint)!!
        trimLines = a.getInt(R.styleable.awesometext_View_awesometext_setTrimLines, 2)
        actionTextColor = a.getColor(R.styleable.awesometext_View_awesometext_setActionTextColor, 0)
        expandInterpolator = AccelerateDecelerateInterpolator()
        collapseInterpolator = AccelerateDecelerateInterpolator()
        isCheckedText = a.getBoolean(R.styleable.awesometext_View_awesometext_setCheckedText, false)
        mChecked = a.getBoolean(R.styleable.awesometext_View_awesometext_setChecked, false)
        checkBoxPosition = a.getInteger(R.styleable.awesometext_View_awesometext_checkedIconPosition, LEFT)
        checkBoxDrawablePadding =
            a.getDimensionPixelSize(R.styleable.awesometext_View_awesometext_checkedDrawablePadding, 5)
        checked = a.getDrawable(R.styleable.awesometext_View_awesometext_checkedDrawable)
        unChecked = a.getDrawable(R.styleable.awesometext_View_awesometext_unCheckedDrawable)
        checkMarkDrawableTint = a.getColor(R.styleable.awesometext_View_awesometext_checkMarkTint, 0)
        needTint = checked != null && unChecked != null
        checked =
            if (checked == null) getResources().getDrawable(android.R.drawable.checkbox_on_background) else checked
        unChecked =
            if (unChecked == null) getResources().getDrawable(android.R.drawable.checkbox_off_background) else unChecked
        refresh()
        a.recycle()
    }


    private fun setText() {
        super.setText(getTrimmedText(text), bufferType)
        setMovementMethod(LinkMovementMethod.getInstance())
        setHighlightColor(Color.TRANSPARENT)
    }

    override fun setText(text: CharSequence, type: BufferType) {
        this.textData = text
        bufferType = type
        setText()
    }

    private fun getTrimmedText(text: CharSequence?): CharSequence? {
        if (isExpandableText) {
            if (text != null && lineEndIndex > 0) {
                return if (readMore) {
                    updateCollapsedText()
                } else {
                    updateExpandedText()
                }
            }
        }
        return text
    }

    private fun updateCollapsedText(): CharSequence {
        val ELLIPSIZE = "..."
        val trimEndIndex = lineEndIndex - (ELLIPSIZE.length + defValue[0].length + 1)
        val s = SpannableStringBuilder(text, 0, trimEndIndex)
            .append(ELLIPSIZE)
            .append(defValue[0])
        return addClickableSpan(s, defValue[0])
    }

    private fun updateExpandedText(): CharSequence? {
        if (DEFAULT_SHOW_EXPANDED_TEXT) {
            val s = SpannableStringBuilder(text, 0, text!!.length).append(" ").append(defValue[1])
            return addClickableSpan(s, defValue[1])
        }
        return text
    }

    private fun addClickableSpan(s: SpannableStringBuilder, trimText: CharSequence): CharSequence {
        s.setSpan(
            viewMoreSpan,
            s.length - trimText.length,
            s.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return s
    }

    private fun setBackgroundLayout(drawable: Drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            this.setBackground(drawable)
        } else {
            this.setBackgroundDrawable(drawable)
        }
    }

    protected override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (isCheckedText) {
            val checkMarkDrawable = if (mChecked) checked else unChecked
            if (checkMarkDrawable != null) {
                val height = checkMarkDrawable.intrinsicHeight
                val y = (getHeight() - height) / 2
                when (checkBoxPosition) {
                    LEFT -> {
                        checkMarkDrawable.setBounds(
                            baseCheckBoxPadding,
                            y,
                            baseCheckBoxPadding + checkBoxWidth,
                            y + height
                        )
                        checkMarkDrawable.draw(canvas)
                    }
                    RIGHT -> {
                        val right = getWidth()
                        checkMarkDrawable.setBounds(
                            right - checkBoxWidth - baseCheckBoxPadding,
                            y, right - baseCheckBoxPadding, y + height
                        )
                        checkMarkDrawable.draw(canvas)
                    }
                }
            }
        }
    }

    private fun refresh() {
        //check checkedText is active or not... if active change view as checkedText
        if (isCheckedText) {
            if (checkMarkDrawableTint != 0 && needTint) {
                checked!!.colorFilter =
                    PorterDuffColorFilter(checkMarkDrawableTint, PorterDuff.Mode.SRC_IN)
                unChecked!!.colorFilter =
                    PorterDuffColorFilter(checkMarkDrawableTint, PorterDuff.Mode.SRC_IN)
            }
            setGravity(Gravity.CENTER_VERTICAL)
            setCheckMarkDrawable(if (mChecked) checked else unChecked)
            isChecked = mChecked
        }
        //check borderView is active or not... if active set border view
        if (isBorderView) {
            if (padding != -1) {
                setPadding(padding, padding, padding, padding)
            } else {
                setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
            }
            setBackgroundLayout(getShapeBackground(mBorderColor))
        }
        //check ExpandableText is active or not... if active set view in expandable mode
        if (isExpandableText) {
            setMaxLines(trimLines)
            viewMoreSpan = ReadMoreClickableSpan()
            onGlobalLayoutLineEndIndex()
        }
        setUnderLineText(isUnderLine)
        setStrikeText(isStrikeText)
        setFont()
    }


    @SuppressLint("WrongConstant")
    private fun getShapeBackground(@ColorInt color: Int): Drawable {
        val radius: Int
        if (this.shape == GradientDrawable.OVAL) {
            radius =
                (if (this.getHeight() > this.getWidth()) this.getHeight() else this.getWidth()) / 2
        } else {
            radius = this.radius.toInt()
        }
        val shape = GradientDrawable()
        shape.shape = this.shape
        shape.cornerRadius = radius.toFloat()
        shape.setColor(mBackgroundColor)
        shape.setStroke(strokeWidth.toInt(), color)
        return shape
    }

    private fun setFont() {
        if (fontName != null) {
            try {
                typeface = Typefaces[context, fontName!!]
            } catch (ignored: Exception) {
            }

        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        try {
            val actionX: Int
            val actionY: Int
            invalidate()
            if (isClick || isExpandableText)
                return super.onTouchEvent(event)

            if (isEnabled()) {
                if (event.action == MotionEvent.ACTION_DOWN) {
                    actionX = event.x.toInt()
                    actionY = event.y.toInt()
                    if (isCheckedText) {
                        if (checkBoxDrawable != null && checkBoxDrawable!!.bounds.contains(
                                actionX,
                                actionY
                            )
                        ) {
                            if (listener != null)
                                listener!!.onCheckedChanged(!isChecked)
                            isChecked = !isChecked
                            return super.onTouchEvent(event)
                        }
                    } else {
                        return false
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

    override fun setOnClickListener(l: OnClickListener?) {
        try {
            isClick = true
            if (isCheckedText) {
                isChecked = !isChecked
                if (listener != null)
                    listener!!.onCheckedChanged(isChecked)
            }
            super.setOnClickListener(l)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightMeasureSpec = heightMeasureSpec
        if (this.trimLines == 0 && !this.expanded)
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }


    private inner class ReadMoreClickableSpan : ClickableSpan() {
        override fun onClick(widget: View) {
            this@awesometext_View.setClickable(false)
            this@awesometext_View.setFocusable(false)
            this@awesometext_View.setFocusableInTouchMode(false)
            readMore = !readMore
            toggleView()
            setText()
            enableClick()
        }

        override fun updateDrawState(ds: TextPaint) {
            ds.color =
                if (actionTextColor != 0) actionTextColor else getThemeColor(COLOR_ACCENT_INDEX)
        }
    }

    private fun enableClick() {
        Handler().postDelayed(Runnable {
            this@awesometext_View.setClickable(true)
            this@awesometext_View.setFocusable(true)
            this@awesometext_View.setFocusableInTouchMode(true)
        }, 100L)
    }

    private fun onGlobalLayoutLineEndIndex() {
        getViewTreeObserver().addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val obs = getViewTreeObserver()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    obs.removeOnGlobalLayoutListener(this)
                } else {
                    obs.removeGlobalOnLayoutListener(this)
                }
                try {
                    if (trimLines == 0) {
                        lineEndIndex = getLayout().getLineEnd(0)
                    } else if (trimLines > 0 && getLineCount() >= trimLines) {
                        lineEndIndex = getLayout().getLineEnd(trimLines - 1)
                    } else {
                        lineEndIndex = INVALID_END_INDEX
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                setText()
            }
        })
        invalidate()
    }


    private fun toggleView() {
        if (this.trimLines >= 0) {
            if (expanded) {
                expandedHeight = this.getMeasuredHeight()
                animateView(false)
            } else {
                this.measure(
                    MeasureSpec.makeMeasureSpec(this.getMeasuredWidth(), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                )
                this.collapsedHeight = this.getMeasuredHeight()
                this.setMaxLines(Integer.MAX_VALUE)
                if (this.expandedHeight == 0) {
                    this.expandedHeight = measuredHeightOfTextView
                }
                animateView(true)
            }
        }
    }

    private fun animateView(expanded: Boolean) {
        val valueAnimator: ValueAnimator = if (expanded) {
            ValueAnimator.ofInt(this.collapsedHeight, expandedHeight)
        } else {
            ValueAnimator.ofInt(this.expandedHeight, this.collapsedHeight)
        }

        valueAnimator.addUpdateListener { animation -> this@awesometext_View.setHeight(animation.animatedValue as Int) }
        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if (expanded) {
                    this@awesometext_View.setMaxHeight(Integer.MAX_VALUE)
                    val layoutParams = this@awesometext_View.getLayoutParams()
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    this@awesometext_View.setLayoutParams(layoutParams)
                    this@awesometext_View.expanded = true
                } else {
                    val layoutParams = this@awesometext_View.getLayoutParams()
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    this@awesometext_View.setLayoutParams(layoutParams)
                    this@awesometext_View.expanded = false
                }
            }
        })
        valueAnimator.interpolator =
            if (expanded) this.expandInterpolator else this.collapseInterpolator
        valueAnimator.setDuration(this.animationDuration).start()
    }

    private fun getThemeColor(index: Int): Int {
        val attribute = intArrayOf(R.attr.colorPrimary, R.attr.colorPrimaryDark, R.attr.colorAccent)
        val array = getContext().getTheme().obtainStyledAttributes(attribute)
        val color = array.getColor(index, Color.parseColor("#FF4081"))
        array.recycle()
        return color
    }

    fun setOnCheckedChangeListener(l: OnCheckedChangeListener) {
        this.listener = l
    }

    interface OnCheckedChangeListener {
        fun onCheckedChanged(isChecked: Boolean)
    }

    override fun toggle() {
        if (isCheckedText)
            isChecked = !mChecked
    }

    override fun isChecked(): Boolean {
        return mChecked
    }

    /**
     *
     * Changes the checked state of this text view.
     *
     * @param checked true to check the text, false to uncheck it
     */
    override fun setChecked(checked: Boolean) {
        if (isCheckedText)
            if (mChecked != checked) {
                mChecked = checked
                invalidate()
            }
    }

    /**
     * Set the checkmark to a given Drawable. This will be drawn when [.isChecked] is true.
     *
     * @param d The Drawable to use for the checkmark.
     */
    private fun setCheckMarkDrawable(d: Drawable?) {
        if (isCheckedText) {
            if (checkBoxDrawable != null) {
                checkBoxDrawable!!.callback = null
                unscheduleDrawable(checkBoxDrawable)
            }
            if (d != null) {
                d.callback = this
                d.setVisible(getVisibility() === VISIBLE, false)
                d.state = CHECKED_STATE_SET
                minHeight = d.intrinsicHeight
                checkBoxWidth = d.intrinsicWidth
                checkBoxDrawable = d
                if (!isBorderView)
                    super.setPadding(
                        paddingLeft + checkBoxWidth + checkBoxDrawablePadding,
                        paddingTop,
                        paddingRight,
                        paddingBottom
                    )
                d.state = getDrawableState()
            }
            requestLayout()
        }
    }

    protected override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (isChecked) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET)
        }
        return drawableState
    }

    protected override fun drawableStateChanged() {
        super.drawableStateChanged()
        if (checkBoxDrawable != null) {
            val myDrawableState = getDrawableState()
            checkBoxDrawable!!.state = myDrawableState
            invalidate()
        }
    }

    override fun dispatchPopulateAccessibilityEvent(event: AccessibilityEvent): Boolean {
        val populated = super.dispatchPopulateAccessibilityEvent(event)
        if (!populated) {
            event.isChecked = mChecked
        }
        return populated
    }

    /**
     * Set the checkMark to a given Drawable, identified by its resourece id. This will be drawn
     * when it is true.
     *
     * @param resId The Drawable to use for the checkmark.
     */
    fun setCheckedDrawable(resId: Int) {
        if (resId != 0 && !isCheckedText) {
            return
        }
        val d = getResources().getDrawable(resId)
        checked = d
        setCheckMarkDrawable(d)
    }

    /**
     * Set the unCheckMark to a given Drawable, identified by its resourece id. This will be drawn
     * when it is true.
     *
     * @param resId The Drawable to use for the checkmark.
     */
    fun setUnCheckedDrawable(resId: Int) {
        if (resId != 0 && !isCheckedText) {
            return
        }
        val d = getResources().getDrawable(resId)
        unChecked = d
        setCheckMarkDrawable(d)
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        if (checkBoxDrawable != null && isCheckedText) {
            when (checkBoxPosition) {
                LEFT -> {
                    baseCheckBoxPadding = left
                    super.setPadding(
                        left + checkBoxWidth + checkBoxDrawablePadding,
                        top,
                        right,
                        bottom
                    )
                }
                RIGHT -> {
                    baseCheckBoxPadding = right
                    super.setPadding(
                        left,
                        top,
                        right + checkBoxWidth + checkBoxDrawablePadding,
                        bottom
                    )
                }
            }
        } else {
            super.setPadding(left, top, right, bottom)
        }
    }

    fun setUnderLineText(isUnderLine: Boolean) {
        this.setPaintFlags(if (isUnderLine) this.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG else this.getPaintFlags() and Paint.UNDERLINE_TEXT_FLAG.inv())
    }

    fun setStrikeText(isStrikeText: Boolean) {
        this.setPaintFlags(if (isStrikeText) this.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG else this.getPaintFlags() and Paint.STRIKE_THRU_TEXT_FLAG.inv())
    }

    fun setFontName(fontName: String) {
        this.fontName = fontName
        refresh()
    }

    fun setTrimLines(trimLines: Int) {
        this.trimLines = trimLines
        refresh()
    }

    fun setShape(shape: Int) {
        this.shape = shape
        refresh()
    }

    fun setActionTextColor(actionTextColor: Int) {
        this.actionTextColor = actionTextColor
        refresh()
    }

    fun setExpandableText(isExpandable: Boolean) {
        isExpandableText = isExpandable
        refresh()

    }

    fun setBorderView(isBorderNeed: Boolean) {
        isBorderView = isBorderNeed
        refresh()
    }

    fun setStrokeWidth(strokeWidth: Float) {
        this.strokeWidth = strokeWidth
        refresh()
    }

    override fun setBackgroundColor(mBackgroundColor: Int) {
        this.mBackgroundColor = mBackgroundColor
        refresh()
    }

    fun setBorderColor(mBorderColor: Int) {
        this.mBorderColor = mBorderColor
        refresh()
    }

    fun setRadius(radius: Float) {
        this.radius = radius
        refresh()
    }


    fun setCheckedText(checkedText: Boolean) {
        isCheckedText = checkedText
        refresh()
    }


    fun setAnimationDuration(animationDuration: Long) {
        this.animationDuration = animationDuration
    }

    fun setInterpolator(interpolator: TimeInterpolator) {
        this.expandInterpolator = interpolator
        this.collapseInterpolator = interpolator
    }



}
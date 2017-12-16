package us.buddman.teachertracker.views


import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Point
import android.graphics.RectF
import android.graphics.Shader
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import us.buddman.teachertracker.R


/**
 * Created by JunseokOh on 2016. 8. 6..
 */
class CartaTagView : AppCompatTextView {

    internal var fullMode = false
    var isTextColorEnabled = false
        internal set
    internal var gradientEnabled = false
    var color = Color.BLACK
        internal set
    var startColor = Color.BLACK
        internal set
    var endColor = Color.WHITE
        internal set
    internal var touchOverlayColor = Color.TRANSPARENT
    var textColor = Color.WHITE
        internal set
    internal var height: Int = 0
    internal var width: Int = 0
    var center = Point()
    var gradientOverlay: LinearGradient? = null
    var bgRect = RectF()
    var innerRect = RectF()
    var innerPaint = Paint()
    var bgPaint = Paint()
    var touchOverlayPaint = Paint()

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        getAttrs(attrs)
        gravity = Gravity.CENTER
        setPadding(
                resources.getDimensionPixelSize(R.dimen.button_left_padding),
                resources.getDimensionPixelSize(R.dimen.button_top_padding),
                resources.getDimensionPixelSize(R.dimen.button_left_padding),
                resources.getDimensionPixelSize(R.dimen.button_top_padding)
        )

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> touchOverlayColor = Color.TRANSPARENT
            MotionEvent.ACTION_DOWN -> touchOverlayColor = Color.parseColor("#32BDBDBD")
        }
        return super.onTouchEvent(event)
    }

    private fun getAttrs(attrs: AttributeSet) {
        val array = context.obtainStyledAttributes(attrs, R.styleable.CartaTagView)
        setTypedArray(array)
    }

    private fun setTypedArray(array: TypedArray) {
        fullMode = array.getBoolean(R.styleable.CartaTagView_fullMode, false)
        color = array.getColor(R.styleable.CartaTagView_themeColor, Color.BLACK)
        textColor = array.getColor(R.styleable.CartaTagView_textThemeColor, Color.BLACK)
        isTextColorEnabled = array.getBoolean(R.styleable.CartaTagView_textThemeColorEnabled, false)
        gradientEnabled = array.getBoolean(R.styleable.CartaTagView_enableGradient, false)
        startColor = array.getColor(R.styleable.CartaTagView_gradientStartColor, Color.BLACK)
        endColor = array.getColor(R.styleable.CartaTagView_gradientEndColor, Color.WHITE)
        array.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        height = measuredHeight
        width = measuredWidth
    }

    fun setView() {
        if (!isTextColorEnabled)
            setTextColor(if (fullMode) Color.WHITE else color)
        else
            setTextColor(textColor)

        if (gradientEnabled) {
            gradientOverlay = LinearGradient(0f, 0f, getWidth().toFloat(), 0f,
                    startColor, endColor,
                    Shader.TileMode.CLAMP)
        }
        setLayerType(View.LAYER_TYPE_SOFTWARE, innerPaint)
        setLayerType(View.LAYER_TYPE_SOFTWARE, bgPaint)

    }

    override fun onDraw(canvas: Canvas) {
        setView()
        center.set(width / 2, height / 2)
        val strokeWidth = resources.getDimensionPixelSize(R.dimen.stroke_width)
        val innerH = height - strokeWidth
        val innerW = width - strokeWidth
        val left = center.x - innerW / 2
        val top = center.y - innerH / 2
        val right = center.x + innerW / 2
        val bottom = center.y + innerH / 2
        // Background Paint
        bgPaint.color = color
        bgPaint.style = Paint.Style.STROKE
        bgPaint.isAntiAlias = true
        bgPaint.strokeWidth = strokeWidth.toFloat()

        // Inner Paint
        innerPaint.isAntiAlias = true
        innerPaint.color = if (fullMode) color else Color.WHITE
        innerPaint.style = Paint.Style.FILL

        // Touch Overlay Paint
        touchOverlayPaint.color = touchOverlayColor
        touchOverlayPaint.style = Paint.Style.FILL
        touchOverlayPaint.isAntiAlias = true

        if (gradientEnabled) {
            if (fullMode)
                innerPaint.shader = gradientOverlay
            else
                bgPaint.shader = gradientOverlay
        }

        bgRect.set(0.0f + strokeWidth, 0.0f + strokeWidth, (width - strokeWidth).toFloat(), (height - strokeWidth).toFloat())
        innerRect.set(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
        if (!fullMode)
            canvas.drawRoundRect(bgRect, (height / 2).toFloat(), (height / 2).toFloat(), bgPaint)
        else
            canvas.drawRoundRect(bgRect, (innerH / 2).toFloat(), (innerH / 2).toFloat(), innerPaint)
        canvas.drawRoundRect(bgRect, (innerH / 2).toFloat(), (innerH / 2).toFloat(), touchOverlayPaint)
        super.onDraw(canvas)
    }


    fun setShapeColor(color: Int) {
        this.color = color
        setView()
        requestLayout()
    }

    fun setShapeColor(colorStr: String) {
        this.color = Color.parseColor(colorStr)
        setView()
        requestLayout()
    }

    fun setShapeGradientColor(startColor: Int, endColor: Int) {
        this.startColor = startColor
        this.endColor = endColor
        setView()
        requestLayout()
    }

    fun setShapeGradientColor(startColorStr: String, endColorStr: String) {
        this.startColor = Color.parseColor(startColorStr)
        this.endColor = Color.parseColor(endColorStr)
        setView()
        requestLayout()
    }

    fun setTextColorForceFully(color: Int) {
        this.isTextColorEnabled = true
        this.textColor = color
        setView()
        requestLayout()
    }

    var getFullMode: Boolean
        get() = this.fullMode
        set(fullMode) {
            this.fullMode = fullMode
            setView()
            requestLayout()
        }


    var isGradientEnabled: Boolean
        get() = gradientEnabled
        set(gradientEnabled) {
            this.gradientEnabled = gradientEnabled
            setView()
            requestLayout()
        }

    //    public void setConfiguration(CartaTagConfiguration configuration){
    //        setFullMode(configuration.isFullMode());
    //        setGradientEnabled(configuration.isGradient());
    //        setShapeColor(configuration.getThemeColor());
    //        if(configuration.isTextColorEnabled()) setTextColorForceFully(configuration.getTextColor());
    //        setShapeGradientColor(configuration.getGradientStartColor(), configuration.getGradientEndColor());
    //    }
}
package com.example.a4starter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.widget.ImageView
import java.lang.Math.*

@SuppressLint("AppCompatCustomView")
class CanvasView(context: Context?) : ImageView(context){
    val SAMPLE_SIZE = 130
    val STROKE_WIDTH = 20
    val sx = 100.0
    val sy = 100.0
    var path: Path? = null
    var paintbrush = Paint(Color.BLACK)
    var background = Color.rgb(52, 235, 174)
    var points: Array<PointF?> = arrayOfNulls(SAMPLE_SIZE)

    fun update(){
        invalidate()
    }

    fun reset(){
        path = null
        points = arrayOfNulls(SAMPLE_SIZE)
        invalidate()
    }
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path = Path()
                path!!.moveTo(x, y)
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                path!!.lineTo(x, y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                invalidate()
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paintbrush.strokeWidth = STROKE_WIDTH.toFloat()
        if(path != null){
            canvas.drawPath(path!!, paintbrush)
        }
    }

    fun resample(){
        var toGo = 0f
        var i = 0
        val pm = PathMeasure(path, false)
        val divideAmount = pm.length / SAMPLE_SIZE
        val convertedCoords = FloatArray(2)
        while (toGo < pm.length && i < SAMPLE_SIZE){
            pm.getPosTan(toGo, convertedCoords, null)
            points[i] = PointF(convertedCoords[0], convertedCoords[1])
            i++
            toGo += divideAmount
        }
    }

    fun transform(){
        resample()
        scale()
        rotate()
        translate()
    }

    private fun translate() {
        val centroid = calculateCentroid()
        val tx = -centroid.x
        val ty = -centroid.y
        val transformationMatrix = arrayOf(
            floatArrayOf(1.0F, 0.0F, tx), floatArrayOf(0.0F, 1.0F, ty), floatArrayOf(
                0.0F, 0.0F, 1.0F
            )
        )

        for (point in points) {
            if (point != null) {
                val pointMatrix = arrayOf(floatArrayOf(point.x), floatArrayOf(point.y), floatArrayOf(1.0F))
                val product = multiplyMatrices(transformationMatrix, pointMatrix, 3, 3, 1)

                point.x = product[0][0]
                point.y = product[1][0]
            }
        }
    }

    private fun scale(){
        val scaleMatrix = arrayOf(floatArrayOf(sx.toFloat(), 0.0F, 0.0F), floatArrayOf(0.0F, sy.toFloat(), 0.0F), floatArrayOf(0.0F, 0.0F, 1.0F))
        for (point in points) {
            if (point != null) {
                val pointMatrix = arrayOf(floatArrayOf(point.x), floatArrayOf(point.y), floatArrayOf(1.0F))
                val product = multiplyMatrices(scaleMatrix, pointMatrix, 3, 3, 1)

                point.x = product[0][0]
                point.y = product[1][0]
            }
        }
    }

    private fun rotate(){
        val centroid = calculateCentroid()
        var m1: Float = (points[0]!!.y - centroid.y)/(points[0]!!.x - centroid.x)
        val m2: Float = 0.0F
        val theta = atan(((m1-m2)/(1+m1*m2)).toDouble())
        val rotateMatrix = arrayOf(floatArrayOf(kotlin.math.cos(theta).toFloat(), -1.0F * kotlin.math.sin(theta).toFloat(), 0.0F),
                                    floatArrayOf(kotlin.math.sin(theta).toFloat(), kotlin.math.cos(theta).toFloat(), 0.0F),
                                    floatArrayOf(0.0F, 0.0F, 1.0F))

        for (point in points) {
            if (point != null) {
                val pointMatrix = arrayOf(floatArrayOf(point.x), floatArrayOf(point.y), floatArrayOf(1.0F))
                val product = multiplyMatrices( rotateMatrix, pointMatrix, 3, 3, 1)

                point.x = product[0][0]
                point.y = product[1][0]
            }
        }
    }

    private fun calculateCentroid(): PointF{
        var x = 0.0
        var y = 0.0
        var count = 0

        for(point in points){
            if(point != null){
                x += point.x
                y +=point.y
                ++ count
            }
        }
        return PointF((x/count).toFloat(), (y/count).toFloat())
    }

    fun multiplyMatrices(firstMatrix: Array <FloatArray>,
                         secondMatrix: Array <FloatArray>,
                         r1: Int,
                         c1: Int,
                         c2: Int): Array <FloatArray> {
        val product = Array(r1) { FloatArray(c2) }
        for (i in 0..r1 - 1) {
            for (j in 0..c2 - 1) {
                for (k in 0..c1 - 1) {
                    product[i][j] += firstMatrix[i][k] * secondMatrix[k][j]
                }
            }
        }

        return product
    }

    init {
        paintbrush.style = Paint.Style.STROKE
        paintbrush.strokeWidth = 5f
        this.setBackgroundColor(background)
    }
}
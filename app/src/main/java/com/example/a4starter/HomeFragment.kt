package com.example.a4starter

import android.app.AlertDialog
import android.graphics.Path
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlin.math.pow
import kotlin.math.sqrt

class HomeFragment : Fragment() {
    private var mViewModel: SharedViewModel? = null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle? ): View? {

        mViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        val image1 = ImageView(this.activity)
        val name1 = TextView(this.activity)
        val image2 = ImageView(this.activity)
        val name2 = TextView(this.activity)
        val image3 = ImageView(this.activity)
        val name3 = TextView(this.activity)

        val canvas = CanvasView(this.activity)
        canvas!!.setMinimumWidth(100)
        canvas!!.setMinimumHeight(400)

        val recButton = Button(this.activity)
        recButton.text = "Recognize"
        recButton.setOnClickListener(){
            if(canvas.path == null){
                AlertDialog.Builder(this.activity)
                    .setMessage("No gesture drawn!")
                    .setPositiveButton(
                        "Ok"
                    ) { dialogInterface, _ -> dialogInterface.dismiss() }
                    .show()
            }
            else {
                if (mViewModel!!.gestures.size == 0){
                    AlertDialog.Builder(this.activity)
                        .setMessage("No gesture in library!")
                        .setPositiveButton(
                            "Ok"
                        ) { dialogInterface, _ -> dialogInterface.dismiss() }
                        .show()
                } else {
                    canvas.transform()
                    val drawnPoints = canvas.points

                    val diffs = HashMap<Int, Double>()
                    for(gesture in  mViewModel!!.gestures){
                        Log.d("${gesture.name}", "${gesture.points[0]}")
                    }

                    for((i, gesture) in mViewModel!!.gestures.withIndex()){
                        var n = 0
                        var sum = 0.0
                        while(n < drawnPoints.size && n < gesture.points.size){
                            sum += sqrt((drawnPoints[n]!!.x - gesture.points[n]!!.x).pow(2) +
                                    (drawnPoints[n]!!.y - gesture.points[n]!!.y).pow(2))
                            ++n
                        }
                        Log.d("calculating", "$i ${sum/n}")
                        diffs[i] = sum/n
                    }
                    canvas.reset()

                    var first = 0
                    var second = 0
                    var third = 0

                    var min = diffs.minByOrNull{ it.value }
                    if(min != null){
                        first = min.key
                        image1.setImageBitmap( mViewModel!!.gestures[first].bitmap)
                        image1.getLayoutParams().height = 200
                        image1.requestLayout()
                        name1.text = "Name: ${mViewModel!!.gestures[first].name} Score: ${min.value}"
                        name1.setGravity(Gravity.CENTER)
                        Log.d("rec", "first is " + mViewModel!!.gestures[first].name)
                    }
                    diffs.remove(first)

                    min = diffs.minByOrNull{ it.value }
                    if(min != null){
                        second = min.key
                        image2.setImageBitmap( mViewModel!!.gestures[second].bitmap)
                        image2.getLayoutParams().height = 200
                        image2.requestLayout()
                        name2.text = "Name: ${mViewModel!!.gestures[second].name} Score: ${min.value}"
                        name2.setGravity(Gravity.CENTER)
                        Log.d("rec", "second is " + mViewModel!!.gestures[second].name)
                    }
                    diffs.remove(second)

                    min = diffs.minByOrNull{ it.value }
                    if(min != null) {
                        third = min.key
                        image3.setImageBitmap(mViewModel!!.gestures[third].bitmap)
                        image3.getLayoutParams().height = 200
                        image3.requestLayout()
                        name3.text =
                            "Name: ${mViewModel!!.gestures[third].name} Score: ${min.value}"
                        name3.setGravity(Gravity.CENTER)
                        Log.d("rec", "third is " + mViewModel!!.gestures[third].name)
                    }
                }
            }
        }

        val clearButton = Button(this.activity)
        clearButton.text = "Clear"
        clearButton.setOnClickListener(){
            canvas.path = null
            canvas.update()
        }

        val layout = root.findViewById<LinearLayout>(R.id.homeLayout)
        layout.addView(canvas)
        layout.isEnabled = true
        layout.addView(recButton)
        layout.addView(clearButton)
        layout.addView(image1)
        layout.addView(name1)
        layout.addView(image2)
        layout.addView(name2)
        layout.addView(image3)
        layout.addView(name3)

        return root
    }
}
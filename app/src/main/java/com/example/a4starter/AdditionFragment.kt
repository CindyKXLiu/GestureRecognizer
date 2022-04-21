package com.example.a4starter

import android.app.AlertDialog
import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import android.widget.EditText
import androidx.core.view.drawToBitmap


class AdditionFragment : Fragment() {
    private var mViewModel: SharedViewModel? = null
    val SAMPLE_SIZE = 130

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        mViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        val root: View = inflater.inflate(R.layout.fragment_addition, container, false)

        //mViewModel!!.desc.observe(viewLifecycleOwner, { s:String -> textView.text = "$s - Addition" })
        //mViewModel!!.strokeGestures.observe(viewLifecycleOwner, { s:ArrayList<Path> -> textView.text = "stroke count: ${s.size}"})

        val canvas = CanvasView(this.activity)
        canvas!!.setMinimumWidth(100)
        canvas!!.setMinimumHeight(1100)

        val addButton = Button(this.activity)
        addButton.text = "Add"
        addButton.setOnClickListener(){
            if(canvas.path == null){
                AlertDialog.Builder(this.activity)
                    .setMessage("No gesture drawn!")
                    .setPositiveButton(
                        "Ok"
                    ) { dialogInterface, _ -> dialogInterface.dismiss() }
                    .show()
            }
            else {
                val txt = EditText(this.activity)

                AlertDialog.Builder(this.activity)
                    .setMessage("Name:")
                    .setView(txt)
                    .setPositiveButton(
                        "Add"
                    ) { _, _ ->
                        if(inGesture(txt.text.toString())){
                            AlertDialog.Builder(this.activity)
                                .setMessage("${txt.text} already in library!")
                                .setPositiveButton(
                                    "Ok"
                                ) { dialogInterface, _ -> dialogInterface.dismiss() }
                                .show()
                        } else {
                            canvas.transform()
                            // make a copy of the points
                            val points: Array<PointF?> = arrayOfNulls(SAMPLE_SIZE)
                            for((i, point) in canvas.points.withIndex()){
                                points[i] = point
                            }
                            mViewModel!!.addStroke(Gesture(points, txt.text.toString(), canvas.drawToBitmap()))
                            canvas.reset()
                        }
                    }
                    .setNegativeButton(
                        "Cancel"
                    ) { dialog, _ -> dialog.dismiss()}
                    .show()
            }
        }

        val clearButton = Button(this.activity)
        clearButton.text = "Clear"
        clearButton.setOnClickListener(){
            canvas.path = null
            canvas.reset()
        }

        val layout = root.findViewById<LinearLayout>(R.id.additionLayout)
        layout.addView(canvas)
        layout.isEnabled = true
        layout.addView(addButton)
        layout.addView(clearButton)

        return root
    }

    fun inGesture(name: String): Boolean{
        for(gesture in mViewModel!!.gestures){
            if(name == gesture.name){
                return true
            }
        }
        return false
    }
}

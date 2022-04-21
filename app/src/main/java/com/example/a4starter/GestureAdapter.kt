package com.example.a4starter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

class GestureAdapter(context: Context?, gestures: ArrayList<Gesture>)
    : ArrayAdapter<Gesture>(context!!, 0, gestures!!){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Get the data item for this position
        var view: View? = convertView
        val gesture = getItem(position)

        // Check if an existing view is being reused, otherwise inflate the view
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.gesture_item, parent, false)
        }

        // Lookup view for data population
        val image = view?.findViewById(R.id.image) as ImageView
        val name = view?.findViewById(R.id.name) as TextView
        val delete = view?.findViewById(R.id.delete) as Button
        delete.setOnClickListener(){
            AlertDialog.Builder(context)
                .setMessage("Are you sure you want to delete this gesture?")
                .setPositiveButton(
                    "Yes"
                ) { dialogInterface, _ ->
                    remove(getItem(position))
                    dialogInterface.dismiss() }
                .setNegativeButton(
                    "No"
                ) { dialogInterface, _ -> dialogInterface.dismiss() }
                .show()
        }

        image.setImageBitmap(gesture!!.bitmap)
        name.text = gesture!!.name

        return view
    }
}
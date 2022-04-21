package com.example.a4starter

import android.graphics.Bitmap
import android.graphics.Path
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

class SharedViewModel : ViewModel() {
    val gestures: ArrayList<Gesture> = ArrayList<Gesture>()

    fun addStroke(gesture: Gesture) {
        gestures.add(gesture)
        for(gesture in gestures){
            Log.d("gesture add", "${gesture.name} -- ${gesture.points[0]}, ${gesture.points[1]}")
        }
    }
}
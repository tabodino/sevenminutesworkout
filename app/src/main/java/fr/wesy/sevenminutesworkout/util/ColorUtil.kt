package fr.wesy.sevenminutesworkout.util

import android.graphics.Color
import android.view.View

object ColorUtil {

    fun applyBackgroundColor(view: View, colorHex: String) {
        try {
            val colorInt = Color.parseColor(colorHex)
            view.setBackgroundColor(colorInt)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }
}
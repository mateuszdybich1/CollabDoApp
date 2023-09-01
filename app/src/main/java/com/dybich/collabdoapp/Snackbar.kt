package com.dybich.collabdoapp

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

class Snackbar(private val view: View, context: Context) {

    private val whiteColor = ContextCompat.getColor(context, R.color.white)
    private val blueColor = ContextCompat.getColor(context, R.color.secondaryBlue)

    fun show(text : String){
        Snackbar.make(view,text, Snackbar.LENGTH_LONG )
            .setBackgroundTint(whiteColor)
            .setTextColor(blueColor)
            .show()
    }

}
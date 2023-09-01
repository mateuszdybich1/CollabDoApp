package com.dybich.collabdoapp

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources.getColorStateList
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat

class ButtonTransition(
    private val layout : ConstraintLayout,
    private val progressBar: ProgressBar,
    private val button : Button,
    context : Context
) {

    private val fadeIN = AnimationUtils.loadAnimation(context, com.google.android.material.R.anim.abc_fade_in)
    private val fadeOUT = AnimationUtils.loadAnimation(context, com.google.android.material.R.anim.abc_fade_out)

    private val transparentColor = ContextCompat.getColor(context, R.color.color_transparent_100)
    private val whiteColor = ContextCompat.getColor(context, R.color.secondaryBlue)


    fun startLoading(){

        disableChildrenInputs(layout)


        button.startAnimation(fadeOUT)

        button.setTextColor(transparentColor)

        progressBar.startAnimation(fadeIN)
        progressBar.visibility = View.VISIBLE
    }

    fun stopLoading(){

        progressBar.visibility = View.GONE
        progressBar.startAnimation(fadeOUT)


        button.setTextColor(whiteColor)

        button.startAnimation(fadeIN)

        enableChildrenInputs(layout)
    }

    private fun disableChildrenInputs(parent: ViewGroup) {
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            if (child is ViewGroup) {
                disableChildrenInputs(child)
            } else if (child is EditText || child is Button || child is TextView) {
                child.isEnabled = false
            }
        }
    }

    private fun enableChildrenInputs(parent: ViewGroup) {
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            if (child is ViewGroup) {
                enableChildrenInputs(child)
            } else if (child is EditText || child is Button || child is TextView) {
                child.isEnabled = true
            }
        }
    }
}
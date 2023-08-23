package com.dybich.collabdoapp.login

import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout

object ClearErrors {
     fun clearErrors(layouts: List<ErrorObj>) {
        layouts.forEach { layout ->
            layout.editText.addTextChangedListener{
                layout.layout.error = null
            }

        }
    }
}
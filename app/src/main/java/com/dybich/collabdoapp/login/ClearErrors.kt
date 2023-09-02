package com.dybich.collabdoapp.login

import androidx.core.widget.addTextChangedListener

object ClearErrors {
     fun clearErrors(layouts: List<ErrorObj>) {
        layouts.forEach { layout ->
            layout.editText.addTextChangedListener{
                layout.layout.error = null
            }

        }
    }
}
package com.romandevyatov.bestfinance.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.romandevyatov.bestfinance.R

class WindowUtil {
    companion object {

        fun showExistingDialog(context: Context, message: String?) {
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_info)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val tvMessage: TextView = dialog.findViewById(R.id.tvMessage)
            val btnOk: Button = dialog.findViewById(R.id.btnOk)

            tvMessage.text = message

            btnOk.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }

    }
}
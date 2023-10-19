package com.romandevyatov.bestfinance.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.DialogInfoBinding

class WindowUtil {
    companion object {

        fun showExistingDialog(context: Context, message: String?) {
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)

            val binding = DialogInfoBinding.inflate(LayoutInflater.from(context))
            dialog.setContentView(binding.root)

            binding.tvMessage.text = message

            binding.btnOk.setOnClickListener {
                dialog.dismiss()
            }

            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialog.show()
        }
    }
}

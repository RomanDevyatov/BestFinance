package com.romandevyatov.bestfinance.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import androidx.lifecycle.ViewModel
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.DialogAlertBinding
import com.romandevyatov.bestfinance.databinding.DialogInfoBinding
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.UpdateExpenseHistoryViewModel
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.UpdateIncomeHistoryViewModel
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.UpdateTransferHistoryViewModel

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

        fun showDeleteDialog(
            context: Context,
            viewModel: ViewModel,
            itemId: Long,
            navigateFunction: () -> Unit
        ) {
            val message = context.getString(R.string.delete_confirmation_warning_message)

            val binding = DialogAlertBinding.inflate(LayoutInflater.from(context))
            val dialog = Dialog(context)

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(binding.root)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            binding.tvMessage.text = message

            binding.btnYes.setOnClickListener {
                dialog.dismiss()

                when (viewModel) {
                    is UpdateExpenseHistoryViewModel -> {
                        viewModel.deleteItem(itemId)
                    }
                    is UpdateTransferHistoryViewModel -> {
                        viewModel.deleteItem(itemId)
                    }
                    is UpdateIncomeHistoryViewModel -> {
                        viewModel.deleteItem(itemId)
                    }
                }

                navigateFunction()
            }

            binding.btnNo.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }

        fun showUnarchiveDialog(
            context: Context,
            message: String?,
            onUnarchiveAction: () -> Unit
        ) {
            val binding = DialogAlertBinding.inflate(LayoutInflater.from(context))
            val dialog = Dialog(context)

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(binding.root)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            binding.tvMessage.text = message

            binding.btnYes.setOnClickListener {
                dialog.dismiss()
                onUnarchiveAction()
            }

            binding.btnNo.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }
    }
}

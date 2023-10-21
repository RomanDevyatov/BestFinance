package com.romandevyatov.bestfinance.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.Snackbar
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.DialogAlertBinding
import com.romandevyatov.bestfinance.databinding.DialogInfoBinding
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.*

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

        fun showDeleteDialog(
            context: Context,
            viewModel: ViewModel,
            itemId: Long,
            rootView: View? = null,
            groupOrSubGroup: Boolean? = null,
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
                    is IncomeGroupsAndSubGroupsViewModel -> {
                        if (groupOrSubGroup != null) {
                            when (groupOrSubGroup) {
                                true -> {
                                    viewModel.deleteItem(itemId)
                                }
                                false -> {
                                    viewModel.deleteSubItem(itemId)
                                }
                            }

                        }
                    }
                    is ExpenseGroupsAndSubGroupsViewModel -> {
                        if (groupOrSubGroup != null) {
                            when (groupOrSubGroup) {
                                true -> {
                                    viewModel.deleteItem(itemId)
                                }
                                false -> {
                                    viewModel.deleteSubItem(itemId)
                                }
                            }

                        }
                    }
                    is SettingsWalletsViewModel -> {
                        viewModel.deleteItem(itemId)
                    }
                }

                if (rootView != null) {
                    showUndoSnackbar(
                        view = rootView,
                        message = message,
                        actionText = context.getString(R.string.undo),
                        action = {
                            when (viewModel) {
                                is UpdateExpenseHistoryViewModel -> viewModel.undoDeleteItem()
                                is UpdateTransferHistoryViewModel -> viewModel.undoDeleteItem()
                                is UpdateIncomeHistoryViewModel -> viewModel.undoDeleteItem()
                                is SettingsWalletsViewModel -> viewModel.undoDeleteItem()
                                is IncomeGroupsAndSubGroupsViewModel -> {
                                    if (groupOrSubGroup != null) {
                                        when (groupOrSubGroup) {
                                            true -> {
                                                viewModel.undoDeleteItem()
                                            }
                                            false -> {
                                                viewModel.undoDeleteSubItem()
                                            }
                                        }
                                    }
                                }
                                is ExpenseGroupsAndSubGroupsViewModel -> {
                                    if (groupOrSubGroup != null) {
                                        when (groupOrSubGroup) {
                                            true -> {
                                                viewModel.undoDeleteItem()
                                            }
                                            false -> {
                                                viewModel.undoDeleteSubItem()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    )
                }
                
                navigateFunction()
            }

            binding.btnNo.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }

        private fun showUndoSnackbar(view: View, message: String, actionText: String, action: () -> Unit) {
            val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            snackbar.setAction(actionText) {
                action.invoke()
            }
            snackbar.show()

//            val context = view.context
//            val customSnackbarView = LayoutInflater.from(context).inflate(R.layout.custom_snackbar, null)
//            val snackbar = Snackbar.make(view, "", Snackbar.LENGTH_INDEFINITE)
//
//            val timer = object : CountDownTimer(3000, 1000) {
//                override fun onTick(millisUntilFinished: Long) {
//                    val secondsLeft = (millisUntilFinished / 1000).toInt()
//                    val countdownText = customSnackbarView.findViewById<TextView>(R.id.countdown_text)
//                    countdownText.text = secondsLeft.toString()
//                }
//
//                override fun onFinish() {
//                    // Countdown finished, display "done" message
//                    val countdownText = customSnackbarView.findViewById<TextView>(R.id.countdown_text)
//                    countdownText.text = "Done"
//                }
//            }
//            timer.start()
//
//            val message = customSnackbarView.findViewById<TextView>(R.id.snackbar_text)
//            message.text = "Countdown:"
//
//            val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout
//            snackbarLayout.addView(customSnackbarView, 0)
//
//            snackbar.show()
        }
    }
}

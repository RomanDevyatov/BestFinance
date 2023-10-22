package com.romandevyatov.bestfinance.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.Snackbar
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.CustomSnackbarBinding
import com.romandevyatov.bestfinance.databinding.DialogAlertBinding
import com.romandevyatov.bestfinance.databinding.DialogInfoBinding
import com.romandevyatov.bestfinance.utils.Constants.UNDO_DELAY
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.UpdateExpenseHistoryViewModel
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.UpdateIncomeHistoryViewModel
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.UpdateWalletViewModel
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.UpdateTransferHistoryViewModel
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.ExpenseGroupsAndSubGroupsViewModel
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.IncomeGroupsAndSubGroupsViewModel
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.SettingsWalletsViewModel

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
            message: String,
            itemId: Long,
            isCountdown: Boolean = false,
            rootView: View? = null,
            groupOrSubGroup: Boolean? = null,
            navigateFunction: () -> Unit
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
                    is UpdateWalletViewModel -> {
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
                    if (isCountdown) {
                        showUndoCountdownSnackbar(
                            view = rootView,
                            message = context.getString(R.string.until_delete),
                            actionText = context.getString(R.string.undo),
                            action = {
                                when (viewModel) {
                                    is SettingsWalletsViewModel -> viewModel.undoDeleteItem()
                                    is UpdateWalletViewModel -> viewModel.undoDeleteItem()
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
                    } else {
                        showUndoSnackbar(
                            view = rootView,
                            message = context.getString(R.string.deleted),
                            actionText = context.getString(R.string.undo),
                            action = {
                                when (viewModel) {
                                    is UpdateExpenseHistoryViewModel -> viewModel.undoDeleteItem()
                                    is UpdateTransferHistoryViewModel -> viewModel.undoDeleteItem()
                                    is UpdateIncomeHistoryViewModel -> viewModel.undoDeleteItem()
                                }
                            }
                        )
                    }
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
        }

        private fun showUndoCountdownSnackbar(view: View, message: String, actionText: String, action: () -> Unit) {
            val customSnackbar = CustomSnackbar(view.context, view)
            customSnackbar.setText(message)
            customSnackbar.setCountdownMilSec(UNDO_DELAY)
            customSnackbar.setAction(actionText, action)
            customSnackbar.show()
        }

        class CustomSnackbar(context: Context, rootView: View, ) {
            private val snackbarView = LayoutInflater.from(context).inflate(R.layout.custom_snackbar, null)
            private val countdownText = snackbarView.findViewById<TextView>(R.id.countdown_text)
            private val snackbarText = snackbarView.findViewById<TextView>(R.id.snackbar_text)

            private val snackbar = Snackbar.make(rootView, "", Snackbar.LENGTH_INDEFINITE)
            private var timer: CountDownTimer? = null

            fun show() {
                // To remove the default Snackbar background
                val layout = snackbar.view as Snackbar.SnackbarLayout
                layout.setBackgroundColor(Color.GRAY)

                // Set the custom view for the Snackbar
                val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout
                snackbarLayout.removeAllViews()
                snackbarLayout.addView(snackbarView, 0)

                snackbar.show()
            }

            fun setAction(actionText: String, action: () -> Unit) {
                val actionButton = snackbarView.findViewById<Button>(R.id.customSnackbarAction)
                actionButton.text = actionText
                actionButton.setOnClickListener {
                    action.invoke()
                    dismiss()
                }
            }

            fun setText(message: String) {
                snackbarText.text = message
            }

            fun setCountdownMilSec(milliseconds: Long) {
                timer = object : CountDownTimer(milliseconds, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        val secondsLeft = (millisUntilFinished / 1000).toInt()
                        countdownText.text = secondsLeft.toString()
                    }

                    override fun onFinish() {
                        countdownText.text = "Done"
                    }
                }

                timer?.start()
            }

            fun dismiss() {
                timer?.cancel()
                snackbar.dismiss()
            }
        }


    }
}

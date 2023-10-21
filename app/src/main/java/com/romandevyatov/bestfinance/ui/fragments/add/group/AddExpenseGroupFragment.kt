package com.romandevyatov.bestfinance.ui.fragments.add.group

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.data.entities.ExpenseGroup
import com.romandevyatov.bestfinance.data.validation.EmptyValidator
import com.romandevyatov.bestfinance.databinding.DialogAlertBinding
import com.romandevyatov.bestfinance.databinding.FragmentAddExpenseGroupBinding
import com.romandevyatov.bestfinance.utils.Constants
import com.romandevyatov.bestfinance.utils.WindowUtil
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.AddExpenseGroupViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddExpenseGroupFragment : Fragment() {

    private var _binding: FragmentAddExpenseGroupBinding? = null
    private val binding get() = _binding!!

    private val addGroupViewModel: AddExpenseGroupViewModel by viewModels()

    private var isButtonClickable = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddExpenseGroupBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callback = object : OnBackPressedCallback(
            true
        ) {
            override fun handleOnBackPressed() {
                val action =
                    AddExpenseGroupFragmentDirections.actionNavigationAddExpenseGroupToNavigationAddExpense()
                action.expenseGroupName = null
                findNavController().navigate(action)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        binding.addNewExpenseGroupNameButton.setOnClickListener {
            handleButtonClick(view)
        }
    }

    private fun handleButtonClick(view: View) {
        if (!isButtonClickable) return
        isButtonClickable = false
        view.isEnabled = false

        val groupNameBinding = binding.newExpenseGroupName.text.toString().trim()
        val descriptionBinding = binding.descriptionEditText.text.toString().trim()

        val nameEmptyValidation = EmptyValidator(groupNameBinding).validate()
        binding.newExpenseGroupNameLayout.error = if (!nameEmptyValidation.isSuccess) getString(nameEmptyValidation.message) else null

        if (nameEmptyValidation.isSuccess) {
            addGroupViewModel.getExpenseGroupByNameLiveData(groupNameBinding)?.observe(viewLifecycleOwner) { expenseGroup ->
                if (expenseGroup == null) {
                    addGroupViewModel.insertExpenseGroup(
                        ExpenseGroup(
                            name = groupNameBinding,
                            description = descriptionBinding
                        )
                    )

                    val action =
                        AddExpenseGroupFragmentDirections.actionNavigationAddExpenseGroupToNavigationAddExpense()
                    action.expenseGroupName = groupNameBinding
                    findNavController().navigate(action)
                } else if (expenseGroup.archivedDate == null) {
                    WindowUtil.showExistingDialog(
                        requireContext(),
                        getString(R.string.group_is_already_existing, groupNameBinding)
                    )
                } else {
                    showWalletDialog(
                        requireContext(),
                        expenseGroup,
                        getString(R.string.group_is_archived, groupNameBinding, groupNameBinding)
                    )
                }
            }
        }

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            isButtonClickable = true
            view.isEnabled = true
        }, Constants.CLICK_DELAY_MS.toLong())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showWalletDialog(context: Context, group: ExpenseGroup, message: String?) {
        val binding = DialogAlertBinding.inflate(LayoutInflater.from(context))
        val dialog = Dialog(context)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.tvMessage.text = message

        binding.btnYes.setOnClickListener {
            addGroupViewModel.unarchiveExpenseGroup(group)
            dialog.dismiss()
            val action = AddExpenseGroupFragmentDirections.actionNavigationAddExpenseGroupToNavigationAddExpense()
            action.expenseGroupName = group.name
            findNavController().navigate(action)
        }

        binding.btnNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

}

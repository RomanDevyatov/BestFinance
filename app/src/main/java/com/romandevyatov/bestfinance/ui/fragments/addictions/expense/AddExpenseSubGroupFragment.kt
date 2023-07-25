package com.romandevyatov.bestfinance.ui.fragments.addictions.expense

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.FragmentAddExpenseSubGroupBinding
import com.romandevyatov.bestfinance.db.entities.ExpenseSubGroup
import com.romandevyatov.bestfinance.ui.validators.EmptyValidator
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.AddExpenseSubGroupViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddExpenseSubGroupFragment : Fragment() {

    private var _binding: FragmentAddExpenseSubGroupBinding? = null
    private val binding get() = _binding!!

    private val addSubGroupViewModel: AddExpenseSubGroupViewModel by viewModels()

    private val args: AddExpenseSubGroupFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddExpenseSubGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initGroupSpinner()

        binding.addNewExpenseSubGroupNameButton.setOnClickListener {

            val expenseSubGroupNameBinding = binding.subGroupNameEditText.text.toString()
            val expenseSubGroupDescriptionBinding = binding.subGroupDescriptionEditText.text.toString()
            val selectedExpenseGroupNameBinding = binding.groupSpinner.text.toString()

            val expenseSubGroupNameValidation = EmptyValidator(expenseSubGroupNameBinding).validate()
            binding.subGroupNameTextInputLayout.error = if (!expenseSubGroupNameValidation.isSuccess) getString(expenseSubGroupNameValidation.message) else null

            val expenseGroupMaterialSpinnerValidation = EmptyValidator(selectedExpenseGroupNameBinding).validate()
            binding.groupSpinnerLayout.error = if (!expenseGroupMaterialSpinnerValidation.isSuccess) getString(expenseGroupMaterialSpinnerValidation.message) else null

            if (expenseSubGroupNameValidation.isSuccess
                && expenseGroupMaterialSpinnerValidation.isSuccess
            ) {
                addSubGroupViewModel.getExpenseGroupNotArchivedByNameLiveData(
                    selectedExpenseGroupNameBinding
                ).observe(viewLifecycleOwner) {
                    val expenseGroupId = it.id!!

                    addSubGroupViewModel.getExpenseSubGroupByNameLiveData(
                        expenseSubGroupNameBinding
                    ).observe(viewLifecycleOwner) { expenseSubGroup ->

                        val action =
                            AddExpenseSubGroupFragmentDirections.actionNavigationAddExpenseSubGroupToNavigationAddExpense()
                        action.expenseGroupName = selectedExpenseGroupNameBinding
                        action.expenseSubGroupName = expenseSubGroupNameBinding

                        if (expenseSubGroup?.archivedDate != null) {
                            showWalletDialog(
                                requireContext(),
                                expenseSubGroup,
                                "Do you want to unarchive `${expenseSubGroupNameBinding}` expense sub group?")
                        } else {
                            val newExpenseSubGroup = ExpenseSubGroup(
                                name = expenseSubGroupNameBinding,
                                description = expenseSubGroupDescriptionBinding,
                                expenseGroupId = expenseGroupId
                            )

                            addSubGroupViewModel.insertExpenseSubGroup(newExpenseSubGroup)
                        }

                        findNavController().navigate(action)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initGroupSpinner() {
        addSubGroupViewModel.allExpenseGroupsNotArchivedLiveData.observe(viewLifecycleOwner) { expenseGroupList ->
            val spinnerItems = getExpenseGroupList(expenseGroupList)

            val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, spinnerItems)

            binding.groupSpinner.setAdapter(spinnerAdapter)

            if (args.expenseGroupName?.isNotBlank() == true) {
                binding.groupSpinner.setText(args.expenseGroupName.toString(), false) //.setSelection(spinnerPosition)
            }
        }
    }

    private fun getExpenseGroupList(expenseGroupList: List<ExpenseSubGroup>): ArrayList<String> {
        val spinnerItems = ArrayList<String>()

        expenseGroupList.forEach {
            spinnerItems.add(it.name)
        }

        return spinnerItems
    }

    private fun showWalletDialog(
        context: Context,
        expenseSubGroup: ExpenseSubGroup,
        message: String
    ) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_alert)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val tvMessage: TextView = dialog.findViewById(R.id.tvMessage)
        val btnYes: Button = dialog.findViewById(R.id.btnYes)
        val bntNo: Button = dialog.findViewById(R.id.btnNo)

        tvMessage.text = message

        btnYes.setOnClickListener {
            addSubGroupViewModel.unarchiveExpenseSubGroup(expenseSubGroup)
            dialog.dismiss()
        }

        bntNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

}

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

        binding.addSubGroupNameButton.setOnClickListener {

            val subGroupNameBinding = binding.subGroupNameEditText.text.toString()
            val descriptionBinding = binding.subGroupDescriptionEditText.text.toString()
            val selectedGroupNameBinding = binding.groupSpinner.text.toString()

            val subGroupNameValidation = EmptyValidator(subGroupNameBinding).validate()
            binding.subGroupNameTextInputLayout.error = if (!subGroupNameValidation.isSuccess) getString(subGroupNameValidation.message) else null

            val groupMaterialSpinnerValidation = EmptyValidator(selectedGroupNameBinding).validate()
            binding.groupSpinnerLayout.error = if (!groupMaterialSpinnerValidation.isSuccess) getString(groupMaterialSpinnerValidation.message) else null

            if (subGroupNameValidation.isSuccess
                && groupMaterialSpinnerValidation.isSuccess
            ) {
                addSubGroupViewModel.getExpenseGroupNotArchivedByNameLiveData(
                    selectedGroupNameBinding
                ).observe(viewLifecycleOwner) {
                    val groupId = it.id!!

                    addSubGroupViewModel.getExpenseSubGroupByNameLiveData(
                        subGroupNameBinding
                    ).observe(viewLifecycleOwner) { subGroup ->

                        val action =
                            AddExpenseSubGroupFragmentDirections.actionNavigationAddExpenseSubGroupToNavigationAddExpense()
                        action.expenseGroupName = selectedGroupNameBinding
                        action.expenseSubGroupName = subGroupNameBinding

                        if (subGroup?.archivedDate != null) {
                            showWalletDialog(
                                requireContext(),
                                subGroup,
                                "Do you want to unarchive `${subGroupNameBinding}` expense sub group?")
                        } else {
                            val newExpenseSubGroup = ExpenseSubGroup(
                                name = subGroupNameBinding,
                                description = descriptionBinding,
                                expenseGroupId = groupId
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

    private fun getExpenseGroupList(groups: List<ExpenseSubGroup>): ArrayList<String> {
        val spinnerItems = ArrayList<String>()

        groups.forEach {
            spinnerItems.add(it.name)
        }

        return spinnerItems
    }

    private fun showWalletDialog(
        context: Context,
        subGroup: ExpenseSubGroup,
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
            addSubGroupViewModel.unarchiveExpenseSubGroup(subGroup)
            dialog.dismiss()
        }

        bntNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

}

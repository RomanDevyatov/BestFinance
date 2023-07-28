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
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.FragmentAddExpenseGroupBinding
import com.romandevyatov.bestfinance.db.entities.ExpenseGroup
import com.romandevyatov.bestfinance.db.entities.IncomeGroup
import com.romandevyatov.bestfinance.ui.fragments.addictions.income.AddIncomeGroupFragmentDirections
import com.romandevyatov.bestfinance.ui.validators.EmptyValidator
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.AddExpenseGroupViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddExpenseGroupFragment : Fragment() {

    private var _binding: FragmentAddExpenseGroupBinding? = null
    private val binding get() = _binding!!

    private val addGroupViewModel: AddExpenseGroupViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)

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
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

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

        binding.addNewExpenseGroupNameButton.setOnClickListener {
            val groupNameBinding = binding.newExpenseGroupName.text.toString().trim()
            val descriptionBinding = binding.descriptionEditText.text.toString().trim()

            val nameEmptyValidation = EmptyValidator(groupNameBinding).validate()
            binding.newExpenseGroupNameLayout.error = if (!nameEmptyValidation.isSuccess) getString(nameEmptyValidation.message) else null

            if (nameEmptyValidation.isSuccess) {
                addGroupViewModel.getExpenseGroupByNameLiveData(groupNameBinding).observe(viewLifecycleOwner) { expenseGroup ->
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
                        showExistingDialog(
                            requireContext(),
                            "This group `$groupNameBinding` is already existing."
                        )
                    } else {
                        showWalletDialog(
                            requireContext(),
                            expenseGroup,
                            "The group with this name is archived. Do you want to unarchive `$groupNameBinding` expense group?")
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showWalletDialog(context: Context, group: ExpenseGroup, message: String?) {
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
            addGroupViewModel.unarchiveExpenseGroup(group)
            dialog.dismiss()
            val action =
                AddExpenseGroupFragmentDirections.actionNavigationAddExpenseGroupToNavigationAddExpense()
            action.expenseGroupName = group.name
            findNavController().navigate(action)
        }

        bntNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showExistingDialog(context: Context, message: String?) {
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

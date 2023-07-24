package com.romandevyatov.bestfinance.ui.fragments.addictions.income

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.FragmentAddIncomeGroupBinding
import com.romandevyatov.bestfinance.db.entities.IncomeGroup
import com.romandevyatov.bestfinance.ui.validators.EmptyValidator
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.AddIncomeGroupViewModel
import com.romandevyatov.bestfinance.viewmodels.shared.SharedModifiedViewModel
import com.romandevyatov.bestfinance.viewmodels.shared.models.AddTransactionForm
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddIncomeGroupFragment : Fragment() {

    private var _binding: FragmentAddIncomeGroupBinding? = null
    private val binding get() = _binding!!

    private val incomeGroupViewModel: AddIncomeGroupViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val callback = object : OnBackPressedCallback(
            true
        ) {
            override fun handleOnBackPressed() {
                val action =
                    AddIncomeGroupFragmentDirections.actionNavigationAddNewIncomeGroupToNavigationAddIncome()
                action.incomeGroupName = null
                findNavController().navigate(action)
//                findNavController().navigate(R.id.action_navigation_add_new_income_group_to_navigation_add_income)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddIncomeGroupBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addNewGroupButton.setOnClickListener {
            val newIncomeGroupName = binding.groupNameInputEditText.text.toString()
            val newIncomeGroupDescriptionInput = binding.groupDescriptionInputEditText.text.toString()
            val newIncomeGroupIsPassive = binding.isPassiveCheckBox.isChecked


            val nameEmptyValidation = EmptyValidator(newIncomeGroupName).validate()
            binding.groupNameInputLayout.error = if (!nameEmptyValidation.isSuccess) getString(nameEmptyValidation.message) else null

            if (nameEmptyValidation.isSuccess) {
                incomeGroupViewModel.insertIncomeGroup(
                    IncomeGroup(
                        name = newIncomeGroupName,
                        description = newIncomeGroupDescriptionInput,
                        isPassive = newIncomeGroupIsPassive
                    )
                )

                val action =
                    AddIncomeGroupFragmentDirections.actionNavigationAddNewIncomeGroupToNavigationAddIncome()
                action.incomeGroupName = newIncomeGroupName
                findNavController().navigate(action)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

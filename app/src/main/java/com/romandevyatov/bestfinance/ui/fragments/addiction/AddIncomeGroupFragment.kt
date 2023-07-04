package com.romandevyatov.bestfinance.ui.fragments.addiction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.romandevyatov.bestfinance.databinding.FragmentAddIncomeGroupBinding
import com.romandevyatov.bestfinance.db.entities.IncomeGroup
import com.romandevyatov.bestfinance.ui.validators.EmptyValidator
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.IncomeGroupViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddIncomeGroupFragment : Fragment() {

    private var _binding: FragmentAddIncomeGroupBinding? = null
    private val binding get() = _binding!!

    private val incomeGroupViewModel: IncomeGroupViewModel by viewModels()


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

        binding.addNewIncomeGroupNameButton.setOnClickListener {
            val newIncomeGroupName = binding.newIncomeGroupNameInputEditText.text.toString()
            val newIncomeGroupDescriptionInput = binding.newIncomeGroupDescriptionInputEditText.text.toString()

            val newIncomeGroupNameEmptyValidation = EmptyValidator(newIncomeGroupName).validate()
            binding.newIncomeGroupNameInputLayout.error = if (!newIncomeGroupNameEmptyValidation.isSuccess) getString(newIncomeGroupNameEmptyValidation.message) else null

            val newIncomeGroupDescriptionEmptyValidation = EmptyValidator(newIncomeGroupDescriptionInput).validate()
            binding.newIncomeGroupNameInputLayout.error = if (!newIncomeGroupDescriptionEmptyValidation.isSuccess) getString(newIncomeGroupDescriptionEmptyValidation.message) else null

            incomeGroupViewModel.insertIncomeGroup(
                IncomeGroup(
                    name = newIncomeGroupName,
                    description = newIncomeGroupDescriptionInput
                )
            )

            val action = AddIncomeGroupFragmentDirections.actionNavigationAddNewIncomeGroupToNavigationAddIncome()
            action.incomeGroupName = newIncomeGroupName
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
package com.romandevyatov.bestfinance.ui.fragments.addictions.income

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.romandevyatov.bestfinance.databinding.FragmentAddIncomeSubGroupBinding
import com.romandevyatov.bestfinance.db.entities.IncomeGroup
import com.romandevyatov.bestfinance.db.entities.IncomeSubGroup
import com.romandevyatov.bestfinance.ui.validators.EmptyValidator
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.AddIncomeSubGroupViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddIncomeSubGroupFragment : Fragment() {

    private var _binding: FragmentAddIncomeSubGroupBinding? = null
    private val binding get() = _binding!!

    private val addIncomeSubGroupViewModel: AddIncomeSubGroupViewModel by viewModels()

    private val args: AddIncomeSubGroupFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddIncomeSubGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initIncomeGroupSpinner()

        binding.addNewIncomeSubGroupNameButton.setOnClickListener {

            val incomeSubGroupNameBinding = binding.incomeSubGroupName.text.toString()
            val descriptionBinding = binding.incomeSubGroupDescription.text.toString()
            val selectedIncomeGroupName = binding.incomeGroupSpinnerAutoCompleteTextView.text.toString()

            val incomeSubGroupNameValidation = EmptyValidator(incomeSubGroupNameBinding).validate()
            binding.incomeSubGroupNameTextInputLayout.error = if (!incomeSubGroupNameValidation.isSuccess) getString(incomeSubGroupNameValidation.message) else null

            val incomeGroupSpinnerValidation = EmptyValidator(selectedIncomeGroupName).validate()
            binding.groupSpinnerTextInputLayout.error = if (!incomeGroupSpinnerValidation.isSuccess) getString(incomeGroupSpinnerValidation.message) else null

            if (incomeSubGroupNameValidation.isSuccess
                && incomeGroupSpinnerValidation.isSuccess
            ) {
                addIncomeSubGroupViewModel.getIncomeGroupByNameAndNotArchivedLiveData(
                    selectedIncomeGroupName
                ).observe(viewLifecycleOwner) {
                    val incomeGroupId = it.id!!

                    val newIncomeSubGroup = IncomeSubGroup(
                        name = incomeSubGroupNameBinding,
                        description = descriptionBinding,
                        incomeGroupId = incomeGroupId
                    )

                    addIncomeSubGroupViewModel.insertIncomeSubGroup(newIncomeSubGroup)

                    val action =
                        AddIncomeSubGroupFragmentDirections.actionNavigationAddIncomeSubGroupToNavigationAddIncome()
                    action.incomeGroupName = selectedIncomeGroupName
                    action.incomeSubGroupName = incomeSubGroupNameBinding
                    findNavController().navigate(action)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initIncomeGroupSpinner() {
        addIncomeSubGroupViewModel.getAllIncomeGroupNotArchivedLiveData().observe(viewLifecycleOwner) { incomeGroupList ->
            val spinnerItems = getIncomeGroupList(incomeGroupList)

            val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, spinnerItems)

            binding.incomeGroupSpinnerAutoCompleteTextView.setAdapter(spinnerAdapter)

            if (args.incomeGroupName != null && args.incomeGroupName!!.isNotBlank()) {
                binding.incomeGroupSpinnerAutoCompleteTextView.setText(args.incomeGroupName.toString(), false)
            }
        }
    }

    private fun getIncomeGroupList(incomeGroupList: List<IncomeGroup>?): ArrayList<String> {
        val spinnerItems = ArrayList<String>()

        incomeGroupList?.forEach { it ->
            spinnerItems.add(it.name)
        }

        return spinnerItems
    }

}

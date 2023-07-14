package com.romandevyatov.bestfinance.ui.fragments.addictions.income

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.R
import com.romandevyatov.bestfinance.databinding.FragmentAddIncomeSubGroupBinding
import com.romandevyatov.bestfinance.db.entities.IncomeSubGroup
import com.romandevyatov.bestfinance.ui.adapters.spinnerutils.SpinnerUtils
import com.romandevyatov.bestfinance.utils.Constants
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.IncomeGroupViewModel
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.IncomeSubGroupViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddIncomeSubGroupFragment : Fragment() {

    private var _binding: FragmentAddIncomeSubGroupBinding? = null
    private val binding get() = _binding!!

    private val incomeSubGroupViewModel: IncomeSubGroupViewModel by viewModels()
    private val incomeGroupViewModel: IncomeGroupViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddIncomeSubGroupBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initIncomeGroupSpinner()

        incomeSubGroupFocusListener()

        binding.addNewIncomeSubGroupNameButton.setOnClickListener {
            val selectedIncomeGroupName = binding.toIncomeGroupSpinner.selectedItem.toString()

            val incomeSubGroupNameBinding = binding.newIncomeSubGroupName.text.toString()

            incomeGroupViewModel.getIncomeGroupByNameAndNotArchivedLiveData(selectedIncomeGroupName).observe(viewLifecycleOwner) {
                val incomeGroupId = it.id!!

                val descriptionBinding = binding.newIncomeSubGroupDescription.text.toString()

                val newIncomeSubGroup = IncomeSubGroup(
                    name = incomeSubGroupNameBinding,
                    description = descriptionBinding,
                    incomeGroupId = incomeGroupId
                )

                incomeSubGroupViewModel.insertIncomeSubGroup(newIncomeSubGroup)

                val action = AddIncomeSubGroupFragmentDirections.actionNavigationAddNewIncomeSubGroupToNavigationAddIncome()
                action.incomeGroupName = selectedIncomeGroupName
                action.incomeSubGroupName = incomeSubGroupNameBinding
                findNavController().navigate(action)
            }
        }
    }

    private fun initIncomeGroupSpinner() {
        val spinnerAdapter = SpinnerUtils.getArraySpinner(requireContext())

        incomeGroupViewModel.getAllIncomeGroupNotArchivedLiveData().observe(viewLifecycleOwner) { incomeGroupList ->
            spinnerAdapter.clear()
            spinnerAdapter.add(Constants.INCOME_GROUP)
            incomeGroupList?.forEach { it ->
                spinnerAdapter.add(it.name)
            }

            if (args.incomeGroupName != null && args.incomeGroupName!!.isNotBlank()) {
                val spinnerPosition = spinnerAdapter.getPosition(args.incomeGroupName.toString())
                binding.toIncomeGroupSpinner.setSelection(spinnerPosition)
            }
        }

        val incomeGroupSpinner = binding.toIncomeGroupSpinner
        incomeGroupSpinner.adapter = spinnerAdapter
    }

    private fun submitNewIncomeSubGroup(selectedIncomeGroupName: String) {
        incomeGroupViewModel.getIncomeGroupNameByNameLiveData(selectedIncomeGroupName).observe(viewLifecycleOwner) {
            incomeSubGroupViewModel.insertIncomeSubGroup(
                IncomeSubGroup(
                    name = binding.newIncomeSubGroupName.text.toString(),
                    description = binding.newIncomeSubGroupDescription.text.toString(),
                    incomeGroupId = it.id!!
                )
            )
        }
    }

    private fun incomeSubGroupFocusListener() {
        binding.newIncomeSubGroupName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                binding.newIncomeSubGroupNameContainer.helperText = validIncomeSubGroup()
            }
        }
    }

    private fun validIncomeSubGroup(): String? {
        val incomeSubGroupNameEditText = binding.newIncomeSubGroupName.text.toString()
        if (incomeSubGroupNameEditText.isBlank()) {
            return "Invalid Income Sub Group Name"
        }
        return null
    }

    val args: AddIncomeSubGroupFragmentArgs by navArgs()


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

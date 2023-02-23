package com.romandevyatov.bestfinance.ui.fragments

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
import com.romandevyatov.bestfinance.databinding.FragmentAddNewIncomeSubGroupBinding
import com.romandevyatov.bestfinance.db.entities.IncomeSubGroup
import com.romandevyatov.bestfinance.viewmodels.IncomeGroupViewModel
import com.romandevyatov.bestfinance.viewmodels.IncomeSubGroupViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddNewIncomeSubGroupFragment : Fragment() {

    private var _binding: FragmentAddNewIncomeSubGroupBinding? = null
    private val binding get() = _binding!!

    private val incomeSubGroupViewModel: IncomeSubGroupViewModel by viewModels()
    private val incomeGroupViewModel: IncomeGroupViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddNewIncomeSubGroupBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    private fun getArraySpinner(): ArrayAdapter<String> {
        val spinnerAdapter: ArrayAdapter<String> =
            object : ArrayAdapter<String>(requireContext(), R.layout.support_simple_spinner_dropdown_item) {

                override fun isEnabled(position: Int): Boolean {
                    return position != 0
                }

                override fun areAllItemsEnabled(): Boolean {
                    return false
                }

                override fun getDropDownView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup
                ): View {
                    val view: TextView = super.getDropDownView(position, convertView, parent) as TextView
                    if (position == 0) {
                        view.setTextColor(Color.GRAY)
                    } else {

                    }

                    return view
                }
            }

        return spinnerAdapter
    }

    private fun initIncomeGroupSpinner() {
        val spinnerAdapter = getArraySpinner()

        incomeGroupViewModel.incomeGroupsLiveData.observe(viewLifecycleOwner) { incomeGroupList ->
            spinnerAdapter.clear()
            spinnerAdapter.add("Income group")
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initIncomeGroupSpinner()


        binding.addNewIncomeSubGroupNameButton.setOnClickListener {
            val selectedIncomeGroupName = binding.toIncomeGroupSpinner.selectedItem.toString()
            incomeGroupViewModel.getIncomeGroupNameByName(selectedIncomeGroupName).observe(viewLifecycleOwner) {
                incomeSubGroupViewModel.insertIncomeSubGroup(
                    IncomeSubGroup(
                        name = binding.newIncomeSubGroupName.text.toString(),
                        incomeGroupId = it.id!!
                    )
                )
            }


            val action = AddNewIncomeSubGroupFragmentDirections.actionNavigationAddNewIncomeSubGroupToNavigationAddIncome()
            action.incomeGroupName = selectedIncomeGroupName
            action.incomeSubGroupName = binding.newIncomeSubGroupName.text.toString()
            findNavController().navigate(action)
        }
    }

    val args: AddNewIncomeSubGroupFragmentArgs by navArgs()


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

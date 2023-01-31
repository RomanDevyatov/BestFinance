package com.romandevyatov.bestfinance.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.FragmentAddIncomeHistoryBinding
import com.romandevyatov.bestfinance.viewmodels.IncomeGroupViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddIncomeHistory : Fragment() {

    private lateinit var binding: FragmentAddIncomeHistoryBinding
    private val incomeGroupViewModel: IncomeGroupViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddIncomeHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddIncomeHistoryBinding.bind(view)

        val spinnerAdapter = ArrayAdapter<String>(requireContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item)

        incomeGroupViewModel.incomeGroupsLiveData.observe(viewLifecycleOwner) { incomeGroupList ->
            incomeGroupList?.forEach { it ->
                spinnerAdapter.add(it.name)
            }
        }

        val spinner = binding.incomeGroupSpinner
        spinner.adapter = spinnerAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val item = binding.incomeGroupSpinner.getItemAtPosition(position).toString()
                Toast.makeText(
                    requireContext(),
                    item,
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                Toast.makeText(activity, "Nothing Selected", Toast.LENGTH_LONG).show()
            }
        }




    }

}
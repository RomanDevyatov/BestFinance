package com.romandevyatov.bestfinance.ui.fragments

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.romandevyatov.bestfinance.databinding.FragmentAddIncomeHistoryBinding
import com.romandevyatov.bestfinance.viewmodels.IncomeGroupViewModel
import com.romandevyatov.bestfinance.viewmodels.WalletViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class AddIncomeHistory : Fragment() {

    private lateinit var binding: FragmentAddIncomeHistoryBinding
    private val incomeGroupViewModel: IncomeGroupViewModel by viewModels()
    private val walletViewModel: WalletViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddIncomeHistoryBinding.inflate(inflater, container, false)

        initIncomeGroupSpinner()
        initWalletSpinner()

        return binding.root
    }

    private fun initIncomeGroupSpinner() {
        val spinnerAdapter = ArrayAdapter<String>(requireContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item)

        incomeGroupViewModel.incomeGroupsLiveData.observe(viewLifecycleOwner) { incomeGroupList ->
            incomeGroupList?.forEach { it ->
                spinnerAdapter.add(it.name)
            }
        }

        val incomeGroupSpinner = binding.incomeGroupSpinner
        incomeGroupSpinner.adapter = spinnerAdapter

        incomeGroupSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

    private fun initWalletSpinner() {
        val spinnerAdapter = ArrayAdapter<String>(requireContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item)

        walletViewModel.walletsLiveData.observe(viewLifecycleOwner) { walletList ->
            walletList?.forEach { it ->
                spinnerAdapter.add(it.name)
            }
        }

        val walletSpinner = binding.walletSpinner
        walletSpinner.adapter = spinnerAdapter

        walletSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val item = binding.walletSpinner.getItemAtPosition(position).toString()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddIncomeHistoryBinding.bind(view)

        val dateET = binding.dateEditText

        val myCalendar = Calendar.getInstance()

        val datePicker = DatePickerDialog.OnDateSetListener() {
                view, year, month, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH,month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDate(myCalendar)
        }

        dateET.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                datePicker,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    fun updateDate(calendar: Calendar){
        val dateFormat = "dd/MM/yy";
        val sdf = SimpleDateFormat(dateFormat, Locale.US);
        binding.dateEditText.setText(sdf.format(calendar.time));
    }


}
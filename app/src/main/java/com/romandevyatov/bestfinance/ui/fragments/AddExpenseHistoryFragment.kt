package com.romandevyatov.bestfinance.ui.fragments

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.romandevyatov.bestfinance.databinding.FragmentAddExpenseHistoryBinding
import com.romandevyatov.bestfinance.viewmodels.ExpenseGroupWithExpenseSubGroupViewModel
import com.romandevyatov.bestfinance.viewmodels.WalletViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class AddExpenseHistoryFragment : Fragment() {

    private lateinit var binding: FragmentAddExpenseHistoryBinding

    private val expenseGroupWithExpenseSubGroupViewModel: ExpenseGroupWithExpenseSubGroupViewModel by viewModels()
    private val walletViewModel: WalletViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddExpenseHistoryBinding.inflate(inflater, container, false)


        initExpenseGroupSpinner()
        initWalletSpinner()

        return binding.root
    }

    private fun getSpinnerAdapter(name: String): ArrayAdapter<String> {
        val spinnerAdapter: ArrayAdapter<String> =
            object : ArrayAdapter<String>(requireContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item) {

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
                    if(position == 0) {
                        view.setTextColor(Color.GRAY)
                    } else {
                        //here it is possible to define color for other items by
                        //view.setTextColor(Color.RED)
                    }
                    return view
                }
            }

        spinnerAdapter.add(name)

        return spinnerAdapter
    }

    private fun initExpenseGroupSpinner() {
        val expenseGroupSpinnerAdapter = getSpinnerAdapter("Expense group")

        expenseGroupWithExpenseSubGroupViewModel.expenseGroupsLiveData.observe(viewLifecycleOwner) { expenseGroupList ->
            expenseGroupList?.forEach { it ->
                expenseGroupSpinnerAdapter.add(it.expenseGroup.name)
            }
        }

        val expenseGroupSpinner = binding.expenseGroupSpinner
        expenseGroupSpinner.adapter = expenseGroupSpinnerAdapter

        expenseGroupSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val item = binding.expenseGroupSpinner.getItemAtPosition(position).toString()

                val subGroups = expenseGroupWithExpenseSubGroupViewModel.getExpenseGroupWithExpenseSubGroupByGroupName(item)

                val expenseSubGroupSpinnerAdapter = getSpinnerAdapter("Expense sub group")

                subGroups?.forEach {
                    expenseSubGroupSpinnerAdapter.add(it.name)
                }

                val expenseSubGroupSpinner = binding.expenseSubGroupSpinner
                expenseSubGroupSpinner.adapter = expenseSubGroupSpinnerAdapter

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
        binding = FragmentAddExpenseHistoryBinding.bind(view)


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
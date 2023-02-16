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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.FragmentAddIncomeHistoryBinding
import com.romandevyatov.bestfinance.db.entities.IncomeHistory
import com.romandevyatov.bestfinance.db.entities.Wallet
import com.romandevyatov.bestfinance.viewmodels.IncomeGroupViewModel
import com.romandevyatov.bestfinance.viewmodels.IncomeHistoryViewModel
import com.romandevyatov.bestfinance.viewmodels.WalletViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class AddIncomeHistoryFragment : Fragment() {

    private lateinit var binding: FragmentAddIncomeHistoryBinding

    private val incomeGroupViewModel: IncomeGroupViewModel by viewModels()
    private val walletViewModel: WalletViewModel by viewModels()
    private val incomeHistoryViewModel: IncomeHistoryViewModel by viewModels()

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
                    //set the color of first item in the drop down list to gray
                    if(position == 0) {
                        view.setTextColor(Color.GRAY)
                    } else {
                        //here it is possible to define color for other items by
                        //view.setTextColor(Color.RED)
                    }
                    return view
                }
            }

        spinnerAdapter.add("Income group")

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

        updateDate(myCalendar)

        binding.addIncomeHistoryButton.setOnClickListener {
            val incomeGroupName =  binding.incomeGroupSpinner.selectedItem.toString()
            val incomeGroup = incomeGroupViewModel.incomeGroupsLiveData.value?.filter { incomeGroup ->
                incomeGroup.name == incomeGroupName
            }!!.single()
            val incomeGroupId = incomeGroup.id!!.toLong()

            val amountBinding = binding.amountEditText.text.toString().toDouble()

            val walletNameBinding = binding.walletSpinner.selectedItem.toString()
            val wallet = walletViewModel.walletsLiveData.value?.filter { wallet ->
                wallet.name == walletNameBinding
            }!!.single()
            val walletName = wallet.name
            val walletId = wallet.id!!.toLong()

//            val str = binding.dateEditText.text.toString()
//            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
//            val dateTime: LocalDateTime = LocalDateTime.parse(str, formatter)

            incomeHistoryViewModel.insertIncomeHistory(
                IncomeHistory(
                    incomeSubGroupId = incomeGroupId,
                    amount = amountBinding,
                    comment = binding.commentEditText.text.toString(),
                    date = Date(binding.dateEditText.text.toString()),
                    walletId = walletId
                )
            )

            val updatedBalance = wallet.balance + amountBinding

            walletViewModel.updateWallet(
                Wallet(
                    id = walletId,
                    name = walletName,
                    balance = updatedBalance)
            )

            findNavController().navigate(R.id.action_navigation_add_income_to_navigation_home)
        }

    }

    fun updateDate(calendar: Calendar) {
        val dateFormat = "yyyy-MM-dd HH:mm:ss"
        val sdf = SimpleDateFormat(dateFormat, Locale.US)
        binding.dateEditText.setText(sdf.format(calendar.time))
    }


}
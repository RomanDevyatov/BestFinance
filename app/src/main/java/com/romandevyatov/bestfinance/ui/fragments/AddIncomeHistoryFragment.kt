package com.romandevyatov.bestfinance.ui.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.FragmentAddIncomeHistoryBinding
import com.romandevyatov.bestfinance.db.entities.IncomeHistory
import com.romandevyatov.bestfinance.db.entities.Wallet
import com.romandevyatov.bestfinance.viewmodels.IncomeGroupViewModel
import com.romandevyatov.bestfinance.viewmodels.IncomeHistoryViewModel
import com.romandevyatov.bestfinance.viewmodels.WalletViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
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
    ): View {
        binding = FragmentAddIncomeHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun getArraySpinner(): ArrayAdapter<String> {
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
                    if (position == 0) {
                        view.setTextColor(Color.GRAY)
                    } else {

                    }

                    return view
                }
            }

        return spinnerAdapter
    }

    val args: AddIncomeHistoryFragmentArgs by navArgs()

    private fun initIncomeGroupSpinner() {
        val incomeGroupSpinnerAdapter = getArraySpinner()
        binding.incomeGroupSpinner.adapter = incomeGroupSpinnerAdapter

        incomeGroupViewModel.incomeGroupsLiveData.observe(viewLifecycleOwner) { incomeGroupList ->
            incomeGroupSpinnerAdapter.clear()
            incomeGroupSpinnerAdapter.add("Income group")
            incomeGroupList?.forEach { it ->
                incomeGroupSpinnerAdapter.add(it.name)
            }

            incomeGroupSpinnerAdapter.add("Add new income group")

            if (args.incomeGroupName != null && args.incomeGroupName!!.isNotBlank()) {
                val spinnerPosition = incomeGroupSpinnerAdapter.getPosition(args.incomeGroupName)

                binding.incomeGroupSpinner.setSelection(spinnerPosition)
            }
        }

        val incomeSubGroupArraySpinner = getArraySpinner()
        incomeSubGroupArraySpinner.add("Income sub group")

        binding.incomeGroupSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val selectedIncomeGroupName = binding.incomeGroupSpinner.getItemAtPosition(position).toString()
                if (selectedIncomeGroupName == "Add new income group") {
                    val action = AddIncomeHistoryFragmentDirections.actionNavigationAddIncomeToNavigationAddNewIncomeGroup()
                    findNavController().navigate(action)
                }

                incomeGroupViewModel.getIncomeGroupWithIncomeSubGroupsByIncomeGroupNameAndArchivedDateIsNull(selectedIncomeGroupName).observe(viewLifecycleOwner) { list ->
                    incomeSubGroupArraySpinner.clear()
                    incomeSubGroupArraySpinner.add("Income sub group")
                    if (list != null) {
                        val subGroups = list.incomeSubGroups

                        subGroups.forEach {
                            incomeSubGroupArraySpinner.add(it.name)
                        }
                    }
                    incomeSubGroupArraySpinner.add("Add new sub income group")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                Toast.makeText(activity, "Nothing Selected", Toast.LENGTH_LONG).show()
            }

        }

        binding.incomeSubGroupSpinner.adapter = incomeSubGroupArraySpinner
        binding.incomeSubGroupSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedIncomeSubGroupName = binding.incomeSubGroupSpinner.getItemAtPosition(position).toString()

                if (selectedIncomeSubGroupName == "Add new sub income group") {
                    val action = AddIncomeHistoryFragmentDirections.actionNavigationAddIncomeToNavigationAddNewSubIncomeGroup()
                    val selectedIncomeGroup = binding.incomeGroupSpinner.selectedItem
                    if (selectedIncomeGroup != null) {
                        action.incomeGroupName = selectedIncomeGroup.toString()
                    }
                    findNavController().navigate(action)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }


    }

    private fun initWalletSpinner() {
        val spinnerAdapter = getArraySpinner()

        walletViewModel.walletsLiveData.observe(viewLifecycleOwner) { walletList ->
            spinnerAdapter.clear()
            spinnerAdapter.add("Wallet")

            walletList?.forEach { it ->
                spinnerAdapter.add(it.name)
            }

            spinnerAdapter.add("Add new wallet")
        }

        val walletSpinner = binding.walletSpinner
        walletSpinner.adapter = spinnerAdapter

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback = object : OnBackPressedCallback(
            true
        ) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_navigation_add_income_to_navigation_home)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

//    private val iso8601DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddIncomeHistoryBinding.bind(view)

        initIncomeGroupSpinner()
        initWalletSpinner()

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
            val walletId = wallet.id!!.toLong()

//            val str = binding.dateEditText.text.toString()
//            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
//            val dateTime: LocalDateTime = LocalDateTime.parse(str, formatter)

            val iso8601DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
            incomeHistoryViewModel.insertIncomeHistory(
                IncomeHistory(
                    incomeSubGroupId = incomeGroupId,
                    amount = amountBinding,
                    description = binding.commentEditText.text.toString(),
                    createdDate = OffsetDateTime.from(iso8601DateTimeFormatter.parse(binding.dateEditText.text.toString())),
                    walletId = walletId
                )
            )

            val updatedBalance = wallet.balance + amountBinding

            walletViewModel.updateWallet(
                Wallet(
                    id = walletId,
                    name = wallet.name,
                    balance = updatedBalance,
                    archivedDate = wallet.archivedDate,
                    input = wallet.input + amountBinding,
                    output = wallet.output,
                    description = wallet.description
                )
            )

            findNavController().navigate(R.id.action_navigation_add_income_to_navigation_home)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateDate(calendar: Calendar) {
//        val dateFormat =  //"yyyy-MM-dd HH:mm:ss"
//        val sdf = SimpleDateFormat(dateFormat, Locale.US)
//        binding.dateEditText.setText(sdf.format(calendar.time))
        val iso8601DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        binding.dateEditText.setText(OffsetDateTime.now().format(iso8601DateTimeFormatter))
    }




}
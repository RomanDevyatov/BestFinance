package com.romandevyatov.bestfinance.ui.fragments.addictions.expense

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.FragmentAddExpenseHistoryBinding
import com.romandevyatov.bestfinance.db.entities.ExpenseHistory
import com.romandevyatov.bestfinance.db.entities.ExpenseSubGroup
import com.romandevyatov.bestfinance.db.entities.Wallet
import com.romandevyatov.bestfinance.ui.adapters.spinnerutils.CustomSpinnerAdapter
import com.romandevyatov.bestfinance.ui.fragments.addictions.income.AddIncomeHistoryFragmentDirections
import com.romandevyatov.bestfinance.utils.Constants
import com.romandevyatov.bestfinance.viewmodels.*
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.AddExpenseHistoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@AndroidEntryPoint
class AddExpenseHistoryFragment : Fragment() {

    private lateinit var binding: FragmentAddExpenseHistoryBinding

//    private val expenseHistoryViewModel: ExpenseHistoryViewModel by viewModels()
//    private val walletViewModel: WalletViewModel by viewModels()
//    private val expenseSubGroupViewModel: ExpenseSubGroupViewModel by viewModels()
//    private val expenseGroupViewModel: ExpenseGroupViewModel by viewModels()

    private val addExpenseHistoryViewModel: AddExpenseHistoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddExpenseHistoryBinding.inflate(inflater, container, false)

        initExpenseGroupAndSubGroupSpinners()

        initWalletSpinner()

        return binding.root
    }


    private val args: AddExpenseHistoryFragmentArgs by navArgs()

    private fun initExpenseGroupAndSubGroupSpinners() {
        var spinnerSubItems = ArrayList<String>()
        spinnerSubItems.add(Constants.EXPENSE_SUB_GROUP)

        val archiveIncomeSubGroupOnLongPressListener =
            object : CustomSpinnerAdapter.DeleteItemClickListener {

                @RequiresApi(Build.VERSION_CODES.O)
                override fun archive(name: String) {
                    addExpenseHistoryViewModel.archiveExpenseSubGroup(name)
                }
            }
        var customIncomeSubGroupSpinnerAdapter = CustomSpinnerAdapter(requireContext(), spinnerSubItems, archiveIncomeSubGroupOnLongPressListener)

        addExpenseHistoryViewModel.getAllExpenseGroupNotArchivedLiveData().observe(viewLifecycleOwner) { expenseGroupList ->
            val spinnerItems = ArrayList<String>()
            spinnerItems.add(Constants.EXPENSE_GROUP)
            expenseGroupList?.forEach { it ->
                spinnerItems.add(it.name)
            }
            spinnerItems.add(Constants.ADD_NEW_EXPENSE_GROUP)

            val archiveExpenseGroupListener =
                object : CustomSpinnerAdapter.DeleteItemClickListener {

                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun archive(name: String) {
                        addExpenseHistoryViewModel.archiveExpenseGroup(name)
                    }
                }

            val incomeGroupSpinnerAdapter =
                CustomSpinnerAdapter(requireContext(), spinnerItems, archiveExpenseGroupListener)
            binding.expenseGroupSpinner.adapter = incomeGroupSpinnerAdapter

            if (args.expenseGroupName != null && args.expenseGroupName!!.isNotBlank()) {
                val spinnerPosition = incomeGroupSpinnerAdapter.getPosition(args.expenseGroupName)

                binding.expenseGroupSpinner.setSelection(spinnerPosition)
            }

            binding.expenseGroupSpinner.setSelection(0,false)
            binding.expenseGroupSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    binding.expenseSubGroupSpinner.isVisible = true

                    val selectedIncomeGroupName =
                        binding.expenseGroupSpinner.getItemAtPosition(position).toString()

                    Toast.makeText(
                        context,
                        "" + binding.expenseGroupSpinner.selectedItemPosition.toString(),
                        Toast.LENGTH_SHORT
                    ).show()

                    if (selectedIncomeGroupName == Constants.ADD_NEW_EXPENSE_GROUP) {
                        val action =
                            AddIncomeHistoryFragmentDirections.actionNavigationAddIncomeToNavigationAddNewIncomeGroup()
                        findNavController().navigate(action)
                    }

                    addExpenseHistoryViewModel.getExpenseGroupWithExpenseSubGroupsByExpenseGroupNameNotArchivedLiveData(selectedIncomeGroupName)
                        .observe(viewLifecycleOwner) { expenseGroupWithExpenseSubGroups ->
                            if (expenseGroupWithExpenseSubGroups != null) {
                                spinnerSubItems = getSpinnerSubItems(expenseGroupWithExpenseSubGroups.expenseSubGroups)
                                customIncomeSubGroupSpinnerAdapter = CustomSpinnerAdapter(requireContext(), spinnerSubItems, archiveIncomeSubGroupOnLongPressListener)
                                binding.expenseSubGroupSpinner.adapter = customIncomeSubGroupSpinnerAdapter
                            }

                            if (args.expenseSubGroupName != null && args.expenseSubGroupName!!.isNotBlank()) {
                                val spinnerPosition = customIncomeSubGroupSpinnerAdapter.getPosition(args.expenseSubGroupName)
                                binding.expenseSubGroupSpinner.setSelection(spinnerPosition)
                            }
                        }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    Toast.makeText(activity, "Nothing Selected", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.expenseSubGroupSpinner.adapter = customIncomeSubGroupSpinnerAdapter
        binding.expenseSubGroupSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedIncomeSubGroupName = binding.expenseSubGroupSpinner.getItemAtPosition(position).toString()

                if (selectedIncomeSubGroupName == Constants.ADD_NEW_EXPENSE_SUB_GROUP) {
                    val action = AddIncomeHistoryFragmentDirections.actionNavigationAddIncomeToNavigationAddNewSubIncomeGroup()
                    val selectedIncomeGroup = binding.expenseGroupSpinner.selectedItem
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

    private fun getSpinnerSubItems(incomeSubGroups: List<ExpenseSubGroup>): ArrayList<String> {
        val spinnerSubItems = ArrayList<String>()
        spinnerSubItems.add(Constants.EXPENSE_SUB_GROUP)

        incomeSubGroups.forEach {
            spinnerSubItems.add(it.name)
        }
        spinnerSubItems.add(Constants.ADD_NEW_EXPENSE_SUB_GROUP)

        return spinnerSubItems
    }

    private fun initWalletSpinner() {
        addExpenseHistoryViewModel.walletsNotArchivedLiveData.observe(viewLifecycleOwner) { walletList ->

            val spinnerWalletItems = ArrayList<String>()
            spinnerWalletItems.add(Constants.WALLET)
            walletList.forEach {
                spinnerWalletItems.add(it.name)
            }
            spinnerWalletItems.add(Constants.ADD_NEW_WALLET)

            val archiveWalletListener =
                object : CustomSpinnerAdapter.DeleteItemClickListener {

                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun archive(name: String) {
                        addExpenseHistoryViewModel.archiveWallet(name)
                    }
                }

            var customWalletAdapter = CustomSpinnerAdapter(requireContext(), spinnerWalletItems, archiveWalletListener)

            // Populate the spinner with the names
            binding.walletSpinner.adapter = customWalletAdapter

            if (args.walletName != null && args.walletName!!.isNotBlank()) {
                val spinnerPosition = customWalletAdapter.getPosition(args.walletName)

                binding.walletSpinner.setSelection(spinnerPosition)
            }
        }

        binding.walletSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedIncomeSubGroupName = binding.walletSpinner.getItemAtPosition(position).toString()

                if (selectedIncomeSubGroupName == Constants.ADD_NEW_WALLET) {
                    val action = AddIncomeHistoryFragmentDirections.actionNavigationAddIncomeToNavigationAddWallet()
                    action.source = Constants.ADD_INCOME_HISTORY_FRAGMENT
                    findNavController().navigate(action)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddExpenseHistoryBinding.bind(view)

        val dateET = binding.dateEditText

        val myCalendar = Calendar.getInstance()

        val datePicker = DatePickerDialog.OnDateSetListener() { view, year, month, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
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

        binding.addExpenseHistoryButton.setOnClickListener {

            if (true) {// if (isFormValid()) {
                val incomeSubGroupNameBinding =
                    binding.expenseSubGroupSpinner.selectedItem.toString()
                val amountBinding = binding.amountEditText.text.toString().toDouble()
                val commentBinding = binding.commentEditText.text.toString()
                val dateBinding = binding.dateEditText.text.toString()
                val walletNameBinding = binding.walletSpinner.selectedItem.toString()
                addExpenseHistoryViewModel.addExpenseHistory(
                    incomeSubGroupNameBinding,
                    amountBinding,
                    commentBinding,
                    dateBinding,
                    walletNameBinding
                )

                findNavController().navigate(R.id.action_navigation_add_income_to_navigation_home)

//            val str = binding.dateEditText.text.toString()
//            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
//            val dateTime: LocalDateTime = LocalDateTime.parse(str, formatter)
            }

        }

//        binding.addExpenseHistoryButton.setOnClickListener {
//            val expenseSubGroupName = binding.expenseSubGroupSpinner.selectedItem.toString()
//            addExpenseHistoryViewModel.getExpensegetExpenseSubGroupByNameWhereArchivedDateIsNull(expenseSubGroupName)
//                .observe(viewLifecycleOwner) { incomeSubGroup ->
//                val incomeGroupId = incomeSubGroup.id!!.toLong()
//
//                val selectedWalletName = binding.walletSpinner.selectedItem.toString()
//                walletViewModel.getWalletByNameNotArchivedLiveData(selectedWalletName)
//                    .observe(viewLifecycleOwner) { wallet ->
//                        val walletId = wallet.id!!
//
//                        val amountBinding = binding.amountEditText.text.toString().toDouble()
//                        val iso8601DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
//
//                        val commentBinding = binding.commentEditText.text.toString()
//                        val dateBinding = binding.dateEditText.text.toString()
//
//                        expenseHistoryViewModel.insertExpenseHistory(
//                            ExpenseHistory(
//                                expenseSubGroupId = incomeGroupId,
//                                amount = amountBinding,
//                                description = commentBinding,
//                                createdDate = OffsetDateTime.from(
//                                    iso8601DateTimeFormatter.parse(
//                                        dateBinding
//                                    )
//                                ),
//                                walletId = walletId
//                            )
//                        )
//
//                        val updatedBalance = wallet.balance - amountBinding
//
//                        walletViewModel.updateWallet(
//                            Wallet(
//                                id = walletId,
//                                name = wallet.name,
//                                balance = updatedBalance,
//                                archivedDate = wallet.archivedDate,
//                                input = wallet.input + amountBinding,
//                                output = wallet.output,
//                                description = wallet.description
//                            )
//                        )
//
//                        findNavController().navigate(R.id.action_navigation_add_expense_to_navigation_home)
//                    }
//
//            }


//            val str = binding.dateEditText.text.toString()
//            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
//            val dateTime: LocalDateTime = LocalDateTime.parse(str, formatter)


    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun updateDate(calendar: Calendar) {
//        val dateFormat = "yyyy-MM-dd HH:mm:ss"
//        val sdf = SimpleDateFormat(dateFormat, Locale.US)
//        binding.dateEditText.setText(sdf.format(calendar.time))
        val iso8601DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        binding.dateEditText.setText(OffsetDateTime.now().format(iso8601DateTimeFormatter))
    }



}

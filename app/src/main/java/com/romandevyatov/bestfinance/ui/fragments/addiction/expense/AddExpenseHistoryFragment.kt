package com.romandevyatov.bestfinance.ui.fragments.addiction.expense

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.FragmentAddExpenseHistoryBinding
import com.romandevyatov.bestfinance.db.entities.ExpenseHistory
import com.romandevyatov.bestfinance.db.entities.Wallet
import com.romandevyatov.bestfinance.ui.adapters.spinnerutils.SpinnerUtils
import com.romandevyatov.bestfinance.utils.Constants
import com.romandevyatov.bestfinance.utils.Constants.ADD_NEW_SUB_EXPENSE_GROUP
import com.romandevyatov.bestfinance.viewmodels.*
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.ExpenseGroupViewModel
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.ExpenseHistoryViewModel
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.ExpenseSubGroupViewModel
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.WalletViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@AndroidEntryPoint
class AddExpenseHistoryFragment : Fragment() {

    private lateinit var binding: FragmentAddExpenseHistoryBinding

    private val expenseHistoryViewModel: ExpenseHistoryViewModel by viewModels()
    private val walletViewModel: WalletViewModel by viewModels()
    private val expenseSubGroupViewModel: ExpenseSubGroupViewModel by viewModels()
    private val expenseGroupViewModel: ExpenseGroupViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddExpenseHistoryBinding.inflate(inflater, container, false)

        initGroupSpinners()

        initWalletSpinner()

        return binding.root
    }

    private fun getSpinnerAdapter(): ArrayAdapter<String> {
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

    private val args: AddExpenseHistoryFragmentArgs by navArgs()

    private fun initGroupSpinners() {
        val expenseGroupSpinnerAdapter = getSpinnerAdapter()
        binding.expenseGroupSpinner.adapter = expenseGroupSpinnerAdapter

        expenseGroupViewModel.expenseGroupsLiveData.observe(viewLifecycleOwner) { expenseGroupList ->
            expenseGroupSpinnerAdapter.clear()
            expenseGroupSpinnerAdapter.add("Expense group")

            expenseGroupList?.forEach { it ->
                expenseGroupSpinnerAdapter.add(it.name)
            }

            expenseGroupSpinnerAdapter.add("Add new expense group")

            if (args.expenseGroupName != null && args.expenseGroupName!!.isNotBlank()) {
                val spinnerPosition = expenseGroupSpinnerAdapter.getPosition(args.expenseGroupName.toString())
                binding.expenseGroupSpinner.setSelection(spinnerPosition)
            }
        }

        val expenseSubGroupSpinnerAdapter = getSpinnerAdapter()
        expenseSubGroupSpinnerAdapter.add("Expense sub group")

        binding.expenseGroupSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val selectedExpenseGroupName = binding.expenseGroupSpinner.getItemAtPosition(position).toString()

                if (selectedExpenseGroupName == "Add new expense group") {
                    val action = AddExpenseHistoryFragmentDirections.actionNavigationAddExpenseToNavigationAddNewExpenseGroup()
                    findNavController().navigate(action)
                }

                expenseGroupViewModel.getExpenseGroupWithExpenseSubGroupsByExpenseGroupNameAndArchivedDateIsNullLiveData(selectedExpenseGroupName).observe(viewLifecycleOwner) { list ->
                    expenseSubGroupSpinnerAdapter.clear()
                    expenseSubGroupSpinnerAdapter.add("Expense sub group")

                    if (list != null) {
                        val subGroups = list.expenseSubGroups

                        subGroups.forEach {
                            expenseSubGroupSpinnerAdapter.add(it.name)
                        }
                    }
                    expenseSubGroupSpinnerAdapter.add(ADD_NEW_SUB_EXPENSE_GROUP)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                Toast.makeText(activity, "Nothing Selected", Toast.LENGTH_LONG).show()
            }
        }

        binding.expenseSubGroupSpinner.adapter = expenseSubGroupSpinnerAdapter
        binding.expenseSubGroupSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedIncomeSubGroupName = binding.expenseSubGroupSpinner.getItemAtPosition(position).toString()

                if (selectedIncomeSubGroupName == ADD_NEW_SUB_EXPENSE_GROUP) {
                    val action = AddExpenseHistoryFragmentDirections.actionNavigationAddExpenseToNavigationAddNewExpenseSubGroup()
                    val selectedExpenseGroup = binding.expenseGroupSpinner.selectedItem
                    if (selectedExpenseGroup != null) {
                        action.expenseGroupName = selectedExpenseGroup.toString()
                    }
                    findNavController().navigate(action)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) { }

        }

    }

    private fun initWalletSpinner() {
        walletViewModel.walletsLiveData.observe(viewLifecycleOwner) { walletList ->
            val walletSpinnerAdapter = SpinnerUtils.getArraySpinner(requireContext())

            walletSpinnerAdapter.clear()
            walletSpinnerAdapter.add("Wallet")

            val names = walletList.map { it.name }
            walletSpinnerAdapter.addAll(names)

            walletSpinnerAdapter.add(Constants.ADD_NEW_WALLET_STRING)

            // Populate the spinner with the names
            binding.walletSpinner.adapter = walletSpinnerAdapter

            if (args.walletName != null && args.walletName!!.isNotBlank()) {
                val spinnerPosition = walletSpinnerAdapter.getPosition(args.walletName)

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

                if (selectedIncomeSubGroupName == Constants.ADD_NEW_WALLET_STRING) {
                    val action = AddExpenseHistoryFragmentDirections.actionNavigationAddExpenseToNavigationAddWallet()
                    action.source = Constants.ADD_EXPENSE_HISTORY_FRAGMENT
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

        expenseSubGroupViewModel.expenseSubGroupsLiveData.observe(viewLifecycleOwner) {

        }

        binding.addWalletButton.setOnClickListener {
            val expenseSubGroupName = binding.expenseSubGroupSpinner.selectedItem.toString()
            expenseSubGroupViewModel.getExpenseSubGroupByNameWhereArchivedDateIsNull(
                expenseSubGroupName
            ).observe(viewLifecycleOwner) { incomeSubGroup ->
                val incomeGroupId = incomeSubGroup.id!!.toLong()

                val selectedWalletName = binding.walletSpinner.selectedItem.toString()
                walletViewModel.getWalletByNameNotArchivedLiveData(selectedWalletName)
                    .observe(viewLifecycleOwner) { wallet ->
                        val walletId = wallet.id!!

                        val amountBinding = binding.amountEditText.text.toString().toDouble()
                        val iso8601DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

                        val commentBinding = binding.commentEditText.text.toString()
                        val dateBinding = binding.dateEditText.text.toString()

                        expenseHistoryViewModel.insertExpenseHistory(
                            ExpenseHistory(
                                expenseSubGroupId = incomeGroupId,
                                amount = amountBinding,
                                description = commentBinding,
                                createdDate = OffsetDateTime.from(
                                    iso8601DateTimeFormatter.parse(
                                        dateBinding
                                    )
                                ),
                                walletId = walletId
                            )
                        )

                        val updatedBalance = wallet.balance - amountBinding

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

                        findNavController().navigate(R.id.action_navigation_add_expense_to_navigation_home)
                    }

            }


//            val str = binding.dateEditText.text.toString()
//            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
//            val dateTime: LocalDateTime = LocalDateTime.parse(str, formatter)


        }
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

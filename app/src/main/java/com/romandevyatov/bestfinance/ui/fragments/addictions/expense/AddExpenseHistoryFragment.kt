package com.romandevyatov.bestfinance.ui.fragments.addictions.expense

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.FragmentAddExpenseHistoryBinding
import com.romandevyatov.bestfinance.databinding.FragmentAddIncomeHistoryBinding
import com.romandevyatov.bestfinance.db.entities.ExpenseSubGroup
import com.romandevyatov.bestfinance.ui.adapters.spinnerutils.CustomSpinnerAdapter
import com.romandevyatov.bestfinance.utils.Constants
import com.romandevyatov.bestfinance.viewmodels.*
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.AddExpenseHistoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@AndroidEntryPoint
class AddExpenseHistoryFragment : Fragment() {

    private var _binding: FragmentAddExpenseHistoryBinding? = null
    private val binding get() = _binding!!

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
        _binding = FragmentAddExpenseHistoryBinding.inflate(inflater, container, false)

        initExpenseGroupAndSubGroupSpinners()

        initWalletSpinner()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback = object : OnBackPressedCallback(
            true
        ) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_navigation_add_expense_to_navigation_home)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    private val args: AddExpenseHistoryFragmentArgs by navArgs()

    private fun initExpenseGroupAndSubGroupSpinners() {
        var spinnerSubItems = ArrayList<String>()
        spinnerSubItems.add(Constants.EXPENSE_SUB_GROUP)

        val archiveExpenseSubGroupOnLongPressListener =
            object : CustomSpinnerAdapter.DeleteItemClickListener {

                @RequiresApi(Build.VERSION_CODES.O)
                override fun archive(name: String) {
                    addExpenseHistoryViewModel.archiveExpenseSubGroup(name)
                }
            }
        var customExpenseSubGroupSpinnerAdapter = CustomSpinnerAdapter(requireContext(), spinnerSubItems, archiveExpenseSubGroupOnLongPressListener)

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

            val expenseGroupSpinnerAdapter =
                CustomSpinnerAdapter(requireContext(), spinnerItems, archiveExpenseGroupListener)
            binding.expenseGroupSpinner.adapter = expenseGroupSpinnerAdapter

            if (args.expenseGroupName != null && args.expenseGroupName!!.isNotBlank()) {
                val spinnerPosition = expenseGroupSpinnerAdapter.getPosition(args.expenseGroupName)

                binding.expenseGroupSpinner.setSelection(spinnerPosition)
            }

//            binding.expenseGroupSpinner.setSelection(0,false)
            binding.expenseGroupSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
//                    binding.expenseSubGroupSpinner.isVisible = true

                    val selectedExpenseGroupName =
                        binding.expenseGroupSpinner.getItemAtPosition(position).toString()

                    Toast.makeText(
                        context,
                        "" + binding.expenseGroupSpinner.selectedItemPosition.toString(),
                        Toast.LENGTH_SHORT
                    ).show()

                    if (selectedExpenseGroupName == Constants.ADD_NEW_EXPENSE_GROUP) {
                        val action =
                            AddExpenseHistoryFragmentDirections.actionNavigationAddExpenseToNavigationAddNewExpenseGroup()
                        findNavController().navigate(action)
                    }

                    addExpenseHistoryViewModel.getExpenseGroupWithExpenseSubGroupsByExpenseGroupNameNotArchivedLiveData(selectedExpenseGroupName)
                        .observe(viewLifecycleOwner) { expenseGroupWithExpenseSubGroups ->
                            if (expenseGroupWithExpenseSubGroups != null) {
                                spinnerSubItems = getSpinnerSubItems(expenseGroupWithExpenseSubGroups.expenseSubGroups)
                                customExpenseSubGroupSpinnerAdapter = CustomSpinnerAdapter(requireContext(), spinnerSubItems, archiveExpenseSubGroupOnLongPressListener)
                                binding.expenseSubGroupSpinner.adapter = customExpenseSubGroupSpinnerAdapter
                            }

                            if (args.expenseSubGroupName != null && args.expenseSubGroupName!!.isNotBlank()) {
                                val spinnerPosition = customExpenseSubGroupSpinnerAdapter.getPosition(args.expenseSubGroupName)
                                binding.expenseSubGroupSpinner.setSelection(spinnerPosition)
                            }
                        }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    Toast.makeText(activity, "Nothing Selected", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.expenseSubGroupSpinner.adapter = customExpenseSubGroupSpinnerAdapter
        binding.expenseSubGroupSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedExpenseSubGroupName = binding.expenseSubGroupSpinner.getItemAtPosition(position).toString()

                if (selectedExpenseSubGroupName == Constants.ADD_NEW_EXPENSE_SUB_GROUP) {
                    val action = AddExpenseHistoryFragmentDirections.actionNavigationAddExpenseToNavigationAddNewExpenseSubGroup()

                    val selectedExpenseGroup = binding.expenseGroupSpinner.selectedItem
                    if (selectedExpenseGroup != null) {
                        action.expenseGroupName = selectedExpenseGroup.toString()
                    }
                    findNavController().navigate(action)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    private fun getSpinnerSubItems(expenseSubGroups: List<ExpenseSubGroup>): ArrayList<String> {
        val spinnerSubItems = ArrayList<String>()
        spinnerSubItems.add(Constants.EXPENSE_SUB_GROUP)

        expenseSubGroups.forEach {
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
                val selectedExpenseSubGroupName = binding.walletSpinner.getItemAtPosition(position).toString()

                if (selectedExpenseSubGroupName == Constants.ADD_NEW_WALLET) {
                    val action = AddExpenseHistoryFragmentDirections.actionNavigationAddExpenseToNavigationAddWallet()
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
                val expenseSubGroupNameBinding =
                    binding.expenseSubGroupSpinner.selectedItem.toString()
                val amountBinding = binding.amountEditText.text.toString().toDouble()
                val commentBinding = binding.commentEditText.text.toString()
                val dateBinding = binding.dateEditText.text.toString()
                val walletNameBinding = binding.walletSpinner.selectedItem.toString()

                addExpenseHistoryViewModel.addExpenseHistory(
                    expenseSubGroupNameBinding,
                    amountBinding,
                    commentBinding,
                    dateBinding,
                    walletNameBinding
                )

                findNavController().navigate(R.id.action_navigation_add_expense_to_navigation_home)

//            val str = binding.dateEditText.text.toString()
//            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
//            val dateTime: LocalDateTime = LocalDateTime.parse(str, formatter)
            }

        }


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

package com.romandevyatov.bestfinance.ui.fragments.addictions.expense

import android.app.DatePickerDialog
import android.content.Context
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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.FragmentAddExpenseHistoryBinding
import com.romandevyatov.bestfinance.db.entities.ExpenseGroup
import com.romandevyatov.bestfinance.db.entities.ExpenseSubGroup
import com.romandevyatov.bestfinance.ui.adapters.spinnerutils.CustomSpinnerAdapter
import com.romandevyatov.bestfinance.utils.Constants
import com.romandevyatov.bestfinance.viewmodels.*
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.AddExpenseHistoryViewModel
import com.romandevyatov.bestfinance.viewmodels.shared.SharedViewModel
import com.romandevyatov.bestfinance.viewmodels.shared.models.AddTransactionForm
import dagger.hilt.android.AndroidEntryPoint
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@AndroidEntryPoint
class AddExpenseHistoryFragment : Fragment() {

    private var _binding: FragmentAddExpenseHistoryBinding? = null
    private val binding get() = _binding!!

    private val addExpenseHistoryViewModel: AddExpenseHistoryViewModel by viewModels()

    private val sharedViewModel: SharedViewModel<AddTransactionForm> by activityViewModels()

    private var expenseGroupSpinnerPosition = 0
    private var expenseSubGroupSpinnerPosition = 0
    private var walletSpinnerPosition = 0

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
                sharedViewModel.set(null)
                findNavController().navigate(R.id.action_navigation_add_expense_to_navigation_home)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    private val args: AddExpenseHistoryFragmentArgs by navArgs()

    private fun initExpenseGroupAndSubGroupSpinners() {
        var subGroupItems = ArrayList<String>()
        subGroupItems.add(Constants.EXPENSE_SUB_GROUP)

        val archiveExpenseSubGroupOnLongPressListener =
            object : CustomSpinnerAdapter.DeleteItemClickListener {

                @RequiresApi(Build.VERSION_CODES.O)
                override fun archive(name: String) {
                    addExpenseHistoryViewModel.archiveExpenseSubGroup(name)
                }
            }

        var expenseSubGroupSpinnerAdapter = CustomSpinnerAdapter(requireContext(), subGroupItems, archiveExpenseSubGroupOnLongPressListener)

        addExpenseHistoryViewModel.getAllExpenseGroupNotArchivedLiveData().observe(viewLifecycleOwner) { expenseGroupList ->
            val groupItems = getExpenseGroupItems(expenseGroupList)

            val archiveExpenseGroupListener =
                object : CustomSpinnerAdapter.DeleteItemClickListener {

                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun archive(name: String) {
                        addExpenseHistoryViewModel.archiveExpenseGroup(name)
                    }
                }

            val expenseGroupSpinnerAdapter = CustomSpinnerAdapter(requireContext(), groupItems, archiveExpenseGroupListener)
            binding.expenseGroupSpinner.adapter = expenseGroupSpinnerAdapter

            restoreAddingExpenseForm()

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
                    expenseGroupSpinnerPosition = position

                    binding.expenseSubGroupSpinner.isVisible = true

                    val selectedExpenseGroupName =
                        binding.expenseGroupSpinner.getItemAtPosition(position).toString()

                    if (selectedExpenseGroupName == Constants.ADD_NEW_EXPENSE_GROUP) {
                        setAddExpenseFormBeforeAddingExpenseGroup()

                        val action =
                            AddExpenseHistoryFragmentDirections.actionNavigationAddExpenseToNavigationAddNewExpenseGroup()
                        findNavController().navigate(action)
                    }

                    addExpenseHistoryViewModel.getExpenseGroupWithExpenseSubGroupsByExpenseGroupNameNotArchivedLiveData(selectedExpenseGroupName)
                        .observe(viewLifecycleOwner) { expenseGroupWithExpenseSubGroups ->
                            if (expenseGroupWithExpenseSubGroups != null) {
                                subGroupItems = getSpinnerSubItems(expenseGroupWithExpenseSubGroups.expenseSubGroups)
                                expenseSubGroupSpinnerAdapter = CustomSpinnerAdapter(requireContext(), subGroupItems, archiveExpenseSubGroupOnLongPressListener)
                                binding.expenseSubGroupSpinner.adapter = expenseSubGroupSpinnerAdapter
                            }

                            if (args.expenseSubGroupName != null && args.expenseSubGroupName!!.isNotBlank()) {
                                val spinnerPosition = expenseSubGroupSpinnerAdapter.getPosition(args.expenseSubGroupName)
                                binding.expenseSubGroupSpinner.setSelection(spinnerPosition)
                            }
                        }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    Toast.makeText(activity, "Nothing Selected", Toast.LENGTH_LONG).show()
                }
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
                expenseSubGroupSpinnerPosition = position

                val selectedExpenseSubGroupName = binding.expenseSubGroupSpinner.getItemAtPosition(position).toString()

                if (selectedExpenseSubGroupName == Constants.ADD_NEW_EXPENSE_SUB_GROUP) {
                    setAddExpenseFormBeforeAddingExpenseSubGroup()

                    val action = AddExpenseHistoryFragmentDirections.actionNavigationAddExpenseToNavigationAddNewExpenseSubGroup()

                    val selectedExpenseGroup = binding.expenseGroupSpinner.selectedItem
                    action.expenseGroupName = selectedExpenseGroup.toString()

                    findNavController().navigate(action)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    private fun getExpenseGroupItems(expenseGroupList: List<ExpenseGroup>?): ArrayList<String> {
        val groupItems = ArrayList<String>()
        groupItems.add(Constants.EXPENSE_GROUP)
        expenseGroupList?.forEach { it ->
            groupItems.add(it.name)
        }
        groupItems.add(Constants.ADD_NEW_EXPENSE_GROUP)

        return groupItems
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
                walletSpinnerPosition = position

                val selectedExpenseSubGroupName = binding.walletSpinner.getItemAtPosition(position).toString()

                if (selectedExpenseSubGroupName == Constants.ADD_NEW_WALLET) {
                    setAddIncomeFormBeforeAddingWallet()

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

                sharedViewModel.set(null)
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

    private fun setAddExpenseFormBeforeAddingExpenseGroup() {
        val amountBinding = binding.amountEditText.text.toString().trim()
        val dateEditText = binding.dateEditText.text.toString().trim()
        val commentBinding = binding.commentEditText.text.toString().trim()

        val addTransactionForm = AddTransactionForm(
            walletSpinnerPosition = walletSpinnerPosition,
            amount = amountBinding,
            date = dateEditText,
            comment = commentBinding
        )
        sharedViewModel.set(addTransactionForm)
    }

    private fun setAddExpenseFormBeforeAddingExpenseSubGroup() {
        val amountBinding = binding.amountEditText.text.toString().trim()
        val dateEditText = binding.dateEditText.text.toString().trim()
        val commentBinding = binding.commentEditText.text.toString().trim()

        val addTransactionForm = AddTransactionForm(
            groupSpinnerPosition = expenseGroupSpinnerPosition,
            walletSpinnerPosition = walletSpinnerPosition,
            amount = amountBinding,
            date = dateEditText,
            comment = commentBinding
        )
        sharedViewModel.set(addTransactionForm)
    }

    private fun setAddIncomeFormBeforeAddingWallet() {
        val amountBinding = binding.amountEditText.text.toString().trim()
        val commentBinding = binding.commentEditText.text.toString().trim()
        val dateEditText = binding.dateEditText.text.toString().trim()

        val addTransactionForm = AddTransactionForm(
            groupSpinnerPosition = expenseGroupSpinnerPosition,
            subGroupSpinnerPosition = expenseSubGroupSpinnerPosition,
            amount = amountBinding,
            comment = commentBinding,
            date = dateEditText
        )
        sharedViewModel.set(addTransactionForm)
    }

    private fun restoreAddingExpenseForm() {
        sharedViewModel.modelForm.observe(viewLifecycleOwner) { transferForm ->
            if (transferForm != null) {
                binding.expenseGroupSpinner.setSelection(transferForm.groupSpinnerPosition)
                binding.expenseSubGroupSpinner.setSelection(transferForm.subGroupSpinnerPosition)
                binding.walletSpinner.setSelection(transferForm.walletSpinnerPosition)
                binding.amountEditText.setText(transferForm.amount)
                binding.dateEditText.setText(transferForm.date)
                binding.commentEditText.setText(transferForm.comment)
            }
        }
    }

}

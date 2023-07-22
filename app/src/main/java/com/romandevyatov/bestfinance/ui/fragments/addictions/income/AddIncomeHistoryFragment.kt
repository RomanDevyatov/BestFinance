package com.romandevyatov.bestfinance.ui.fragments.addictions.income

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.FragmentAddIncomeHistoryBinding
import com.romandevyatov.bestfinance.db.entities.IncomeGroup
import com.romandevyatov.bestfinance.db.entities.Wallet
import com.romandevyatov.bestfinance.db.entities.relations.IncomeGroupWithIncomeSubGroups
import com.romandevyatov.bestfinance.ui.adapters.spinnerutils.SpinnerAdapter
import com.romandevyatov.bestfinance.ui.validators.EmptyValidator
import com.romandevyatov.bestfinance.utils.Constants
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.AddIncomeHistoryViewModel
import com.romandevyatov.bestfinance.viewmodels.shared.SharedViewModel
import com.romandevyatov.bestfinance.viewmodels.shared.models.AddTransactionForm
import dagger.hilt.android.AndroidEntryPoint
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class AddIncomeHistoryFragment : Fragment() {

    private var _binding: FragmentAddIncomeHistoryBinding? = null
    private val binding get() = _binding!!

    private val addIncomeHistoryViewModel: AddIncomeHistoryViewModel by viewModels()

    private val sharedViewModel: SharedViewModel<AddTransactionForm> by activityViewModels()

    private var incomeGroupSpinnerPosition = 0
    private var incomeSubGroupSpinnerPosition = 0
    private var walletSpinnerPosition = 0

    private var incomeSubGroupSpinnerAdapter: SpinnerAdapter? = null
    private var incomeGroupSpinnerAdapter: SpinnerAdapter? = null

    val args: AddIncomeHistoryFragmentArgs by navArgs()

    val archiveIncomeGroupListener =
        object : SpinnerAdapter.DeleteItemClickListener {

            @RequiresApi(Build.VERSION_CODES.O)
            override fun archive(name: String) {
                addIncomeHistoryViewModel.archiveIncomeGroup(name)
            }
        }

    val archiveIncomeSubGroupListener =
        object : SpinnerAdapter.DeleteItemClickListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun archive(name: String) {
                addIncomeHistoryViewModel.archiveIncomeSubGroup(name)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddIncomeHistoryBinding.inflate(inflater, container, false)
//        markButtonDisable(binding.addIncomeHistoryButton)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initIncomeGroupAndIncomeSubGroupSpinner()
        initWalletSpinner()

        setDateEditText()

        setButtonOnClickListener()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setButtonOnClickListener() {
        binding.addIncomeHistoryButton.setOnClickListener {
            val incomeSubGroupNameBinding = binding.incomeSubGroupSpinner.text.toString()
            val amountBinding = binding.amountEditText.text.toString()
            val commentBinding = binding.commentEditText.text.toString()
            val dateBinding = binding.dateEditText.text.toString()
            val walletNameBinding = binding.walletSpinner.text.toString()

            val incomeSubGroupNameBindingValidation = EmptyValidator(incomeSubGroupNameBinding).validate()
            binding.incomeSubGroupSpinnerLayout.error = if (!incomeSubGroupNameBindingValidation.isSuccess) getString(incomeSubGroupNameBindingValidation.message) else null

            val amountBindingValidation = EmptyValidator(amountBinding).validate()
            binding.amountTextInputLayout.error = if (!amountBindingValidation.isSuccess) getString(amountBindingValidation.message) else null

            val walletNameBindingValidation = EmptyValidator(walletNameBinding).validate()
            binding.walletSpinnerLayout.error = if (!walletNameBindingValidation.isSuccess) getString(walletNameBindingValidation.message) else null

            if (incomeSubGroupNameBindingValidation.isSuccess
                && amountBindingValidation.isSuccess
                && walletNameBindingValidation.isSuccess) {
                addIncomeHistoryViewModel.addIncomeHistory(
                    incomeSubGroupNameBinding,
                    amountBinding.toDouble(),
                    commentBinding,
                    dateBinding,
                    walletNameBinding
                )

                sharedViewModel.set(null)
                findNavController().navigate(R.id.action_navigation_add_income_to_navigation_home)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setDateEditText() {
        val myCalendar = Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener() {
                view, year, month, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH,month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDate(myCalendar)
        }

        val dateET = binding.dateEditText
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
                findNavController().navigate(R.id.action_navigation_add_income_to_navigation_home)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun getIncomeGroupItemsForSpinner(incomeGroupList: List<IncomeGroup>?): ArrayList<String> {
        val spinnerItems = ArrayList<String>()

        incomeGroupList?.forEach { it ->
            spinnerItems.add(it.name)
        }
        spinnerItems.add(Constants.ADD_NEW_INCOME_GROUP)

        return spinnerItems
    }

    private fun initIncomeGroupAndIncomeSubGroupSpinner() {
        val subGroupSpinnerItems = ArrayList<String>()
        subGroupSpinnerItems.add(Constants.ADD_NEW_INCOME_GROUP)

        incomeSubGroupSpinnerAdapter = SpinnerAdapter(requireContext(), R.layout.item_with_del, subGroupSpinnerItems, Constants.ADD_NEW_INCOME_GROUP, archiveIncomeSubGroupListener)

        addIncomeHistoryViewModel.getAllIncomeGroupNotArchived().observe(viewLifecycleOwner) { incomeGroups ->
            val spinnerGroupItems = getIncomeGroupItemsForSpinner(incomeGroups)

            incomeGroupSpinnerAdapter = SpinnerAdapter(requireContext(), R.layout.item_with_del, spinnerGroupItems, Constants.ADD_NEW_INCOME_GROUP, archiveIncomeGroupListener)
            binding.incomeGroupSpinner.setAdapter(incomeGroupSpinnerAdapter)

            restoreAddingIncomeForm()

            incomeGroupSpinnerAdapter?.let { setSpinnersArgs() }
        }

        setIncomeGroupSpinnerOnClickListener()

        binding.incomeSubGroupSpinner.setAdapter(incomeSubGroupSpinnerAdapter)

        setIncomeSubGroupSpinnerListener()
    }

    private fun setIncomeGroupSpinnerOnClickListener() {
        binding.incomeGroupSpinner.setOnItemClickListener {
                parent, view, position, rowId ->
            incomeGroupSpinnerPosition = position

            binding.incomeSubGroupSpinner.isVisible = true

            val selectedIncomeGroupName =
                binding.incomeGroupSpinner.text.toString()

            if (selectedIncomeGroupName == Constants.ADD_NEW_INCOME_GROUP) {
                setAddIncomeFormBeforeAddingIncomeGroup()

                val action =
                    AddIncomeHistoryFragmentDirections.actionNavigationAddIncomeToNavigationAddNewIncomeGroup()
                findNavController().navigate(action)
            }

            // TODO: getIncomeGroupNotArchivedWithIncomeSubGroupsNotArchivedByIncomeGroupNameLiveData doesn't work
            addIncomeHistoryViewModel.getIncomeGroupNotArchivedWithIncomeSubGroupsNotArchivedByIncomeGroupNameLiveData(selectedIncomeGroupName)
                .observe(viewLifecycleOwner) { incomeGroupWithIncomeSubGroups ->
                    val spinnerSubItems = getSpinnerSubItemsNotArchived(incomeGroupWithIncomeSubGroups)
                    incomeSubGroupSpinnerAdapter = SpinnerAdapter(requireContext(), R.layout.item_with_del, spinnerSubItems, Constants.ADD_NEW_INCOME_SUB_GROUP, archiveIncomeSubGroupListener)
                    binding.incomeSubGroupSpinner.setAdapter(incomeSubGroupSpinnerAdapter)
                }
        }
    }

    private fun setIncomeSubGroupSpinnerListener() {
        binding.incomeSubGroupSpinner.setOnItemClickListener {
                parent, view, position, rowId ->
            incomeSubGroupSpinnerPosition = position
            val selectedIncomeSubGroupName = binding.incomeSubGroupSpinner.text.toString()

            if (selectedIncomeSubGroupName == Constants.ADD_NEW_INCOME_SUB_GROUP) {
                setAddIncomeFormBeforeAddingIncomeSubGroup()

                val action = AddIncomeHistoryFragmentDirections.actionNavigationAddIncomeToNavigationAddNewSubIncomeGroup()
                action.incomeGroupName = binding.incomeGroupSpinner.text.toString()
                findNavController().navigate(action)
            }
        }
    }

    private fun setSpinnersArgs() {
        if (args.incomeGroupName != null && args.incomeGroupName!!.isNotBlank()) {
            val spinnerPosition = incomeGroupSpinnerAdapter!!.getPosition(args.incomeGroupName)

            binding.incomeGroupSpinner.setText(incomeGroupSpinnerAdapter!!.getItem(spinnerPosition))
        }

        if (args.incomeSubGroupName != null && args.incomeSubGroupName!!.isNotBlank()) {
            val selectedGroupName = binding.incomeGroupSpinner.text.toString()
            addIncomeHistoryViewModel.getIncomeGroupNotArchivedWithIncomeSubGroupsNotArchivedByIncomeGroupNameLiveData(selectedGroupName)
                .observe(viewLifecycleOwner) { incomeGroupWithIncomeSubGroups ->
                    val spinnerSubItems = getSpinnerSubItemsNotArchived(incomeGroupWithIncomeSubGroups)
                    incomeSubGroupSpinnerAdapter = SpinnerAdapter(requireContext(), R.layout.item_with_del, spinnerSubItems, Constants.ADD_NEW_INCOME_SUB_GROUP, archiveIncomeSubGroupListener)

                    val spinnerPosition = incomeSubGroupSpinnerAdapter!!.getPosition(args.incomeSubGroupName)

                    binding.incomeSubGroupSpinner.setAdapter(incomeSubGroupSpinnerAdapter)
                    binding.incomeSubGroupSpinner.setText(incomeSubGroupSpinnerAdapter!!.getItem(spinnerPosition))
                }
        }
    }

    private fun getSpinnerSubItemsNotArchived(incomeGroupWithIncomeSubGroups: IncomeGroupWithIncomeSubGroups?): ArrayList<String> {
        val spinnerSubItems = ArrayList<String>()

        incomeGroupWithIncomeSubGroups?.incomeSubGroups?.forEach {
            if (it.archivedDate == null) {
                spinnerSubItems.add(it.name)
            }
        }

        spinnerSubItems.add(Constants.ADD_NEW_INCOME_SUB_GROUP)

        return spinnerSubItems
    }

    private fun initWalletSpinner() {
        addIncomeHistoryViewModel.walletsNotArchivedLiveData.observe(viewLifecycleOwner) { walletList ->

            val spinnerWalletItems = getWalletItemsForSpinner(walletList)

            val archiveWalletListener =
                object : SpinnerAdapter.DeleteItemClickListener {

                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun archive(name: String) {
                        addIncomeHistoryViewModel.archiveWallet(name)
                    }
                }

            val walletSpinnerAdapter = SpinnerAdapter(requireContext(), R.layout.item_with_del, spinnerWalletItems, Constants.ADD_NEW_WALLET, archiveWalletListener)
            binding.walletSpinner.setAdapter(walletSpinnerAdapter)

            setWalletArgs(walletSpinnerAdapter)
        }

        setWalletSpinnerOnItemClickListener()
    }

    private fun setWalletArgs(walletSpinnerAdapter: SpinnerAdapter) {
        if (args.walletName != null && args.walletName!!.isNotBlank()) {
            val spinnerPosition = walletSpinnerAdapter.getPosition(args.walletName)

            binding.walletSpinner.setText(walletSpinnerAdapter.getItem(spinnerPosition))
        }
    }

    private fun setWalletSpinnerOnItemClickListener() {
        binding.walletSpinner.setOnItemClickListener {
                parent, view, position, rowId ->

            walletSpinnerPosition = position

            val selectedWalletName = binding.walletSpinner.text.toString()

            if (selectedWalletName == Constants.ADD_NEW_WALLET) {
                setAddIncomeFormBeforeAddingWallet()

                val action = AddIncomeHistoryFragmentDirections.actionNavigationAddIncomeToNavigationAddWallet()
                action.source = Constants.ADD_INCOME_HISTORY_FRAGMENT
                findNavController().navigate(action)
            }
        }
    }

    private fun getWalletItemsForSpinner(walletList: List<Wallet>?): ArrayList<String> {
        val spinnerItems = ArrayList<String>()

        walletList?.forEach { it ->
            spinnerItems.add(it.name)
        }
        spinnerItems.add(Constants.ADD_NEW_WALLET)

        return spinnerItems
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateDate(calendar: Calendar) {
//        val dateFormat =  //"yyyy-MM-dd HH:mm:ss"
//        val sdf = SimpleDateFormat(dateFormat, Locale.US)
//        binding.dateEditText.setText(sdf.format(calendar.time))
        val iso8601DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        binding.dateEditText.setText(OffsetDateTime.now().format(iso8601DateTimeFormatter))
    }

    private fun setAddIncomeFormBeforeAddingIncomeGroup() {
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

    private fun setAddIncomeFormBeforeAddingIncomeSubGroup() {
        val amountBinding = binding.amountEditText.text.toString().trim()
        val dateEditText = binding.dateEditText.text.toString().trim()
        val commentBinding = binding.commentEditText.text.toString().trim()

        val addTransactionForm = AddTransactionForm(
            groupSpinnerPosition = incomeGroupSpinnerPosition,
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
            groupSpinnerPosition = incomeGroupSpinnerPosition,
            subGroupSpinnerPosition = incomeSubGroupSpinnerPosition,
            amount = amountBinding,
            comment = commentBinding,
            date = dateEditText
        )
        sharedViewModel.set(addTransactionForm)
    }

    private fun restoreAddingIncomeForm() {
        sharedViewModel.modelForm.observe(viewLifecycleOwner) { transferForm ->
            if (transferForm != null) {
                binding.incomeGroupSpinner.setSelection(transferForm.groupSpinnerPosition)
                binding.incomeSubGroupSpinner.setSelection(transferForm.subGroupSpinnerPosition)
                binding.walletSpinner.setSelection(transferForm.walletSpinnerPosition)
                binding.amountEditText.setText(transferForm.amount)
                binding.dateEditText.setText(transferForm.date)
                binding.commentEditText.setText(transferForm.comment)
            }
        }
    }

    private fun markButtonDisable(button: Button) {
        button.isEnabled = false
        button.setTextColor(ContextCompat.getColor(binding.addIncomeHistoryButton.context, R.color.white))
        button.setBackgroundColor(ContextCompat.getColor(binding.addIncomeHistoryButton.context, R.color.black))
    }

    fun showWalletDialog(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Title")

        val input = EditText(requireContext())
        input.hint = "Enter Text"
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
            // Here you get get input text from the Edittext
            var m_Text = input.text.toString()
        })
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

        builder.show()
    }
}

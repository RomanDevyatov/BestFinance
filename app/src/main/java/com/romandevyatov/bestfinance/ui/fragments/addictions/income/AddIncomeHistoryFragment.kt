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
import com.romandevyatov.bestfinance.viewmodels.shared.SharedModifiedViewModel
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
    private val sharedModViewModel: SharedModifiedViewModel<AddTransactionForm> by activityViewModels()

    private var groupSpinnerPositionGlobal: Int? = null
    private var subGroupSpinnerPositionGlobal: Int? = null
    private var walletSpinnerPositionGlobal: Int? = null

    private var subGroupSpinnerAdapterGlobal: SpinnerAdapter? = null
    private var groupSpinnerAdapterGlobal: SpinnerAdapter? = null
    private var walletSpinnerAdapterGlobal: SpinnerAdapter? = null

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

    val archiveWalletListener =
        object : SpinnerAdapter.DeleteItemClickListener {

            @RequiresApi(Build.VERSION_CODES.O)
            override fun archive(name: String) {
                addIncomeHistoryViewModel.archiveWallet(name)
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

        setSpinners()

        setDateEditText()

        setButtonOnClickListener()

//        restoreAddingIncomeForm(groupSpinnerAdapterGlobal, subGroupSpinnerAdapterGlobal, walletSpinnerAdapterGlobal)
    }

    private fun setSpinners() {
        setGroupAndSubGroupSpinnerAdapter()
        setIncomeGroupSpinnerOnClickListener()
        setIncomeSubGroupSpinnerListener()

        setWalletSpinnerAdapter()
        setWalletSpinnerOnItemClickListener()
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

    private fun getIncomeGroupItemsForSpinner(incomeGroupList: List<IncomeGroup>?): ArrayList<String> {
        val spinnerItems = ArrayList<String>()

        incomeGroupList?.forEach { it ->
            spinnerItems.add(it.name)
        }
        spinnerItems.add(Constants.ADD_NEW_INCOME_GROUP)

        return spinnerItems
    }

    private fun setGroupAndSubGroupSpinnerAdapter() {
        addIncomeHistoryViewModel.getAllIncomeGroupNotArchived().observe(viewLifecycleOwner) { incomeGroups ->
            val spinnerGroupItems = getIncomeGroupItemsForSpinner(incomeGroups)

            val groupSpinnerAdapter = SpinnerAdapter(requireContext(), R.layout.item_with_del, spinnerGroupItems, Constants.ADD_NEW_INCOME_GROUP, archiveIncomeGroupListener)
            groupSpinnerAdapterGlobal = groupSpinnerAdapter

            binding.incomeGroupSpinner.setAdapter(groupSpinnerAdapterGlobal)

            setGroupSpinnersArgs(groupSpinnerAdapter)

            setSubGroupSpinnerAdapter()
        }
    }

    private fun setSubGroupSpinnerAdapter() {
        setEmptySubGroupSpinnerAdapter()

        setSubGroupSpinnersArgs()
    }

    private fun setEmptySubGroupSpinnerAdapter() {
        val subGroupSpinnerItems = ArrayList<String>()
        subGroupSpinnerItems.add(Constants.ADD_NEW_INCOME_SUB_GROUP)

        val subGroupAdapter = SpinnerAdapter(requireContext(), R.layout.item_with_del, subGroupSpinnerItems, Constants.ADD_NEW_INCOME_GROUP, archiveIncomeSubGroupListener)
        subGroupSpinnerAdapterGlobal = subGroupAdapter

        binding.incomeSubGroupSpinner.setAdapter(subGroupAdapter)
    }

    private fun setIncomeGroupSpinnerOnClickListener() {
        binding.incomeGroupSpinner.setOnItemClickListener {
                parent, view, position, rowId ->

            resetSubGroupSpinner()

            groupSpinnerPositionGlobal = position

            val selectedGroupName =
                binding.incomeGroupSpinner.text.toString()

            if (selectedGroupName == Constants.ADD_NEW_INCOME_GROUP) {
                setAddIncomeFormBeforeAddIncomeGroup()

                val action =
                    AddIncomeHistoryFragmentDirections.actionNavigationAddIncomeToNavigationAddNewIncomeGroup()
                findNavController().navigate(action)
            }

            // TODO: getIncomeGroupNotArchivedWithIncomeSubGroupsNotArchivedByIncomeGroupNameLiveData doesn't work
            addIncomeHistoryViewModel.getIncomeGroupNotArchivedWithIncomeSubGroupsNotArchivedByIncomeGroupNameLiveData(selectedGroupName)
                .observe(viewLifecycleOwner) { incomeGroupWithIncomeSubGroups ->
                    val spinnerSubItems = getSpinnerSubItemsNotArchived(incomeGroupWithIncomeSubGroups)
                    val adapter = SpinnerAdapter(requireContext(), R.layout.item_with_del, spinnerSubItems, Constants.ADD_NEW_INCOME_SUB_GROUP, archiveIncomeSubGroupListener)

                    subGroupSpinnerAdapterGlobal = adapter
                    binding.incomeSubGroupSpinner.setAdapter(adapter)
                }
        }
    }

    private fun resetSubGroupSpinner() {
        subGroupSpinnerPositionGlobal = null
        binding.incomeSubGroupSpinner.isVisible = true
        binding.incomeSubGroupSpinner.text = null
    }

    private fun setIncomeSubGroupSpinnerListener() {
        binding.incomeSubGroupSpinner.setOnItemClickListener {
                parent, view, position, rowId ->
            subGroupSpinnerPositionGlobal = position
            val selectedIncomeSubGroupName = binding.incomeSubGroupSpinner.text.toString()

            if (selectedIncomeSubGroupName == Constants.ADD_NEW_INCOME_SUB_GROUP) {
                setAddIncomeFormBeforeAddingIncomeSubGroup()

                val action = AddIncomeHistoryFragmentDirections.actionNavigationAddIncomeToNavigationAddNewSubIncomeGroup()
                action.incomeGroupName = binding.incomeGroupSpinner.text.toString()
                findNavController().navigate(action)
            }
        }
    }

    private fun setGroupSpinnersArgs(
        groupSpinnerAdapter: SpinnerAdapter
    ) {
        val groupName = args.incomeGroupName
        if (groupName != null && groupName.isNotBlank()) {
            val spinnerPosition = groupSpinnerAdapter.getPosition(groupName)

            binding.incomeGroupSpinner.setText(groupSpinnerAdapter.getItem(spinnerPosition))
        }
    }

    private fun setSubGroupSpinnersArgs() {
        val groupName = args.incomeGroupName
        val subGroupName = args.incomeSubGroupName

        if (groupName != null
            && groupName.isNotBlank()
            && subGroupName != null
            && subGroupName.isNotBlank()) {
            addIncomeHistoryViewModel.getIncomeGroupNotArchivedWithIncomeSubGroupsNotArchivedByIncomeGroupNameLiveData(groupName)
                .observe(viewLifecycleOwner) { incomeGroupWithIncomeSubGroups ->
                    val spinnerSubItems = getSpinnerSubItemsNotArchived(incomeGroupWithIncomeSubGroups)

                    val subGroupSpinnerAdapter = SpinnerAdapter(requireContext(), R.layout.item_with_del, spinnerSubItems, Constants.ADD_NEW_INCOME_SUB_GROUP, archiveIncomeSubGroupListener)
                    subGroupSpinnerAdapterGlobal = subGroupSpinnerAdapter

                    val subGroupSpinnerPosition = subGroupSpinnerAdapter.getPosition(args.incomeSubGroupName)
                    subGroupSpinnerPositionGlobal = subGroupSpinnerPosition

                    binding.incomeSubGroupSpinner.setAdapter(subGroupSpinnerAdapter)
                    binding.incomeSubGroupSpinner.setText(subGroupSpinnerAdapter.getItem(subGroupSpinnerPosition))
                }
        }
    }

    private fun checkWalletSpinnersValue(
        walletSpinnerAdapter: SpinnerAdapter
    ) {
        if (args.walletName != null && args.walletName!!.isNotBlank()) {
            val spinnerPosition = walletSpinnerAdapter.getPosition(args.walletName)

            val walletName = walletSpinnerAdapter.getItem(spinnerPosition)

            binding.walletSpinner.setText(walletName)
        } else {
            restoreWallet(walletSpinnerAdapter)
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

    private fun setWalletSpinnerAdapter() {
        addIncomeHistoryViewModel.walletsNotArchivedLiveData.observe(viewLifecycleOwner) { walletList ->

            val spinnerWalletItems = getWalletItemsForSpinner(walletList)

            val walletSpinnerAdapter = SpinnerAdapter(requireContext(), R.layout.item_with_del, spinnerWalletItems, Constants.ADD_NEW_WALLET, archiveWalletListener)
            walletSpinnerAdapterGlobal = walletSpinnerAdapter

            binding.walletSpinner.setAdapter(walletSpinnerAdapter)

            checkWalletSpinnersValue(walletSpinnerAdapter)
        }
    }


    private fun setWalletSpinnerOnItemClickListener() {
        binding.walletSpinner.setOnItemClickListener {
                parent, view, position, rowId ->

            walletSpinnerPositionGlobal = position

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

    private fun setAddIncomeFormBeforeAddIncomeGroup() {
        val amountBinding = binding.amountEditText.text.toString().trim()
        val dateEditText = binding.dateEditText.text.toString().trim()
        val commentBinding = binding.commentEditText.text.toString().trim()

        val addTransactionForm = AddTransactionForm(
            walletSpinnerPosition = walletSpinnerPositionGlobal,
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
            groupSpinnerPosition = groupSpinnerPositionGlobal,
            walletSpinnerPosition = walletSpinnerPositionGlobal,
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
            groupSpinnerPosition = groupSpinnerPositionGlobal,
            subGroupSpinnerPosition = subGroupSpinnerPositionGlobal,
            amount = amountBinding,
            comment = commentBinding,
            date = dateEditText
        )
        sharedViewModel.set(addTransactionForm)
    }

    private fun restoreWallet( // need to save before moving to retrieve value here
        walletSpinnerAdapter: SpinnerAdapter?
    ) {
        val mod = sharedModViewModel.modelForm
        binding.walletSpinner.setText(mod?.walletSpinnerPosition?.let {
            walletSpinnerAdapter?.getItem(
                it
            )
        })
    }

    private fun restoreAddingIncomeForm(
        groupSpinnerAdapter: SpinnerAdapter?,
        subGroupSpinnerAdapter: SpinnerAdapter?,
        walletSpinnerAdapter: SpinnerAdapter?
    ) {
        sharedViewModel.modelForm.observe(viewLifecycleOwner) { addTransactionForm ->
            if (addTransactionForm != null) {
                addTransactionForm.groupSpinnerPosition?.let {
                    binding.incomeGroupSpinner.setText(groupSpinnerAdapter?.getItem(it))
                }

                addTransactionForm.subGroupSpinnerPosition?.let {
                    binding.incomeSubGroupSpinner.setText(subGroupSpinnerAdapter?.getItem(it))
                }

                addTransactionForm.walletSpinnerPosition?.let {
                    binding.walletSpinner.setText(walletSpinnerAdapter?.getItem(it))
                }

                binding.amountEditText.setText(addTransactionForm.amount)
                binding.dateEditText.setText(addTransactionForm.date)
                binding.commentEditText.setText(addTransactionForm.comment)
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

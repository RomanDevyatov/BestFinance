package com.romandevyatov.bestfinance.ui.fragments.addiction.income

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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.FragmentAddIncomeHistoryBinding
import com.romandevyatov.bestfinance.ui.adapters.spinnerutils.CustomSpinnerAdapter
import com.romandevyatov.bestfinance.ui.adapters.spinnerutils.SpinnerUtils
import com.romandevyatov.bestfinance.utils.Constants
import com.romandevyatov.bestfinance.utils.Constants.ADD_NEW_WALLET_STRING
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.AddIncomeHistoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@AndroidEntryPoint
class AddIncomeHistoryFragment : Fragment() {

    private lateinit var binding: FragmentAddIncomeHistoryBinding

    private val addIncomeHistoryViewModel: AddIncomeHistoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddIncomeHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    val args: AddIncomeHistoryFragmentArgs by navArgs()

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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddIncomeHistoryBinding.bind(view)

        initIncomeGroupAndIncomeSubGroupSpinner()
        initWalletSpinner()

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

        binding.addIncomeHistoryButton.setOnClickListener {

            if (true) {// if (isFormValid()) {
                val incomeSubGroupNameBinding = binding.incomeSubGroupSpinner.selectedItem.toString()
                val amountBinding = binding.amountEditText.text.toString().toDouble()
                val commentBinding = binding.commentEditText.text.toString()
                val dateBinding = binding.dateEditText.text.toString()
                val walletNameBinding = binding.walletSpinner.selectedItem.toString()
                addIncomeHistoryViewModel.addIncomeHistory(incomeSubGroupNameBinding, amountBinding, commentBinding, dateBinding, walletNameBinding)

                findNavController().navigate(R.id.action_navigation_add_income_to_navigation_home)

//            val str = binding.dateEditText.text.toString()
//            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
//            val dateTime: LocalDateTime = LocalDateTime.parse(str, formatter)
            }

        }
    }

    private fun isFormValid(): Boolean {
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateDate(calendar: Calendar) {
//        val dateFormat =  //"yyyy-MM-dd HH:mm:ss"
//        val sdf = SimpleDateFormat(dateFormat, Locale.US)
//        binding.dateEditText.setText(sdf.format(calendar.time))
        val iso8601DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        binding.dateEditText.setText(OffsetDateTime.now().format(iso8601DateTimeFormatter))
    }

    private fun initIncomeGroupAndIncomeSubGroupSpinner() {
        addIncomeHistoryViewModel.getAllIncomeGroupNotArchived().observe(viewLifecycleOwner) { incomeGroupList ->
            val spinnerItems = ArrayList<String>()
            spinnerItems.add("Income group")
            incomeGroupList?.forEach { it ->
//                incomeGroupSpinnerAdapter.add(it.name)
                spinnerItems.add(it.name)
            }
            spinnerItems.add(Constants.ADD_NEW_INCOME_GROUP)

            val incomeGroupSpinnerAdapter = CustomSpinnerAdapter(requireContext(), spinnerItems)
            binding.incomeGroupSpinner.adapter = incomeGroupSpinnerAdapter

//            incomeGroupSpinnerAdapter.clear()
//            incomeGroupSpinnerAdapter.add("Income group")
//            incomeGroupSpinnerAdapter.add(Constants.ADD_NEW_INCOME_GROUP)

            if (args.incomeGroupName != null && args.incomeGroupName!!.isNotBlank()) {
                val spinnerPosition = incomeGroupSpinnerAdapter.getPosition(args.incomeGroupName)

                binding.incomeGroupSpinner.setSelection(spinnerPosition)
            }
        }

        val incomeSubGroupArraySpinner = SpinnerUtils.getArraySpinner(requireContext())
        incomeSubGroupArraySpinner.add(Constants.INCOME_SUB_GROUP)

        binding.incomeGroupSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val selectedIncomeGroupName = binding.incomeGroupSpinner.getItemAtPosition(position).toString()
                if (selectedIncomeGroupName == Constants.ADD_NEW_INCOME_GROUP) {
                    val action = AddIncomeHistoryFragmentDirections.actionNavigationAddIncomeToNavigationAddNewIncomeGroup()
                    findNavController().navigate(action)
                }

                addIncomeHistoryViewModel.getIncomeGroupWithIncomeSubGroupsByIncomeGroupNameAndNotArchived(selectedIncomeGroupName)
                    .observe(viewLifecycleOwner) { incomeGroupWithIncomeSubGroups ->
                        incomeSubGroupArraySpinner.clear()
                        incomeSubGroupArraySpinner.add(Constants.INCOME_SUB_GROUP)
                        if (incomeGroupWithIncomeSubGroups != null) {
                            val subGroups = incomeGroupWithIncomeSubGroups.incomeSubGroups

                            subGroups.forEach {
                                incomeSubGroupArraySpinner.add(it.name)
                            }
                        }
                        incomeSubGroupArraySpinner.add(Constants.ADD_NEW_SUB_INCOME_GROUP)

                        if (args.incomeSubGroupName != null && args.incomeSubGroupName!!.isNotBlank()) {
                            val spinnerPosition = incomeSubGroupArraySpinner.getPosition(args.incomeSubGroupName)

                            binding.incomeSubGroupSpinner.setSelection(spinnerPosition)
                        }
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

                if (selectedIncomeSubGroupName == Constants.ADD_NEW_SUB_INCOME_GROUP) {
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
        addIncomeHistoryViewModel.walletsNotArchivedLiveData.observe(viewLifecycleOwner) { walletList ->
            val walletSpinnerAdapter = SpinnerUtils.getArraySpinner(requireContext())

            walletSpinnerAdapter.clear()
            walletSpinnerAdapter.add("Wallet")

            val names = walletList.map { it.name }
            walletSpinnerAdapter.addAll(names)

            walletSpinnerAdapter.add(ADD_NEW_WALLET_STRING)

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


                if (selectedIncomeSubGroupName == ADD_NEW_WALLET_STRING) {
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


}

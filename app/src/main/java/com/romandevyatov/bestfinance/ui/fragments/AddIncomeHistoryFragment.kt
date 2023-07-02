package com.romandevyatov.bestfinance.ui.fragments

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
import com.romandevyatov.bestfinance.ui.adapters.spinnerutils.SpinnerUtils
import com.romandevyatov.bestfinance.viewmodels.newapproach.AddIncomeHistoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*


@AndroidEntryPoint
class AddIncomeHistoryFragment : Fragment() {

    private lateinit var binding: FragmentAddIncomeHistoryBinding

    private val addIncomeHistoryViewModel: AddIncomeHistoryViewModel by viewModels()

    companion object {
        const val addNewWalletString: String = "Add new wallet"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddIncomeHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    /*
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
     */

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
        val incomeGroupSpinnerAdapter = SpinnerUtils.getArraySpinner(requireContext())
        binding.incomeGroupSpinner.adapter = incomeGroupSpinnerAdapter
        addIncomeHistoryViewModel.getAllIncomeGroupNotArchived().observe(viewLifecycleOwner) { incomeGroupList ->
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

        val incomeSubGroupArraySpinner = SpinnerUtils.getArraySpinner(requireContext())
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

                addIncomeHistoryViewModel.getIncomeGroupWithIncomeSubGroupsByIncomeGroupNameAndNotArchived(selectedIncomeGroupName)
                    .observe(viewLifecycleOwner) { incomeGroupWithIncomeSubGroups ->
                        incomeSubGroupArraySpinner.clear()
                        incomeSubGroupArraySpinner.add("Income sub group")
                        if (incomeGroupWithIncomeSubGroups != null) {
                            val subGroups = incomeGroupWithIncomeSubGroups.incomeSubGroups

                            subGroups.forEach {
                                incomeSubGroupArraySpinner.add(it.name)
                            }
                        }
                        incomeSubGroupArraySpinner.add("Add new sub income group")

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
        val spinnerAdapter = SpinnerUtils.getArraySpinner(requireContext())

        addIncomeHistoryViewModel.walletsNotArchivedLiveData.observe(viewLifecycleOwner) { walletList ->
            spinnerAdapter.clear()
            spinnerAdapter.add("Wallet")

            walletList?.forEach { it ->
                spinnerAdapter.add(it.name)
            }

            spinnerAdapter.add(addNewWalletString)
        }

        binding.walletSpinner.adapter = spinnerAdapter
        binding.walletSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedIncomeSubGroupName = binding.walletSpinner.getItemAtPosition(position).toString()

                if (selectedIncomeSubGroupName == addNewWalletString) {
                    val action = AddIncomeHistoryFragmentDirections.actionNavigationAddIncomeToNavigationAddNewWallet2()
                    findNavController().navigate(action)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
    }


}
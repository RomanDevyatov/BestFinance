package com.romandevyatov.bestfinance.ui.fragments


import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.FragmentAddExpenseHistoryBinding
import com.romandevyatov.bestfinance.db.entities.ExpenseHistory
import com.romandevyatov.bestfinance.db.entities.Wallet
import com.romandevyatov.bestfinance.viewmodels.*
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
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
    ): View? {
        binding = FragmentAddExpenseHistoryBinding.inflate(inflater, container, false)



        initExpenseGroupSpinner()
        initWalletSpinner()

        return binding.root
    }

    private fun getSpinnerAdapter(name: String): ArrayAdapter<String> {
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
                        //here it is possible to define color for other items by
                        //view.setTextColor(Color.RED)
                    }

                    return view
                }
            }

        spinnerAdapter.add(name)

        return spinnerAdapter
    }

    private fun initExpenseGroupSpinner() {
        val expenseGroupSpinnerAdapter = getSpinnerAdapter("Expense group")

        expenseGroupViewModel.expenseGroupsLiveData.observe(viewLifecycleOwner) { expenseGroupList ->
            expenseGroupList?.forEach { it ->
                expenseGroupSpinnerAdapter.add(it.name)
            }

            if (args.expenseGroupName != null && args.expenseGroupName!!.isNotBlank()) {
                val spinnerPosition = expenseGroupSpinnerAdapter.getPosition(args.expenseGroupName.toString())
                binding.expenseGroupSpinner.setSelection(spinnerPosition)
            }
        }



        val expenseGroupSpinner = binding.expenseGroupSpinner
        expenseGroupSpinner.adapter = expenseGroupSpinnerAdapter

        expenseGroupSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val item = binding.expenseGroupSpinner.getItemAtPosition(position).toString()

                expenseGroupViewModel.getExpenseGroupWithExpenseSubGroupsByExpenseGroupNameLiveData(item)!!.observe(viewLifecycleOwner) { list ->
                    if (list != null) {
                        val subGroups = list.expenseSubGroups

                        val expenseSubGroupSpinnerAdapter = getSpinnerAdapter("Expense sub group")

                        subGroups.forEach {
                            expenseSubGroupSpinnerAdapter.add(it.name)
                        }

                        val expenseSubGroupSpinner = binding.expenseSubGroupSpinner
                        expenseSubGroupSpinner.adapter = expenseSubGroupSpinnerAdapter

//                        for (i in 0 until subGroups.size) {
//                            if (subGroups[i].name == args.expenseGroupName) {
//                                expenseSubGroupSpinner.setSelection(i)
//                            }
//                        }

                    }
                }



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

    val args: AddExpenseHistoryFragmentArgs by navArgs() // new

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

        expenseSubGroupViewModel.expenseSubGroupsLiveData.observe(viewLifecycleOwner){
            //In such case, we won't observe multiple LiveData but one
        }
//Then during our ClickListener, we just do API method call without any callback.

        binding.addExpenseButton.setOnClickListener {
            val expenseSubGroupNameBinding = binding.expenseSubGroupSpinner.selectedItem.toString()

            val expenseSubGroup = expenseSubGroupViewModel.getExpenseSubGroupByName(expenseSubGroupNameBinding)

            val walletNameBinding = binding.walletSpinner.selectedItem.toString()
            val wallet = walletViewModel.walletsLiveData.value?.filter { wallet ->
                wallet.name == walletNameBinding
            }!!.single()
            val walletId = wallet.id!!.toLong()

            val amountBinding = binding.amountEditText.text.toString().toDouble()

            val id = expenseSubGroup.id!!

            expenseHistoryViewModel.insertExpenseHistory(
                ExpenseHistory(
                    expenseSubGroupId = id,
                    amount = amountBinding,
                    comment = binding.commentEditText.text.toString(),
                    date = Date(binding.dateEditText.text.toString()),
                    walletId = walletId
                )
            )

            val updatedBalance = wallet.balance - amountBinding
            walletViewModel.updateWallet(
                Wallet(
                    id = wallet.id,
                    name = wallet.name,
                    balance = updatedBalance
                )
            )


            findNavController().navigate(R.id.action_navigation_add_expense_to_navigation_home)
        }
    }

    fun updateDate(calendar: Calendar){
        val dateFormat = "dd/MM/yy";
        val sdf = SimpleDateFormat(dateFormat, Locale.US);
        binding.dateEditText.setText(sdf.format(calendar.time));
    }



}
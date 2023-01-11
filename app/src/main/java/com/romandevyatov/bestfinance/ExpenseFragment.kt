package com.romandevyatov.bestfinance

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.romandevyatov.bestfinance.adapters.GroupListAdapter
import com.romandevyatov.bestfinance.databinding.FragmentExpenseBinding
import com.romandevyatov.bestfinance.models.IncomeGroup


class ExpenseFragment : Fragment() {
    private var _binding : FragmentExpenseBinding? = null

    private val binding get() = _binding!!

    class IncomeViewModel : ViewModel() {

        private val _text = MutableLiveData<String>().apply {
            value = "This is expense Fragment"
        }
        val text: LiveData<String> = _text
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val expenseViewModel =
            ViewModelProvider(this).get(IncomeViewModel::class.java)

        _binding = FragmentExpenseBinding.inflate(inflater, container, false)
        val root: View = binding.root

        expenseViewModel.text.observe(viewLifecycleOwner) {
            binding.textExpense.text = it
        }

        binding.viewButton.setOnClickListener {
            viewRecord()
        }

        binding.saveButton.setOnClickListener {
            saveRecord()
        }

        binding.updateButton.setOnClickListener {
            updateRecord()
        }

        binding.deleteButton.setOnClickListener {
            deleteRecord()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun saveRecord() {
        val id = binding.expenseGroupIdEditText.text.toString()
        val name = binding.expenseGroupNameEditText.text.toString()

        val expenseGroupDatabaseHandler: IncomeGroupDatabaseHandler = IncomeGroupDatabaseHandler(requireActivity())
        if (id.trim() != "" && name.trim() != "" ) {
            val status = expenseGroupDatabaseHandler.addIncomeGroup(IncomeGroup(Integer.parseInt(id), name))

            if (status > -1) {
                Toast.makeText(activity,"record save", Toast.LENGTH_LONG).show()
                binding.expenseGroupIdEditText.text.clear()
                binding.expenseGroupNameEditText.text.clear()
            }
        } else {
            Toast.makeText(activity,"id or name or email cannot be blank", Toast.LENGTH_LONG).show()
        }

    }

    private fun viewRecord() {
        val expenseGroupDatabaseHandler: IncomeGroupDatabaseHandler = IncomeGroupDatabaseHandler(requireActivity())

        val expenseGroups: List<IncomeGroup> = expenseGroupDatabaseHandler.getAllIncomeGroups()

        val expenseGroupIds = Array<String>(expenseGroups.size) {"0"}
        val expenseGroupNames = Array<String>(expenseGroups.size) {"null"}

        var index = 0
        for (expenseGroup in expenseGroups) {
            expenseGroupIds[index] = expenseGroup.id.toString()
            expenseGroupNames[index] = expenseGroup.name
            ++index
        }

        val expenseGroupListAdapter = GroupListAdapter(requireActivity(), expenseGroupIds, expenseGroupNames)
        binding.listView.adapter = expenseGroupListAdapter
    }

    fun updateRecord() {
        val dialogBuilder = AlertDialog.Builder(requireActivity())
        val inflater = this.layoutInflater

        val dialogView = inflater.inflate(R.layout.update_group_by_id_template, null)
        dialogBuilder.setView(dialogView)

        val idEditText = dialogView.findViewById(R.id.updateId) as EditText
        val nameEditText = dialogView.findViewById(R.id.updateName) as EditText

        dialogBuilder.setTitle("Update Record")
        dialogBuilder.setMessage("Enter data below")
        dialogBuilder.setPositiveButton("Update", DialogInterface.OnClickListener { _, _ ->

            val updateId = idEditText.text.toString()
            val updateName = nameEditText.text.toString()

            val incomGroupDatabaseHandler: IncomeGroupDatabaseHandler = IncomeGroupDatabaseHandler(requireActivity())
            if (updateId.trim() != "" && updateName.trim() != "") {
                val status = incomGroupDatabaseHandler.updateIncomeGroup(IncomeGroup(Integer.parseInt(updateId), updateName))
                if(status > -1) {
                    Toast.makeText(activity,"Record is updated", Toast.LENGTH_LONG).show()
                }
            } else{
                Toast.makeText(activity,"id or name cannot be blank", Toast.LENGTH_LONG).show()
            }
        })
        dialogBuilder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
            // pass
        })

        val b = dialogBuilder.create()
        b.show()
    }

    fun deleteRecord() {
        val dialogBuilder = AlertDialog.Builder(requireActivity())
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.delete_group_by_id_template, null)
        dialogBuilder.setView(dialogView)

        val idEditText = dialogView.findViewById(R.id.deleteId) as EditText

        dialogBuilder.setTitle("Delete Record")
        dialogBuilder.setMessage("Enter id below")
        dialogBuilder.setPositiveButton("Delete", DialogInterface.OnClickListener { _, _ ->
            val deleteId = idEditText.text.toString()
            val incomGroupDatabaseHandler: IncomeGroupDatabaseHandler= IncomeGroupDatabaseHandler(requireActivity())
            if (deleteId.trim() != "") {
                val status = incomGroupDatabaseHandler.deleteIncomeGroup(IncomeGroup(Integer.parseInt(deleteId),""))
                if (status > -1){
                    Toast.makeText(activity,"Record is deleted", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(activity,"id or name or email cannot be blank", Toast.LENGTH_LONG).show()
            }

        })
        dialogBuilder.setNegativeButton("Cancel", DialogInterface.OnClickListener { _, _ ->
            //pass
        })

        val b = dialogBuilder.create()
        b.show()
    }

}
package com.romandevyatov.bestfinance

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.romandevyatov.bestfinance.adapters.IncomeGroupListAdapter
import com.romandevyatov.bestfinance.databinding.FragmentIncomeBinding
import com.romandevyatov.bestfinance.models.IncomeGroup


class IncomeFragment : Fragment() {
    private var _binding : FragmentIncomeBinding? = null

    private val binding get() = _binding!!

    class IncomeViewModel : ViewModel() {

        private val _text = MutableLiveData<String>().apply {
            value = "This is income Fragment"
        }
        val text: LiveData<String> = _text
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val incomeViewModel =
            ViewModelProvider(this).get(IncomeViewModel::class.java)

        _binding = FragmentIncomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        incomeViewModel.text.observe(viewLifecycleOwner) {
            binding.textIncome.text = it
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
        val id = binding.incomeGroupIdEditText.text.toString()
        val name = binding.incomeGroupNameEditText.text.toString()

        val databaseHandler: DatabaseHandler = DatabaseHandler(requireActivity())
        if (id.trim() != "" && name.trim() != "" ) {
            val status = databaseHandler.addIncomeGroup(IncomeGroup(Integer.parseInt(id), name))

            if (status > -1) {
                Toast.makeText(activity,"record save", Toast.LENGTH_LONG).show()
                binding.incomeGroupIdEditText.text.clear()
                binding.incomeGroupNameEditText.text.clear()
            }
        } else {
            Toast.makeText(activity,"id or name or email cannot be blank", Toast.LENGTH_LONG).show()
        }

    }

    private fun viewRecord() {
        val databaseHandler: DatabaseHandler = DatabaseHandler(requireActivity())

        val incomeGroups: List<IncomeGroup> = databaseHandler.getAllIncomeGroups()

        val incomeGroupIds = Array<String>(incomeGroups.size) {"0"}
        val incomeGroupNames = Array<String>(incomeGroups.size) {"null"}

        var index = 0
        for (incomeGroup in incomeGroups) {
            incomeGroupIds[index] = incomeGroup.id.toString()
            incomeGroupNames[index] = incomeGroup.name
            ++index
        }

        val incomeGroupListAdapter = IncomeGroupListAdapter(requireActivity(), incomeGroupIds, incomeGroupNames)
        binding.listView.adapter = incomeGroupListAdapter
    }

    fun updateRecord() {
        val dialogBuilder = AlertDialog.Builder(requireActivity())
        val inflater = this.layoutInflater

        val dialogView = inflater.inflate(R.layout.income_group_update, null)
        dialogBuilder.setView(dialogView)

        val idEditText = dialogView.findViewById(R.id.updateId) as EditText
        val nameEditText = dialogView.findViewById(R.id.updateName) as EditText

        dialogBuilder.setTitle("Update Record")
        dialogBuilder.setMessage("Enter data below")
        dialogBuilder.setPositiveButton("Update", DialogInterface.OnClickListener { _, _ ->

            val updateId = idEditText.text.toString()
            val updateName = nameEditText.text.toString()

            val databaseHandler: DatabaseHandler = DatabaseHandler(requireActivity())
            if (updateId.trim() != "" && updateName.trim() != "") {
                val status = databaseHandler.updateIncomeGroup(IncomeGroup(Integer.parseInt(updateId), updateName))
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
        val dialogView = inflater.inflate(R.layout.income_group_delete, null)
        dialogBuilder.setView(dialogView)

        val idEditText = dialogView.findViewById(R.id.deleteId) as EditText

        dialogBuilder.setTitle("Delete Record")
        dialogBuilder.setMessage("Enter id below")
        dialogBuilder.setPositiveButton("Delete", DialogInterface.OnClickListener { _, _ ->
            val deleteId = idEditText.text.toString()
            val databaseHandler: DatabaseHandler= DatabaseHandler(requireActivity())
            if (deleteId.trim() != "") {
                val status = databaseHandler.deleteIncomeGroup(IncomeGroup(Integer.parseInt(deleteId),""))
                if (status > -1){
                    Toast.makeText(activity,"Record is deleted",Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(activity,"id or name or email cannot be blank",Toast.LENGTH_LONG).show()
            }

        })
        dialogBuilder.setNegativeButton("Cancel", DialogInterface.OnClickListener { _, _ ->
            //pass
        })

        val b = dialogBuilder.create()
        b.show()
    }

}
package com.romandevyatov.bestfinance.ui.fragments.addictions.income

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.FragmentAddIncomeSubGroupBinding
import com.romandevyatov.bestfinance.db.entities.IncomeGroup
import com.romandevyatov.bestfinance.db.entities.IncomeSubGroup
import com.romandevyatov.bestfinance.ui.validators.EmptyValidator
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.AddIncomeSubGroupViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddIncomeSubGroupFragment : Fragment() {

    private var _binding: FragmentAddIncomeSubGroupBinding? = null
    private val binding get() = _binding!!

    private val addSubGroupViewModel: AddIncomeSubGroupViewModel by viewModels()

    private val args: AddIncomeSubGroupFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddIncomeSubGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initGroupSpinner()

        binding.addSubGroupButton.setOnClickListener {

            val incomeSubGroupNameBinding = binding.subGroupNameEditText.text.toString()
            val descriptionBinding = binding.incomeSubGroupDescription.text.toString()
            val selectedIncomeGroupNameBinding = binding.groupSpinner.text.toString()

            val incomeSubGroupNameValidation = EmptyValidator(incomeSubGroupNameBinding).validate()
            binding.incomeSubGroupNameTextInputLayout.error = if (!incomeSubGroupNameValidation.isSuccess) getString(incomeSubGroupNameValidation.message) else null

            val incomeGroupSpinnerValidation = EmptyValidator(selectedIncomeGroupNameBinding).validate()
            binding.groupSpinnerLayout.error = if (!incomeGroupSpinnerValidation.isSuccess) getString(incomeGroupSpinnerValidation.message) else null

            if (incomeSubGroupNameValidation.isSuccess
                && incomeGroupSpinnerValidation.isSuccess
            ) {
                addSubGroupViewModel.getIncomeGroupNotArchivedByNameLiveData(
                    selectedIncomeGroupNameBinding
                ).observe(viewLifecycleOwner) {
                    val incomeGroupId = it.id!!

                    addSubGroupViewModel.getIncomeSubGroupByNameLiveData(
                        incomeSubGroupNameBinding
                    ).observe(viewLifecycleOwner) { incomeSubGroup ->

                        val action =
                            AddIncomeSubGroupFragmentDirections.actionNavigationAddIncomeSubGroupToNavigationAddIncome()
                        action.incomeGroupName = selectedIncomeGroupNameBinding
                        action.incomeSubGroupName = incomeSubGroupNameBinding

                        if (incomeSubGroup?.archivedDate != null) {
                            showWalletDialog(
                                requireContext(),
                                incomeSubGroup,
                                "Do you want to unarchive `${incomeSubGroupNameBinding}` income sub group?")
                        } else {
                            val newIncomeSubGroup = IncomeSubGroup(
                                name = incomeSubGroupNameBinding,
                                description = descriptionBinding,
                                incomeGroupId = incomeGroupId
                            )

                            addSubGroupViewModel.insertIncomeSubGroup(newIncomeSubGroup)
                        }

                        findNavController().navigate(action)
                    }

                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initGroupSpinner() {
        addSubGroupViewModel.getAllIncomeGroupNotArchivedLiveData().observe(viewLifecycleOwner) { incomeGroupList ->
            val spinnerItems = getIncomeGroupList(incomeGroupList)

            val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, spinnerItems)

            binding.groupSpinner.setAdapter(spinnerAdapter)

            if (args.incomeGroupName != null && args.incomeGroupName!!.isNotBlank()) {
                binding.groupSpinner.setText(args.incomeGroupName.toString(), false)
            }
        }
    }

    private fun getIncomeGroupList(incomeGroupList: List<IncomeGroup>?): ArrayList<String> {
        val spinnerItems = ArrayList<String>()

        incomeGroupList?.forEach { it ->
            spinnerItems.add(it.name)
        }

        return spinnerItems
    }

    private fun showWalletDialog(
        context: Context,
        incomeSubGroup: IncomeSubGroup,
        message: String
    ) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_alert)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val tvMessage: TextView = dialog.findViewById(R.id.tvMessage)
        val btnYes: Button = dialog.findViewById(R.id.btnYes)
        val bntNo: Button = dialog.findViewById(R.id.btnNo)

        tvMessage.text = message

        btnYes.setOnClickListener {
            addSubGroupViewModel.unarchiveIncomeSubGroup(incomeSubGroup)
            dialog.dismiss()
        }

        bntNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

}

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
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.FragmentAddIncomeGroupBinding
import com.romandevyatov.bestfinance.db.entities.IncomeGroup
import com.romandevyatov.bestfinance.ui.validators.EmptyValidator
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.AddIncomeGroupViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddIncomeGroupFragment : Fragment() {

    private var _binding: FragmentAddIncomeGroupBinding? = null
    private val binding get() = _binding!!

    private val incomeGroupViewModel: AddIncomeGroupViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val callback = object : OnBackPressedCallback(
            true
        ) {
            override fun handleOnBackPressed() {
                val action =
                    AddIncomeGroupFragmentDirections.actionNavigationAddIncomeGroupToNavigationAddIncome()
                action.incomeGroupName = null
                findNavController().navigate(action)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddIncomeGroupBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addNewGroupButton.setOnClickListener {
            val newIncomeGroupNameBinding = binding.groupNameInputEditText.text.toString()
            val newIncomeGroupDescriptionBinding = binding.groupDescriptionInputEditText.text.toString()
            val newIncomeGroupIsPassiveBinding = binding.isPassiveCheckBox.isChecked

            val nameEmptyValidation = EmptyValidator(newIncomeGroupNameBinding).validate()
            binding.groupNameInputLayout.error = if (!nameEmptyValidation.isSuccess) getString(nameEmptyValidation.message) else null

            if (nameEmptyValidation.isSuccess) {
                incomeGroupViewModel.getIncomeGroupNameByNameLiveData(newIncomeGroupNameBinding).observe(viewLifecycleOwner) { incomeGroup ->
                    if (incomeGroup?.archivedDate != null) {
                        showWalletDialog(
                            requireContext(),
                            incomeGroup, "Do you want to unarchive `$newIncomeGroupNameBinding` income group?")
                    } else {
                        incomeGroupViewModel.insertIncomeGroup(
                            IncomeGroup(
                                name = newIncomeGroupNameBinding,
                                description = newIncomeGroupDescriptionBinding,
                                isPassive = newIncomeGroupIsPassiveBinding
                            )
                        )
                        val action =
                            AddIncomeGroupFragmentDirections.actionNavigationAddIncomeGroupToNavigationAddIncome()
                        action.incomeGroupName = newIncomeGroupNameBinding
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

    fun showWalletDialog(context: Context, incomeGroup: IncomeGroup, message: String?) {
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
            incomeGroupViewModel.unarchiveIncomeGroup(incomeGroup)
            dialog.dismiss()
            val action =
                AddIncomeGroupFragmentDirections.actionNavigationAddIncomeGroupToNavigationAddIncome()
            action.incomeGroupName = incomeGroup.name
            findNavController().navigate(action)
        }

        bntNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

}

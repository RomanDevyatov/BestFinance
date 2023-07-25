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

    private val addGroupViewModel: AddIncomeGroupViewModel by viewModels()

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addNewGroupButton.setOnClickListener {
            val groupNameBinding = binding.groupNameInputEditText.text.toString()
            val groupDescriptionBinding = binding.groupDescriptionInputEditText.text.toString()
            val isPassiveBinding = binding.isPassiveCheckBox.isChecked

            val nameEmptyValidation = EmptyValidator(groupNameBinding).validate()
            binding.groupNameInputLayout.error = if (!nameEmptyValidation.isSuccess) getString(nameEmptyValidation.message) else null

            if (nameEmptyValidation.isSuccess) {
                addGroupViewModel.getIncomeGroupNameByNameLiveData(groupNameBinding).observe(viewLifecycleOwner) { incomeGroup ->
                    if (incomeGroup?.archivedDate != null) {
                        showWalletDialog(
                            requireContext(),
                            incomeGroup,
                            "Do you want to unarchive `$groupNameBinding` income group?")
                    } else {
                        addGroupViewModel.insertIncomeGroup(
                            IncomeGroup(
                                name = groupNameBinding,
                                description = groupDescriptionBinding,
                                isPassive = isPassiveBinding
                            )
                        )
                        val action =
                            AddIncomeGroupFragmentDirections.actionNavigationAddIncomeGroupToNavigationAddIncome()
                        action.incomeGroupName = groupNameBinding
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

    fun showWalletDialog(context: Context, group: IncomeGroup, message: String?) {
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
            addGroupViewModel.unarchiveIncomeGroup(group)
            dialog.dismiss()
            val action =
                AddIncomeGroupFragmentDirections.actionNavigationAddIncomeGroupToNavigationAddIncome()
            action.incomeGroupName = group.name
            findNavController().navigate(action)
        }

        bntNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

}

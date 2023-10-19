package com.romandevyatov.bestfinance.ui.fragments.add.group

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.romandevyatov.bestfinance.data.entities.IncomeGroup
import com.romandevyatov.bestfinance.data.validation.EmptyValidator
import com.romandevyatov.bestfinance.databinding.DialogAlertBinding
import com.romandevyatov.bestfinance.databinding.FragmentAddIncomeGroupBinding
import com.romandevyatov.bestfinance.utils.Constants
import com.romandevyatov.bestfinance.utils.WindowUtil
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.AddIncomeGroupViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddIncomeGroupFragment : Fragment() {

    private var _binding: FragmentAddIncomeGroupBinding? = null
    private val binding get() = _binding!!

    private val addGroupViewModel: AddIncomeGroupViewModel by viewModels()

    private var isButtonClickable = true

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

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val action =
                    AddIncomeGroupFragmentDirections.actionNavigationAddIncomeGroupToNavigationAddIncome()
                action.incomeGroupName = null
                action.incomeSubGroupName = null
                action.walletName = null
                findNavController().navigate(action)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        binding.addNewGroupButton.setOnClickListener {
            handleButtonClick(view)
        }
    }

    private fun handleButtonClick(view: View) {
        if (!isButtonClickable) return
        isButtonClickable = false
        view.isEnabled = false

        val groupNameBinding = binding.groupNameInputEditText.text.toString()
        val groupDescriptionBinding = binding.groupDescriptionInputEditText.text.toString()
        val isPassiveBinding = binding.isPassiveCheckBox.isChecked

        val nameEmptyValidation = EmptyValidator(groupNameBinding).validate()
        binding.groupNameInputLayout.error = if (!nameEmptyValidation.isSuccess) getString(nameEmptyValidation.message) else null

        if (nameEmptyValidation.isSuccess) {
            addGroupViewModel.getIncomeGroupNameByNameLiveData(groupNameBinding)?.observe(viewLifecycleOwner) { incomeGroup ->
                if (incomeGroup == null) {
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
                } else if (incomeGroup.archivedDate == null) {
                    WindowUtil.showExistingDialog(
                        requireContext(),
                        "This group `$groupNameBinding` is already existing."
                    )
                } else {
                    showUnarchiveDialog(
                        requireContext(),
                        incomeGroup,
                        "The group with this name is archived. Do you want to unarchive `$groupNameBinding` income group?"
                    )
                }
            }
        }

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            isButtonClickable = true
            view.isEnabled = true
        }, Constants.CLICK_DELAY_MS.toLong())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showUnarchiveDialog(context: Context, group: IncomeGroup, message: String?) {
        val binding = DialogAlertBinding.inflate(LayoutInflater.from(context))
        val dialog = Dialog(context)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.tvMessage.text = message

        binding.btnYes.setOnClickListener {
            addGroupViewModel.unarchiveIncomeGroup(group)
            dialog.dismiss()
            val action =
                AddIncomeGroupFragmentDirections.actionNavigationAddIncomeGroupToNavigationAddIncome()
            action.incomeGroupName = group.name
            findNavController().navigate(action)
        }


        binding.btnNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

}

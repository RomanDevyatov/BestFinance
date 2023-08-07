package com.romandevyatov.bestfinance.ui.fragments.add.income

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
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.FragmentAddIncomeSubGroupBinding
import com.romandevyatov.bestfinance.data.entities.IncomeGroup
import com.romandevyatov.bestfinance.data.entities.IncomeSubGroup
import com.romandevyatov.bestfinance.data.validation.EmptyValidator
import com.romandevyatov.bestfinance.utils.WindowUtil
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.AddIncomeSubGroupViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddIncomeSubGroupFragment : Fragment() {

    private var _binding: FragmentAddIncomeSubGroupBinding? = null
    private val binding get() = _binding!!

    private val addSubGroupViewModel: AddIncomeSubGroupViewModel by viewModels()

    private val args: AddIncomeSubGroupFragmentArgs by navArgs()

    private val clickDelay = 1000 // Set the delay time in milliseconds
    private var isButtonClickable = true

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val callback = object : OnBackPressedCallback(
            true
        ) {
            override fun handleOnBackPressed() {
                val action =
                    AddIncomeSubGroupFragmentDirections.actionNavigationAddIncomeSubGroupToNavigationAddIncome()
                action.incomeGroupName = null
                action.incomeSubGroupName = null
                action.walletName = null
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
        _binding = FragmentAddIncomeSubGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initGroupSpinner()

        binding.addSubGroupButton.setOnClickListener {
            if (!isButtonClickable) return@setOnClickListener
            isButtonClickable = false
            view.isEnabled = false

            val subGroupNameBinding = binding.subGroupNameEditText.text.toString()
            val descriptionBinding = binding.subGroupDescriptionEditText.text.toString()
            val selectedGroupNameBinding = binding.groupSpinner.text.toString()

            val subGroupNameValidation = EmptyValidator(subGroupNameBinding).validate()
            binding.incomeSubGroupNameTextInputLayout.error = if (!subGroupNameValidation.isSuccess) getString(subGroupNameValidation.message) else null

            val groupSpinnerValidation = EmptyValidator(selectedGroupNameBinding).validate()
            binding.groupSpinnerLayout.error = if (!groupSpinnerValidation.isSuccess) getString(groupSpinnerValidation.message) else null

            if (subGroupNameValidation.isSuccess
                && groupSpinnerValidation.isSuccess
            ) {
                addSubGroupViewModel.getIncomeGroupNotArchivedByNameLiveData(
                    selectedGroupNameBinding
                ).observe(viewLifecycleOwner) {
                    val groupId = it.id!!

                    addSubGroupViewModel.getIncomeSubGroupByNameWithIncomeGroupIdLiveData(
                        subGroupNameBinding, groupId
                    )?.observe(viewLifecycleOwner) { subGroup ->
                        if (subGroup == null) {
                            val newIncomeSubGroup = IncomeSubGroup(
                                name = subGroupNameBinding,
                                description = descriptionBinding,
                                incomeGroupId = groupId
                            )

                            addSubGroupViewModel.insertIncomeSubGroup(newIncomeSubGroup)
                            val action =
                                AddIncomeSubGroupFragmentDirections.actionNavigationAddIncomeSubGroupToNavigationAddIncome()
                            action.incomeGroupName = selectedGroupNameBinding
                            action.incomeSubGroupName = subGroupNameBinding
                            findNavController().navigate(action)
                        } else if (subGroup.archivedDate == null) {
                            WindowUtil.showExistingDialog(
                                requireContext(),
                                "This sub group `$subGroupNameBinding` is already existing."
                            )
                        } else {
                            showUnarchiveSubGroupDialog(
                                requireContext(),
                                selectedGroupNameBinding,
                                subGroup,
                                "The sub group with this name is archived. Do you want to unarchive `${subGroupNameBinding}` income sub group?")
                        }
                    }
                }
            }

            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                isButtonClickable = true
                view.isEnabled = true
            }, clickDelay.toLong())
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

            if (args.incomeGroupName?.isNotBlank() == true) {
                binding.groupSpinner.setText(args.incomeGroupName.toString(), false)
            }
        }
    }

    private fun getIncomeGroupList(groups: List<IncomeGroup>?): ArrayList<String> {
        val spinnerItems = ArrayList<String>()

        groups?.forEach {
            spinnerItems.add(it.name)
        }

        return spinnerItems
    }

    private fun showUnarchiveSubGroupDialog(
        context: Context,
        groupName: String,
        subGroup: IncomeSubGroup,
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
            addSubGroupViewModel.unarchiveIncomeSubGroup(subGroup)
            dialog.dismiss()

            val action =
                AddIncomeSubGroupFragmentDirections.actionNavigationAddIncomeSubGroupToNavigationAddIncome()
            action.incomeGroupName = groupName
            action.incomeSubGroupName = subGroup.name
            findNavController().navigate(action)
        }

        bntNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

}

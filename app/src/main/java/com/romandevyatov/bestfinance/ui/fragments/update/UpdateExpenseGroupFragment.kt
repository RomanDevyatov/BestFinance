package com.romandevyatov.bestfinance.ui.fragments.update

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.romandevyatov.bestfinance.data.entities.ExpenseGroup
import com.romandevyatov.bestfinance.data.entities.Wallet
import com.romandevyatov.bestfinance.databinding.FragmentUpdateExpenseGroupBinding
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.WalletViewModel

class UpdateExpenseGroupFragment : Fragment() {
    private var _binding: FragmentUpdateExpenseGroupBinding? = null

    private val binding get() = _binding!!

    private val updateExpenseGroupViewModel: WalletViewModel by viewModels()

    private val args: UpdateWalletFragmentArgs by navArgs()

    private var expenseGroupId: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateExpenseGroupBinding.inflate(inflater, container, false)

//        updateExpenseGroupViewModel.getExpenseGroupByNameLiveData(args.expenseGroupName.toString()).observe(viewLifecycleOwner) { expenseGroup ->
//            binding.reusable.newExpenseGroupName.setText(expenseGroup.name)
//            binding.reusable.descriptionEditText.setText(expenseGroup.description)
//            expenseGroupId = expenseGroup.id
//        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.reusable.addNewExpenseGroupNameButton.setOnClickListener {
//            val nameBinding = binding.reusable.newExpenseGroupName.text.toString()
//            val descriptionBinding = binding.reusable.descriptionEditText.text.toString()
//
//            if (nameBinding != args.expenseGroupName.toString()) {
//                updateExpenseGroupViewModel.getExpenseGroupByNameLiveData(nameBinding)?.observe(viewLifecycleOwner) { group ->
//                    if (group == null) {
//                        val updatedExpenseGroup = ExpenseGroup(
//                            id = expenseGroupId,
//                            name = nameBinding,
//                            description = descriptionBinding
//                        )
//
//                        updateExpenseGroupViewModel.updateExpenseGroupById(updatedExpenseGroup)
//
//                        val action =
//                            UpdateWalletFragmentDirections.actionNavigationUpdateWalletToNavigationWallet()
//                        findNavController().navigate(action)
//                    } else if (group.archivedDate == null){
//                        showExistingDialog(
//                            requireContext(),
//                            "The group with this name `$nameBinding` is already existing."
//                        )
//                    } else {
//                        showUnarchiveDialog(
//                            requireContext(),
//                            group,
//                            "The group with this name is archived. Do you want to unarchive `$nameBinding` group and proceed updating?")
//                    }
//                }
//            } else {
//                val updatedWallet = ExpenseGroup(
//                    id = expenseGroupId,
//                    name = nameBinding,
//                    description = descriptionBinding,
//                    archivedDate =
//                )
//
//                updateExpenseGroupViewModel.updateWalletById(updatedWallet)
//
//                val action =
//                    UpdateWalletFragmentDirections.actionNavigationUpdateWalletToNavigationWallet()
//                findNavController().navigate(action)
//            }
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

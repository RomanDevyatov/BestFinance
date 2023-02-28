package com.romandevyatov.bestfinance.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.romandevyatov.bestfinance.databinding.FragmentAddNewIncomeGroupBinding
import com.romandevyatov.bestfinance.db.entities.IncomeGroup
import com.romandevyatov.bestfinance.viewmodels.IncomeGroupViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddNewIncomeGroupNameFragment : Fragment() {

    private var _binding: FragmentAddNewIncomeGroupBinding? = null
    private val binding get() = _binding!!

    private val incomeGroupViewModel: IncomeGroupViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddNewIncomeGroupBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addNewIncomeGroupNameButton.setOnClickListener {
            incomeGroupViewModel.insertIncomeGroup(
                IncomeGroup(
                    name = binding.newIncomeGroupName.text.toString(),
                    description = binding.newIncomeGroupDescription.text.toString()
                )
            )

            val action = AddNewIncomeGroupNameFragmentDirections.actionNavigationAddNewIncomeGroupToNavigationAddIncome()
            action.incomeGroupName = binding.newIncomeGroupName.text.toString()
            findNavController().navigate(action)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

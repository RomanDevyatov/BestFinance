package com.romandevyatov.bestfinance.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.romandevyatov.bestfinance.databinding.FragmentAddIncomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddIncomeGroupFragment : Fragment() {

    private var _binding: FragmentAddIncomeBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentAddIncomeBinding.bind(view)
    }

}
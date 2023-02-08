package com.romandevyatov.bestfinance.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.FragmentExpenseHistoryBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ExpenseHistoryFragment : Fragment() {

    private var _binding: FragmentExpenseHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpenseHistoryBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }
}
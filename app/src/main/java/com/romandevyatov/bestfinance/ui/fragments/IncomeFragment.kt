package com.romandevyatov.bestfinance.ui.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.FragmentHomeBinding
import com.romandevyatov.bestfinance.databinding.FragmentIncomeBinding
import com.romandevyatov.bestfinance.viewmodels.IncomeGroupViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IncomeFragment : Fragment() {

    private var _binding: FragmentIncomeBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentIncomeBinding.bind(view)
    }
}
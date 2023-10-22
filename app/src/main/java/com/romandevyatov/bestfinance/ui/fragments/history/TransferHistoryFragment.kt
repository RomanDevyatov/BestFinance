package com.romandevyatov.bestfinance.ui.fragments.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.romandevyatov.bestfinance.databinding.FragmentTransferHistoryBinding
import com.romandevyatov.bestfinance.ui.adapters.history.transfer.TransferHistoryAdapter
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.TransferHistoryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransferHistoryFragment : Fragment() {

    private var _binding: FragmentTransferHistoryBinding? = null
    private val binding get() = _binding!!

    private val transferHistoryViewModel: TransferHistoryViewModel by viewModels()

    private val listener = object : TransferHistoryAdapter.ItemClickListener {

        override fun navigate(id: Long) {
            val action = HistoryFragmentDirections.actionHistoryFragmentToUpdateTransferHistoryFragment()
            action.transferHistoryId = id
            findNavController().navigate(action)
        }
    }

    private val transferHistoryAdapter: TransferHistoryAdapter = TransferHistoryAdapter(listener)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransferHistoryBinding.inflate(inflater, container, false)

        initRecyclerView()

        return binding.root
    }

    private fun initRecyclerView() {
        binding.transferHistoryRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.transferHistoryRecyclerView.adapter = transferHistoryAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        transferHistoryViewModel.allTransferHistoriesLiveData.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                transferHistoryAdapter.submitList(it)
            } else {
                Log.d("TransferHistory", "allTransferHistoriesLiveData is null!")
            }
        }

    }
}

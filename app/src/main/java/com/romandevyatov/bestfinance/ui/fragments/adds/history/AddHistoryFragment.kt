package com.romandevyatov.bestfinance.ui.fragments.adds.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

abstract class AddHistoryFragment <VB : ViewBinding, VM : ViewModel>(
    private val inflateMethod : (LayoutInflater, ViewGroup?, Boolean) -> VB
) : Fragment() {

    private var _binding: VB? = null

    // This can be accessed by the child fragments
    // Only valid between onCreateView and onDestroyView
    protected val binding get() = _binding!!

    open var useSharedViewModel: Boolean = false
    protected lateinit var addHistoryViewModel: VM

    // Make it open, so it can be overridden in child fragments
    open fun VB.initialize(){}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = inflateMethod.invoke(inflater, container, false)

        binding.initialize()

        // e.g. we are ProfileFragment<ProfileVM>, get my genericSuperclass which is BaseFragment<ProfileVM>
        // Actually ParameterizedType will give us actual type parameters
        val parameterizedType = javaClass.genericSuperclass as? ParameterizedType

        // now get first actual class, which is the class of VM (ProfileVM in this case)
        @Suppress("UNCHECKED_CAST")
        val vmClass = parameterizedType?.actualTypeArguments?.getOrNull(0) as? Class<VM>?

        if (vmClass != null)
            addHistoryViewModel = ViewModelProvider(this).get(vmClass)
        else
            Log.i("BaseFragment", "could not find VM class for $this")

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




    }
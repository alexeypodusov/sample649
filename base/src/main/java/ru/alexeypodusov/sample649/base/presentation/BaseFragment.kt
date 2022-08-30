package ru.alexeypodusov.sample649.base.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment<VB: ViewBinding, VS, VE, VM: BaseViewModel<VS, VE>>: Fragment() {
    abstract val viewModel: VM

    private var _binding: VB? = null
    protected val binding get() = _binding!!
    abstract val bindingInflater: (LayoutInflater) -> VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return bindingInflater.invoke(layoutInflater)
            .also { _binding = it }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenCreated {
            viewModel.uiState.collect {
                render(it)
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.singleActions.collect {
                handleSingleAction(it)
            }
        }
    }

    private fun handleSingleAction(action: SingleAction) {
        when (action) {
            is SingleAction.ShowSnakbar -> {
                Snackbar.make(
                    binding.root,
                    action.text.getString(requireContext()),
                    action.duration
                ).show()
            }
            is SingleAction.ShowToast -> {
                Toast.makeText(
                    requireContext(),
                    action.text.getString(requireContext()),
                    when (action.isLenghtLong) {
                        true -> Toast.LENGTH_LONG
                        false -> Toast.LENGTH_SHORT
                    }
                ).show()
            }
            is SingleAction.ScreenSpecificAction -> {
                handleScreenSpecificAction(action)
            }
        }
    }

    protected abstract fun render(state: VS)

    protected open fun handleScreenSpecificAction(action: SingleAction) {}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
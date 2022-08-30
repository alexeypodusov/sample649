package ru.alexeypodusov.sample649.graphfeature.presentation.view

import android.graphics.Color
import android.view.LayoutInflater
import androidx.fragment.app.viewModels
import ru.alexeypodusov.sample649.base.presentation.BaseFragment
import ru.alexeypodusov.sample649.graphfeature.databinding.FragmentGraphBinding
import ru.alexeypodusov.sample649.graphfeature.presentation.GraphEvent
import ru.alexeypodusov.sample649.graphfeature.presentation.GraphState
import ru.alexeypodusov.sample649.graphfeature.presentation.GraphViewModel

class GraphFragment
    : BaseFragment<FragmentGraphBinding, GraphState, GraphEvent, GraphViewModel>() {
    override val viewModel: GraphViewModel by viewModels()

    override val bindingInflater = { layoutInflater: LayoutInflater ->
        FragmentGraphBinding.inflate(layoutInflater)
    }

    override fun render(state: GraphState) {
        binding.graph.post {
            binding.graph.setItems(state.items)
        }
    }
}
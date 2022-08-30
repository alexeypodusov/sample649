package ru.alexeypodusov.sample649.charactersfeature.presentation.view

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors
import ru.alexeypodusov.sample649.charactersfeature.di.CharactersComponentProvider
import ru.alexeypodusov.sample649.charactersfeature.presentation.*
import ru.alexeypodusov.sample649.base.presentation.BaseFragment
import ru.alexeypodusov.sample649.charactersfeature.presentation.view.list.CharactersAdapter
import ru.alexeypodusov.sample649.charactersfeature.presentation.view.list.SwipeToDelete
import ru.alexeypodusov.sample649.base.presentation.SingleAction
import ru.alexeypodusov.sample649.charactersfeature.R
import ru.alexeypodusov.sample649.charactersfeature.databinding.FragmentCharactersBinding
import ru.alexeypodusov.searchabletoolbar.State
import javax.inject.Inject

class CharactersFragment :
    BaseFragment<FragmentCharactersBinding, CharactersState, CharactersEvent, CharactersViewModel>() {

    @Inject
    lateinit var viewModelFactory: CharactersViewModel.Factory

    override val viewModel: CharactersViewModel by viewModels {
        viewModelFactory
    }

    override val bindingInflater = { layoutInflater: LayoutInflater ->
        FragmentCharactersBinding.inflate(layoutInflater)
    }

    private var adapter: CharactersAdapter? = null

    override fun onAttach(context: Context) {
        (context.applicationContext as CharactersComponentProvider)
            .provideCharactersComponent()
            .inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.retryButton.setOnClickListener {
            viewModel.obtainEvent(CharactersEvent.RetryButtonClicked)
        }

        with(binding.searchBar) {
            onBackButtonClickedListener = {
                viewModel.obtainEvent(CharactersEvent.ToolbarBackButtonClicked)
            }
            onSearchButtonClickedListener = {
                viewModel.obtainEvent(CharactersEvent.SearchButtonClicked)
            }
            onTextChangedListener = {
                viewModel.obtainEvent(CharactersEvent.SearchInput(it))
            }
        }

        adapter = CharactersAdapter()
            .apply { stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY }
        with(binding.characters) {
            layoutManager = LinearLayoutManager(context)

            itemAnimator = DefaultItemAnimator().apply { addDuration = 300 }

            ItemTouchHelper(
                SwipeToDelete(
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_delete_24)
                        ?.toBitmap()
                ) {
                    viewModel.obtainEvent(CharactersEvent.ItemDeleted(it))
                }
            ).attachToRecyclerView(this)
            this.adapter = this@CharactersFragment.adapter
        }
    }

    override fun render(state: CharactersState) {
        with(binding) {
            progressBar.visibility = View.GONE
            characters.visibility = View.GONE
            emptyLayout.visibility = View.GONE
            errorLayout.visibility = View.GONE
            when (state) {
                is CharactersState.Loading -> progressBar.visibility = View.VISIBLE
                is CharactersState.Loaded -> {
                    characters.post {
                        adapter?.submitList(state.characters)
                    }
                    characters.visibility = View.VISIBLE
                }
                is CharactersState.Empty -> emptyLayout.visibility = View.VISIBLE
                is CharactersState.Error -> {
                    errorMessage.text = state.errorMessage.getString(requireContext())
                    errorLayout.visibility = View.VISIBLE
                }
            }

            searchBar.post {

            }
            with(searchBar) {
                post {
                    when (state.isOpenedSearchBar) {
                        true -> {
                            setState(
                                State.INPUT
                            )
                            requireActivity().window.statusBarColor = toolbarColor
                        }
                        false -> {
                            setState(
                                State.BUTTON
                            )
                            requireActivity().window.statusBarColor = MaterialColors.getColor(
                                context,
                                ru.alexeypodusov.sample649.base.R.attr.secondScreenBackgroundColor,
                                Color.BLACK
                            )
                        }
                    }
                }
            }
        }
    }

    override fun handleScreenSpecificAction(action: SingleAction) {
        if (action is ChangeStatusBarColorAction) {
            when(action.color) {
                is StatusBarColor.SearchBar -> binding.searchBar.toolbarColor
                is StatusBarColor.WindowBackground -> MaterialColors.getColor(
                    requireContext(),
                    android.R.attr.windowBackground,
                    Color.BLACK
                )
                StatusBarColor.CharactersBackground -> MaterialColors.getColor(
                    requireContext(),
                    ru.alexeypodusov.sample649.base.R.attr.secondScreenBackgroundColor,
                    Color.BLACK
                )
            }.run {
                requireActivity().window.statusBarColor = this
            }
        }
    }

    override fun onStart() {
        viewModel.obtainEvent(CharactersEvent.OnStart)
        super.onStart()
    }

    override fun onStop() {
        viewModel.obtainEvent(CharactersEvent.OnStop)
        super.onStop()
    }
}
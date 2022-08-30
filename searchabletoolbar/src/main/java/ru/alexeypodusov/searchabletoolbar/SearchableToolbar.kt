package ru.alexeypodusov.searchabletoolbar

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginStart
import androidx.core.widget.doOnTextChanged
import ru.alexeypodusov.searchabletoolbar.databinding.SearchableToolbarBinding


typealias OnButtonClickedListener = () -> Unit
typealias OnTextChangedListener = (String) -> Unit

class SearchableToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {
    var onBackButtonClickedListener: OnButtonClickedListener? = null
    var onSearchButtonClickedListener: OnButtonClickedListener? = null
    var onTextChangedListener: OnTextChangedListener? = null

    var toolbarColor: Int = 0
        private set

    private val binding: SearchableToolbarBinding

    var currentState = State.BUTTON
        private set

    private var isIgnoreNextTextChanged = false

    init {
        LayoutInflater.from(context)
            .inflate(R.layout.searchable_toolbar, this, true)
        binding = SearchableToolbarBinding.bind(this)

        initAttrs(attrs, defStyleAttr, defStyleRes)
        renderState()

        binding.editText.doOnTextChanged { text, _, _, _ ->
            if(isIgnoreNextTextChanged) {
                isIgnoreNextTextChanged = false
                return@doOnTextChanged
            }
            onTextChangedListener?.invoke(text.toString())
        }

        binding.backButton.setOnClickListener {
            if (currentState == State.INPUT) {
                onBackButtonClickedListener?.invoke()
            }
        }

        binding.backgroundViewStub.setOnClickListener {
            if (currentState == State.BUTTON) {
                onSearchButtonClickedListener?.invoke()
            }
        }

        binding.editText.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                if (currentState == State.INPUT) {
                    onBackButtonClickedListener?.invoke()
                    return@setOnKeyListener true
                }
            }
            return@setOnKeyListener false
        }
    }

    private fun initAttrs(
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) {
        if (attrs == null) return

        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.SearchableToolbar,
            defStyleAttr,
            defStyleRes
        )
        with(binding) {
            with(editText) {
                setTextColor(
                    typedArray.getColor(
                        R.styleable.SearchableToolbar_editTextColor,
                        Color.BLACK
                    )
                )
                setHintTextColor(
                    typedArray.getColor(
                        R.styleable.SearchableToolbar_hintColor,
                        Color.BLACK
                    )
                )
                hint = typedArray.getString(
                    R.styleable.SearchableToolbar_hintText
                )
            }

            with(buttonText) {
                text = typedArray.getString(
                    R.styleable.SearchableToolbar_hintText
                )

                setTextColor(
                    typedArray.getColor(
                        R.styleable.SearchableToolbar_hintColor,
                        Color.BLACK
                    )
                )
            }

            typedArray.getColor(
                R.styleable.SearchableToolbar_iconColor,
                Color.BLACK
            ).let { color ->
                AppCompatResources.getDrawable(context, R.drawable.ic_baseline_arrow_back_24)?.let {
                    it.setTint(color)
                    backButton.setImageDrawable(it)
                }

                AppCompatResources.getDrawable(context, R.drawable.ic_baseline_search_24)?.let {
                    it.setTint(color)
                    searchIcon.setImageDrawable(it)
                }
            }
        }

        currentState = State.fromIndex(
            typedArray.getInt(R.styleable.SearchableToolbar_state, 0),
            State.BUTTON
        )

        with(binding.backgroundView) {
            toolbarColor = typedArray.getColor(R.styleable.SearchableToolbar_backgroundColor, Color.GRAY)
                .also { setColor(it) }
        }
        typedArray.recycle()
    }

    private fun renderState(withAnimation: Boolean = false) {
        with(binding) {
            when (currentState) {
                State.BUTTON -> {
                    editText.text?.clear()
                    editText.clearFocus()
                    (context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
                        .hideSoftInputFromWindow(editText.windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY)
                    editText.visibility = View.INVISIBLE

                    searchIcon.visibility = View.VISIBLE
                    buttonText.visibility = View.VISIBLE

                    backButton.translationZ = 0f
                    if (!withAnimation) {
                        backButton.visibility = View.INVISIBLE
                    } else {
                        searchIcon.alpha = 0f
                        searchIcon.animate().apply {
                            alpha(1f)
                            duration = 200
                            interpolator = LinearInterpolator()
                        }

                        backButton.animate().apply {
                            alpha(0f)
                            rotation(-180f)
                            translationX(searchIcon.marginStart.toFloat())
                            duration = 200
                            interpolator = LinearInterpolator()
                        }
                    }
                }
                State.INPUT -> {
                    if (withAnimation) {
                        backButton.rotation = -180f
                        backButton.alpha = 0f
                        backButton.translationX = searchIcon.marginStart.toFloat()
                        backButton.animate().apply {
                            alpha(1f)
                            rotation(0f)
                            translationX(0f)
                            duration = 200
                            interpolator = LinearInterpolator()
                        }
                    }

                    backButton.visibility = View.VISIBLE
                    backButton.translationZ = 1f
                    editText.visibility = View.VISIBLE
                    editText.requestFocus()
                    (context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
                        .showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)

                    searchIcon.visibility = View.INVISIBLE
                    buttonText.visibility = View.INVISIBLE
                }
            }

            backgroundView.setState(
                currentState,
                withAnimation
            )
        }
    }

    fun setState(state: State) {
        val withAnimation = state.index != currentState.index
        currentState = state
        renderState(withAnimation)
    }

    override fun onSaveInstanceState(): Parcelable {
        return super.onSaveInstanceState().let {
            SavedState(it).apply {
                state = currentState
            }
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        with(state as SavedState) {
            super.onRestoreInstanceState(this.superState)
            isIgnoreNextTextChanged = true
            currentState = this.state ?: State.BUTTON
            renderState()
            binding.backgroundView.setState(currentState, withAnimation = false)
        }
    }

    class SavedState : BaseSavedState {
        var state: State? = null

        constructor(superState: Parcelable?) : super(superState)
        constructor(source: Parcel) : super(source) {
            state = State.fromIndex(source.readInt(), State.BUTTON)
        }


        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(state?.index ?: 0)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(parcel: Parcel): SavedState {
                    return SavedState(parcel)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return Array(size) { null }
                }
            }
        }
    }
}
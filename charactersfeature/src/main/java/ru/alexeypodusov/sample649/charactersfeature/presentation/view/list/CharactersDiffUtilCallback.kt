package ru.alexeypodusov.sample649.charactersfeature.presentation.view.list

import androidx.recyclerview.widget.DiffUtil
import ru.alexeypodusov.sample649.charactersfeature.presentation.Character

internal class CharactersDiffUtilCallback() : DiffUtil.ItemCallback<Character>() {
    override fun areItemsTheSame(oldItem: Character, newItem: Character): Boolean = oldItem.charId == newItem.charId

    override fun areContentsTheSame(oldItem: Character, newItem: Character): Boolean = oldItem == newItem
}
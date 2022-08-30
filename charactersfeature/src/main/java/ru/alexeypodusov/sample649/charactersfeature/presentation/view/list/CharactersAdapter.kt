package ru.alexeypodusov.sample649.charactersfeature.presentation.view.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.alexeypodusov.sample649.charactersfeature.presentation.Character
import ru.alexeypodusov.sample649.charactersfeature.databinding.CharacterItemBinding

internal class CharactersAdapter : ListAdapter<Character, CharactersAdapter.CharacterViewHolder>(
    CharactersDiffUtilCallback()
) {
    class CharacterViewHolder(
        private val binding: CharacterItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(character: Character) {
            binding.name.text = character.name
            binding.nickname.text = character.nickname
            Glide.with(binding.root)
                .load(character.imgUrl)
                .centerCrop()
                .into(binding.img)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder =
        CharacterViewHolder(
            CharacterItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ).apply {
                root.clipToOutline = true
            }
        )

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) =
        holder.bind(currentList[position])


}
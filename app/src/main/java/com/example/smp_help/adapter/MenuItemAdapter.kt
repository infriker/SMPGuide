package com.example.smp_help.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smp_help.data.MenuItem
import com.example.smp_help.databinding.ItemMenuEntryBinding

class MenuItemAdapter(
    private val allItems: List<MenuItem>,
    private val onItemClick: (MenuItem) -> Unit
) : RecyclerView.Adapter<MenuItemAdapter.ViewHolder>() {

    private var filteredItems: List<MenuItem> = allItems.toList()

    inner class ViewHolder(private val binding: ItemMenuEntryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MenuItem) {
            binding.emojiText.text = item.emoji
            binding.titleText.text = item.title
            binding.root.setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMenuEntryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(filteredItems[position])
    }

    override fun getItemCount(): Int = filteredItems.size

    fun filter(query: String) {
        filteredItems = if (query.isBlank()) {
            allItems.toList()
        } else {
            allItems.filter { it.title.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }
}

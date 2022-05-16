package com.example.personapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.personapp.R
import com.example.personapp.data.Person
import com.example.personapp.databinding.ItemPersonBinding

class PersonAdapter : RecyclerView.Adapter<PersonAdapter.ViewHolder>() {

    private var items = mutableListOf<Person>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemPersonBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_person,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.person = item
    }

    fun addAll(list: MutableList<Person>) {
        val start = items.size + 1
        items.addAll(list)
        notifyItemRangeInserted(start, list.size)
    }

    fun clearList() {
        val size = items.size
        items.clear()
        notifyItemRangeRemoved(0, size)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(val binding: ItemPersonBinding) : RecyclerView.ViewHolder(binding.root)

}

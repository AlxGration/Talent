package com.team.alex.talent.presentation.utils

import androidx.recyclerview.widget.DiffUtil

class RecyclerDiffUtil(
    private val oldList: List<DiffUtilItem>,
    private val newList: List<DiffUtilItem>
): DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].areItemsTheSame(newList[newItemPosition])
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].equals(newList[newItemPosition])
    }

    interface DiffUtilItem{
        fun areItemsTheSame(other: DiffUtilItem):Boolean
    }
}
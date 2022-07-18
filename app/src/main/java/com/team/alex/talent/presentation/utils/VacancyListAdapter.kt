package com.team.alex.talent.presentation.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.team.alex.talent.databinding.LiVacancyBinding
import com.team.alex.talent.domain.model.Vacancy

class VacancyListAdapter(private var vacancies: MutableList<Vacancy> = mutableListOf()) :
    RecyclerView.Adapter<VacancyListAdapter.ViewHolder>() {

    class ViewHolder(val binding: LiVacancyBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LiVacancyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vacancy = vacancies[position]
        holder.binding.tvTitle.text = vacancy.name

        holder.itemView.setOnClickListener {
            if (clickOnItemListener != null) {
                clickOnItemListener!!.onListItemClicked(vacancy)
            }
        }
    }

    override fun getItemCount(): Int {
        return vacancies.size
    }

    fun setData(newVacancies: List<Vacancy>){
        val diffUtil = RecyclerDiffUtil(vacancies, newVacancies)
        val result = DiffUtil.calculateDiff(diffUtil)
        vacancies = newVacancies.toMutableList()
        result.dispatchUpdatesTo(this)
    }

    interface IOnItemClickListener {
        fun onListItemClicked(vacancy: Vacancy)
    }
    companion object {
        private var clickOnItemListener: IOnItemClickListener? = null

        fun setOnItemClickListener(lis: IOnItemClickListener?) {
            clickOnItemListener = lis
        }
    }
}
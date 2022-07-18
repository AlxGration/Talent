package com.team.alex.talent.presentation.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.team.alex.talent.R
import com.team.alex.talent.databinding.LiQuestionBinding
import com.team.alex.talent.domain.model.Question
import com.team.alex.talent.domain.model.Tag

class QuestionListAdapter(
    private val ctx: Context,
    private var questions: MutableList<Question> = mutableListOf(),
    private val isSelectionModeOn: Boolean = false
) : RecyclerView.Adapter<QuestionListAdapter.ViewHolder>() {

    private val selectedItemsIds = mutableSetOf<String>()

    class ViewHolder(val binding: LiQuestionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LiQuestionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val question = questions[position]

        holder.itemView.tag = position
        holder.binding.tvTitle.text = question.question

        holder.binding.chgTags.removeAllViews()
        val scale = ctx.resources.displayMetrics.scaledDensity;
        question.tags.forEach { tag: Tag ->
            val chip: Chip = Chip(ctx).also {
                it.text = tag.name
                it.textSize = ctx.resources.getDimension(R.dimen.h4) / scale
                it.setTextColor(ContextCompat.getColor(ctx, R.color.red))
                it.setChipBackgroundColorResource(R.color.white)
                it.chipStrokeWidth = 2f
                it.textAlignment = View.TEXT_ALIGNMENT_CENTER
                it.setChipStrokeColorResource(R.color.red)
                it.minHeight = 30
                it.setEnsureMinTouchTargetSize(false)
            }
            holder.binding.chgTags.addView(chip)
        }

        if (isSelectionModeOn) {
            holder.binding.checkBox.visibility = View.VISIBLE
            if (selectedItemsIds.contains(question.id))
                holder.binding.checkBox.isChecked = true
        }


        holder.itemView.setOnClickListener {
            if (clickOnItemListener != null) {
                val _question: Question = questions[it.tag as Int]
                clickOnItemListener!!.openQuestionDetailsWith(_question)
            }
        }

        holder.binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedItemsIds.add(question.id)
            } else {
                selectedItemsIds.remove(question.id)
            }

            selectedQuestionsSizeListener?.selectedQuestionsSize(selectedItemsIds.size)
        }
    }

    fun removeItem(position: Int) {
        this.questions.removeAt(position)
        this.notifyItemRemoved(position)
    }

    override fun getItemCount(): Int {
        return questions.size
    }

    fun setData(newQuestions: List<Question>) {
        val diffUtil = RecyclerDiffUtil(questions, newQuestions)
        val result = DiffUtil.calculateDiff(diffUtil)
        questions = newQuestions.toMutableList()
        result.dispatchUpdatesTo(this)
    }

    fun getSelectedItems():Set<String>{
        return selectedItemsIds
    }

    interface IOnItemClickListener {
        fun openQuestionDetailsWith(question: Question)
    }

    interface ISelectedQuestionsSizeListener {
        fun selectedQuestionsSize(size: Int)
    }

    companion object {
        private var clickOnItemListener: IOnItemClickListener? = null
        private var selectedQuestionsSizeListener: ISelectedQuestionsSizeListener? = null

        fun setOnItemClickListener(lis: IOnItemClickListener?) {
            clickOnItemListener = lis
        }

        fun setOnSelectedQuestionsSizeListener(lis: ISelectedQuestionsSizeListener) {
            selectedQuestionsSizeListener = lis
        }
    }

}
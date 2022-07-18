package com.team.alex.talent.presentation.question_details

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.chip.Chip
import com.team.alex.talent.R
import com.team.alex.talent.databinding.FragmentQuestionDetailsBinding
import com.team.alex.talent.domain.model.Question
import com.team.alex.talent.domain.model.Tag
import com.team.alex.talent.presentation.MainActivity

class QuestionDetailsFragment : Fragment(R.layout.fragment_question_details) {

    private val questionDetailsViewModel: QuestionDetailsViewModel by viewModels()
    private var _binding: FragmentQuestionDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentQuestionDetailsBinding.bind(view)

        setupAppBar()

        questionDetailsViewModel.question.observe(viewLifecycleOwner, { question ->
            binding.tvQuestion.text = question.question
            binding.tvAnswer.text = question.answer

            binding.chgTags.removeAllViews()
            val scale = requireContext().resources.displayMetrics.scaledDensity;
            question.tags.forEach { tag: Tag ->
                val chip: Chip = Chip(requireContext()).also {
                    it.text = tag.name
                    it.textSize = requireContext().resources.getDimension(R.dimen.h4) / scale
                    it.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                    it.setChipBackgroundColorResource(R.color.white)
                    it.chipStrokeWidth = 2f
                    it.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    it.setChipStrokeColorResource(R.color.red)
                    it.minHeight = 30
                    it.setEnsureMinTouchTargetSize(false)
                }
                binding.chgTags.addView(chip)
            }
        })

        binding.chipAnswerVisibility.setOnClickListener { chip ->
            binding.tvAnswer.visibility =
                if ((chip as Chip).isChecked) View.VISIBLE else View.INVISIBLE
        }

        //try to get arguments from parent
        arguments?.get(QUESTION_ARG).let {
            if (it is Question) {
                questionDetailsViewModel.setQuestion(it)
            }
        }
    }

    private fun setupAppBar() {
        (activity as MainActivity).setSupportActionBar(binding.toolbar)
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as MainActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.empty_app_bar_menu, menu)
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).setNavBarVisibility(isVisible = false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val QUESTION_ARG = "question"
    }
}
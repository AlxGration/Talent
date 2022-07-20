package com.team.alex.talent.presentation.question_details

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.team.alex.talent.R
import com.team.alex.talent.databinding.FragmentQuestionDetailsBinding
import com.team.alex.talent.domain.model.Question
import com.team.alex.talent.domain.model.Tag
import com.team.alex.talent.presentation.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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
    }

    private fun setupAppBar() {
        (activity as MainActivity).setSupportActionBar(binding.toolbar)
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as MainActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
        setupAppBarMenu()
    }

    private fun setupAppBarMenu() {
        val menuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.empty_app_bar_menu, menu)
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean  = false
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
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
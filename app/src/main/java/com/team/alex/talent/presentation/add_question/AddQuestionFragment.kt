package com.team.alex.talent.presentation.add_question

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.chip.Chip
import com.team.alex.talent.R
import com.team.alex.talent.databinding.FragmentAddQuestionBinding
import com.team.alex.talent.presentation.MainActivity
import com.team.alex.talent.presentation.add_vacancy.AddVacancyFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AddQuestionFragment : Fragment(R.layout.fragment_add_question) {

    private val addQuestionViewModel: AddQuestionViewModel by viewModels()
    private var _binding: FragmentAddQuestionBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddQuestionBinding.bind(view)

        setupAppBar()
        setupErrorObserver()
        setupSuccessVacancyAdditionObserver()
        setupTagsInputField()

        addQuestionViewModel.isLoading.observe(viewLifecycleOwner, { isLoading ->
            binding.btnAddQuestion.isEnabled = !isLoading
            binding.pBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        binding.btnAddQuestion.setOnClickListener{
            val question = binding.tieQuestion.text.toString()
            if (question.isBlank()) {
                binding.tieQuestion.error = getString(R.string.required_field)
                return@setOnClickListener
            }

            val answer = binding.tieAnswer.text.toString()
            if (answer.isEmpty()) {
                binding.tieAnswer.error = getString(R.string.required_field)
                return@setOnClickListener
            }

            if (binding.chgTags.childCount == 0 && binding.tieTags.text.toString().isBlank()) {
                binding.tieTags.error = getString(R.string.required_field)
                return@setOnClickListener
            }

            if (binding.tieTags.text.toString().isNotBlank()) {
                addNewTag(binding.tieTags.text.toString())
            }

            addQuestionViewModel.addQuestion(question, binding.tieAnswer.text.toString())
        }

    }

    private fun setupSuccessVacancyAdditionObserver() {
        lifecycleScope.launchWhenCreated {
            addQuestionViewModel.isQuestionAdded.collectLatest { isAdded ->
                if (isAdded) {
                    setFragmentResult(AddVacancyFragment.RESULT, bundleOf(RESULT to true))
                    activity?.onBackPressed()
                    Toast.makeText( activity?.applicationContext,
                        getString(R.string.question_added), Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setupErrorObserver() {
        lifecycleScope.launchWhenCreated {
            addQuestionViewModel.errorMsg.collectLatest { msg ->
                Toast.makeText(context, msg.toString(requireContext()), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupAppBar() {
        (activity as MainActivity).setSupportActionBar(binding.toolbar)
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as MainActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setupTagsInputField() {
        binding.tieTags.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                addNewTag(binding.tieTags.text.toString())
            }
            true
        }
    }

    private fun addNewTag(tag: String) {
        if (tag.isBlank()) return

        addQuestionViewModel.addTag(tag)

        val chip: Chip = Chip(context).also {
            it.text = tag
            it.isCloseIconVisible = true
            it.isClickable=true
            it.isCheckable = false
            it.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            it.setChipBackgroundColorResource(R.color.white)
            it.chipStrokeWidth = 2f
            it.textAlignment = View.TEXT_ALIGNMENT_CENTER
            it.setChipStrokeColorResource(R.color.red)
            it.minHeight = 30
            it.setEnsureMinTouchTargetSize(false)
        }
        val chipGroup = binding.chgTags

        chipGroup.addView(chip as View, chipGroup.childCount - 1)
        chip.setOnCloseIconClickListener {
            addQuestionViewModel.removeTag(chip.text.toString())
            chipGroup.removeView(chip as View)
        }

        binding.tieTags.setText("")
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
        const val RESULT = "OK"
    }
}
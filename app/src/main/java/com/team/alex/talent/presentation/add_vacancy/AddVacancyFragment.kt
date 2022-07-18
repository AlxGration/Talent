package com.team.alex.talent.presentation.add_vacancy

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.team.alex.talent.R
import com.team.alex.talent.databinding.FragmentAddVacancyBinding
import com.team.alex.talent.presentation.MainActivity
import com.team.alex.talent.presentation.questions.QuestionsFragment
import com.team.alex.talent.presentation.utils.QuestionListAdapter
import dagger.hilt.android.AndroidEntryPoint
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AddVacancyFragment : Fragment(R.layout.fragment_add_vacancy),
    QuestionListAdapter.ISelectedQuestionsSizeListener {

    private val addVacancyViewModel: AddVacancyViewModel by viewModels()
    private var _binding: FragmentAddVacancyBinding? = null
    private var alertDialog: AlertDialog? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddVacancyBinding.bind(view)

        QuestionListAdapter.setOnSelectedQuestionsSizeListener(this)

        setupErrorObserver()
        setupSuccessQuestionAdditionObserver()
        setupAlertDialogView()

        // send arguments to child fragment
        val fragment =
            childFragmentManager.findFragmentById(R.id.fragment_container) as QuestionsFragment
        fragment.arguments =
            bundleOf(QuestionsFragment.OPEN_CONDITIONS to QuestionsFragment.MODE_AS_LIST)

        binding.btnAddVacancy.setOnClickListener {
            fragment.getSelectedQuestions().also { questionsIds ->
                addVacancyViewModel.setQuestionIds(questionsIds)
                if (!questionsIds.isNullOrEmpty())
                    alertDialog?.show()
            }
        }

        addVacancyViewModel.questionsSize.observe(viewLifecycleOwner, { size ->
            binding.btnAddVacancy.text = getString(R.string.crete_n_questions, size)
        })
        addVacancyViewModel.isLoading.observe(viewLifecycleOwner, { isLoading ->
            binding.btnAddVacancy.isEnabled = !isLoading
            binding.pBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        })
    }

    private fun setupSuccessQuestionAdditionObserver() {
        lifecycleScope.launchWhenCreated {
            addVacancyViewModel.isVacancyAdded.collectLatest { isAdded ->
                if (isAdded) {
                    setFragmentResult(RESULT, bundleOf(RESULT to true))
                    activity?.onBackPressed()
                    Toast.makeText( activity?.applicationContext,
                        getString(R.string.vacancy_added), Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setupErrorObserver() {
        lifecycleScope.launchWhenStarted {
            addVacancyViewModel.errorMsg.collectLatest { msg ->
                Toast.makeText(context, msg.toString(requireContext()), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupAlertDialogView() {
        val view: View = layoutInflater.inflate(R.layout.dialog_vacancy_title, null)
        alertDialog = AlertDialog.Builder(requireContext()).create()
        alertDialog?.setTitle(getString(R.string.who_it_will_be))
        alertDialog?.setCancelable(false)
        val etTitle = view.findViewById<TextInputEditText>(R.id.tie_title)
        alertDialog?.setButton(
            AlertDialog.BUTTON_POSITIVE,
            getString(R.string.ok)
        ) { _, _ ->
            val title = etTitle.text.toString()
            addVacancyViewModel.addVacancy(title = title)
            alertDialog?.dismiss()
        }
        alertDialog?.setButton(
            AlertDialog.BUTTON_NEGATIVE,
            getString(R.string.cancel)
        ) { _, _ ->
            alertDialog?.dismiss()
        }
        alertDialog?.setView(view)
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).setNavBarVisibility(isVisible = false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun selectedQuestionsSize(size: Int) {
        addVacancyViewModel.selectedQuestionsSize(size)
    }

    companion object {
        const val RESULT = "OK"
    }
}
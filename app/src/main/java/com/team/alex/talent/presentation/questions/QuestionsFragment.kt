package com.team.alex.talent.presentation.questions

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.chip.Chip
import com.team.alex.talent.R
import com.team.alex.talent.databinding.FragmentQuestionsBinding
import com.team.alex.talent.domain.model.Question
import com.team.alex.talent.presentation.MainActivity
import com.team.alex.talent.presentation.add_question.AddQuestionFragment
import com.team.alex.talent.presentation.filter.SearchFilterFragment
import com.team.alex.talent.presentation.question_details.QuestionDetailsFragment
import com.team.alex.talent.presentation.utils.QuestionListAdapter
import dagger.hilt.android.AndroidEntryPoint
import com.team.alex.talent.presentation.utils.SwipeHelper

@AndroidEntryPoint
class QuestionsFragment : Fragment(R.layout.fragment_questions),
    QuestionListAdapter.IOnItemClickListener, SearchView.OnQueryTextListener {

    private val questionsViewModel: QuestionsViewModel by viewModels()
    private var _binding: FragmentQuestionsBinding? = null
    private val binding get() = _binding!!
    private var OPEN_MODE: FragmentState = FragmentState()
    private var searchView:SearchView? = null

    private val adapter: QuestionListAdapter by lazy {
        QuestionListAdapter(
            requireContext(),
            isSelectionModeOn = OPEN_MODE.isSelectionModeOn
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentQuestionsBinding.bind(view)

        questionsViewModel.errorMsg.observe(viewLifecycleOwner, { msg ->
            binding.tvError.text = msg.toString(requireContext())
        })

        questionsViewModel.isLoading.observe(viewLifecycleOwner, { isLoading ->
            binding.pBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        questionsViewModel.questionsList.observe(viewLifecycleOwner, { questions ->
            adapter.setData(questions)
            binding.rvQuestions.adapter = adapter
            toggleNoQuestionImage(questions.size == 0)
        })

        binding.nestedSv.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                questionsViewModel.loadMoreQuestions()
            }
        })

        questionsViewModel.tagsList.observe(viewLifecycleOwner, { tags ->
            binding.chgTags.removeAllViews()
            tags.forEach { tag -> addNewTag(tag) }
        })

        setupOpeningSettings()
        setupAppBar()
        setupRecyclerView()
        setupFragmentResultListeners()
    }

    private fun setupFragmentResultListeners() {
        //receive args(tags list) from SearchFilterFragment
        parentFragmentManager.setFragmentResultListener(
            SearchFilterFragment.TAGS_LIST,
            viewLifecycleOwner,
            { key, bundle ->
                if (key == SearchFilterFragment.TAGS_LIST) {
                    bundle.getStringArrayList(key)?.let { tagsList ->
                        questionsViewModel.setTagsFilter(tagsList.toList())
                    }
                }
            })

        //receive args(success or not) from AddQuestionFragment
        //refresh questions list in case of success, otherwise, an error will be shown.
        parentFragmentManager.setFragmentResultListener(
            AddQuestionFragment.RESULT,
            viewLifecycleOwner,
            { key, bundle ->
                if (key == AddQuestionFragment.RESULT) {
                    val isAdded = bundle.getBoolean(key)
                    if (isAdded) {
                        questionsViewModel.refreshList()
                    }
                }
            })
    }

    private fun setupOpeningSettings() {
        //try to get arguments from parent fragment
        arguments?.get(OPEN_CONDITIONS).let { MODE ->
            if (MODE == MODE_AS_LIST)
                OPEN_MODE = FragmentState(
                    isSelectionModeOn = true,
                    isAbleToDeleteItems = false,
                    isNavBarVisible = false,
                    isAppBarChanged = true,
                    menuRes = R.menu.add_vacancies_app_bar_menu,
                    actionNavToQuestionDetails = R.id.action_navigation_add_vacancy_to_question_details,
                    appBarTitleRes = R.string.create_vacancy,
                )
        }
    }

    private fun setupAppBar() {
        (activity as MainActivity).setSupportActionBar(binding.toolbar)
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(OPEN_MODE.isAppBarChanged)
        (activity as MainActivity).supportActionBar?.setDisplayShowHomeEnabled(OPEN_MODE.isAppBarChanged)
        setHasOptionsMenu(true)
        binding.toolbar.title = getString(OPEN_MODE.appBarTitleRes)
    }

    private fun setupRecyclerView() {
        QuestionListAdapter.setOnItemClickListener(this)
        if (!OPEN_MODE.isAbleToDeleteItems) return

        val itemTouchHelper = ItemTouchHelper(object : SwipeHelper(binding.rvQuestions) {
            override fun instantiateUnderlayButton(position: Int): List<UnderlayButton> {
                return listOf(deleteButton(position))
            }
        })

        itemTouchHelper.attachToRecyclerView(binding.rvQuestions)
    }

    private fun deleteButton(position: Int): SwipeHelper.UnderlayButton {
        return SwipeHelper.UnderlayButton(
            requireContext(),
            getString(R.string.delete),
            14.0f,
            R.color.red_60,
            object : SwipeHelper.UnderlayButtonClickListener {
                override fun onClick() {
                    questionsViewModel.removeQuestion(position)
                    adapter.removeItem(position)
                }
            })
    }

    private fun toggleNoQuestionImage(needToShow: Boolean) {
        binding.tvError.text = if (needToShow) getString(R.string.not_found) else ""
        binding.ivNotFound.visibility = if (needToShow) View.VISIBLE else View.GONE
    }

    override fun openQuestionDetailsWith(question: Question) {
        findNavController().navigate(
            OPEN_MODE.actionNavToQuestionDetails,
            bundleOf(QuestionDetailsFragment.QUESTION_ARG to question)
        )
    }

    fun getSelectedQuestions(): Set<String> {
        return adapter.getSelectedItems()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(OPEN_MODE.menuRes, menu)
        val searchItem =
            menu.findItem(if (OPEN_MODE.isAppBarChanged) R.id.search_vacancies else R.id.search_questions)
        searchView = searchItem.actionView as SearchView

        //restore searchView state
        val searchQuery = questionsViewModel.searchQuery
        if (searchQuery.isNotEmpty()) {
            searchView!!.onActionViewExpanded()
            searchView!!.setQuery(searchQuery, false)
        }

        searchView!!.setOnQueryTextListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search_questions -> {
                true
            }
            R.id.search_vacancies -> {
                true
            }
            R.id.filter -> {
                val filterFragment = SearchFilterFragment()
                filterFragment.arguments =
                    bundleOf(SearchFilterFragment.TAGS_LIST to questionsViewModel.tagsList.value)
                filterFragment.show(parentFragmentManager, "tag")
                true
            }
            R.id.add_question -> {
                findNavController().navigate(R.id.action_navigation_questions_to_add_question)
                true
            }
            else -> false
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        query?.let { questionsViewModel.setTitle(query) }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText.isNullOrEmpty())
            questionsViewModel.setTitle("")
        return true
    }

    private fun addNewTag(tag: String) {
        if (tag.isBlank()) return

        val chip: Chip = Chip(context).also {
            it.text = tag
            it.isCloseIconVisible = true
            it.isClickable = true
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
            questionsViewModel.removeTagFromFilter(chip.text.toString())
            chipGroup.removeView(chip as View)
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).setNavBarVisibility(
            isVisible = OPEN_MODE.isNavBarVisible
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        QuestionListAdapter.setOnItemClickListener(null)
        searchView?.setOnQueryTextListener(null)
        _binding = null
    }

    companion object {
        const val OPEN_CONDITIONS = "OPEN_CONDITIONS"
        const val MODE_AS_LIST = "AS_LIST"
    }
}
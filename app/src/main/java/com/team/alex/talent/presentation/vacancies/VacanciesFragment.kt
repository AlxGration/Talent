package com.team.alex.talent.presentation.vacancies

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.team.alex.talent.R
import com.team.alex.talent.databinding.FragmentVacanciesBinding
import com.team.alex.talent.domain.model.Vacancy
import com.team.alex.talent.presentation.MainActivity
import com.team.alex.talent.presentation.add_vacancy.AddVacancyFragment
import com.team.alex.talent.presentation.utils.VacancyListAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VacanciesFragment : Fragment(R.layout.fragment_vacancies),
    SearchView.OnQueryTextListener, VacancyListAdapter.IOnItemClickListener
{

    private val vacanciesViewModel: VacanciesViewModel by viewModels()
    private var _binding: FragmentVacanciesBinding? = null
    private val binding get() = _binding!!

    private val adapter: VacancyListAdapter by lazy { VacancyListAdapter() }
    private lateinit var layoutManager: GridLayoutManager
    private var searchView:SearchView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentVacanciesBinding.bind(view)

        (activity as MainActivity).setSupportActionBar(binding.toolbar)
        setHasOptionsMenu(true)

        vacanciesViewModel.errorMsg.observe(viewLifecycleOwner, { msg ->
            binding.tvError.text = msg.toString(requireContext())
        })

        vacanciesViewModel.isLoading.observe(viewLifecycleOwner, { isLoading ->
            binding.pBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        vacanciesViewModel.vacancies.observe(viewLifecycleOwner, { vacancies ->
            adapter.setData(vacancies)
            binding.rvVacancies.adapter = adapter
            toggleNoVacanciesImage(vacancies.isEmpty())
        })

        binding.nestedSv.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                vacanciesViewModel.loadMoreVacancies()
            }
        })

        setupRecyclerView()
        setupFragmentResultListeners()

    }

    private fun setupFragmentResultListeners() {
        //receive args(success or not) from AddVacancyFragment
        //refresh vacancies list in case of success, otherwise, an error will be shown.
        parentFragmentManager.setFragmentResultListener(
            AddVacancyFragment.RESULT,
            viewLifecycleOwner,
            { key, bundle ->
                if (key == AddVacancyFragment.RESULT) {
                    val isAdded = bundle.getBoolean(key)
                    if (isAdded) {
                        vacanciesViewModel.refreshList()
                    }
                }
            })
    }

    private fun setupRecyclerView() {
        layoutManager = GridLayoutManager(activity, 2)
        binding.rvVacancies.layoutManager = layoutManager
        VacancyListAdapter.setOnItemClickListener(this)
    }

    private fun toggleNoVacanciesImage(needToShow: Boolean) {
        binding.tvError.text = if (needToShow) getString(R.string.not_found) else ""
        binding.ivNotFound.visibility = if (needToShow) View.VISIBLE else View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.vacancies_app_bar_menu, menu)
        val searchItem = menu.findItem(R.id.search_vacancies)
        searchView = searchItem.actionView as SearchView
        searchView!!.setOnQueryTextListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search_vacancies -> {
                true
            }
            R.id.add_vacancy -> {
                findNavController().navigate(R.id.action_navigation_vacancies_to_add_vacancy)
                true
            }
            else -> false
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        query?.let { vacanciesViewModel.filterVacancies(query) }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText.isNullOrEmpty()) vacanciesViewModel.filterVacancies("")
        return true
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).setNavBarVisibility(isVisible = true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        VacancyListAdapter.setOnItemClickListener(null)
        searchView?.setOnQueryTextListener(null)
        _binding = null
    }

    override fun onListItemClicked(vacancy: Vacancy) {
        Toast.makeText(context, getString(R.string.no_such_functionslity), Toast.LENGTH_SHORT).show()
    }
}
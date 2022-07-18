package com.team.alex.talent.presentation.vacancies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team.alex.talent.R
import com.team.alex.talent.common.Resource
import com.team.alex.talent.domain.model.Vacancy
import com.team.alex.talent.domain.use_case.VacancyUsecases
import com.team.alex.talent.presentation.utils.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class VacanciesViewModel @Inject constructor(
    private val vacanciesUsecase: VacancyUsecases
) : ViewModel() {

    private val _errorMsg = MutableLiveData<UiText>()
    val errorMsg: LiveData<UiText> = _errorMsg

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _vacancies = MutableLiveData<List<Vacancy>>()
    val vacancies: LiveData<List<Vacancy>> = _vacancies

    private var _originalVacancies = mutableListOf<Vacancy>()
    private var _vacanciesSize: Int? = null
    private var _size = 10
    private var _offset = 0

    init {
        _offset = 0
        getVacanciesSize()
    }

    fun loadMoreVacancies() {
        if (_vacanciesSize == null || _vacanciesSize == 0) {
            _vacancies.value = listOf()
            return
        }

        if (_offset < _vacanciesSize!!) {
            getVacancies()
            _offset += _size
        }
    }

    private fun getVacanciesSize() {
        setError("")
        vacanciesUsecase.getVacanciesSize().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _isLoading.value = false
                    _vacanciesSize = result.data ?: 0
                    loadMoreVacancies()
                }
                is Resource.Loading -> {
                    _isLoading.value = true
                }
                is Resource.Error -> {
                    _isLoading.value = false
                    setError(result.message)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun getVacancies(
        size: Int = _size,
        offset: Int = _offset,
    ) {
        vacanciesUsecase.getVacancies(size, offset).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _isLoading.value = false
                    result.data?.let {
                        _originalVacancies.addAll(it)
                    }
                    _vacancies.value = _originalVacancies
                }
                is Resource.Loading -> {
                    _isLoading.value = true
                }
                is Resource.Error -> {
                    _isLoading.value = false
                    setError(result.message)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun filterVacancies(query: String) {
        if (query.isBlank()) {
            _vacancies.value = _originalVacancies
            return
        }

        val filteredVacancies = _originalVacancies.filter { vacancy ->
            vacancy.name.toLowerCase().contains(query)
        }

        _vacancies.value = filteredVacancies
    }

    private fun setError(msg:String?){
        if (msg == null)
            _errorMsg.value = UiText.StringResource(R.string.err_unexpected)
        else
            _errorMsg.value = UiText.DynamicString(msg)
    }

    fun refreshList() {
        _offset = 0
        getVacanciesSize()
    }
}
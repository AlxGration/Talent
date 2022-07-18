package com.team.alex.talent.presentation.add_vacancy

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team.alex.talent.R
import com.team.alex.talent.common.Resource
import com.team.alex.talent.domain.use_case.VacancyUsecases
import com.team.alex.talent.presentation.utils.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddVacancyViewModel @Inject constructor(
    private val vacancyUsecases: VacancyUsecases
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMsg = MutableSharedFlow<UiText>()
    val errorMsg = _errorMsg.asSharedFlow()

    private val _isVacancyAdded = MutableSharedFlow<Boolean>()
    val isVacancyAdded = _isVacancyAdded.asSharedFlow()

    private var _questionIds = mutableSetOf<String>()
    private val _questionsSize = MutableLiveData<Int>(0)
    val questionsSize: LiveData<Int> = _questionsSize

    fun addVacancy(title: String, qIds: Set<String> = _questionIds) {
        if (title.isBlank()) {
            setError(strId = R.string.err_empty_title)
            return
        }

        if (qIds.isEmpty()) {
            setError(strId = R.string.err_at_least_1_question)
            return
        }

        vacancyUsecases.addVacancy(qIds.toList(), title).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _isLoading.value = false
                    _isVacancyAdded.emit(true)
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

    fun selectedQuestionsSize(size: Int) {
        _questionsSize.value = size
    }

    fun setQuestionIds(questionIds: Set<String>?) {
        if (questionIds == null || questionIds.isEmpty()) {
            setError(strId = R.string.err_at_least_1_question)
            return
        }

        _questionIds.clear()
        _questionIds.addAll(questionIds)
    }

    private fun setError(msg:String? = null, strId:Int = R.string.err_unexpected){
        if (msg == null)
            emitError(UiText.StringResource(strId))
        else
            emitError(UiText.DynamicString(msg))
    }

    private fun emitError(msg:UiText){
        viewModelScope.launch {
            _errorMsg.emit(msg)
        }
    }
}
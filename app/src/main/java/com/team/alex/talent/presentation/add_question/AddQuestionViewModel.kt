package com.team.alex.talent.presentation.add_question

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team.alex.talent.R
import com.team.alex.talent.common.Resource
import com.team.alex.talent.domain.model.Question
import com.team.alex.talent.domain.model.toTag
import com.team.alex.talent.domain.use_case.QuestionUsecases
import com.team.alex.talent.presentation.utils.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddQuestionViewModel @Inject constructor(
    private val questionsUsecases: QuestionUsecases
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMsg = MutableSharedFlow<UiText>()
    val errorMsg = _errorMsg.asSharedFlow()

    private val _isQuestionAdded = MutableSharedFlow<Boolean>()
    val isQuestionAdded = _isQuestionAdded.asSharedFlow()

    private val tagsList = mutableSetOf<String>()

    fun addTag(tag: String) = tagsList.add(tag)
    fun removeTag(tag: String) = tagsList.remove(tag)

    fun addQuestion(question: String, answer: String) {

        if (question.isBlank()) {
            setError(strId = R.string.err_empty_question)
            return
        }

        if (tagsList.isEmpty()) {
            setError(strId = R.string.err_at_least_1_tag)
            return
        }

        val q = Question(
            question = question,
            answer = answer,
            tags = tagsList.map(String::toTag)
        )

        questionsUsecases.addQuestion(q).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _isLoading.value = false
                    _isQuestionAdded.emit(true)
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

    private fun setError(msg: String? = null, strId: Int = R.string.err_unexpected) {
        if (msg == null)
            emitError(UiText.StringResource(strId))
        else
            emitError(UiText.DynamicString(msg))
    }

    private fun emitError(msg: UiText) {
        viewModelScope.launch {
            _errorMsg.emit(msg)
        }
    }
}
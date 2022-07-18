package com.team.alex.talent.presentation.questions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team.alex.talent.R
import com.team.alex.talent.common.Resource
import com.team.alex.talent.domain.model.Question
import com.team.alex.talent.domain.use_case.QuestionUsecases
import com.team.alex.talent.presentation.utils.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class QuestionsViewModel @Inject constructor(
    private val questionsUsecases: QuestionUsecases
) : ViewModel() {

    private val _errorMsg = MutableLiveData<UiText>()
    val errorMsg: LiveData<UiText> = _errorMsg

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _questionsList = MutableLiveData<MutableList<Question>>()
    val questionsList: LiveData<MutableList<Question>> = _questionsList

    private val _tagsList =
        MutableLiveData<MutableList<String>>().apply { value = mutableListOf<String>() }
    val tagsList: LiveData<MutableList<String>> = _tagsList

    private var _questionsSize: Int? = null
    private var _size = 10
    private var _offset = 0
    private val _receivedQuestions = mutableListOf<Question>()
    private var _title = ""
    val searchQuery get() =_title

    init {
        refreshList()
    }

    fun removeQuestion(position: Int) {
        _questionsList.value?.removeAt(position)
    }

    fun removeTagFromFilter(tag: String) {
        _tagsList.value?.remove(tag)
        clearFilters(offset = 0, needClearQuestions = true)
        getQuestionsSize()
    }

    fun setTitle(title: String) {
        clearFilters(title = title, offset = 0, needClearQuestions = true)
        getQuestionsSize()
    }

    fun setTagsFilter(tags: List<String>) {
        clearFilters(needClearTags = true, offset = 0, needClearQuestions = true)
        _tagsList.value = tags.toMutableList()
        getQuestionsSize()
    }

    fun loadMoreQuestions() {
        if (_questionsSize == null || _questionsSize == 0) {
            _questionsList.value = mutableListOf()
            return
        }

        if (_offset < _questionsSize!!) {
            searchQuestion()
            _offset += _size
        }
    }

    private fun getQuestionsSize(
        title: String = _title,
        isStrict: Boolean = false,
        tags: List<String> = _tagsList.value!!
    ) {
        setError("")
        questionsUsecases.searchQuestionsSize(title, isStrict, tags).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _isLoading.value = false
                    _questionsSize = result.data ?: 0
                    loadMoreQuestions()
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

    private fun clearFilters(
        title: String = _title,
        needClearTags: Boolean = false,
        offset: Int = _offset,
        needClearQuestions: Boolean = false
    ) {
        _title = title
        _offset = offset
        if (needClearTags) _tagsList.value?.clear()
        if (needClearQuestions) _receivedQuestions.clear()
    }

    private fun searchQuestion(
        size: Int = _size,
        offset: Int = _offset,
        title: String = _title,
        isStrict: Boolean = false,
        tags: List<String> = _tagsList.value!!
    ) {
        questionsUsecases.searchQuestions(size, offset, title, isStrict, tags).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _isLoading.value = false
                    result.data?.let {
                        _receivedQuestions.addAll(it)
                    }
                    _questionsList.value = _receivedQuestions
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
    private fun setError(msg:String?){
        if (msg == null)
            _errorMsg.value = UiText.StringResource(R.string.err_unexpected)
        else
            _errorMsg.value = UiText.DynamicString(msg)
    }

    fun refreshList() {
        clearFilters(title = "", offset = 0, needClearQuestions = true, needClearTags = true)
        getQuestionsSize()
    }
}
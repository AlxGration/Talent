package com.team.alex.talent.presentation.filter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team.alex.talent.R
import com.team.alex.talent.common.Resource
import com.team.alex.talent.data.dto.TagDto
import com.team.alex.talent.domain.use_case.QuestionUsecases
import com.team.alex.talent.presentation.utils.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SearchFilterViewModel @Inject constructor(
    private val questionsUsecases: QuestionUsecases
) : ViewModel() {

    private val _errorMsg = MutableLiveData<UiText>()
    val errorMsg: LiveData<UiText> = _errorMsg

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _tagsList = MutableLiveData<List<TagDto>>()
    val tagsList: LiveData<List<TagDto>> = _tagsList

    private var _selectedTagsList = mutableSetOf<String>()
    fun getSelectedTagsList() = _selectedTagsList.toList()
    fun selectTag(tag: String) {
        _selectedTagsList.add(tag)
    }

    fun selectTags(tags: List<String>) {
        _selectedTagsList = tags.toMutableSet()
    }

    fun removeTag(tag: String) {
        _selectedTagsList.remove(tag)
    }

    init {
        getTags()
    }

    private fun getTags() {
        questionsUsecases.getTags().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _isLoading.value = false
                    _tagsList.value = result.data ?: emptyList<TagDto>()
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
}
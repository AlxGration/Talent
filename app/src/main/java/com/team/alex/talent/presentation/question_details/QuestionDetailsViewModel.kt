package com.team.alex.talent.presentation.question_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.team.alex.talent.domain.model.Question
import com.team.alex.talent.presentation.question_details.QuestionDetailsFragment.Companion.QUESTION_ARG
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class QuestionDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _question = MutableLiveData<Question>()
    val question: LiveData<Question> = _question

    init {
        //try to get navigation argument from parent fragment
        try {
            savedStateHandle.get<Question>(QUESTION_ARG).let { question ->
                _question.value = question
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}
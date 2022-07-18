package com.team.alex.talent.presentation.question_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.team.alex.talent.domain.model.Question

class QuestionDetailsViewModel: ViewModel() {

    private val _question = MutableLiveData<Question>()
    val question: LiveData<Question> = _question

    fun setQuestion(question: Question) {
        _question.value = question
    }
}
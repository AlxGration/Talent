package com.team.alex.talent.domain.repository

import com.team.alex.talent.data.dto.QuestionDto
import com.team.alex.talent.data.dto.TagDto
import com.team.alex.talent.domain.model.Question

interface QuestionRepository {

    suspend fun getTags(): List<TagDto>
    suspend fun getQuestionById(id: String): QuestionDto
    suspend fun addQuestion(question: Question): QuestionDto

    suspend fun searchQuestionsSize(
        title: String,
        isStrict: Boolean,
        tags: List<String>
    ): Int

    suspend fun searchQuestions(
        size: Int,
        offset: Int,
        title: String,
        isStrict: Boolean,
        tags: List<String>
    ): List<QuestionDto>
}
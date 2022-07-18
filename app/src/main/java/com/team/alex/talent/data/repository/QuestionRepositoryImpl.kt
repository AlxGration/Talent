package com.team.alex.talent.data.repository

import com.team.alex.talent.data.api.QuestionApi
import com.team.alex.talent.data.dto.QuestionDto
import com.team.alex.talent.data.dto.SearchQuestionDto
import com.team.alex.talent.data.dto.TagDto
import com.team.alex.talent.data.dto.toDto
import com.team.alex.talent.domain.model.Question
import com.team.alex.talent.domain.repository.QuestionRepository
import javax.inject.Inject

class QuestionRepositoryImpl @Inject constructor(
    private val api: QuestionApi
) : QuestionRepository {

    override suspend fun getTags(): List<TagDto> {
        return api.getTags()
    }

    override suspend fun getQuestionById(id: String): QuestionDto {
        return api.getQuestionById(id)
    }

    override suspend fun addQuestion(question: Question): QuestionDto{
        return api.addQuestion(question.toDto())
    }

    override suspend fun searchQuestionsSize(
        title: String,
        isStrict: Boolean,
        tags: List<String>
    ): Int {
        return api.searchQuestionsSize(
            searchQuestionDto = SearchQuestionDto(
                strict = isStrict,
                text = title,
                tags = tags,
            )
        ).value
    }

    override suspend fun searchQuestions(
        size: Int,
        offset: Int,
        title: String,
        isStrict: Boolean,
        tags: List<String>
    ): List<QuestionDto> {
        return api.searchQuestions(
            size,
            offset,
            searchQuestionDto = SearchQuestionDto(
                strict = isStrict,
                text = title,
                tags = tags,
            )
        )
    }
}
package com.team.alex.talent.data.dto

import com.team.alex.talent.domain.model.Question
import com.team.alex.talent.domain.model.Tag

// QuestionWithoutIdDto is used to create a new one.
data class QuestionWithoutIdDto(
    val answer: String,
    val header: String,
    val question: String,
    val tags: List<TagWithoutIdDto>
)

fun Question.toDto(): QuestionWithoutIdDto{
    return QuestionWithoutIdDto(
        answer = this.answer,
        question = this.question,
        header = "Вопрос",
        tags = tags.map(Tag::toDto)
    )
}
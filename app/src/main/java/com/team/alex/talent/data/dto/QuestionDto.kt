package com.team.alex.talent.data.dto

import com.team.alex.talent.domain.model.Question

data class QuestionDto(
    val answer: String,
    val header: String,
    val id: String,
    val question: String,
    val tags: List<TagDto>
)

fun QuestionDto.toQuestion():Question{
    return Question(
        id = id,
        answer = answer,
        question = question,
        tags = tags.map(TagDto::toTag)
    )
}
package com.team.alex.talent.data.dto

data class SearchQuestionDto(
    val strict: Boolean,
    val tags: List<String>,
    val text: String
)
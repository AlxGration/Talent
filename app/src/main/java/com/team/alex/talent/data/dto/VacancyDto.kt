package com.team.alex.talent.data.dto

import com.team.alex.talent.domain.model.Vacancy

data class VacancyDto(
    val id: String,
    val name: String,
)

fun VacancyDto.toVacancy(): Vacancy {
    return Vacancy(
        id = id,
        name = name
    )
}
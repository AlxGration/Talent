package com.team.alex.talent.domain.repository

import com.team.alex.talent.data.dto.VacancyDto

interface VacancyRepository {
    suspend fun getVacancies(size: Int, offset: Int): List<VacancyDto>
    suspend fun getVacanciesSize(): Int
    suspend fun addVacancy(questionIds: List<String>, title: String)
}
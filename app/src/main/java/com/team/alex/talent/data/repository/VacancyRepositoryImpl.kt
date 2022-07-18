package com.team.alex.talent.data.repository

import com.team.alex.talent.data.api.VacancyApi
import com.team.alex.talent.data.dto.AddVacancyBodyDto
import com.team.alex.talent.data.dto.VacancyDto
import com.team.alex.talent.domain.repository.VacancyRepository
import javax.inject.Inject

class VacancyRepositoryImpl @Inject constructor(
    private val api: VacancyApi
) : VacancyRepository {

    override suspend fun getVacancies(size: Int, offset: Int): List<VacancyDto> {
        return api.getVacancies(size, offset)
    }

    override suspend fun getVacanciesSize(): Int {
        return api.getVacanciesSize().value
    }

    override suspend fun addVacancy(questionIds: List<String>, title: String) {
        api.addVacancy(
            AddVacancyBodyDto(
                question_ids = questionIds,
                title = title
            )
        )
    }
}
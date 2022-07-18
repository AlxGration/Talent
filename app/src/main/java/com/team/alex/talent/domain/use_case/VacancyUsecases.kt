package com.team.alex.talent.domain.use_case

import com.team.alex.talent.common.Resource
import com.team.alex.talent.data.dto.toVacancy
import com.team.alex.talent.domain.model.Vacancy
import com.team.alex.talent.domain.repository.VacancyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.lang.Exception
import javax.inject.Inject

class VacancyUsecases @Inject constructor(
    private val vacancyRepo: VacancyRepository
) {

    fun getVacanciesSize(): Flow<Resource<Int>> = flow {
        try {
            emit(Resource.Loading<Int>())
            val vacanciesSize = vacancyRepo.getVacanciesSize()
            emit(Resource.Success<Int>(vacanciesSize))
        } catch (e: HttpException) {
            emit(Resource.Error<Int>(e.message() ?: "Http error occurred"))
            e.printStackTrace()
        } catch (e: Exception) {
            emit(Resource.Error<Int>("Check your internet connection"))
            e.printStackTrace()
        }
    }

    fun getVacancies(size: Int, offset: Int): Flow<Resource<List<Vacancy>>> = flow {
        try {
            emit(Resource.Loading<List<Vacancy>>())
            val vacancies = vacancyRepo.getVacancies(size, offset).map { it.toVacancy() }
            emit(Resource.Success<List<Vacancy>>(vacancies))
        } catch (e: HttpException) {
            emit(Resource.Error<List<Vacancy>>(e.message() ?: "Http error occurred"))
            e.printStackTrace()
        } catch (e: Exception) {
            emit(Resource.Error<List<Vacancy>>("Check your internet connection"))
            e.printStackTrace()
        }
    }

    fun addVacancy(questionIds: List<String>, title: String): Flow<Resource<Any>> = flow {
        try {
            emit(Resource.Loading<Any>())
            vacancyRepo.addVacancy(questionIds, title)
            emit(Resource.Success<Any>(""))
        } catch (e: HttpException) {
            emit(Resource.Error<Any>(e.message() ?: "Http error occurred"))
            e.printStackTrace()
        } catch (e: Exception) {
            emit(Resource.Error<Any>("Check your internet connection"))
            e.printStackTrace()
        }
    }
}
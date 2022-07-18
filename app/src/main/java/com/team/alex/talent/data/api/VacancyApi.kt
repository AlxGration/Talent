package com.team.alex.talent.data.api

import com.team.alex.talent.data.dto.AddVacancyBodyDto
import com.team.alex.talent.data.dto.IntValueDto
import com.team.alex.talent.data.dto.VacancyDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface VacancyApi {

    @GET("/api/v1/interviews/size")
    suspend fun getVacanciesSize(): IntValueDto

    @GET("/api/v1/interviews")
    suspend fun getVacancies(
        @Query("size") size:Int,
        @Query("offset") offset:Int,
    ):List<VacancyDto>

    @POST("/api/v1/interviews/create")
    suspend fun addVacancy(@Body addVacancyMsg:AddVacancyBodyDto)

}
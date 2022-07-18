package com.team.alex.talent.data.api

import com.team.alex.talent.data.dto.*
import retrofit2.http.*

interface QuestionApi {

    @GET("/api/v1/questions/{id}")
    suspend fun getQuestionById(
        @Path("id") id: String,
    ): QuestionDto

    @POST("/api/v1/questions/search")
    suspend fun searchQuestions(
        @Query("size") size: Int,
        @Query("offset") offset: Int,
        @Body searchQuestionDto: SearchQuestionDto
    ): List<QuestionDto>

    @POST("/api/v1/questions/search/size")
    suspend fun searchQuestionsSize(
        @Body searchQuestionDto: SearchQuestionDto
    ): IntValueDto

    @POST("/api/v1/questions/create")
    suspend fun addQuestion(
        @Body question: QuestionWithoutIdDto
    ): QuestionDto

    @GET("/api/v1/questions/tags")
    suspend fun getTags(): List<TagDto>
}
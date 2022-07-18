package com.team.alex.talent.domain.use_case

import com.team.alex.talent.common.Resource
import com.team.alex.talent.data.dto.TagDto
import com.team.alex.talent.data.dto.toQuestion
import com.team.alex.talent.domain.model.Question
import com.team.alex.talent.domain.repository.QuestionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.lang.Exception
import javax.inject.Inject

class QuestionUsecases @Inject constructor(
    private val questionRepo: QuestionRepository
) {
    fun searchQuestionsSize(
        title: String,
        isStrict: Boolean,
        tags: List<String>
    ): Flow<Resource<Int>> = flow {
        try {
            emit(Resource.Loading<Int>())
            val questionsSize = questionRepo.searchQuestionsSize(title, isStrict, tags)
            emit(Resource.Success<Int>(questionsSize))
        } catch (e: HttpException) {
            emit(Resource.Error<Int>(e.message() ?: "Http error occurred"))
            e.printStackTrace()
        } catch (e: Exception) {
            emit(Resource.Error<Int>("Check your internet connection"))
            e.printStackTrace()
        }
    }

    fun getTags(): Flow<Resource<List<TagDto>>> = flow {
        try {
            emit(Resource.Loading<List<TagDto>>())
            val tags = questionRepo.getTags()
            emit(Resource.Success<List<TagDto>>(tags))
        } catch (e: HttpException) {
            emit(Resource.Error<List<TagDto>>(e.message() ?: "Http error occurred"))
            e.printStackTrace()
        } catch (e: Exception) {
            emit(Resource.Error<List<TagDto>>("Check your internet connection"))
            e.printStackTrace()
        }
    }

    fun getQuestionById(questionId: String): Flow<Resource<Question>> = flow {
        try {
            emit(Resource.Loading<Question>())
            val question = questionRepo.getQuestionById(questionId).toQuestion()
            emit(Resource.Success<Question>(question))
        } catch (e: HttpException) {
            emit(Resource.Error<Question>(e.message() ?: "Http error occurred"))
            e.printStackTrace()
        } catch (e: Exception) {
            emit(Resource.Error<Question>("Check your internet connection"))
            e.printStackTrace()
        }
    }

    fun addQuestion(question: Question): Flow<Resource<Question>> = flow {
        try {
            emit(Resource.Loading<Question>())
            val _question = questionRepo.addQuestion(question).toQuestion()
            emit(Resource.Success<Question>(_question))
        } catch (e: HttpException) {
            emit(Resource.Error<Question>(e.message() ?: "Http error occurred"))
            e.printStackTrace()
        } catch (e: Exception) {
            emit(Resource.Error<Question>("Check your internet connection"))
            e.printStackTrace()
        }
    }

    fun searchQuestions(
        size: Int,
        offset: Int,
        title: String,
        isStrict: Boolean,
        tags: List<String>
    ): Flow<Resource<List<Question>>> = flow {
        try {
            emit(Resource.Loading<List<Question>>())
            val questions =
                questionRepo.searchQuestions(size, offset, title, isStrict, tags)
                    .map { it.toQuestion() }
            emit(Resource.Success<List<Question>>(questions))
        } catch (e: HttpException) {
            emit(Resource.Error<List<Question>>(e.message() ?: "Http error occurred"))
            e.printStackTrace()
        } catch (e: Exception) {
            emit(Resource.Error<List<Question>>("Check your internet connection"))
            e.printStackTrace()
        }
    }
}
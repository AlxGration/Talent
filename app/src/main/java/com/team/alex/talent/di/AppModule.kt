package com.team.alex.talent.di

import com.team.alex.talent.BuildConfig
import com.team.alex.talent.common.Constants
import com.team.alex.talent.data.api.QuestionApi
import com.team.alex.talent.data.api.VacancyApi
import com.team.alex.talent.data.repository.QuestionRepositoryImpl
import com.team.alex.talent.data.repository.VacancyRepositoryImpl
import com.team.alex.talent.domain.repository.QuestionRepository
import com.team.alex.talent.domain.repository.VacancyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import okhttp3.OkHttpClient

import okhttp3.logging.HttpLoggingInterceptor

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor()

        // Только в режиме отладки
        if (BuildConfig.DEBUG) {
            loggingInterceptor.apply {
                loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            }
        }

        val okClient: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideQuestionApi(retrofit: Retrofit): QuestionApi {
        return retrofit.create(QuestionApi::class.java)
    }

    @Provides
    @Singleton
    fun provideQuestionRepository(api: QuestionApi): QuestionRepository {
        return QuestionRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideVacancyApi(retrofit: Retrofit): VacancyApi {
        return retrofit.create(VacancyApi::class.java)
    }

    @Provides
    @Singleton
    fun provideVacancyRepository(api: VacancyApi): VacancyRepository {
        return VacancyRepositoryImpl(api)
    }
}
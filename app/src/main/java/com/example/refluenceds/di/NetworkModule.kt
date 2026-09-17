package com.example.refluenceds.di

import com.example.refluenceds.data.local.SessionManager
import com.example.refluenceds.data.remote.api.ApiService
import com.example.refluenceds.data.remote.RefluencedsApi
import com.example.refluenceds.utils.Constants
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(sessionManager: SessionManager): Interceptor {
        return Interceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .header("Accept", "application/json")

            val token = sessionManager.getToken()
            if (!token.isNullOrEmpty() && original.header("Authorization") == null) {
                requestBuilder.header("Authorization", "Bearer $token")
            }

            val response = chain.proceed(requestBuilder.build())

            if (response.code == 401) {
                sessionManager.onSessionExpired()
                return@Interceptor response
            }

            val responseBody = response.body
            if (responseBody != null) {
                try {
                    val source = responseBody.source()
                    source.request(Long.MAX_VALUE)
                    val buffer = source.buffer
                    val contentType = responseBody.contentType()
                    val charset = contentType?.charset(java.nio.charset.StandardCharsets.UTF_8)
                        ?: java.nio.charset.StandardCharsets.UTF_8
                    val bodyString = buffer.clone().readString(charset)

                    if (isUnauthenticatedResponse(bodyString)) {
                        sessionManager.onSessionExpired()
                    }
                } catch (_: Exception) {}
            }

            response
        }
    }

    private fun isUnauthenticatedResponse(bodyString: String): Boolean {
        if (bodyString.isBlank()) return false
        return try {
            val json = org.json.JSONObject(bodyString)
            val status = json.optInt("status", -1)
            val success = json.optBoolean("success", true)
            val message = json.optString("message", "")

            status == 401 ||
                message.equals("Unauthenticated.", ignoreCase = true) ||
                message.equals("Unauthenticated", ignoreCase = true) ||
                (!success && (status == 401 || message.contains("Unauthenticated", ignoreCase = true)))
        } catch (_: Exception) {
            bodyString.contains("\"status\":401") ||
                bodyString.contains("\"status\": 401") ||
                bodyString.contains("Unauthenticated", ignoreCase = true)
        }
    }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(Constants.NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(Constants.NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(Constants.NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideRefluencedsApi(okHttpClient: OkHttpClient, moshi: Moshi): RefluencedsApi {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()
            .create(RefluencedsApi::class.java)
    }
}

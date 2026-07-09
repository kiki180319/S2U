package com.example.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

data class Part(
    val text: String? = null
)

data class Content(
    val parts: List<Part>,
    val role: String? = null
)

data class Tool(
    @Json(name = "google_search") val googleSearch: Map<String, String>? = null
)

data class ThinkingConfig(
    @Json(name = "thinking_budget") val thinkingBudget: Int? = null
)

data class GenerationConfig(
    @Json(name = "thinking_config") val thinkingConfig: ThinkingConfig? = null,
    @Json(name = "max_output_tokens") val maxOutputTokens: Int? = null,
    val temperature: Float? = null
)

data class GenerateContentRequest(
    val contents: List<Content>,
    @Json(name = "system_instruction") val systemInstruction: Content? = null,
    @Json(name = "generation_config") val generationConfig: GenerationConfig? = null,
    val tools: List<Tool>? = null
)

data class WebSource(
    val uri: String?,
    val title: String?
)

data class GroundingChunk(
    val web: WebSource? = null
)

data class GroundingMetadata(
    @Json(name = "webSearchQueries") val webSearchQueries: List<String>? = null,
    @Json(name = "groundingChunks") val groundingChunks: List<GroundingChunk>? = null
)

data class Candidate(
    val content: Content?,
    @Json(name = "groundingMetadata") val groundingMetadata: GroundingMetadata? = null
)

data class GenerateContentResponse(
    val candidates: List<Candidate>?
)

interface GeminiApiService {
    @POST("v1beta/models/{model}:generateContent")
    suspend fun generateContent(
        @Path("model") model: String,
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object GeminiApiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val service: GeminiApiService = retrofit.create(GeminiApiService::class.java)
}


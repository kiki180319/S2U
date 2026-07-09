package com.example.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

@JsonClass(generateAdapter = true)
data class H2HResponse(
    @Json(name = "group_name") val groupName: String,
    @Json(name = "agency") val agency: String,
    @Json(name = "fandom_name") val fandomName: String,
    @Json(name = "official_colors") val officialColors: List<String>,
    @Json(name = "members") val members: List<H2HMember>,
    @Json(name = "videos") val videos: List<H2HVideo>
)

@JsonClass(generateAdapter = true)
data class H2HMember(
    @Json(name = "stage_name") val stageName: String,
    @Json(name = "real_name_english") val realNameEnglish: String,
    @Json(name = "real_name_original") val realNameOriginal: String,
    @Json(name = "birthday") val birthday: String,
    @Json(name = "position") val position: List<String>,
    @Json(name = "height_cm") val heightCm: Double,
    @Json(name = "blood_type") val bloodType: String,
    @Json(name = "mbti") val mbti: String,
    @Json(name = "nationality") val nationality: String
)

@JsonClass(generateAdapter = true)
data class H2HVideo(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "type") val type: String,
    @Json(name = "youtube_id") val youtubeId: String,
    @Json(name = "video_url") val videoUrl: String,
    @Json(name = "thumbnail_url") val thumbnailUrl: String
)

interface H2HApiService {
    @GET("kiki180319/193f3d35d8109c78b30806c8b3ad76ec/raw/hearts2hearts.json")
    suspend fun getH2HData(): H2HResponse
}

object H2HApiClient {
    private const val BASE_URL = "https://gist.githubusercontent.com/"

    val retrofitService: H2HApiService by lazy {
        Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(H2HApiService::class.java)
    }
}

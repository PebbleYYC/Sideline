package com.sideline.app.data.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TheScoreApi {
    /**
     * Scores for one league on a date window.
     * @param league theScore slug: "nhl", "mlb", "nba". IIHF is tournament-specific
     *        (e.g. "wjc" for World Juniors) — confirm the slug for the event you want.
     * @param dateRange "<startISO>,<endISO>" in UTC, e.g.
     *        "2026-11-04T00:00:00Z,2026-11-05T00:00:00Z".
     */
    @GET("{league}/events")
    suspend fun events(
        @Path("league") league: String,
        @Query("game_date.in") dateRange: String,
    ): List<EventDto>

    companion object {
        private const val BASE_URL = "https://api.thescore.com/"

        fun create(): TheScoreApi {
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
            val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                })
                .build()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(TheScoreApi::class.java)
        }
    }
}

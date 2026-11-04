package com.sideline.app.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/*
 * DTOs for theScore's public REST API (api.thescore.com/{league}/events).
 * Mapped against a real NHL response — see ScoresRepository for the mapping.
 */

@JsonClass(generateAdapter = true)
data class EventDto(
    val id: Long? = null,
    // "final" | "pre_game" | "in_progress"
    @Json(name = "event_status") val eventStatus: String? = null,
    // RFC-1123, e.g. "Thu, 05 Nov 2026 00:30:00 -0000"
    @Json(name = "game_date") val gameDate: String? = null,
    val location: String? = null,
    val stadium: String? = null,
    @Json(name = "home_team") val homeTeam: TeamDto? = null,
    @Json(name = "away_team") val awayTeam: TeamDto? = null,
    @Json(name = "box_score") val boxScore: BoxScoreDto? = null,
)

@JsonClass(generateAdapter = true)
data class TeamDto(
    val id: Long? = null,
    val name: String? = null,
    @Json(name = "full_name") val fullName: String? = null,
    val abbreviation: String? = null,
    @Json(name = "colour_1") val colour1: String? = null,   // hex, no '#'
    @Json(name = "colour_2") val colour2: String? = null,
    val logos: LogosDto? = null,
)

@JsonClass(generateAdapter = true)
data class LogosDto(
    val tiny: String? = null,
    val small: String? = null,
    val large: String? = null,
)

@JsonClass(generateAdapter = true)
data class BoxScoreDto(
    val score: ScoreDto? = null,
    val progress: ProgressDto? = null,
)

@JsonClass(generateAdapter = true)
data class ScoreDto(
    val home: SideDto? = null,
    val away: SideDto? = null,
)

@JsonClass(generateAdapter = true)
data class SideDto(
    val score: Int? = null,
)

@JsonClass(generateAdapter = true)
data class ProgressDto(
    // Display-ready status, e.g. "Final", "7:00 pm".
    val string: String? = null,
    // Live clock + segment, e.g. clock "12:34", segment_string "3rd".
    val clock: String? = null,
    @Json(name = "segment_string") val segmentString: String? = null,
    val overtime: Boolean? = null,
    val shootout: Boolean? = null,
    @Json(name = "event_status") val eventStatus: String? = null,
)

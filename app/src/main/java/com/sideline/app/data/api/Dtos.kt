package com.sideline.app.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/*
 * DTOs for theScore's public REST API (api.thescore.com).
 *
 * IMPORTANT — verify against a live response before shipping.
 * Fetch https://api.thescore.com/nhl/events once and confirm the exact
 * field names below; theScore occasionally renames fields and some are
 * league-specific. Everything is nullable so a changed field degrades
 * gracefully instead of crashing. The mapping lives in Mappers.kt.
 */

@JsonClass(generateAdapter = true)
data class EventDto(
    val id: Long? = null,
    // "pre_game" | "in_progress" | "final" | "postponed" | "cancelled"
    val status: String? = null,
    @Json(name = "game_date") val gameDate: String? = null,
    // Human-readable status theScore itself shows, e.g. "Final", "Final (OT)", "7:00 pm".
    val description: String? = null,
    @Json(name = "status_detail") val statusDetail: String? = null,
    @Json(name = "home_team") val homeTeam: TeamDto? = null,
    @Json(name = "away_team") val awayTeam: TeamDto? = null,
    @Json(name = "box_score") val boxScore: BoxScoreDto? = null,
    // Live clock/segment while in_progress.
    @Json(name = "event_status") val eventStatus: EventStatusDto? = null,
)

@JsonClass(generateAdapter = true)
data class TeamDto(
    val id: Long? = null,
    val name: String? = null,
    @Json(name = "full_name") val fullName: String? = null,
    val abbreviation: String? = null,
    // Hex without the leading '#', e.g. "041E42".
    @Json(name = "colour_1") val colour1: String? = null,
    @Json(name = "colour_2") val colour2: String? = null,
    val logos: LogosDto? = null,
)

@JsonClass(generateAdapter = true)
data class LogosDto(
    val small: String? = null,
    val large: String? = null,
)

@JsonClass(generateAdapter = true)
data class BoxScoreDto(
    val score: ScoreDto? = null,
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
data class EventStatusDto(
    val clock: String? = null,
    val segment: Int? = null,
    @Json(name = "segment_string") val segmentString: String? = null,
)

package com.sideline.app.data

import com.sideline.app.data.api.EventDto
import com.sideline.app.data.api.TeamDto
import com.sideline.app.data.api.TheScoreApi
import com.sideline.app.data.model.GameState
import com.sideline.app.data.model.GameUi
import com.sideline.app.data.model.League
import com.sideline.app.data.model.TeamUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ScoresRepository(
    private val api: TheScoreApi = TheScoreApi.create(),
) {
    /** Games for a league on a given local day. */
    suspend fun games(league: League, day: LocalDate = LocalDate.now()): List<GameUi> =
        withContext(Dispatchers.IO) {
            // theScore's game_date is UTC; query the UTC instant window that
            // covers the user's local day, so evening games aren't missed.
            val zone = ZoneId.systemDefault()
            val start = day.atStartOfDay(zone).toInstant()
            val end = day.plusDays(1).atStartOfDay(zone).toInstant()
            val range = "${DateTimeFormatter.ISO_INSTANT.format(start)},${DateTimeFormatter.ISO_INSTANT.format(end)}"
            api.events(league.slug, range)
                // Guard: keep only games on the requested local day, in case the
                // server-side date filter behaves differently than expected.
                .filter { localDate(it.gameDate) == day }
                .map { it.toUi() }
        }

    private fun localDate(raw: String?): LocalDate? = try {
        if (raw.isNullOrBlank()) null
        else ZonedDateTime.parse(raw, DateTimeFormatter.RFC_1123_DATE_TIME)
            .withZoneSameInstant(ZoneId.systemDefault()).toLocalDate()
    } catch (e: Exception) { null }

    private fun EventDto.toUi(): GameUi {
        val state = when (eventStatus) {
            "in_progress" -> GameState.LIVE
            "final" -> GameState.FINAL
            else -> GameState.PRE
        }
        val prog = boxScore?.progress
        val statusLine = when (state) {
            GameState.FINAL -> when {
                prog?.shootout == true -> "Final (SO)"
                prog?.overtime == true -> "Final (OT)"
                else -> prog?.string ?: "Final"
            }
            GameState.LIVE -> prog?.string ?: "Live"
            GameState.PRE -> gameTime(gameDate)
        }
        return GameUi(
            id = id ?: 0L,
            away = awayTeam.toUi(boxScore?.score?.away?.score),
            home = homeTeam.toUi(boxScore?.score?.home?.score),
            state = state,
            statusLine = statusLine,
            liveClock = prog?.clock,
            liveSegment = prog?.segmentString,
            venue = stadium ?: location,
            dateLine = dateLine(gameDate),
        )
    }

    private fun TeamDto?.toUi(score: Int?): TeamUi = TeamUi(
        name = this?.name ?: this?.fullName ?: "—",
        fullName = this?.fullName ?: this?.name ?: "—",
        abbreviation = (this?.abbreviation ?: this?.name?.take(3) ?: "?").uppercase(),
        colorHex = this?.colour1?.let { if (it.startsWith("#")) it else "#$it" },
        logoUrl = this?.logos?.let { it.small ?: it.large ?: it.tiny },
        score = score,
    )

    private fun dateLine(raw: String?): String? = try {
        if (raw.isNullOrBlank()) null
        else ZonedDateTime.parse(raw, DateTimeFormatter.RFC_1123_DATE_TIME)
            .withZoneSameInstant(ZoneId.systemDefault())
            .format(DATE_FMT)
    } catch (e: Exception) { null }

    /** Parse the RFC-1123 game_date and show the local start time, e.g. "7:00 PM". */
    private fun gameTime(raw: String?): String = try {
        if (raw.isNullOrBlank()) "Upcoming"
        else ZonedDateTime.parse(raw, DateTimeFormatter.RFC_1123_DATE_TIME)
            .withZoneSameInstant(ZoneId.systemDefault())
            .format(TIME_FMT)
    } catch (e: Exception) { "Upcoming" }

    companion object {
        private val TIME_FMT = DateTimeFormatter.ofPattern("h:mm a")
        private val DATE_FMT = DateTimeFormatter.ofPattern("EEE, MMM d · h:mm a")
    }
}

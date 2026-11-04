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
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class ScoresRepository(
    private val api: TheScoreApi = TheScoreApi.create(),
) {
    /** Games for a league on a given local day. */
    suspend fun games(league: League, day: LocalDate = LocalDate.now()): List<GameUi> =
        withContext(Dispatchers.IO) {
            val start = day.atStartOfDay(ZoneOffset.UTC).format(ISO)
            val end = day.plusDays(1).atStartOfDay(ZoneOffset.UTC).format(ISO)
            api.events(league.slug, "$start,$end").map { it.toUi() }
        }

    private fun EventDto.toUi(): GameUi {
        val state = when (status) {
            "in_progress" -> GameState.LIVE
            "final" -> GameState.FINAL
            else -> GameState.PRE
        }
        val statusLine = description ?: statusDetail ?: when (state) {
            GameState.FINAL -> "Final"
            GameState.LIVE -> "Live"
            GameState.PRE -> "Upcoming"
        }
        return GameUi(
            id = id ?: 0L,
            away = awayTeam.toUi(boxScore?.score?.away?.score),
            home = homeTeam.toUi(boxScore?.score?.home?.score),
            state = state,
            statusLine = statusLine,
            liveClock = eventStatus?.clock,
            liveSegment = eventStatus?.segmentString,
        )
    }

    private fun TeamDto?.toUi(score: Int?): TeamUi = TeamUi(
        name = this?.name ?: this?.fullName ?: "—",
        abbreviation = (this?.abbreviation ?: this?.name?.take(3) ?: "?").uppercase(),
        colorHex = this?.colour1?.let { if (it.startsWith("#")) it else "#$it" },
        logoUrl = this?.logos?.small ?: this?.logos?.large,
        score = score,
    )

    companion object {
        private val ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
    }
}

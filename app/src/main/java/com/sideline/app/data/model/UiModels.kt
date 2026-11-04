package com.sideline.app.data.model

/** Leagues the app follows. `slug` is theScore's path segment. */
enum class League(val label: String, val slug: String) {
    NHL("NHL", "nhl"),
    MLB("MLB", "mlb"),
    NBA("NBA", "nba"),
    // IIHF events live under tournament-specific slugs on theScore (e.g. "wjc").
    // Set this to whichever tournament is active.
    IIHF("IIHF", "wjc"),
}

enum class GameState { PRE, LIVE, FINAL }

data class TeamUi(
    val name: String,
    val fullName: String,
    val abbreviation: String,
    val colorHex: String?,   // "#041E42" or null
    val logoUrl: String?,
    val score: Int?,
)

data class GameUi(
    val id: Long,
    val away: TeamUi,
    val home: TeamUi,
    val state: GameState,
    val statusLine: String,  // "Final (OT)", "7:00 pm"
    val liveClock: String?,  // "12:34" while live
    val liveSegment: String?,// "2nd" while live
    val venue: String?,      // "Scotiabank Arena"
    val dateLine: String?,   // "Tue, Nov 4 · 7:00 PM"
) {
    val awayWon get() = state == GameState.FINAL && (away.score ?: 0) > (home.score ?: 0)
    val homeWon get() = state == GameState.FINAL && (home.score ?: 0) > (away.score ?: 0)
}

/** UI state for the scores screen. */
sealed interface ScoresState {
    data object Loading : ScoresState
    data class Error(val message: String) : ScoresState
    data class Empty(val league: League) : ScoresState
    data class Loaded(val league: League, val games: List<GameUi>) : ScoresState
}

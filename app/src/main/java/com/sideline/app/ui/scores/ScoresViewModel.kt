package com.sideline.app.ui.scores

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sideline.app.data.ScoresRepository
import com.sideline.app.data.model.League
import com.sideline.app.data.model.ScoresState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ScoresViewModel(
    private val repo: ScoresRepository = ScoresRepository(),
) : ViewModel() {

    private val _state = MutableStateFlow<ScoresState>(ScoresState.Loading)
    val state: StateFlow<ScoresState> = _state.asStateFlow()

    var league: League = League.NHL
        private set

    init { load(League.NHL) }

    fun select(league: League) {
        if (league == this.league && _state.value !is ScoresState.Error) return
        load(league)
    }

    fun refresh() = load(league)

    private fun load(league: League) {
        this.league = league
        _state.value = ScoresState.Loading
        viewModelScope.launch {
            _state.value = try {
                val games = repo.games(league)
                if (games.isEmpty()) ScoresState.Empty(league)
                else ScoresState.Loaded(league, games)
            } catch (e: retrofit2.HttpException) {
                // 404 = no such league feed right now (e.g. IIHF out of tournament) → treat as empty.
                if (e.code() == 404) ScoresState.Empty(league)
                else ScoresState.Error("Server error ${e.code()}")
            } catch (e: Exception) {
                ScoresState.Error(e.message ?: "Couldn't load scores")
            }
        }
    }
}

package com.sideline.app.ui.scores

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.sideline.app.data.model.*
import com.sideline.app.ui.theme.*

// Team abbreviations to highlight as favorites (orange name + star).
private val FAVORITES = setOf("MTL", "PIT", "VAN")

@Composable
fun ScoresScreen(vm: ScoresViewModel = viewModel()) {
    val state by vm.state.collectAsStateWithLifecycle()
    Column(Modifier.fillMaxSize().background(Bg).statusBarsPadding()) {
        Header()
        DateRow()
        LeagueTabs(selected = vm.league, onSelect = vm::select)
        when (val s = state) {
            is ScoresState.Loading -> CenterBox { CircularProgressIndicator(color = Accent) }
            is ScoresState.Error -> CenterBox {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Couldn't load scores", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    Text(s.message, color = TextDim, fontSize = 13.sp, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(16.dp))
                    Text("Tap to retry", color = AccentBlue, fontSize = 14.sp,
                        modifier = Modifier.clickable { vm.refresh() })
                }
            }
            is ScoresState.Empty -> CenterBox {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No games today", color = Color(0xFFB9BEC6), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    Text("Nothing on the ${s.league.label} slate right now.", color = TextDim, fontSize = 13.5.sp)
                }
            }
            is ScoresState.Loaded -> LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(top = 4.dp, bottom = 24.dp)) {
                items(s.games, key = { it.id }) { GameCard(it) }
            }
        }
    }
}

@Composable
private fun Header() {
    Row(
        Modifier.fillMaxWidth().padding(start = 18.dp, end = 18.dp, top = 14.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row {
            Text("Side", color = TextPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 21.sp)
            Text("line", color = Accent, fontWeight = FontWeight.ExtraBold, fontSize = 21.sp)
        }
    }
}

@Composable
private fun DateRow() {
    Row(Modifier.fillMaxWidth().padding(start = 18.dp, end = 18.dp, bottom = 12.dp), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
        Text("Yesterday", color = TextDim, fontSize = 15.sp, fontWeight = FontWeight.Medium)
        Text("Today", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
        Text("Upcoming", color = TextDim, fontSize = 15.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun LeagueTabs(selected: League, onSelect: (League) -> Unit) {
    Row(
        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(start = 14.dp, end = 14.dp, bottom = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        League.entries.forEach { lg ->
            val on = lg == selected
            Text(
                lg.label,
                color = if (on) Color(0xFF1A1205) else Color(0xFFAEB3BB),
                fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clip(RoundedCornerShape(22.dp))
                    .background(if (on) Accent else Color(0xFF17181C))
                    .clickable { onSelect(lg) }
                    .padding(horizontal = 16.dp, vertical = 9.dp),
            )
        }
    }
}

@Composable
private fun GameCard(g: GameUi) {
    val ca = parseColor(g.away.colorHex)
    val cb = parseColor(g.home.colorHex)
    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 5.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Card)
            .background(Brush.horizontalGradient(
                0.0f to ca.copy(alpha = 0.22f), 0.42f to Color.Transparent,
                0.58f to Color.Transparent, 1.0f to cb.copy(alpha = 0.22f),
            ))
            .padding(horizontal = 14.dp, vertical = 13.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TeamSide(g.away, alignEnd = false, modifier = Modifier.weight(1f))
            Score(g.away.score, g.state, win = g.awayWon)
            MiddleColumn(g, Modifier.width(66.dp))
            Score(g.home.score, g.state, win = g.homeWon)
            TeamSide(g.home, alignEnd = true, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun TeamSide(t: TeamUi, alignEnd: Boolean, modifier: Modifier = Modifier) {
    val fav = t.abbreviation in FAVORITES
    Row(
        modifier,
        horizontalArrangement = if (alignEnd) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val logo: @Composable () -> Unit = { TeamLogo(t) }
        val name: @Composable () -> Unit = {
            Text(
                (if (fav) "★ " else "") + t.name,
                color = if (fav) Accent else Color(0xFFE9EBEE),
                fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                maxLines = 1, overflow = TextOverflow.Ellipsis,
            )
        }
        if (alignEnd) { name(); Spacer(Modifier.width(9.dp)); logo() }
        else { logo(); Spacer(Modifier.width(9.dp)); name() }
    }
}

@Composable
private fun TeamLogo(t: TeamUi) {
    val c = parseColor(t.colorHex).let { if (it == Color.Transparent) Color(0xFF2A2D33) else it }
    Box(Modifier.size(30.dp).clip(CircleShape).background(c), contentAlignment = Alignment.Center) {
        if (t.logoUrl != null) {
            AsyncImage(model = t.logoUrl, contentDescription = t.name,
                modifier = Modifier.size(30.dp), contentScale = ContentScale.Fit)
        } else {
            Text(t.abbreviation.take(3), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun Score(score: Int?, state: GameState, win: Boolean) {
    val text = if (state == GameState.PRE || score == null) "" else score.toString()
    Text(
        text,
        color = if (win) WinWhite else LoseGray,
        style = ScoreNumeral,
        textAlign = TextAlign.Center,
        modifier = Modifier.widthIn(min = 30.dp),
    )
}

@Composable
private fun MiddleColumn(g: GameUi, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(3.dp)) {
        when (g.state) {
            GameState.LIVE -> {
                Text("LIVE", color = LiveRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(g.liveSegment ?: g.statusLine, color = LiveRed, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                g.liveClock?.let { Text(it, color = TextDim, fontSize = 11.sp, maxLines = 1) }
            }
            GameState.FINAL -> Text(g.statusLine, color = Color(0xFFCFD3DA), fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center, maxLines = 2)
            GameState.PRE -> Text(g.statusLine, color = TextDim, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center, maxLines = 2)
        }
    }
}

@Composable
private fun ColumnScope.CenterBox(content: @Composable () -> Unit) {
    Box(Modifier.weight(1f).fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) { content() }
}

private fun parseColor(hex: String?): Color = try {
    if (hex.isNullOrBlank()) Color.Transparent
    else Color(android.graphics.Color.parseColor(if (hex.startsWith("#")) hex else "#$hex"))
} catch (e: Exception) { Color.Transparent }

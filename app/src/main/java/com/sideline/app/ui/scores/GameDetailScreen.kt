package com.sideline.app.ui.scores

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.sideline.app.data.model.GameState
import com.sideline.app.data.model.GameUi
import com.sideline.app.data.model.TeamUi
import com.sideline.app.ui.theme.*

@Composable
fun GameDetailScreen(g: GameUi, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().background(Bg).statusBarsPadding()) {
        // Top bar
        Row(
            Modifier.fillMaxWidth().padding(start = 12.dp, end = 18.dp, top = 10.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("‹  Back", color = AccentBlue, fontSize = 16.sp, fontWeight = FontWeight.Medium,
                modifier = Modifier.clip(RoundedCornerShape(8.dp)).clickable { onBack() }.padding(horizontal = 6.dp, vertical = 6.dp))
        }

        Spacer(Modifier.height(10.dp))

        // Status
        val (statusText, statusColor) = when (g.state) {
            GameState.LIVE -> (listOfNotNull(g.liveSegment, g.liveClock).joinToString("  ").ifBlank { "Live" }) to LiveRed
            GameState.FINAL -> g.statusLine to Color(0xFFCFD3DA)
            GameState.PRE -> (g.dateLine ?: g.statusLine) to TextDim
        }
        Text(statusText, color = statusColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(18.dp))

        // Matchup — two team rows with big scores
        TeamRow(g.away, g.state, win = g.awayWon)
        Spacer(Modifier.height(8.dp))
        TeamRow(g.home, g.state, win = g.homeWon)

        Spacer(Modifier.height(28.dp))

        // Info card
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp).clip(RoundedCornerShape(14.dp))
                .background(Card).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            g.dateLine?.let { InfoRow("When", it) }
            g.venue?.let { InfoRow("Venue", it) }
            InfoRow("Status", g.statusLine)
        }

        Spacer(Modifier.height(20.dp))
        Text("Box score and team stats coming soon.", color = TextDimmer, fontSize = 12.5.sp,
            textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun TeamRow(t: TeamUi, state: GameState, win: Boolean) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val c = parseColor(t.colorHex).let { if (it == Color.Transparent) Color(0xFF2A2D33) else it }
        Box(Modifier.size(40.dp).clip(CircleShape).background(c), contentAlignment = Alignment.Center) {
            if (t.logoUrl != null) AsyncImage(model = t.logoUrl, contentDescription = t.fullName,
                modifier = Modifier.size(40.dp), contentScale = ContentScale.Fit)
            else Text(t.abbreviation.take(3), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
        }
        Spacer(Modifier.width(14.dp))
        Text(t.fullName, color = if (win || state != GameState.FINAL) WinWhite else LoseGray,
            fontSize = 19.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f))
        Text(
            if (state == GameState.PRE || t.score == null) "" else t.score.toString(),
            color = if (win) WinWhite else LoseGray,
            fontSize = 40.sp, fontWeight = FontWeight.ExtraBold,
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = TextDim, fontSize = 13.sp)
        Text(value, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End, modifier = Modifier.padding(start = 16.dp))
    }
}

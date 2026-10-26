package com.privatereelstracker.ui.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.privatereelstracker.ui.analytics.components.SevenDayBarChart
import com.privatereelstracker.ui.theme.AccentGold
import com.privatereelstracker.ui.theme.AccentPink
import com.privatereelstracker.ui.theme.AccentTeal
import com.privatereelstracker.ui.theme.DarkBackground
import com.privatereelstracker.ui.theme.PrimaryNeon
import com.privatereelstracker.ui.theme.SurfaceBorder
import com.privatereelstracker.ui.theme.SurfaceCard
import com.privatereelstracker.ui.theme.TextMuted
import com.privatereelstracker.ui.theme.TextPrimary
import com.privatereelstracker.ui.theme.TextSecondary
import com.privatereelstracker.util.DateUtils
import java.util.Locale

@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Text(
                text = "Usage Analytics",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 7-day bar chart
        if (state.recentDays.isNotEmpty()) {
            SevenDayBarChart(daysData = state.recentDays)
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceCard)
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(16.dp))
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("No historical data recorded yet.", color = TextMuted, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Summary Statistics Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricBox(
                title = "Daily Average",
                value = String.format(Locale.US, "%.1f", state.averageReelsPerDay),
                subtitle = "Reels / day",
                modifier = Modifier.weight(1f),
                valueColor = PrimaryNeon
            )
            MetricBox(
                title = "Peak Count",
                value = "${state.highestDayCount}",
                subtitle = "Highest day",
                modifier = Modifier.weight(1f),
                valueColor = AccentGold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricBox(
                title = "Avg Screen Time",
                value = DateUtils.formatDuration(state.averageScreenTimeMs),
                subtitle = "Daily average",
                modifier = Modifier.weight(1f),
                valueColor = AccentTeal
            )
            MetricBox(
                title = "Total Screen Time",
                value = DateUtils.formatDuration(state.totalScreenTimeMs),
                subtitle = "Last 7 days",
                modifier = Modifier.weight(1f),
                valueColor = TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Day by day listing
        Text(
            text = "Daily Breakdown",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(10.dp))

        state.recentDays.reversed().forEach { day ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceCard)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = day.dateString, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                    Text(
                        text = "Screen time: ${DateUtils.formatDuration(day.instagramScreenTimeMs)}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
                Text(
                    text = "${day.reelCount} Reels",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (day.reelCount > day.dailyGoal) AccentPink else AccentTeal
                )
            }
        }
    }
}

@Composable
private fun MetricBox(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    valueColor: androidx.compose.ui.graphics.Color
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceCard)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Text(text = title, fontSize = 12.sp, color = TextSecondary)
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = valueColor)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = subtitle, fontSize = 11.sp, color = TextMuted)
    }
}

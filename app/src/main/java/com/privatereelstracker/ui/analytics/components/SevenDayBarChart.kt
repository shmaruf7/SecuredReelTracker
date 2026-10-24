package com.privatereelstracker.ui.analytics.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.privatereelstracker.data.local.entity.DailyReelCount
import com.privatereelstracker.ui.theme.AccentPink
import com.privatereelstracker.ui.theme.AccentTeal
import com.privatereelstracker.ui.theme.PrimaryNeon
import com.privatereelstracker.ui.theme.SurfaceBorder
import com.privatereelstracker.ui.theme.SurfaceCard
import com.privatereelstracker.ui.theme.TextMuted
import com.privatereelstracker.ui.theme.TextPrimary
import com.privatereelstracker.ui.theme.TextSecondary

@Composable
fun SevenDayBarChart(
    daysData: List<DailyReelCount>
) {
    val maxCount = (daysData.maxOfOrNull { it.reelCount } ?: 50).coerceAtLeast(10)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceCard)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "7-Day Reels Activity",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            daysData.forEach { day ->
                val ratio = (day.reelCount.toFloat() / maxCount.toFloat()).coerceIn(0.04f, 1f)
                val isExceeded = day.reelCount > day.dailyGoal

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        text = "${day.reelCount}",
                        fontSize = 10.sp,
                        color = if (isExceeded) AccentPink else TextSecondary,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.55f)
                            .height((120 * ratio).dp)
                            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                            .background(
                                Brush.verticalGradient(
                                    if (isExceeded) listOf(AccentPink, PrimaryNeon)
                                    else listOf(AccentTeal, PrimaryNeon)
                                )
                            )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Show date label (e.g., "24" or last 2 chars)
                    val label = if (day.dateString.length >= 5) day.dateString.takeLast(5) else day.dateString
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }
            }
        }
    }
}

package com.privatereelstracker.ui.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.privatereelstracker.ui.theme.AccentGold
import com.privatereelstracker.ui.theme.AccentPink
import com.privatereelstracker.ui.theme.AccentTeal
import com.privatereelstracker.ui.theme.SurfaceBorder
import com.privatereelstracker.ui.theme.SurfaceCard
import com.privatereelstracker.ui.theme.TextPrimary
import com.privatereelstracker.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun DayComparisonCard(
    todayCount: Int,
    yesterdayCount: Int,
    deltaPercent: Double?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceCard)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Day-over-Day Comparison",
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = TextPrimary
            )

            if (deltaPercent != null) {
                val isIncrease = deltaPercent > 0
                val color = if (isIncrease) AccentPink else AccentTeal
                val prefix = if (isIncrease) "+" else ""
                Text(
                    text = String.format(Locale.US, "%s%.1f%%", prefix, deltaPercent),
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = "Yesterday", fontSize = 12.sp, color = TextSecondary)
                Text(text = "$yesterdayCount Reels", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "Difference", fontSize = 12.sp, color = TextSecondary)
                val diff = todayCount - yesterdayCount
                val diffColor = when {
                    diff > 0 -> AccentPink
                    diff < 0 -> AccentTeal
                    else -> AccentGold
                }
                val diffText = if (diff > 0) "+$diff Reels" else "$diff Reels"
                Text(text = diffText, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = diffColor)
            }
        }
    }
}

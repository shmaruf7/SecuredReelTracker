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
import com.privatereelstracker.ui.theme.AccentPink
import com.privatereelstracker.ui.theme.AccentTeal
import com.privatereelstracker.ui.theme.SurfaceBorder
import com.privatereelstracker.ui.theme.SurfaceCard
import com.privatereelstracker.ui.theme.TextMuted
import com.privatereelstracker.ui.theme.TextPrimary
import com.privatereelstracker.ui.theme.TextSecondary
import com.privatereelstracker.util.DateUtils

@Composable
fun ScreenTimeCard(
    totalScreenTimeMs: Long,
    estimatedReelsTimeMs: Long,
    hasUsagePermission: Boolean,
    onGrantPermissionClick: () -> Unit
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
                text = "Screen Time Today",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = TextPrimary
            )
            if (!hasUsagePermission) {
                Text(
                    text = "Permission Needed",
                    fontSize = 12.sp,
                    color = AccentPink,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Tracked Apps",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Text(
                    text = if (hasUsagePermission) DateUtils.formatDuration(totalScreenTimeMs) else "--",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Est. Short-Video Time",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Text(
                    text = if (hasUsagePermission) DateUtils.formatDuration(estimatedReelsTimeMs) else "--",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentTeal
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Total foreground time measured across Instagram, YouTube, Facebook & TikTok; short-video time estimated from swipe frequency.",
            fontSize = 11.sp,
            color = TextMuted,
            lineHeight = 14.sp
        )
    }
}

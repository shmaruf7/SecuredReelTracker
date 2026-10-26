package com.privatereelstracker.ui.dashboard.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.privatereelstracker.ui.theme.AccentPink
import com.privatereelstracker.ui.theme.SurfaceBorder
import com.privatereelstracker.ui.theme.SurfaceCard
import com.privatereelstracker.ui.theme.TextMuted
import com.privatereelstracker.ui.theme.TextPrimary
import com.privatereelstracker.ui.theme.TextSecondary

private val InstagramColor = Color(0xFFE1306C)
private val YouTubeColor = Color(0xFFFF2A2A)
private val FacebookColor = Color(0xFF1877F2)
private val TikTokColor = Color(0xFF00F2FE)

@Composable
fun AppBreakdownSection(
    instagramCount: Int,
    instagramLimit: Int,
    youtubeCount: Int,
    youtubeLimit: Int,
    facebookCount: Int,
    facebookLimit: Int,
    tiktokCount: Int,
    tiktokLimit: Int,
    onUpdateLimit: (String, Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "App-Wise Reel Counts & Limits",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Set daily limits and monitor scroll counts per app",
            fontSize = 12.sp,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(12.dp))

        AppRowCard(
            platform = "Instagram",
            subtitle = "Instagram Reels",
            count = instagramCount,
            limit = instagramLimit,
            brandColor = InstagramColor,
            onUpdateLimit = { onUpdateLimit("Instagram", it) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        AppRowCard(
            platform = "YouTube",
            subtitle = "YouTube Shorts",
            count = youtubeCount,
            limit = youtubeLimit,
            brandColor = YouTubeColor,
            onUpdateLimit = { onUpdateLimit("YouTube", it) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        AppRowCard(
            platform = "Facebook",
            subtitle = "Facebook Reels",
            count = facebookCount,
            limit = facebookLimit,
            brandColor = FacebookColor,
            onUpdateLimit = { onUpdateLimit("Facebook", it) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        AppRowCard(
            platform = "TikTok",
            subtitle = "TikTok Videos",
            count = tiktokCount,
            limit = tiktokLimit,
            brandColor = TikTokColor,
            onUpdateLimit = { onUpdateLimit("TikTok", it) }
        )
    }
}

@Composable
private fun AppRowCard(
    platform: String,
    subtitle: String,
    count: Int,
    limit: Int,
    brandColor: Color,
    onUpdateLimit: (Int) -> Unit
) {
    val progress = if (limit > 0) (count.toFloat() / limit.toFloat()).coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "appProgress")
    val isOverLimit = count >= limit

    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, if (isOverLimit) AccentPink.copy(alpha = 0.8f) else SurfaceBorder, RoundedCornerShape(14.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(
                        modifier = Modifier
                            .width(10.dp)
                            .height(10.dp)
                            .clip(RoundedCornerShape(50))
                            .background(brandColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = platform,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = subtitle,
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$count",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isOverLimit) AccentPink else brandColor
                    )
                    Text(
                        text = " / $limit limit",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = { onUpdateLimit((limit - 5).coerceAtLeast(5)) },
                        modifier = Modifier.padding(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrease Limit",
                            tint = TextMuted
                        )
                    }

                    IconButton(
                        onClick = { onUpdateLimit(limit + 5) },
                        modifier = Modifier.padding(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase Limit",
                            tint = brandColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (isOverLimit) AccentPink else brandColor,
                trackColor = SurfaceBorder
            )

            if (isOverLimit) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "⚠️ Limit reached for $platform",
                    fontSize = 10.sp,
                    color = AccentPink,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

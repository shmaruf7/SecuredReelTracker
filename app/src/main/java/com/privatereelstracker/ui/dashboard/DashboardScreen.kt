package com.privatereelstracker.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.privatereelstracker.ui.dashboard.components.DailyGoalRing
import com.privatereelstracker.ui.dashboard.components.DayComparisonCard
import com.privatereelstracker.ui.dashboard.components.ScreenTimeCard
import com.privatereelstracker.ui.theme.AccentGold
import com.privatereelstracker.ui.theme.AccentPink
import com.privatereelstracker.ui.theme.AccentTeal
import com.privatereelstracker.ui.theme.DarkBackground
import com.privatereelstracker.ui.theme.PrimaryNeon
import com.privatereelstracker.ui.theme.SurfaceCard
import com.privatereelstracker.ui.theme.TextMuted
import com.privatereelstracker.ui.theme.TextPrimary
import com.privatereelstracker.ui.theme.TextSecondary

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateAnalytics: () -> Unit,
    onNavigateDiagnostics: () -> Unit,
    onNavigateSettings: () -> Unit,
    onRequestAccessibilityPermission: () -> Unit,
    onRequestUsageAccessPermission: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshDashboard()
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Quiet Scrolling",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Local-Only Reels Counter",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }

            Row {
                IconButton(onClick = onNavigateAnalytics) {
                    Icon(
                        imageVector = Icons.Default.BarChart,
                        contentDescription = "Analytics",
                        tint = AccentTeal
                    )
                }
                IconButton(onClick = onNavigateDiagnostics) {
                    Icon(
                        imageVector = Icons.Default.BugReport,
                        contentDescription = "Diagnostics",
                        tint = AccentGold
                    )
                }
                IconButton(onClick = onNavigateSettings) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Permission banners if missing
        if (!state.isAccessibilityEnabled || !state.isUsageAccessEnabled) {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = AccentPink
                        )
                        Text(
                            text = " Action Required for Tracking",
                            fontWeight = FontWeight.Bold,
                            color = AccentPink,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "To track Reels counts and screen time locally without cloud sync, enable the consent permissions below:",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (!state.isAccessibilityEnabled) {
                            Button(
                                onClick = onRequestAccessibilityPermission,
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryNeon),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Accessibility", fontSize = 11.sp)
                            }
                        }
                        if (!state.isUsageAccessEnabled) {
                            Button(
                                onClick = onRequestUsageAccessPermission,
                                colors = ButtonDefaults.buttonColors(containerColor = AccentTeal),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Usage Access", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Large Center Limit Ring
        com.privatereelstracker.ui.dashboard.components.DailyLimitRing(
            currentCount = state.todayReels,
            maxLimit = state.dailyGoal
        )

        if (state.isGoalExceeded) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Daily limit reached. Take a break!",
                color = AccentPink,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // App-wise Reels & Shorts Breakdown
        com.privatereelstracker.ui.dashboard.components.AppBreakdownSection(
            instagramCount = state.instagramCount,
            instagramLimit = state.instagramLimit,
            youtubeCount = state.youtubeCount,
            youtubeLimit = state.youtubeLimit,
            facebookCount = state.facebookCount,
            facebookLimit = state.facebookLimit,
            tiktokCount = state.tiktokCount,
            tiktokLimit = state.tiktokLimit,
            onUpdateLimit = { platform, limit -> viewModel.updateAppLimit(platform, limit) }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Screen Time Card
        ScreenTimeCard(
            totalScreenTimeMs = state.instagramScreenTimeMs,
            estimatedReelsTimeMs = state.estimatedReelsTimeMs,
            hasUsagePermission = state.isUsageAccessEnabled,
            onGrantPermissionClick = onRequestUsageAccessPermission
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Day comparison card
        DayComparisonCard(
            todayCount = state.todayReels,
            yesterdayCount = state.yesterdayReels,
            deltaPercent = state.deltaPercentFromYesterday
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Manual Test Simulator Button
        OutlinedButton(
            onClick = { viewModel.triggerManualTestIncrement("Instagram") },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = AccentTeal)
            Text(
                text = " Test Simulated Instagram Reel Swipe (+1)",
                color = AccentTeal,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

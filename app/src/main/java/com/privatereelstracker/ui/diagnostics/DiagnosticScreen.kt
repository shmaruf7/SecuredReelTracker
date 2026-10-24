package com.privatereelstracker.ui.diagnostics

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.privatereelstracker.ui.theme.AccentPink
import com.privatereelstracker.ui.theme.AccentTeal
import com.privatereelstracker.ui.theme.DarkBackground
import com.privatereelstracker.ui.theme.SurfaceBorder
import com.privatereelstracker.ui.theme.SurfaceCard
import com.privatereelstracker.ui.theme.TextMuted
import com.privatereelstracker.ui.theme.TextPrimary
import com.privatereelstracker.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DiagnosticScreen(
    viewModel: DiagnosticViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val timeFormatter = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
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
            Column {
                Text(
                    text = "Event Diagnostics",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Non-sensitive detection log (last 100 events)",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (state.logs.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceCard)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No detection events recorded yet.\nOpen Instagram or tap 'Test Simulated Reel Swipe' on Dashboard to see events here.",
                            color = TextMuted,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            items(state.logs) { log ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceCard)
                        .border(1.dp, SurfaceBorder, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = log.eventType,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (log.wasIncremented) AccentTeal else TextSecondary
                        )
                        Text(
                            text = if (log.wasIncremented) "+1 COUNTED" else "DEBOUNCED",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (log.wasIncremented) AccentTeal else AccentPink
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = log.details,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = timeFormatter.format(Date(log.timestamp)),
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }
            }
        }
    }
}

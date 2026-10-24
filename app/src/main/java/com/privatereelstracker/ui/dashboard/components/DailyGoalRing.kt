package com.privatereelstracker.ui.dashboard.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.privatereelstracker.ui.theme.AccentPink
import com.privatereelstracker.ui.theme.AccentTeal
import com.privatereelstracker.ui.theme.PrimaryNeon
import com.privatereelstracker.ui.theme.SurfaceBorder
import com.privatereelstracker.ui.theme.TextMuted
import com.privatereelstracker.ui.theme.TextPrimary

@Composable
fun DailyGoalRing(
    currentCount: Int,
    goal: Int,
    size: Dp = 190.dp,
    strokeWidth: Dp = 14.dp
) {
    val progress = if (goal > 0) (currentCount.toFloat() / goal.toFloat()).coerceIn(0f, 1.25f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 800),
        label = "GoalRingProgress"
    )

    val gradientColors = if (currentCount > goal) {
        listOf(AccentPink, PrimaryNeon)
    } else {
        listOf(AccentTeal, PrimaryNeon)
    }

    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            // Background track
            drawCircle(
                color = SurfaceBorder,
                radius = (size.toPx() - strokeWidth.toPx()) / 2f,
                style = stroke
            )
            // Active progress arc
            drawArc(
                brush = Brush.sweepGradient(gradientColors),
                startAngle = -90f,
                sweepAngle = (animatedProgress.coerceAtMost(1f)) * 360f,
                useCenter = false,
                style = stroke
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$currentCount",
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "of $goal goal",
                fontSize = 13.sp,
                color = TextMuted
            )
        }
    }
}

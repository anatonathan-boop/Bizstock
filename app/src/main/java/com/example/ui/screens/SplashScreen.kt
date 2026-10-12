package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandBlueLight
import com.example.ui.theme.BrandCyan
import com.example.ui.theme.BrandNavyDark
import com.example.ui.theme.BrandSlateLight
import com.example.ui.theme.SurfaceMutedLight
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onTimeout: () -> Unit
) {
    val scale = remember { Animatable(0.88f) }
    val alpha = remember { Animatable(0f) }
    val contentAlpha = remember { Animatable(0f) }

    // Infinite rotating animation around the central icon
    val infiniteTransition = rememberInfiniteTransition(label = "splash_spin_transition")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation_spinner"
    )

    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_glow"
    )

    LaunchedEffect(Unit) {
        launch {
            scale.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
            )
        }
        launch {
            alpha.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(durationMillis = 400)
            )
        }
        launch {
            delay(200)
            contentAlpha.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(durationMillis = 400)
            )
        }
        // Non-blocking smooth splash delay before checking auth state
        delay(1400)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceMutedLight)
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .scale(scale.value)
                .alpha(alpha.value)
                .padding(24.dp)
        ) {
            // Container with rotating loading ring around the center BIZSTOCK icon
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(136.dp)
                    .testTag("splash_logo_container")
            ) {
                // Background subtle track circle
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = Color(0xFFE2E8F0),
                        style = Stroke(width = 3.5.dp.toPx())
                    )
                }

                // Rotating animated gradient arc around the central icon
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(pulseGlow)
                        .testTag("splash_loading_indicator")
                ) {
                    val strokeWidth = 4.dp.toPx()
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(
                                BrandCyan,
                                BrandBlue,
                                BrandBlueLight,
                                Color.Transparent
                            )
                        ),
                        startAngle = rotationAngle,
                        sweepAngle = 280f,
                        useCenter = false,
                        style = Stroke(
                            width = strokeWidth,
                            cap = StrokeCap.Round
                        )
                    )
                }

                // Center BIZSTOCK Logo
                Image(
                    painter = painterResource(id = R.drawable.bizstock_app_icon_1791310032354),
                    contentDescription = "Logo BIZSTOCK",
                    modifier = Modifier
                        .size(86.dp)
                        .clip(RoundedCornerShape(20.dp))
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Branding text & tagline
            Text(
                text = "BIZSTOCK",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = BrandNavyDark,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Gestion commerciale & Stock",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = BrandSlateLight
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Loading status text
            Text(
                text = "Démarrage en cours...",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = BrandBlue,
                modifier = Modifier
                    .alpha(contentAlpha.value)
                    .testTag("splash_status_text")
            )
        }
    }
}



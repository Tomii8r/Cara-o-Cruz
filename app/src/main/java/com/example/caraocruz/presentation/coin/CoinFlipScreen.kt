package com.example.caraocruz.presentation.coin

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.caraocruz.R
import com.example.caraocruz.ui.theme.BackgroundBottom
import com.example.caraocruz.ui.theme.BackgroundMiddle
import com.example.caraocruz.ui.theme.BackgroundTop
import com.example.caraocruz.ui.theme.CaraOCruzTheme
import com.example.caraocruz.ui.theme.LilacGlow
import com.example.caraocruz.ui.theme.PeachGlow

@Composable
fun CoinFlipScreen(
    uiState: CoinFlipUiState,
    onFlipClick: () -> Unit,
    showSettings: Boolean = false,
    isDarkMode: Boolean = false,
    onSettingsClick: () -> Unit = {},
    onDismissSettings: () -> Unit = {},
    onDarkModeChange: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        GradientBackground()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.title_question),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            CoinView(
                uiState = uiState,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = resultText(uiState),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onFlipClick,
                enabled = uiState !is CoinFlipUiState.Flipping
            ) {
                Text(text = buttonText(uiState))
            }
        }
        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .safeDrawingPadding()
                .padding(top = 8.dp, end = 16.dp)
                .size(44.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = stringResource(R.string.cd_settings),
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
    }
    if (showSettings) {
        SettingsDialog(
            isDarkMode = isDarkMode,
            onDarkModeChange = onDarkModeChange,
            onDismiss = onDismissSettings
        )
    }
}

private val SwitchDark = Color(0xFF28292C)
private val SwitchLight = Color(0xFFD8DBE0)

@Composable
private fun SettingsDialog(
    isDarkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 12.dp
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, top = 20.dp, end = 12.dp, bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.settings_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.cd_close),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .toggleable(
                            value = isDarkMode,
                            onValueChange = onDarkModeChange,
                            role = Role.Switch
                        )
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.settings_mode),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(
                                if (isDarkMode) {
                                    R.string.settings_mode_dark
                                } else {
                                    R.string.settings_mode_light
                                }
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    ModeSwitch(checked = isDarkMode)
                }
            }
        }
    }
}

@Composable
private fun ModeSwitch(checked: Boolean) {
    val progress by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "modeSwitch"
    )
    val trackColor = lerp(SwitchLight, SwitchDark, progress)
    val knobOffset = 32.dp * (1f - progress)
    val crescentAlpha = progress

    Box(
        modifier = Modifier.size(width = 64.dp, height = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(width = 64.dp, height = 32.dp)
                .clip(CircleShape)
                .background(trackColor)
                .border(2.dp, SwitchDark, CircleShape)
                .padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .offset(x = knobOffset)
                    .size(16.dp)
                    .clip(CircleShape)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(SwitchDark)
                    if (crescentAlpha > 0f) {
                        drawCircle(
                            color = SwitchLight.copy(alpha = crescentAlpha),
                            radius = size.minDimension * 0.55f,
                            center = Offset(size.width * 0.72f, size.height * 0.28f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GradientBackground(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        BackgroundTop,
                        BackgroundMiddle,
                        BackgroundBottom
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val lilacCenter = Offset(size.width * 0.85f, size.height * 0.18f)
            val lilacRadius = size.minDimension * 0.65f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(LilacGlow.copy(alpha = 0.55f), Color.Transparent),
                    center = lilacCenter,
                    radius = lilacRadius
                ),
                radius = lilacRadius,
                center = lilacCenter
            )

            val peachCenter = Offset(size.width * 0.08f, size.height * 0.85f)
            val peachRadius = size.minDimension * 0.55f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(PeachGlow.copy(alpha = 0.4f), Color.Transparent),
                    center = peachCenter,
                    radius = peachRadius
                ),
                radius = peachRadius,
                center = peachCenter
            )
        }
    }
}

private const val FLIP_FULL_TURNS = 4

@Composable
private fun CoinView(
    uiState: CoinFlipUiState,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val coinSize = minOf(maxWidth * 0.60f, 235.dp)
        val density = LocalDensity.current.density
        val initialAngle = if (uiState is CoinFlipUiState.Result && uiState.side == CoinSide.CRUZ) {
            180f
        } else {
            0f
        }
        val rotation = remember { Animatable(initialAngle) }
        val flipId = when (val state = uiState) {
            CoinFlipUiState.Idle -> 0
            is CoinFlipUiState.Flipping -> state.flipId
            is CoinFlipUiState.Result -> state.flipId
        }

        LaunchedEffect(flipId) {
            val state = uiState
            if (state is CoinFlipUiState.Flipping) {
                val currentAngle = rotation.value
                val currentMod = ((currentAngle % 360f) + 360f) % 360f
                val targetMod = if (state.target == CoinSide.CARA) 0f else 180f
                val delta = (targetMod - currentMod + 360f) % 360f
                rotation.animateTo(
                    targetValue = currentAngle + 360f * FLIP_FULL_TURNS + delta,
                    animationSpec = tween(
                        durationMillis = FLIP_DURATION_MS.toInt(),
                        easing = LinearEasing
                    )
                )
            }
        }

        val angle = rotation.value
        val normalized = ((angle % 360f) + 360f) % 360f
        val showCara = normalized < 90f || normalized > 270f
        val side = if (showCara) CoinSide.CARA else CoinSide.CRUZ
        val faceAngle = if (showCara) angle else angle + 180f

        Image(
            painter = painterResource(
                if (side == CoinSide.CARA) R.drawable.coin_cara else R.drawable.coin_cruz
            ),
            contentDescription = stringResource(
                if (side == CoinSide.CARA) R.string.cd_coin_cara else R.string.cd_coin_cruz
            ),
            modifier = Modifier
                .size(coinSize)
                .graphicsLayer {
                    rotationY = faceAngle
                    cameraDistance = 12f * density
                }
        )
    }
}

@Composable
private fun resultText(uiState: CoinFlipUiState): String = when (uiState) {
    CoinFlipUiState.Idle -> ""
    is CoinFlipUiState.Flipping -> stringResource(R.string.action_flipping)
    is CoinFlipUiState.Result -> when (uiState.side) {
        CoinSide.CARA -> stringResource(R.string.result_cara)
        CoinSide.CRUZ -> stringResource(R.string.result_cruz)
    }
}

@Composable
private fun buttonText(uiState: CoinFlipUiState): String = when (uiState) {
    CoinFlipUiState.Idle -> stringResource(R.string.action_flip)
    is CoinFlipUiState.Flipping -> stringResource(R.string.action_flipping)
    is CoinFlipUiState.Result -> stringResource(R.string.action_flip_again)
}

@Preview(showBackground = true)
@Composable
private fun CoinFlipScreenIdlePreview() {
    CaraOCruzTheme {
        CoinFlipScreen(
            uiState = CoinFlipUiState.Idle,
            onFlipClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CoinFlipScreenFlippingPreview() {
    CaraOCruzTheme {
        CoinFlipScreen(
            uiState = CoinFlipUiState.Flipping(CoinSide.CARA, 1),
            onFlipClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CoinFlipScreenResultPreview() {
    CaraOCruzTheme {
        CoinFlipScreen(
            uiState = CoinFlipUiState.Result(CoinSide.CRUZ, 1),
            onFlipClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CoinFlipScreenSettingsPreview() {
    CaraOCruzTheme {
        CoinFlipScreen(
            uiState = CoinFlipUiState.Result(CoinSide.CARA, 1),
            onFlipClick = {},
            showSettings = true
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CoinFlipScreenSettingsDarkPreview() {
    CaraOCruzTheme {
        CoinFlipScreen(
            uiState = CoinFlipUiState.Result(CoinSide.CARA, 1),
            onFlipClick = {},
            showSettings = true,
            isDarkMode = true
        )
    }
}

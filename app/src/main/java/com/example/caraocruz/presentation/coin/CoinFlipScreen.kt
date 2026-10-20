package com.example.caraocruz.presentation.coin

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
            BoxWithConstraints(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                val coinSize = minOf(maxWidth * 0.55f, 220.dp)
                val side = uiState.visibleSide()
                Image(
                    painter = painterResource(
                        if (side == CoinSide.CARA) R.drawable.coin_cara else R.drawable.coin_cruz
                    ),
                    contentDescription = stringResource(
                        if (side == CoinSide.CARA) R.string.cd_coin_cara else R.string.cd_coin_cruz
                    ),
                    modifier = Modifier.size(coinSize)
                )
            }
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

private fun CoinFlipUiState.visibleSide(): CoinSide = when (this) {
    CoinFlipUiState.Idle -> CoinSide.CARA
    is CoinFlipUiState.Flipping -> target
    is CoinFlipUiState.Result -> side
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

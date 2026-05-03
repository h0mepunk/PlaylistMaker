package com.example.playlistmaker.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R

@Composable
fun ErrorChip(
    visible: Boolean,
    modifier: Modifier = Modifier,
    text: String,
    iconId: Int = R.drawable.empty_results_error,
    buttonVisible: Boolean = false,
    buttonText: String = "",
    imageTopPadding: Dp = 0.dp,
    textTopPadding: Dp = 16.dp,
    onClick: () -> Unit,
) {
    if (visible) {
        Column(
            modifier = modifier
                .fillMaxWidth()
        ) {
            Image(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = imageTopPadding),
                painter = painterResource(iconId),
                contentDescription = null
            )
            Text(
                text = text,
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontFamily = FontFamily(
                        fonts = listOf(Font(R.font.ys_text_medium))
                    ),
                    fontSize = 19.sp,
                    fontWeight = FontWeight(400)
                ),
                modifier = Modifier
                    .padding(top = textTopPadding)
                    .align(Alignment.CenterHorizontally)
            )
            if (buttonVisible && buttonText.isNotEmpty()) {
                RoundButton(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = buttonText,
                    onClick = onClick
                )
            }
        }
    }
}

@Composable
@Preview
private fun ErrorChipPreview() = ErrorChip(
    visible = true,
    text = "Network error",
    onClick = {},
    buttonVisible = true,
    buttonText = "Обновить"
)
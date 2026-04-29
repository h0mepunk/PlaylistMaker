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
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R

@Composable
fun ErrorChip (
    visible: Boolean,
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {
    if (visible) {
        Column(
            modifier = modifier
                .fillMaxWidth()
        )
        {
            Image(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                painter = painterResource(R.drawable.empty_results_error),
                contentDescription = null
            )
            Text(
                text = text,
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontFamily = FontFamily(
                        fonts = listOf(Font(R.font.ys_display_medium))
                    ),
                    fontSize = 19.sp,
                    fontWeight = FontWeight(400)
                ),
                modifier = Modifier
                    .padding()
                    .align(Alignment.CenterHorizontally)
            )
            RoundButton(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = "Refresh",
                onClick = onClick
            )
        }
    }
}

@Composable
@Preview
fun ErrorChipPreview() = ErrorChip(
    visible = true,
    text = "Network error",
    onClick = {}
)
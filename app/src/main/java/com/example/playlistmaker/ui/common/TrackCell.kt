package com.example.playlistmaker.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track

@Composable
fun TrackCell(
    track: Track,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 13.dp)
            .clickable { onClick() }
    ) {
        AsyncImage(
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
            model = track.artworkUrl100,
            contentDescription = null,
            placeholder = painterResource(R.drawable.placeholder),
            error = painterResource(R.drawable.placeholder),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        ) {
            Text(
                style = TextStyle(
                    color = colorResource(R.color.tabs_text_color),
                    fontFamily = FontFamily(
                        fonts = listOf(Font(R.font.ys_display_medium))
                    ),
                    fontSize = 16.sp,
                    fontWeight = FontWeight(400)
                ),
                textAlign = TextAlign.Start,
                modifier = Modifier.align(Alignment.Start).padding(top = 13.dp),
                text = track.trackName
            )
            Row() {
                Text(
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontFamily = FontFamily(
                            fonts = listOf(Font(R.font.ys_display_regular))
                        ),
                        fontSize = 11.sp,
                        fontWeight = FontWeight(400)
                    ),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = track.artistName
                )
                Image(
                    painter = painterResource(R.drawable.ellipse),
                    modifier = Modifier.align(Alignment.CenterVertically).padding(end = 10.dp, start = 10.dp),
                    contentDescription = null
                )
                Text(
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontFamily = FontFamily(
                            fonts = listOf(Font(R.font.ys_display_regular))
                        ),
                        fontSize = 11.sp,
                        fontWeight = FontWeight(400)
                    ),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = track.trackTime
                )
            }
        }
            Image(
                painter = painterResource(R.drawable.arrow_forward),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 12.dp),
                contentDescription = null
            )
    }
}

@Preview
@Composable
fun TrackCellPreview() = TrackCell(
    track = Track(
        trackName = "Track name",
        artistName = "Artist name",
        trackId = 1,
        releaseDate = null,
        country = "USA",
        primaryGenreName = "",
        trackTime = "00:30",
        collectionName  = "",
        previewUrl = "",
        artworkUrl100 = ""
    ), {}
)

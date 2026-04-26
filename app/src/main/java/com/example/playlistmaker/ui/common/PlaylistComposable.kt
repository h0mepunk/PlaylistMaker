package com.example.playlistmaker.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Playlist

@Composable
fun PlaylistCell(
    playlist: Playlist
) {
    val style = TextStyle(
        color = MaterialTheme.colorScheme.onTertiary, fontFamily = FontFamily(
            fonts = listOf(Font(R.font.ys_display_regular))
        ), fontSize = 12.sp, fontWeight = FontWeight(400)
    )
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            modifier = Modifier
                .height(160.dp)
                .width(160.dp),
            painter = painterResource(R.drawable.playlist_100_placeholder),
            contentDescription = null
        )
        Text(
            style = style,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .align(Alignment.Start),
            text = playlist.name
        )
        Text(
            style = style,
            textAlign = TextAlign.Start,
            modifier = Modifier.align(Alignment.Start),
            text = playlist.tracksCount.toString() + " треков"
        )
    }
}

@Preview
@Composable
fun PlaylistCellPreview() = PlaylistCell(
    playlist = Playlist(
        name = "Damir",
        description = "My first playlist",
        tracks = "",
        id = 1,
        imgUri = "????",
        tracksCount = 0,
        timestamp = System.currentTimeMillis(),
        timeTotal = 0L
    )
)

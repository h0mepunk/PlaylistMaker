package com.example.playlistmaker.ui.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.common.TopBar
import com.example.playlistmaker.ui.common.TrackCell

@Composable
fun SearchScaffold(
    items: List<Track>,
    onItemClick: () -> Unit = {}
) {
    Scaffold(
        topBar = { TopBar(text = stringResource(R.string.search_title)) }
    ) {
            paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(top = 16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                items(10) { index ->
                    TrackCell(track = items[index])
                }
            }


        }
    }
}

@Preview
@Composable
fun SettingsPreview() = SearchScaffold(
    items = listOf(
        Track(
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
        ),
        Track(
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
        ),
        Track(
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
        )
    ),

)
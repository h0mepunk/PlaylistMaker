package com.example.playlistmaker.ui.library

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.common.ErrorChip
import com.example.playlistmaker.ui.common.TrackCell
import com.example.playlistmaker.ui.library.playlist.PlaylistViewModel
import com.example.playlistmaker.ui.library.tracklist.TrackListViewModel
import kotlinx.coroutines.launch

@Composable
fun LibraryScreen(
    onTrackClick: (track: Track) -> Unit,
    onPlaylistClick: () -> Unit,
    tracksViewModel: TrackListViewModel,
    playlistViewModel: PlaylistViewModel
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 2 })
    val selectedTabIndex = remember { pagerState.currentPage }
    val tracksErrorVisible = tracksViewModel.errorVisible.collectAsState().value
    val playlistsErrorVisible = playlistViewModel.errorVisible.collectAsState().value
    val tracks = tracksViewModel.trackList.collectAsState().value
    val playlists = playlistViewModel.playlistsList.collectAsState().value


    Scaffold() { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.outline,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    },
                    text = { Text(text = stringResource(R.string.fav_tracks_tab_title)) },
                )

                Tab(
                    selected = selectedTabIndex == 1,
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.outline,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    },
                    text = { Text(text = stringResource(R.string.playlists_tab_title)) },
                )
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                when (page) {
                    0 -> TracksScreen(
                        tracks = tracks,
                        onTrackClick = onTrackClick,
                        errorVisible = tracksErrorVisible
                    )
                    1 -> PlaylistsScreen(
                        playlists =
                        onPlaylistClick = onPlaylistClick,
                        playlistsErrorVisible
                    )
                }
            }
        }
    }
}

@Composable
fun TracksScreen(
    tracks: List<Track>,
    onTrackClick: (track: Track) -> Unit,
    errorVisible: Boolean
) {
    Scaffold() { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top =paddingValues.calculateTopPadding())
        ) {
                ErrorChip(
                  visible = errorVisible,
                    text = stringResource(R.string.placeholder_fav_message),
                    buttonVisible = false,
                ) {

                }
            if (!errorVisible) {
                LazyColumn(
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    items(tracks.size) { index ->
                        TrackCell(
                            track = tracks[index],
                            onClick = { onTrackClick(tracks[index]) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PlaylistsScreen(
    onPlaylistClick: () -> Unit,
    errorVisible: Boolean
) {
    // Контент второго экрана
}

@Composable
@Preview
fun LibraryScreenTracksErrorPreview() {
    LibraryScreen({}, {}, )
}

@Composable
@Preview
fun LibraryScreenTracksRecyclerPreview() {
    LibraryScreen({}, {}, )
}

@Composable
@Preview
fun LibraryScreenPlaylistsErrorPreview() {
    LibraryScreen({}, {}, )
}

@Composable
@Preview
fun LibraryScreenPlaylistsRecyclerPreview() {
    LibraryScreen({}, {}, )
}
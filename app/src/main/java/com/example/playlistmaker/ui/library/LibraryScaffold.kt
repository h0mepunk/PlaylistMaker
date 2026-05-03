package com.example.playlistmaker.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.db.LibraryRepository
import com.example.playlistmaker.domain.db.PlaylistInteractor
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.common.ErrorChip
import com.example.playlistmaker.ui.common.PlaylistCell
import com.example.playlistmaker.ui.common.TopBar
import com.example.playlistmaker.ui.common.TrackCell
import com.example.playlistmaker.ui.library.playlist.PlaylistViewModel
import com.example.playlistmaker.ui.library.tracklist.TrackListViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch

@Composable
private fun NewPlaylistTopButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 24.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        Button(
            onClick = onClick,
            shape = RoundedCornerShape(dimensionResource(R.dimen.error_refresh_button_corner_radius)),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.refresh_button_color),
                contentColor = colorResource(R.color.button_text),
            ),
        ) {
            Text(
                text = stringResource(R.string.new_playlist_button),
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                    fontSize = 14.sp,
                    fontWeight = FontWeight(500),
                    color = colorResource(R.color.button_text),
                    textAlign = TextAlign.Center,
                ),
            )
        }
    }
}

@Composable
fun LibraryScreen(
    onTrackClick: (track: Track) -> Unit,
    onPlaylistClick: (playlist: Playlist) -> Unit,
    onCreatePlaylistClick: () -> Unit,
    tracksViewModel: TrackListViewModel,
    playlistViewModel: PlaylistViewModel
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 2 })
    val selectedTabIndex = pagerState.currentPage
    val tracksErrorVisible = tracksViewModel.errorVisible.collectAsState().value
    val playlistsErrorVisible = playlistViewModel.errorVisible.collectAsState().value
    val tracks = tracksViewModel.trackList.collectAsState().value
    val playlists = playlistViewModel.playlistsList.collectAsState().value

    val tabTextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.ys_display_medium)),
        fontSize = 14.sp,
        fontWeight = FontWeight(500),
        color = colorResource(R.color.tabs_text_color),
    )

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(R.color.search_background))
            ) {
                TopBar(text = stringResource(R.string.library_title))
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = colorResource(R.color.tabs_background_color),
                    contentColor = colorResource(R.color.tabs_text_color),
                    indicator = { tabPositions ->
                        if (selectedTabIndex < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                height = 2.dp,
                                color = colorResource(R.color.tabs_text_color),
                            )
                        }
                    },
                    divider = {},
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        selectedContentColor = colorResource(R.color.tabs_text_color),
                        unselectedContentColor = colorResource(R.color.tabs_text_color),
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(0)
                            }
                        },
                        text = {
                            Text(
                                text = stringResource(R.string.fav_tracks_tab_title),
                                style = tabTextStyle,
                            )
                        },
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        selectedContentColor = colorResource(R.color.tabs_text_color),
                        unselectedContentColor = colorResource(R.color.tabs_text_color),
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(1)
                            }
                        },
                        text = {
                            Text(
                                text = stringResource(R.string.playlists_tab_title),
                                style = tabTextStyle,
                            )
                        },
                    )
                }
            }
        },
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) { page ->
            when (page) {
                0 -> TracksScreen(
                    tracks = tracks,
                    onTrackClick = onTrackClick,
                    errorVisible = tracksErrorVisible
                )
                1 -> PlaylistsScreen(
                    playlists = playlists,
                    onPlaylistClick = onPlaylistClick,
                    onCreatePlaylistClick = onCreatePlaylistClick,
                    errorVisible = playlistsErrorVisible
                )
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(R.color.search_background))
        ) {
            ErrorChip(
                visible = errorVisible,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = dimensionResource(R.dimen.error_layout_lib_margin_top)),
                text = stringResource(R.string.placeholder_fav_message),
                imageTopPadding = 46.dp,
                buttonVisible = false,
                onClick = {}
            )
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

@Composable
fun PlaylistsScreen(
    playlists: List<Playlist>,
    onPlaylistClick: (Playlist) -> Unit,
    onCreatePlaylistClick: () -> Unit,
    errorVisible: Boolean
) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(R.color.search_background))
        ) {
            NewPlaylistTopButton(onClick = onCreatePlaylistClick)
            if (errorVisible) {
                ErrorChip(
                    visible = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 46.dp),
                    text = stringResource(R.string.placeholder_playlists_message),
                    buttonVisible = false,
                    imageTopPadding = 0.dp,
                    onClick = {}
                )
            }
            if (!errorVisible) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(328.dp)
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        items = playlists,
                        key = { it.id }
                    ) { playlist ->
                        PlaylistCell(
                            playlist = playlist,
                            onClick = { onPlaylistClick(playlist) }
                        )
                    }
                }
            }
        }
}

val track = Track(
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
val playlist = Playlist(
    name = "Damir",
    description = "My first playlist",
    tracks = "",
    id = 1,
    imgUri = "????",
    tracksCount = 0,
    timestamp = System.currentTimeMillis(),
    timeTotal = 0L
)

class LibraryRepositoryMock: LibraryRepository {

    override suspend fun addTrackToFavorites(track: Track) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTrackFromFavorites(track: Track) {
        TODO("Not yet implemented")
    }

    override fun getTracks(): Flow<List<Track>> {
        return {listOf(track)}.asFlow()
    }

    override fun getTrackById(trackId: Int): Flow<Track> {
        return {track}.asFlow()
    }
}

class PlaylistInteractorMock(): PlaylistInteractor {
    override fun getPlaylists(): Flow<List<Playlist>> {
        return {listOf(playlist)}.asFlow()
    }

    override suspend fun insertPlaylist(playlist: Playlist) {}

    override suspend fun updatePlaylist(playlist: Playlist) {}

    override fun getPlaylistById(playlistId: Int): Flow<Playlist> {
        return { playlist }.asFlow()
    }

    override suspend fun addTrackToPlaylist(playlistId: Int, track: Track) {}

    override fun getTracksFromPlaylist(playlistId: Int): Flow<List<Track>> {
        return {listOf(track)}.asFlow()
    }

    override suspend fun deleteTrackFromPlaylist(playlistId: Int, track: Track) {}

    override suspend fun deletePlaylist(playlistId: Int) {}

    override suspend fun insertTrack(track: Track) {}

    override fun getTrackById(id: Int): Flow<Track> {
        return {track}.asFlow()
    }
}

val trackListViewModelMock = TrackListViewModel(
    libraryRepository = LibraryRepositoryMock()
)

val playlistViewModelMock = PlaylistViewModel(
    playlistInteractor = PlaylistInteractorMock()
)


@Composable
@Preview
fun LibraryScreenTracksErrorPreview() {
    LibraryScreen(
        {},
        {},
        {},
        tracksViewModel = trackListViewModelMock,
        playlistViewModel = playlistViewModelMock
        )
}
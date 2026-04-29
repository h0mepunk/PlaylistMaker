package com.example.playlistmaker.ui.search

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.TracksHistoryInteractor
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.search.TracksSearchViewModel
import com.example.playlistmaker.ui.common.ErrorChip
import com.example.playlistmaker.ui.common.RoundButton
import com.example.playlistmaker.ui.common.TopBar
import com.example.playlistmaker.ui.common.TrackCell
import kotlinx.coroutines.flow.Flow

@Composable
fun SearchScaffold(
    viewModel: TracksSearchViewModel,
    items: List<Track>,
    onSearchTextChange: (String) -> Unit,
    onItemClick: (track: Track) -> Unit,
    onClearHistoryClick: () -> Unit,
    onErrorButtonClick: () -> Unit,
    onClearSearchClick: () -> Unit
) {
    val searchFieldStyle = TextStyle(
        color = colorResource(R.color.black),
        fontFamily = FontFamily(Font(R.font.ys_display_medium)),
        fontSize = 19.sp,
        fontWeight = FontWeight(400)
    )
    val hintColor = colorResource(R.color.grey)
    val fieldBackground = colorResource(R.color.search_text_edit_color)
    val interactionSource = remember { MutableInteractionSource() }

    Scaffold(
        topBar = { TopBar(text = stringResource(R.string.search_title)) }
    ) { paddingValues ->

        val text = viewModel.text.collectAsState().value
        val historyVisible = viewModel.historyTitleVisible.collectAsState()
        val errorVisible = viewModel.errorVisible.collectAsState()
        val keyboardController = LocalSoftwareKeyboardController.current

        Column(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            TextField(
                value = text,
                onValueChange = { newText ->
                    viewModel.onTextChanged(newText)
                },
                singleLine = true,
                maxLines = 1,
                interactionSource = interactionSource,
                shape = RoundedCornerShape(dimensionResource(R.dimen.search_text_edit_corner_radius)),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = colorResource(R.color.black),
                    unfocusedTextColor = colorResource(R.color.black),
                    focusedContainerColor = fieldBackground,
                    unfocusedContainerColor = fieldBackground,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    cursorColor = colorResource(R.color.blue),
                    focusedPlaceholderColor = hintColor,
                    unfocusedPlaceholderColor = hintColor
                ),
                textStyle = searchFieldStyle,
                placeholder = {
                    Text(
                        text = stringResource(R.string.input_hint),
                        style = searchFieldStyle.copy(color = hintColor)
                    )
                },
                leadingIcon = {
                    Image(
                        painter = painterResource(R.drawable.search_mini),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 14.dp, end = 8.dp)
                    )
                },
                trailingIcon = {
                    if (text.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                onSearchTextChange(text)
                                keyboardController?.hide()
                                      },
                            modifier = Modifier
                                .padding(end = 16.dp, top = 14.dp)
                        ) {
                            Icon(
                                modifier = Modifier.align(Alignment.End).clickable(true) {
                                    onClearSearchClick()
                                },
                                painter = painterResource(R.drawable.cross_icon),
                                contentDescription = null
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.text_input_height))
                    .padding(start = 16.dp, top = 8.dp, end = 16.dp)
            )
            Text(
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontFamily = FontFamily(
                        fonts = listOf(Font(R.font.ys_display_medium))
                    ),
                    fontSize = 19.sp,
                    fontWeight = FontWeight(400)
                ),
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .align(Alignment.CenterHorizontally),
                text = stringResource(R.string.search_history_title)
            )
            LazyColumn(
                modifier = Modifier.padding(top = 16.dp)
            ) {
                items(items.size) { index ->
                    TrackCell(
                        track = items[index],
                        onClick = { onItemClick(items[index]) }
                    )
                }
            }
            HistoryButton(
                historyVisible.value,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    keyboardController?.hide()
                    onClearHistoryClick()
                }
            )
            ErrorChip(
                errorVisible.value,
                modifier = Modifier.padding(top = 210.dp),
                text = stringResource(R.string.network_error_text),
                onClick = { onErrorButtonClick() }
            )
        }
    }
}

@Composable
fun HistoryButton(
    visible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier
) {
    if (visible) {
        RoundButton(
            modifier.padding(top = 16.dp),
            text = stringResource(R.string.clear_history_button),
            onClick = { onClick() }
        )
    }
}


class FakeTracksInteractor : TracksInteractor {

    override fun searchTracks(text: String): Flow<Pair<List<Track>?, String?>> {
        TODO("Not yet implemented")
    }
}

class FakeTrackHistoryInteractor : TracksHistoryInteractor {
    override fun getTracksHistory(): ArrayList<Track> {
        TODO("Not yet implemented")
    }

    override fun saveTracksHistory(tracks: ArrayList<Track>) {
        TODO("Not yet implemented")
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview
@Composable
fun SearchScaffoldPreview() = SearchScaffold(
    viewModel = TracksSearchViewModel(
        tracksInteractor = FakeTracksInteractor(),
        trackHistoryInteractor = FakeTrackHistoryInteractor()
    ),
    items = listOf(
        Track(
            trackName = "Track name",
            artistName = "Artist name",
            trackId = 1,
            releaseDate = null,
            country = "USA",
            primaryGenreName = "",
            trackTime = "00:30",
            collectionName = "",
            previewUrl = "",
            artworkUrl100 = ""
        )
    ),
    onSearchTextChange = {},
    onItemClick = {},
    onClearHistoryClick = {},
    onErrorButtonClick = {},
    {}
)

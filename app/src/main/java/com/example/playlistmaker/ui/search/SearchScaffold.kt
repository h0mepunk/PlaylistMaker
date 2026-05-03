package com.example.playlistmaker.ui.search

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
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
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun SearchScaffold(
    viewModel: TracksSearchViewModel,
    onSearchTextChange: (String) -> Unit,
    onItemClick: (track: Track) -> Unit,
    onClearHistoryClick: () -> Unit,
    onErrorButtonClick: () -> Unit
) {
    val searchFieldTextColor = colorResource(R.color.search_field_text)
    val searchFieldStyle = TextStyle(
        color = searchFieldTextColor,
        fontFamily = FontFamily(Font(R.font.ys_display_medium)),
        fontSize = 19.sp,
        fontWeight = FontWeight(400)
    )
    val hintColor = colorResource(R.color.search_field_hint)
    val fieldBackground = colorResource(R.color.search_text_edit_color)
    val interactionSource = remember { MutableInteractionSource() }
    val tracks = viewModel.trackList.collectAsState().value
    val text = viewModel.text.collectAsState().value
    val historyVisible = viewModel.historyTitleVisible.collectAsState().value
    val errorVisible = viewModel.errorVisible.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val recyclerVisible = viewModel.recyclerVisible.collectAsState().value
    val progressBarVisible = viewModel.progressBarVisible.collectAsState().value
    val clearHistoryVisible = viewModel.clearHistoryVisible.collectAsState().value
    val refreshButtonVisible = viewModel.refreshButtonVisible.collectAsState().value
    val clearIconVisibility = viewModel.clearIconVisibility.collectAsState().value
    val errorText = viewModel.errorText.collectAsState().value
    val errorIcon = viewModel.errorIcon.collectAsState().value
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        topBar = { TopBar(text = stringResource(R.string.search_title)) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            val corner = dimensionResource(R.dimen.search_text_edit_corner_radius)
            val fieldShape = RoundedCornerShape(corner)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 8.dp, end = 16.dp)
                    .height(dimensionResource(R.dimen.text_input_height))
                    .clip(fieldShape)
                    .background(fieldBackground),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.search_mini),
                    contentDescription = null,
                    modifier = Modifier.padding(start = 14.dp)
                )
                Spacer(Modifier.width(8.dp))
                BasicTextField(
                    value = text,
                    onValueChange = { newText ->
                        if (newText.length <= 100) onSearchTextChange(newText)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .focusRequester(focusRequester),
                    singleLine = true,
                    textStyle = searchFieldStyle,
                    cursorBrush = SolidColor(colorResource(R.color.blue)),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { keyboardController?.hide() }
                    ),
                    interactionSource = interactionSource,
                    decorationBox = { innerTextField ->
                        Box(
                            Modifier.fillMaxSize(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (text.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.input_hint),
                                    style = searchFieldStyle.copy(color = hintColor),
                                    maxLines = 1
                                )
                            }
                            innerTextField()
                        }
                    }
                )
                if (clearIconVisibility && text.isNotEmpty()) {
                    Image(
                        painter = painterResource(R.drawable.cross_icon),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(colorResource(R.color.search_icon_mini)),
                        modifier = Modifier
                            .padding(start = 4.dp, end = 12.dp)
                            .size(12.dp)
                            .clickable {
                                onSearchTextChange("")
                                viewModel.setText("")
                                viewModel.lastSearchText = ""
                                keyboardController?.hide()
                            }
                    )
                }
            }
            if (historyVisible) {
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
            }
            if (progressBarVisible) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = dimensionResource(R.dimen.searchPreloaderMarginTop)),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(dimensionResource(R.dimen.progressBarSize)),
                        color = colorResource(R.color.blue),
                        strokeWidth = 3.dp,
                    )
                }
            }
            if (recyclerVisible) {
                LazyColumn(
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    items(tracks.size) { index ->
                        TrackCell(
                            track = tracks[index],
                            onClick = { onItemClick(tracks[index]) }
                        )
                    }
                }
            }
            if (clearHistoryVisible) {
                HistoryButton(
                    historyVisible = historyVisible,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        keyboardController?.hide()
                        onClearHistoryClick()
                    }
                )
            }
            ErrorChip(
                buttonVisible = refreshButtonVisible,
                iconId = errorIcon,
                visible = errorVisible.value,
                modifier = Modifier.padding(top = 210.dp),
                text = errorText,
                buttonText = stringResource(R.string.refresh),
                onClick = { onErrorButtonClick() }
            )
        }
    }
}

@Composable
fun HistoryButton(
    historyVisible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier
) {
    if (historyVisible) {
        RoundButton(
            modifier.padding(top = 16.dp),
            text = stringResource(R.string.clear_history_button),
            onClick = { onClick() }
        )
    }
}

private class FakeTracksInteractor : TracksInteractor {
    override fun searchTracks(text: String): Flow<Pair<List<Track>?, String?>> = emptyFlow()
}

private class FakeTracksHistoryInteractor : TracksHistoryInteractor {
    override fun getTracksHistory(): ArrayList<Track> = ArrayList()

    override fun saveTracksHistory(tracks: ArrayList<Track>) = Unit
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview
@Composable
fun SearchScaffoldPreview() = SearchScaffold(
    viewModel = TracksSearchViewModel(
        tracksInteractor = FakeTracksInteractor(),
        trackHistoryInteractor = FakeTracksHistoryInteractor()
    ),
    onSearchTextChange = {},
    onItemClick = {},
    onClearHistoryClick = {},
    onErrorButtonClick = {},
)

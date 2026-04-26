package com.example.playlistmaker.ui.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.common.ErrorChip
import com.example.playlistmaker.ui.common.RoundButton
import com.example.playlistmaker.ui.common.TopBar
import com.example.playlistmaker.ui.common.TrackCell
import com.example.playlistmaker.ui.common.black
import com.example.playlistmaker.ui.common.grey_dark
import com.example.playlistmaker.ui.common.grey_medium

@Composable
fun SearchScaffold(
    items: List<Track>,
    searchText: String,
    onItemClick: () -> Unit,
    onClearHistoryClick: () -> Unit,
    onErrorButtonClick: () -> Unit
) {
    Scaffold(
        topBar = { TopBar(text = stringResource(R.string.search_title)) }
    ) {
            paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            TextField(
                placeholder = {
                    Text(
                        text = stringResource(R.string.input_hint),
                    style = TextStyle(
                        color = grey_dark,
                        fontFamily = FontFamily(
                            fonts = listOf(Font(R.font.ys_display_medium))
                        ),
                        fontSize = 19.sp,
                        fontWeight = FontWeight(400)
                    ))
                              },
                textStyle = TextStyle(
                    color = black,
                    fontFamily = FontFamily(
                        fonts = listOf(Font(R.font.ys_display_medium))
                    ),
                    fontSize = 19.sp,
                    fontWeight = FontWeight(400)
                ),
                value = TextFieldValue(searchText),
                leadingIcon = {
                    Icon(
                        modifier = Modifier.padding(start = 8.dp).align(Alignment.Start),
                        painter = painterResource(R.drawable.search),
                        contentDescription = null
                    ) },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp).height(36.dp),
                onValueChange = {
                //TODO: search text
                     },
                supportingText = {Text(text = stringResource(R.string.input_hint))},
            )
            Text(
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontFamily = FontFamily(
                        fonts = listOf(Font(R.font.ys_display_bold))
                    ),
                    fontSize = 22.sp,
                    fontWeight = FontWeight(500)
                ),
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                text = stringResource(R.string.search_history_title)
            )
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                items(items.size) { index ->
                    TrackCell(
                        track = items[index],
                        onItemClick
                    )
                }
            }
            RoundButton(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = stringResource(R.string.clear_history_button),
                onClick = { onClearHistoryClick() }
            )
            ErrorChip(
                modifier = Modifier.padding( top = 210.dp ),
                text = stringResource(R.string.network_error_text),
                onClick = { onErrorButtonClick() }
            )
        }
    }
}

//@Composable
//fun SimpleTextField(
//    modifier: Modifier,
//    textValue: String,
//
//) {
//    var text: String by remember { mutableStateOf("") }
//
//    Column {
//        TextField(
//            value = text,
//            onValueChange = { newText : String ->
//                text = newText
//            },
//            label = { Text("Введите текст") }
//        )
//        Text(text)
//    }
//}

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
        )
    ),
    searchText = "Sunny day",
    {}, {}, {})
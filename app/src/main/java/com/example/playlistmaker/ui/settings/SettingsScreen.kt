package com.example.playlistmaker.ui.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.example.playlistmaker.ui.common.TopBar

    val style = TextStyle(
        color = Color.DarkGray,
       fontFamily = FontFamily(
           fonts = listOf(Font(R.font.ys_display_medium))
       ),
        fontSize = 18.sp,
        fontWeight = FontWeight(400)
    )

    @Composable
    fun SettingsScaffold(
        items: List<SettingsFragment.SettingsItem>,
        onItemClick: (SettingsFragment.SettingsItem) -> Unit
    ) {
        Scaffold(
            topBar = { TopBar() }
        ) {
                paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                items.forEach { item ->
                    SettingCell(item.textId, item.iconId) { onItemClick(item) }
                }
            }
        }
    }

    @Composable
    fun SettingCell(
        textId: Int,
        iconId: Int?,
        onClick: () -> Unit
    ) {
        //val context = LocalContext.current
        Row {
            Text(
                style = style,
                text = stringResource(textId)
            )
            if (iconId == null) {
                Switch(
                    checked = false,
                    onCheckedChange = { onClick() }
                )
            } else {
                Image(
                    modifier = Modifier.clickable(true, onClick = { onClick() }),
                    painter = painterResource(iconId),
                    contentDescription = null
                )
            }
        }
    }
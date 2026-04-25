package com.example.playlistmaker.ui.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.ui.common.TopBar
import com.example.playlistmaker.ui.common.blue
import com.example.playlistmaker.ui.common.grey_dark
import com.example.playlistmaker.ui.common.grey_medium


@Composable
    fun SettingsScaffold(
        items: List<SettingsItem>,
        onItemClick: (SettingsItem) -> Unit,
        switchChecked: Boolean = false
    ) {
        Scaffold(
            topBar = {
                TopBar(
                    text = stringResource(R.string.settings_title)
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(top = 16.dp)
            ) {
                items.forEach { item ->
                    SettingCell(item.textId, item.iconId,  switchChecked) { onItemClick(item) }
                }
            }
        }
    }

    @Composable
    fun SettingCell(
        textId: Int,
        iconId: Int?,
        switchChecked: Boolean? = null,
        onClick: () -> Unit
    ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(true, onClick = { onClick() })
            ) {
                Text(
                    modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 20.dp),
                    style = TextStyle(
                        color = colorResource(R.color.tabs_text_color),
                        fontFamily = FontFamily(
                            fonts = listOf(Font(R.font.ys_display_medium))
                        ),
                        fontSize = 18.sp,
                        fontWeight = FontWeight(400)
                    ),
                    text = stringResource(textId)
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterVertically)
                        .padding(end = 16.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    if (iconId == null) {
                        Switch(
                            checked = switchChecked == true,
                            onCheckedChange = { onClick() },
                            modifier = Modifier.scale(0.75f),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = blue,
                                uncheckedThumbColor = grey_dark,
                                checkedTrackColor = Color(0xFF9FBBF3),
                                uncheckedTrackColor = grey_medium
                            )
                        )
                    } else {
                        Image(
                            modifier = Modifier
                                .clickable(true, onClick = { onClick() }),
                            painter = painterResource(iconId),
                            contentDescription = null
                        )
                    }
                }
            }
    }

@Preview
@Composable
fun SettingsPreview() = SettingsScaffold(
    items = listOf(SettingsItem.THEME, SettingsItem.SUPPORT, SettingsItem.SHARE, SettingsItem.USER_AGREEMENT),
    onItemClick = { item ->
        when(item) {
            SettingsItem.SHARE -> {}
            SettingsItem.SUPPORT -> {}
            SettingsItem.USER_AGREEMENT -> {}
            SettingsItem.THEME -> {}
        }
    }

)

enum class SettingsItem(val textId: Int, val iconId: Int?) {
    THEME(R.string.dark_theme_menu, null),
    SUPPORT(R.string.support_menu, R.drawable.support),
    SHARE(R.string.share_menu, R.drawable.share),
    USER_AGREEMENT(R.string.user_agreement_menu, R.drawable.arrow_forward),
}
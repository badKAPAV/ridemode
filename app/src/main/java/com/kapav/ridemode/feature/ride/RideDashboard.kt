package com.kapav.ridemode.feature.ride

import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.kapav.ridemode.R
import com.kapav.ridemode.core.ui.components.RideActionButton
import com.kapav.ridemode.core.ui.components.RideSurface
import com.kapav.ridemode.core.ui.theme.RideUiTokens

private data class PreviewPage(@StringRes val title: Int)

private val previewPages = listOf(
    PreviewPage(R.string.ride_page_music),
    PreviewPage(R.string.ride_page_favorites),
    PreviewPage(R.string.ride_page_clock)
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RideDashboard(onEndRide: () -> Unit) {
    val pager = rememberPagerState(pageCount = { previewPages.size })
    Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(RideUiTokens.screenPadding),
            verticalArrangement = Arrangement.spacedBy(RideUiTokens.sectionGap)
        ) {
            Text(stringResource(R.string.ride_preview), style = MaterialTheme.typography.headlineSmall)
            HorizontalPager(
                state = pager,
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) { index ->
                RideSurface(modifier = Modifier.fillMaxSize()) {
                    Column(verticalArrangement = Arrangement.spacedBy(RideUiTokens.contentGap)) {
                        Text(stringResource(previewPages[index].title), style = MaterialTheme.typography.headlineLarge)
                        Text(stringResource(R.string.ride_preview_body), style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(RideUiTokens.contentGap),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RideActionButton(label = stringResource(R.string.end_ride), onClick = onEndRide)
                Text(
                    stringResource(
                        R.string.ride_page_status,
                        pager.currentPage + 1,
                        previewPages.size,
                        stringResource(previewPages[pager.currentPage].title)
                    ),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium
                )
                RidePlannedAction(label = stringResource(R.string.ride_maps_planned))
                RidePlannedAction(label = stringResource(R.string.ride_assistant_planned))
            }
        }
    }
}

@Composable
private fun RidePlannedAction(label: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.heightIn(min = RideUiTokens.primaryTouchTarget)
    ) {
        Box(
            modifier = Modifier.padding(RideUiTokens.surfacePadding),
            contentAlignment = Alignment.Center
        ) {
            Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

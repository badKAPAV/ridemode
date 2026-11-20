package com.kapav.ridemode.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Fill
import com.adamglin.phosphoricons.fill.BookOpen
import com.adamglin.phosphoricons.fill.Motorcycle
import com.adamglin.phosphoricons.fill.ShieldCheck
import com.adamglin.phosphoricons.fill.SlidersHorizontal
import com.adamglin.phosphoricons.fill.SquaresFour
import com.kapav.ridemode.R
import com.kapav.ridemode.core.ui.components.RideInfoCard
import com.kapav.ridemode.core.ui.components.RideScreen
import com.kapav.ridemode.core.ui.components.RideActionButton
import com.kapav.ridemode.core.ui.components.RideCardTone
import com.kapav.ridemode.core.ui.theme.RideUiTokens

@Composable
fun HomeScreen(
    onStartRide: () -> Unit,
    onEditPages: () -> Unit,
    onSettings: () -> Unit,
    onOnboarding: () -> Unit,
    onPermissions: () -> Unit
) {
    RideScreen(title = stringResource(R.string.app_name), centeredTitle = true,
        titleContent = {
            val primary = MaterialTheme.colorScheme.primary
            Text(
                buildAnnotatedString {
                    append("RIDE")
                    withStyle(SpanStyle(color = primary)) { append("MODE") }
                },
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1
            )
        }) {
        Surface(color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(RideUiTokens.panelCorner),
            modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(RideUiTokens.nonRideHorizontalPadding),
                verticalArrangement = Arrangement.spacedBy(RideUiTokens.nonRideSectionGap)) {

                Text(stringResource(R.string.home_hero_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer)
                Text(stringResource(R.string.home_hero_body),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer)
                RideActionButton(
                    label = stringResource(R.string.start_ride),
                    onClick = onStartRide,
                    modifier = Modifier.fillMaxWidth(),
                    icon = PhosphorIcons.Fill.Motorcycle,
                    isHomeScreenButton = true
                )
            }
        }
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val textScale = LocalDensity.current.fontScale.coerceAtLeast(1f)
            val columns = if (maxWidth >= RideUiTokens.homeTileMinWidth * (2 * textScale) +
                RideUiTokens.nonRideSectionGap) 2 else 1
            val destinations = listOf(
                HomeDestination("Dash Setup", "Arrange widgets and pages on your dashboard",
                    PhosphorIcons.Fill.SquaresFour, RideCardTone.PRIMARY, onEditPages),
                HomeDestination(stringResource(R.string.settings_title), stringResource(R.string.home_settings_body),
                    PhosphorIcons.Fill.SlidersHorizontal, RideCardTone.SECONDARY, onSettings),
                HomeDestination(stringResource(R.string.onboarding_title), stringResource(R.string.home_onboarding_body),
                    PhosphorIcons.Fill.BookOpen, RideCardTone.TERTIARY, onOnboarding),
                HomeDestination(stringResource(R.string.permissions_title), stringResource(R.string.home_permissions_body),
                    PhosphorIcons.Fill.ShieldCheck, RideCardTone.PRIMARY, onPermissions)
            )
            Column(verticalArrangement = Arrangement.spacedBy(RideUiTokens.nonRideSectionGap)) {
                destinations.chunked(columns).forEach { row ->
                    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                        horizontalArrangement = Arrangement.spacedBy(RideUiTokens.nonRideSectionGap)) {
                        row.forEach { destination ->
                            RideInfoCard(title = destination.title, body = destination.body,
                                icon = destination.icon, tone = destination.tone, onAction = destination.onClick,
                                modifier = Modifier.weight(1f).fillMaxHeight())
                        }
                    }
                }
            }
        }
    }
}

private data class HomeDestination(val title: String, val body: String, val icon: ImageVector,
    val tone: RideCardTone, val onClick: () -> Unit)

package com.kapav.ridemode.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
        // Text("Make it yours", style = MaterialTheme.typography.titleLarge)
        RideInfoCard(
            title = "Your pages",
            body = "Arrange widgets and pages for your ride",
            icon = PhosphorIcons.Fill.SquaresFour,
            // actionLabel = "Edit pages",
            onAction = onEditPages,
            tone = RideCardTone.PRIMARY
        )
        RideInfoCard(
            title = stringResource(R.string.settings_title),
            body = stringResource(R.string.home_settings_body),
            icon = PhosphorIcons.Fill.SlidersHorizontal,
            // actionLabel = stringResource(R.string.open_settings),
            onAction = onSettings,
            tone = RideCardTone.SECONDARY
        )
        RideInfoCard(
            title = stringResource(R.string.onboarding_title),
            body = stringResource(R.string.home_onboarding_body),
            icon = PhosphorIcons.Fill.BookOpen,
            // actionLabel = stringResource(R.string.open_guide),
            onAction = onOnboarding,
            tone = RideCardTone.TERTIARY
        )
        RideInfoCard(
            title = stringResource(R.string.permissions_title),
            body = stringResource(R.string.home_permissions_body),
            icon = PhosphorIcons.Fill.ShieldCheck,
            // actionLabel = stringResource(R.string.open_permissions),
            onAction = onPermissions,
            tone = RideCardTone.PRIMARY
        )
    }
}

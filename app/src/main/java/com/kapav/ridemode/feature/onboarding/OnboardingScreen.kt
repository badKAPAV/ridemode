package com.kapav.ridemode.feature.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Bold
import com.adamglin.phosphoricons.bold.Compass
import com.adamglin.phosphoricons.bold.Motorcycle
import com.adamglin.phosphoricons.bold.ShieldCheck
import com.kapav.ridemode.R
import com.kapav.ridemode.core.ui.components.RideActionButton
import com.kapav.ridemode.core.ui.components.RideInfoCard
import com.kapav.ridemode.core.ui.components.RideScreen

@Composable
fun OnboardingScreen(onBack: () -> Unit) {
    RideScreen(title = stringResource(R.string.onboarding_title), onBack = onBack) {
        RideInfoCard(
            title = stringResource(R.string.onboarding_prepare_title),
            body = stringResource(R.string.onboarding_prepare_body),
            icon = PhosphorIcons.Bold.Compass
        )
        RideInfoCard(
            title = stringResource(R.string.onboarding_ride_title),
            body = stringResource(R.string.onboarding_ride_body),
            icon = PhosphorIcons.Bold.Motorcycle
        )
        RideInfoCard(
            title = stringResource(R.string.onboarding_safety_title),
            body = stringResource(R.string.onboarding_safety_body),
            icon = PhosphorIcons.Bold.ShieldCheck
        )
        RideActionButton(label = stringResource(R.string.return_home), onClick = onBack)
    }
}

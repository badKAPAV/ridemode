package com.kapav.ridemode.core.ui.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.res.stringResource
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Fill
import com.adamglin.phosphoricons.fill.ArrowLeft
import com.kapav.ridemode.R
import com.kapav.ridemode.core.ui.theme.RideUiTokens

@Composable
fun RideActionButton(
    modifier: Modifier = Modifier,
    label: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    isHomeScreenButton: Boolean = false,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.heightIn(min = RideUiTokens.primaryTouchTarget),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isHomeScreenButton)
                MaterialTheme.colorScheme.surface
            else
                MaterialTheme.colorScheme.primary,

            contentColor = if (isHomeScreenButton)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier.size(RideUiTokens.iconSize)
                )
                Spacer(Modifier.width(RideUiTokens.contentGap))
            }
            Text(label, style = if (isHomeScreenButton)  MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun RideSurface(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    ElevatedCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(RideUiTokens.surfacePadding),
            content = content
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RideScreen(
    title: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    subtitle: String? = null,
    centeredTitle: Boolean = false,
    titleContent: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val appBarTitle: @Composable () -> Unit = {
        if (titleContent != null) titleContent()
        else Text(title, style = MaterialTheme.typography.titleLarge,
            maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
    val navigationIcon: @Composable () -> Unit = {
        if (onBack != null) {
            IconButton(onClick = onBack,
                modifier = Modifier.padding(start = RideUiTokens.nonRideBackStartInset)
                    .size(RideUiTokens.minimumTouchTarget)) {
                Surface(modifier = Modifier.size(RideUiTokens.nonRideBackVisual),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondaryContainer) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(PhosphorIcons.Fill.ArrowLeft,
                            contentDescription = stringResource(R.string.back),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(RideUiTokens.nonRideBackIcon))
                    }
                }
            }
        }
    }
    Scaffold(modifier = modifier, containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            val colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.onBackground)
            if (centeredTitle) {
                CenterAlignedTopAppBar(
                    title = appBarTitle,
                    navigationIcon = navigationIcon,
                    colors = colors)
            } else {
                TopAppBar(
                    title = appBarTitle,
                    navigationIcon = navigationIcon,
                    colors = colors)
            }
        }) { insets ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = RideUiTokens.nonRideHorizontalPadding,
                    vertical = RideUiTokens.nonRideVerticalPadding),
            verticalArrangement = Arrangement.spacedBy(RideUiTokens.nonRideSectionGap)
        ) {
            if (subtitle != null) Text(subtitle, style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            content()
        }
    }
}

enum class RideCardTone { PRIMARY, SECONDARY, TERTIARY }

@Composable
fun RideInfoCard(
    title: String,
    body: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    tone: RideCardTone = RideCardTone.SECONDARY
) {
    val accent = when (tone) {
        RideCardTone.PRIMARY -> MaterialTheme.colorScheme.primaryContainer
        RideCardTone.SECONDARY -> MaterialTheme.colorScheme.secondaryContainer
        RideCardTone.TERTIARY -> MaterialTheme.colorScheme.tertiaryContainer
    }
    val onAccent = when (tone) {
        RideCardTone.PRIMARY -> MaterialTheme.colorScheme.onPrimaryContainer
        RideCardTone.SECONDARY -> MaterialTheme.colorScheme.onSecondaryContainer
        RideCardTone.TERTIARY -> MaterialTheme.colorScheme.onTertiaryContainer
    }
    Surface(
        modifier = modifier.fillMaxWidth(),
        onClick = onAction ?: {},
        enabled = onAction != null,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = RoundedCornerShape(RideUiTokens.panelCorner)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(RideUiTokens.nonRideCardPadding),
            verticalArrangement = Arrangement.spacedBy(RideUiTokens.nonRideCardGap),
            horizontalAlignment = Alignment.Start
        ) {
            Surface(
                color = accent,
                shape = RoundedCornerShape(RideUiTokens.widgetCardCorner)
            ) {
                Box(
                    modifier = Modifier.size(RideUiTokens.nonRideIconContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = onAccent,
                        modifier = Modifier.size(RideUiTokens.iconSize)
                    )
                }
            }

            Text(
                title,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (actionLabel != null && onAction != null) {
                Text(
                    actionLabel,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RideLoadingIndicator(label: String, modifier: Modifier = Modifier) {
    LoadingIndicator(
        modifier = modifier
            .heightIn(min = RideUiTokens.minimumTouchTarget)
            .semantics { contentDescription = label }
    )
}

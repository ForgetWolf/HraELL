package org.matrix.vector.manager.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.DpOffset
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.matrix.vector.manager.R
import org.matrix.vector.manager.data.github.GitHubRepository
import org.matrix.vector.manager.ui.components.FrameworkState
import org.matrix.vector.manager.ui.components.statusWordRes
import org.matrix.vector.manager.ui.theme.VectorLocaleController
import org.matrix.vector.ui.locale.LanguageSheet
import org.matrix.vector.ui.SharedAlertDialog
import org.matrix.vector.ui.R as UiR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenStatus: () -> Unit,
    onOpenUrl: (String) -> Unit,
    onOpenReport: () -> Unit,
    onOpenUpdate: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory),
) {
    val status by viewModel.status.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val sections = buildSections(status, viewModel.device, context)
    val scroll = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var menuOpen by remember { mutableStateOf(false) }
    var appearanceOpen by remember { mutableStateOf(false) }
    var languageOpen by remember { mutableStateOf(false) }
    var confirmSoftReboot by remember { mutableStateOf(false) }
    val canSoftReboot = status.state == FrameworkState.Active ||
        status.state == FrameworkState.Degraded

    Scaffold(
        modifier = Modifier.nestedScroll(scroll.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                scrollBehavior = scroll,
                actions = {
                    IconButton(onClick = { menuOpen = true }) {
                        Icon(Icons.Rounded.MoreVert, stringResource(R.string.classic_menu))
                    }
                    DropdownMenu(
                        expanded = menuOpen,
                        onDismissRequest = { menuOpen = false },
                        modifier = Modifier.widthIn(min = 192.dp, max = 280.dp),
                        offset = DpOffset(x = (-16).dp, y = 0.dp),
                        shape = MaterialTheme.shapes.large,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.appearance_title)) },
                            trailingIcon = { Icon(Icons.Rounded.Palette, contentDescription = null) },
                            onClick = { menuOpen = false; appearanceOpen = true },
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.language_title)) },
                            trailingIcon = { Icon(Icons.Rounded.Translate, contentDescription = null) },
                            onClick = { menuOpen = false; languageOpen = true },
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.classic_check_update)) },
                            trailingIcon = { Icon(Icons.Rounded.SystemUpdate, contentDescription = null) },
                            onClick = { menuOpen = false; onOpenUpdate() },
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.home_open_issue)) },
                            trailingIcon = { Icon(Icons.Rounded.BugReport, contentDescription = null) },
                            onClick = { menuOpen = false; onOpenReport() },
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.action_soft_reboot)) },
                            trailingIcon = { Icon(Icons.Rounded.RestartAlt, contentDescription = null) },
                            enabled = canSoftReboot,
                            onClick = { menuOpen = false; confirmSoftReboot = true },
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.classic_project)) },
                            trailingIcon = { Icon(Icons.AutoMirrored.Rounded.OpenInNew, contentDescription = null) },
                            onClick = { menuOpen = false; onOpenUrl(GitHubRepository.REPO_URL) },
                        )
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                val needsAttention = status.state == FrameworkState.Degraded ||
                    status.state == FrameworkState.Mismatched
                val colors = MaterialTheme.colorScheme
                Card(
                    onClick = onOpenStatus,
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (needsAttention) colors.errorContainer else colors.primary,
                        contentColor = if (needsAttention) colors.onErrorContainer else colors.onPrimary,
                    ),
                ) {
                    Row(
                        modifier = Modifier.padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_hraell),
                            contentDescription = null,
                            modifier = Modifier.size(36.dp),
                        )
                        Spacer(Modifier.width(24.dp))
                        Column {
                            Text(
                                stringResource(status.state.statusWordRes()),
                                style = MaterialTheme.typography.titleMedium,
                            )
                            status.versionLabel?.let {
                                Spacer(Modifier.height(4.dp))
                                Text(it, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                ) {
                    Column(Modifier.padding(16.dp)) {
                        sections.flatMap { it.second }.forEach { row -> InfoRow(row) }
                        TextButton(onClick = onOpenStatus, modifier = Modifier.align(Alignment.End)) {
                            Text(stringResource(R.string.status_open_details))
                        }
                    }
                }
            }
        }
    }

    if (confirmSoftReboot) {
        SharedAlertDialog(
            onDismissRequest = { confirmSoftReboot = false },
            icon = { Icon(Icons.Rounded.RestartAlt, contentDescription = null) },
            title = { Text(stringResource(R.string.action_soft_reboot)) },
            text = { Text(stringResource(R.string.action_soft_reboot_confirm)) },
            confirmButton = {
                TextButton(
                    enabled = canSoftReboot,
                    onClick = {
                        confirmSoftReboot = false
                        viewModel.softReboot()
                    },
                ) {
                    Text(
                        stringResource(R.string.action_soft_reboot),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmSoftReboot = false }) {
                    Text(stringResource(UiR.string.store_cancel))
                }
            },
        )
    }

    if (appearanceOpen) HomeAppearanceSheet(onDismiss = { appearanceOpen = false })
    if (languageOpen) {
        LanguageSheet(
            controller = VectorLocaleController,
            onDismiss = { languageOpen = false },
            showTranslatorCredits = false,
        )
    }
}

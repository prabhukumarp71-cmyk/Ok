package com.example

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.DeviceProfile
import com.example.model.Preset
import com.example.ui.MainViewModel
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

enum class NavTab(val label: String, val activeIcon: ImageVector, val inactiveIcon: ImageVector) {
    HOME("Home", Icons.Default.Home, Icons.Outlined.Home),
    BATTERY_PLUS("Battery Plus", Icons.Default.Bolt, Icons.Outlined.Bolt),
    CUSTOMIZE("Customize", Icons.Default.Tune, Icons.Outlined.Tune),
    SETTINGS("Settings", Icons.Default.Settings, Icons.Outlined.Settings)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                StatusBarStudioApp()
            }
        }
    }
}

@Composable
fun StatusBarStudioApp(
    viewModel: MainViewModel = viewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Check overlay permission whenever activity returns to foreground
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.checkOverlayPermission()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    var currentTab by remember { mutableStateOf(NavTab.HOME) }
    val snackbarHostState = remember { SnackbarHostState() }

    val activeConfig by viewModel.activeConfig.collectAsStateWithLifecycle()
    val presets by viewModel.presets.collectAsStateWithLifecycle()
    val simulation by viewModel.simulation.collectAsStateWithLifecycle()
    val isOverlayGranted by viewModel.isOverlayGranted.collectAsStateWithLifecycle()
    val isOverlayActive by viewModel.isOverlayActive.collectAsStateWithLifecycle()
    val statusMessage by viewModel.statusMessage.collectAsStateWithLifecycle()

    // First run onboarding
    val prefs = remember { context.getSharedPreferences("statusbar_studio_prefs", Context.MODE_PRIVATE) }
    var showOnboarding by remember {
        mutableStateOf(!prefs.getBoolean("has_completed_onboarding", false))
    }

    // React to status messages with snackbar
    LaunchedEffect(statusMessage) {
        statusMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearStatusMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_nav_bar")
            ) {
                NavTab.values().forEach { tab ->
                    val selected = currentTab == tab
                    NavigationBarItem(
                        selected = selected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = if (selected) tab.activeIcon else tab.inactiveIcon,
                                contentDescription = tab.label
                            )
                        },
                        label = { Text(tab.label) },
                        modifier = Modifier.testTag("nav_item_${tab.name.lowercase()}")
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "tab_transition"
            ) { targetTab ->
                when (targetTab) {
                    NavTab.HOME -> {
                        HomeScreen(
                            config = activeConfig,
                            simulation = simulation,
                            presets = presets,
                            isOverlayGranted = isOverlayGranted,
                            isOverlayActive = isOverlayActive,
                            onSimulationChange = { viewModel.updateSimulation(it) },
                            onConfigChange = { viewModel.updateConfig(it) },
                            onSelectPreset = { viewModel.selectPreset(it) },
                            onSelectProfile = { viewModel.selectDeviceProfile(it) },
                            onToggleOverlay = { viewModel.toggleOverlay(context) },
                            onNavigateToCustomize = { currentTab = NavTab.CUSTOMIZE },
                            onNavigateToPresets = { currentTab = NavTab.BATTERY_PLUS }
                        )
                    }
                    NavTab.BATTERY_PLUS -> {
                        BatteryPlusScreen(
                            config = activeConfig,
                            simulation = simulation,
                            onConfigChange = { viewModel.updateConfig(it) },
                            onSimulationChange = { viewModel.updateSimulation(it) }
                        )
                    }
                    NavTab.CUSTOMIZE -> {
                        CustomizeScreen(
                            config = activeConfig,
                            simulation = simulation,
                            onConfigChange = { viewModel.updateConfig(it) },
                            onSimulationChange = { viewModel.updateSimulation(it) }
                        )
                    }
                    NavTab.SETTINGS -> {
                        SettingsScreen(
                            isOverlayGranted = isOverlayGranted,
                            isOverlayActive = isOverlayActive,
                            onToggleOverlay = { viewModel.toggleOverlay(context) },
                            onCheckPermissions = { viewModel.checkOverlayPermission() },
                            onResetDefaults = { viewModel.resetToDefaults() },
                            onExportJson = { viewModel.exportConfiguration() },
                            onImportJson = { json -> viewModel.importConfiguration(json) }
                        )
                    }
                }
            }

            if (showOnboarding) {
                OnboardingDialog(
                    onDismiss = {
                        prefs.edit().putBoolean("has_completed_onboarding", true).apply()
                        showOnboarding = false
                    }
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}


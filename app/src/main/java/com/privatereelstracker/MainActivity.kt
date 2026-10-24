package com.privatereelstracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.privatereelstracker.ui.analytics.AnalyticsScreen
import com.privatereelstracker.ui.analytics.AnalyticsViewModel
import com.privatereelstracker.ui.dashboard.DashboardScreen
import com.privatereelstracker.ui.dashboard.DashboardViewModel
import com.privatereelstracker.ui.diagnostics.DiagnosticScreen
import com.privatereelstracker.ui.diagnostics.DiagnosticViewModel
import com.privatereelstracker.ui.settings.SettingsScreen
import com.privatereelstracker.ui.settings.SettingsViewModel
import com.privatereelstracker.ui.theme.SecuredReelTrackerTheme
import com.privatereelstracker.util.PermissionHelper

class MainActivity : ComponentActivity() {

    private val dashboardViewModel: DashboardViewModel by viewModels()
    private val analyticsViewModel: AnalyticsViewModel by viewModels()
    private val diagnosticViewModel: DiagnosticViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SecuredReelTrackerTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "dashboard") {
                    composable("dashboard") {
                        DashboardScreen(
                            viewModel = dashboardViewModel,
                            onNavigateAnalytics = { navController.navigate("analytics") },
                            onNavigateDiagnostics = { navController.navigate("diagnostics") },
                            onNavigateSettings = { navController.navigate("settings") },
                            onRequestAccessibilityPermission = {
                                PermissionHelper.openAccessibilitySettings(this@MainActivity)
                            },
                            onRequestUsageAccessPermission = {
                                PermissionHelper.openUsageAccessSettings(this@MainActivity)
                            }
                        )
                    }

                    composable("analytics") {
                        AnalyticsScreen(
                            viewModel = analyticsViewModel,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable("diagnostics") {
                        DiagnosticScreen(
                            viewModel = diagnosticViewModel,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable("settings") {
                        SettingsScreen(
                            viewModel = settingsViewModel,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        dashboardViewModel.refreshDashboard()
    }
}

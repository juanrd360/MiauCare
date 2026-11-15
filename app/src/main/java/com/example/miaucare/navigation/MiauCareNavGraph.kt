package com.example.miaucare.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.miaucare.data.MiauCareRepository
import com.example.miaucare.data.MiauRole
import com.example.miaucare.ui.components.McFloatingBottomBar
import com.example.miaucare.ui.components.McFloatingToastHost
import com.example.miaucare.ui.screens.*
import kotlinx.coroutines.delay

@Composable
fun MiauCareApp(
    startDestination: String = "splash"
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: startDestination

    // Auto-clear floating toast after 3 seconds
    val currentToast = MiauCareRepository.toastMessage
    LaunchedEffect(currentToast) {
        if (currentToast != null) {
            delay(3000)
            MiauCareRepository.toastMessage = null
        }
    }

    val navigateToRoleHome: (MiauRole) -> Unit = { role ->
        MiauCareRepository.currentRole = role
        val target = when (role) {
            MiauRole.VOLUNTARIO -> "vol_home"
            MiauRole.VETERINARIO -> "vet_dashboard"
            MiauRole.ADMINISTRADOR -> "admin_home"
        }
        navController.navigate(target) {
            popUpTo("login") { inclusive = true }
            launchSingleTop = true
        }
    }

    val bottomBarRoutes = setOf(
        "vol_home", "vol_calendar", "vol_census", "vol_profile",
        "vet_dashboard", "vet_directory", "vet_agenda", "vet_profile",
        "admin_home", "admin_census", "admin_users", "admin_broadcast"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.fillMaxSize()
        ) {
            // =====================================================
            // ONBOARDING & AUTHENTICATION
            // =====================================================
            composable("splash") {
                SplashScreen(
                    onFinished = {
                        navController.navigate("login") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                )
            }

            composable("login") {
                LoginScreen(
                    onBack = {
                        if (navController.previousBackStackEntry != null) {
                            navController.popBackStack()
                        }
                    },
                    onLoginSuccess = navigateToRoleHome,
                    onForgotPasswordClick = { navController.navigate("forgot_password") },
                    onRegisterVoluntarioClick = { navController.navigate("register_voluntario") },
                    onRegisterVeterinarioClick = { navController.navigate("vet_solicitud") }
                )
            }

            composable("register_voluntario") {
                RegisterVoluntarioScreen(
                    onBack = { navController.popBackStack() },
                    onRegistered = { navigateToRoleHome(MiauRole.VOLUNTARIO) },
                    onLoginClick = { navController.navigate("login") }
                )
            }

            composable("forgot_password") {
                ForgotPasswordScreen(
                    onBack = { navController.popBackStack() },
                    onResetComplete = { navController.popBackStack() }
                )
            }

            // =====================================================
            // ROLE 1: VOLUNTARIO
            // =====================================================
            composable("vol_home") {
                MiauCareRepository.currentRole = MiauRole.VOLUNTARIO
                VoluntarioHomeScreen(
                    onOpenSightingHistory = { navController.navigate("vol_sighting_history") },
                    onOpenNotifications = { navController.navigate("vol_notifications") },
                    onReportUnregisteredCat = { navController.navigate("vol_report_unregistered") },
                    onOpenRoundForm = { catId -> navController.navigate("vol_round_form/$catId") },
                    onOpenCatDetail = { catId -> navController.navigate("vol_cat_detail/$catId") },
                    onRoleSwitch = navigateToRoleHome
                )
            }

            composable(
                route = "vol_round_form/{catId}",
                arguments = listOf(navArgument("catId") { type = NavType.StringType })
            ) { entry ->
                val catId = entry.arguments?.getString("catId") ?: "cat-samantha"
                RoundFormScreen(
                    catId = catId,
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }

            composable("vol_calendar") {
                MiauCareRepository.currentRole = MiauRole.VOLUNTARIO
                VoluntarioCalendarScreen(
                    onOpenNotifications = { navController.navigate("vol_notifications") }
                )
            }

            composable("vol_census") {
                MiauCareRepository.currentRole = MiauRole.VOLUNTARIO
                VoluntarioCensusScreen(
                    onOpenSightingHistory = { navController.navigate("vol_sighting_history") },
                    onReportUnregisteredCat = { navController.navigate("vol_report_unregistered") },
                    onOpenCatDetail = { catId -> navController.navigate("vol_cat_detail/$catId") }
                )
            }

            composable(
                route = "vol_cat_detail/{catId}",
                arguments = listOf(navArgument("catId") { type = NavType.StringType })
            ) { entry ->
                val catId = entry.arguments?.getString("catId") ?: "cat-samantha"
                CatDetailScreen(
                    catId = catId,
                    onBack = { navController.popBackStack() },
                    onReportSightingClick = { id -> navController.navigate("vol_round_form/$id") }
                )
            }

            composable("vol_report_unregistered") {
                ReportUnregisteredCatScreen(
                    onBack = { navController.popBackStack() },
                    onSubmitted = { navController.popBackStack() }
                )
            }

            composable("vol_sighting_history") {
                SightingHistoryScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable("vol_notifications") {
                VoluntarioNotificationsScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable("vol_profile") {
                MiauCareRepository.currentRole = MiauRole.VOLUNTARIO
                VoluntarioProfileScreen(
                    onOpenNotifications = { navController.navigate("vol_notifications") },
                    onLogout = {
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onRoleSwitch = navigateToRoleHome
                )
            }

            // =====================================================
            // ROLE 2: VETERINARIO
            // =====================================================
            composable("vet_solicitud") {
                VetSolicitudScreen(
                    onBack = { navController.popBackStack() },
                    onSubmitted = { navigateToRoleHome(MiauRole.VETERINARIO) },
                    onLoginClick = { navController.navigate("login") }
                )
            }

            composable("vet_dashboard") {
                MiauCareRepository.currentRole = MiauRole.VETERINARIO
                VetDashboardScreen(
                    onOpenClinicalRecord = { catId -> navController.navigate("vet_clinical_record/$catId") },
                    onOpenMedicalAlerts = { navController.navigate("vet_medical_alerts") }
                )
            }

            composable(
                route = "vet_clinical_record/{catId}",
                arguments = listOf(navArgument("catId") { type = NavType.StringType })
            ) { entry ->
                val catId = entry.arguments?.getString("catId") ?: "cat-manchas"
                VetClinicalRecordScreen(
                    catId = catId,
                    onBack = { navController.popBackStack() },
                    onOpenDirectory = { navController.navigate("vet_directory") }
                )
            }

            composable("vet_medical_alerts") {
                VetMedicalAlertsScreen(
                    onBack = { navController.popBackStack() },
                    onOpenClinicalRecord = { catId -> navController.navigate("vet_clinical_record/$catId") }
                )
            }

            composable("vet_directory") {
                MiauCareRepository.currentRole = MiauRole.VETERINARIO
                VetDirectoryScreen(
                    onOpenClinicalRecord = { catId -> navController.navigate("vet_clinical_record/$catId") }
                )
            }

            composable("vet_agenda") {
                MiauCareRepository.currentRole = MiauRole.VETERINARIO
                VetAgendaScreen(
                    onOpenClinicalRecord = { catId -> navController.navigate("vet_clinical_record/$catId") }
                )
            }

            composable("vet_profile") {
                MiauCareRepository.currentRole = MiauRole.VETERINARIO
                VetProfileScreen(
                    onLogout = {
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            // =====================================================
            // ROLE 3: ADMINISTRADOR
            // =====================================================
            composable("admin_home") {
                MiauCareRepository.currentRole = MiauRole.ADMINISTRADOR
                AdminHomeScreen(
                    onNavigate = { route -> navController.navigate(route) }
                )
            }

            composable("admin_census") {
                MiauCareRepository.currentRole = MiauRole.ADMINISTRADOR
                AdminCensusScreen(
                    onOpenEditCat = { catId -> navController.navigate("admin_edit_cat/$catId") }
                )
            }

            composable(
                route = "admin_edit_cat/{catId}",
                arguments = listOf(navArgument("catId") { type = NavType.StringType })
            ) { entry ->
                val catId = entry.arguments?.getString("catId") ?: "cat-manchas"
                AdminEditCatScreen(
                    catId = catId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("admin_approve_records") {
                AdminApproveRecordsScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable("admin_users") {
                MiauCareRepository.currentRole = MiauRole.ADMINISTRADOR
                AdminUsersScreen()
            }

            composable("admin_broadcast") {
                MiauCareRepository.currentRole = MiauRole.ADMINISTRADOR
                AdminBroadcastScreen()
            }

            composable("admin_zone_indicators") {
                AdminZoneIndicatorsScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable("admin_audit") {
                AdminAuditScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable("admin_schedule_round") {
                AdminScheduleRoundScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable("admin_colonies") {
                AdminColoniesScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable("admin_catalogs") {
                AdminCatalogsScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable("admin_medical_directory") {
                AdminMedicalDirectoryScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable("admin_system_params") {
                AdminSystemParamsScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable("admin_profile") {
                AdminProfileScreen(
                    onBack = { navController.popBackStack() },
                    onLogout = {
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }

        // Floating Bottom Navigation Bar for the 3 roles
        if (currentRoute in bottomBarRoutes) {
            Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                McFloatingBottomBar(
                    role = MiauCareRepository.currentRole,
                    currentRoute = currentRoute,
                    onNavigate = { targetRoute ->
                        if (targetRoute != currentRoute) {
                            navController.navigate(targetRoute) {
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
        }

        // Floating Toast Banner (normal & error states)
        McFloatingToastHost()
    }
}

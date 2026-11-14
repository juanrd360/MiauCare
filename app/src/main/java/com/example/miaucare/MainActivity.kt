package com.example.miaucare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.miaucare.navigation.MiauCareApp
import com.example.miaucare.ui.screens.AdminHomeScreen
import com.example.miaucare.ui.screens.BienvenidaScreen
import com.example.miaucare.ui.screens.VetDashboardScreen
import com.example.miaucare.ui.screens.VoluntarioHomeScreen
import com.example.miaucare.ui.theme.MiaucareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MiaucareTheme {
                MiauCareApp()
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844, name = "1. Bienvenida")
@Composable
fun PreviewBienvenida() {
    MiaucareTheme {
        BienvenidaScreen(
            onLoginClick = {},
            onRegisterVoluntarioClick = {},
            onRegisterVeterinarioClick = {},
            onQuickRoleJump = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844, name = "2. Voluntario · Inicio")
@Composable
fun PreviewVoluntarioHome() {
    MiaucareTheme {
        VoluntarioHomeScreen(
            onOpenSightingHistory = {},
            onOpenNotifications = {},
            onReportUnregisteredCat = {},
            onOpenRoundForm = {},
            onOpenCatDetail = {},
            onRoleSwitch = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844, name = "3. Veterinario · Dashboard")
@Composable
fun PreviewVetDashboard() {
    MiaucareTheme {
        VetDashboardScreen(
            onOpenClinicalRecord = {},
            onOpenMedicalAlerts = {},
            onRoleSwitch = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844, name = "4. Administrador · Inicio")
@Composable
fun PreviewAdminHome() {
    MiaucareTheme {
        AdminHomeScreen(
            onNavigate = {},
            onRoleSwitch = {}
        )
    }
}

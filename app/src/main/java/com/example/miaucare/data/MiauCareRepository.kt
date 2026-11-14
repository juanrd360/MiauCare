package com.example.miaucare.data

import androidx.annotation.DrawableRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.miaucare.R

enum class MiauRole(val label: String) {
    VOLUNTARIO("Voluntario"),
    VETERINARIO("Veterinario"),
    ADMINISTRADOR("Administrador")
}

data class CatProfile(
    val id: String,
    var code: String,
    var name: String,
    var breed: String,
    var sex: String,
    var age: String,
    var weightKg: String,
    var sterilizationStatus: String,
    var colony: String,
    var statusTag: String, // "En calle", "No vista hoy", "Hospitalizado", "CES hoy"
    var lifeStatus: String = "Activo", // "Activo", "Fallecido"
    @DrawableRes val imageRes: Int,
    val allergyAlert: String? = null,
    val fivWarningTitle: String? = null,
    val fivWarningDesc: String? = null,
    val secondaryConditionTitle: String? = null,
    val secondaryConditionDesc: String? = null
)

data class SightingRecord(
    val id: String,
    val catName: String,
    val dateGroup: String,
    val dateTime: String,
    val healthStatus: String, // "Sano", "Herido", "Enfermo"
    val ateFood: Boolean,
    val reportedBy: String,
    @DrawableRes val imageRes: Int
)

data class RoundScheduleItem(
    val id: String,
    val timeRange: String,
    val roundType: String, // "General", "Veterinaria"
    val zoneTitle: String,
    val description: String,
    val initials: List<String>,
    val peopleCountText: String,
    var isJoined: Boolean
)

data class MedicalAlertItem(
    val id: String,
    val catId: String,
    val catName: String,
    val timeAgo: String,
    val urgencyBadge: String, // "Crítica", "Nueva", "Moderada"
    val symptomTitle: String,
    val locationAndReporter: String,
    var tabStatus: String, // "Nuevas", "En atención", "Cerradas"
    @DrawableRes val imageRes: Int
)

data class DoctorItem(
    val id: String,
    val name: String,
    val specialty: String,
    val distanceText: String,
    val patientsLoadText: String,
    val isInternal: Boolean,
    @DrawableRes val imageRes: Int
)

data class PendingCatRecord(
    val id: String,
    val nameAndCode: String,
    val breedAgeSex: String,
    val zone: String,
    val submittedByAndTime: String,
    var isReviewed: Boolean = false,
    @DrawableRes val imageRes: Int
)

data class ColonyItem(
    val id: String,
    var name: String,
    var zoneName: String,
    var address: String,
    var feedingTime: String,
    val catsCountText: String
)

data class TeamUserItem(
    val id: String,
    val name: String,
    val roleLabel: String, // "Veterinaria", "Veterinario", "Voluntaria", "Voluntario"
    var isActive: Boolean
)

data class AuditLogItem(
    val id: String,
    val dateGroup: String,
    val title: String,
    val subtitle: String,
    val iconType: String // "settings", "add", "delete", "user"
)

object MiauCareRepository {
    var currentRole by mutableStateOf(MiauRole.VOLUNTARIO)
    var prototypeSimulateError by mutableStateOf(false)
    var toastMessage by mutableStateOf<Pair<String, Boolean>?>(null) // Pair(Title, isError)

    // System parameters (`Administrador · Parámetros del sistema`)
    var daysWithoutSightingX by mutableStateOf("7")
    var consecutiveCriticalN by mutableStateOf("3")
    var maxResponseHoursT by mutableStateOf("24")

    val cats = mutableStateListOf(
        CatProfile(
            id = "cat-benito",
            code = "MC-031",
            name = "Benito",
            breed = "Mestizo",
            sex = "Macho",
            age = "3 años",
            weightKg = "4.0",
            sterilizationStatus = "Esterilizado",
            colony = "Colonia Norte - Parque Norte",
            statusTag = "En calle",
            imageRes = R.drawable.img_cat_benito
        ),
        CatProfile(
            id = "cat-luna",
            code = "MC-019",
            name = "Luna",
            breed = "Mestizo",
            sex = "Hembra",
            age = "2 años",
            weightKg = "3.4",
            sterilizationStatus = "Esterilizado",
            colony = "Colonia Centro - Parque Norte",
            statusTag = "No vista hoy",
            imageRes = R.drawable.img_cat_luna
        ),
        CatProfile(
            id = "cat-gary",
            code = "MC-001",
            name = "Gary",
            breed = "Mestizo",
            sex = "Macho",
            age = "3 años",
            weightKg = "4.1",
            sterilizationStatus = "Esterilizado",
            colony = "Colonia Norte",
            statusTag = "En calle",
            imageRes = R.drawable.img_cat_gary
        ),
        CatProfile(
            id = "cat-peach",
            code = "MC-002",
            name = "Peach",
            breed = "Mestizo",
            sex = "Hembra",
            age = "2,5 años",
            weightKg = "3.6",
            sterilizationStatus = "Esterilizado",
            colony = "Colonia Centro",
            statusTag = "En calle",
            imageRes = R.drawable.img_cat_peach
        ),
        CatProfile(
            id = "cat-whitney",
            code = "MC-003",
            name = "Whitney",
            breed = "British Longhair",
            sex = "Hembra",
            age = "2 meses",
            weightKg = "1.2",
            sterilizationStatus = "Pendiente CES",
            colony = "Colonia Sur",
            statusTag = "En calle",
            imageRes = R.drawable.img_cat_whitney
        ),
        CatProfile(
            id = "cat-buggy",
            code = "MC-004",
            name = "Buggy",
            breed = "Mestizo",
            sex = "Macho",
            age = "4 meses",
            weightKg = "1.8",
            sterilizationStatus = "Pendiente CES",
            colony = "Colonia Norte",
            statusTag = "En calle",
            imageRes = R.drawable.img_cat_buggy
        ),
        CatProfile(
            id = "cat-samantha",
            code = "MC-010",
            name = "Samantha",
            breed = "Persa",
            sex = "Macho",
            age = "3 años",
            weightKg = "4.2",
            sterilizationStatus = "Esterilizado",
            colony = "Colonia Norte - Parque de los Cerezos",
            statusTag = "En Calle",
            imageRes = R.drawable.img_cat_samantha,
            fivWarningTitle = "FIV+ (Inmunodeficiencia Felina)",
            fivWarningDesc = "Diagnosticado el 12/Oct/2026. Requiere monitoreo de peso.",
            secondaryConditionTitle = "Ceguera Parcial - Ojo Izquierdo",
            secondaryConditionDesc = "Condición estable. No requiere medicación."
        ),
        CatProfile(
            id = "cat-manchas",
            code = "MC-024",
            name = "Manchas",
            breed = "Mestizo",
            sex = "Macho",
            age = "3 años",
            weightKg = "3.8",
            sterilizationStatus = "Pendiente CES",
            colony = "Colonia Norte",
            statusTag = "Hospitalizado",
            imageRes = R.drawable.img_cat_manchas,
            allergyAlert = "Alerta: reacción a penicilina"
        )
    )

    val sightingHistory = mutableStateListOf(
        SightingRecord(
            id = "sig-1",
            catName = "Benito",
            dateGroup = "Hoy · 13 nov 2026",
            dateTime = "13 nov 2026 · 10:42",
            healthStatus = "Sano",
            ateFood = true,
            reportedBy = "Reportado por Pepe Pérez",
            imageRes = R.drawable.img_cat_benito
        ),
        SightingRecord(
            id = "sig-2",
            catName = "Gato no censado",
            dateGroup = "Hoy · 13 nov 2026",
            dateTime = "13 nov 2026 · 09:15",
            healthStatus = "Herido",
            ateFood = false,
            reportedBy = "Reportado por Ana López",
            imageRes = R.drawable.img_cat_samantha
        ),
        SightingRecord(
            id = "sig-3",
            catName = "Luna",
            dateGroup = "Ayer · 12 nov 2026",
            dateTime = "12 nov 2026 · 18:30",
            healthStatus = "Enfermo",
            ateFood = true,
            reportedBy = "Reportado por Marta Ruiz",
            imageRes = R.drawable.img_cat_luna
        )
    )

    val rounds = mutableStateListOf(
        RoundScheduleItem(
            id = "rnd-1",
            timeRange = "10:00 AM - 12:00 PM",
            roundType = "General",
            zoneTitle = "Zona Norte - Parque de los Cerezos",
            description = "Revisión general y alimentación",
            initials = listOf("SA", "LG", "MC"),
            peopleCountText = "3 personas",
            isJoined = false
        ),
        RoundScheduleItem(
            id = "rnd-2",
            timeRange = "4:00 PM - 5:30 PM",
            roundType = "Veterinaria",
            zoneTitle = "Zona Norte - Jardines del Río",
            description = "Seguimiento veterinario y cuidados",
            initials = listOf("DG", "SA"),
            peopleCountText = "2 personas",
            isJoined = true
        )
    )

    val medicalAlerts = mutableStateListOf(
        MedicalAlertItem(
            id = "al-1",
            catId = "cat-luna",
            catName = "Luna",
            timeAgo = "Hace 8 min",
            urgencyBadge = "Crítica",
            symptomTitle = "Dificultad respiratoria",
            locationAndReporter = "Colonia Centro · Reportó Ana",
            tabStatus = "Nuevas",
            imageRes = R.drawable.img_cat_luna
        ),
        MedicalAlertItem(
            id = "al-2",
            catId = "cat-benito",
            catName = "Benito",
            timeAgo = "Hace 25 min",
            urgencyBadge = "Nueva",
            symptomTitle = "Valoración prequirúrgica CES",
            locationAndReporter = "Parque Norte · Reportó Pepe",
            tabStatus = "Nuevas",
            imageRes = R.drawable.img_cat_benito
        ),
        MedicalAlertItem(
            id = "al-3",
            catId = "cat-manchas",
            catName = "Manchas",
            timeAgo = "Hace 1 h",
            urgencyBadge = "Crítica",
            symptomTitle = "Deshidratación moderada",
            locationAndReporter = "Colonia Norte · Dra. Sofía García",
            tabStatus = "En atención",
            imageRes = R.drawable.img_cat_manchas
        )
    )

    val doctors = mutableStateListOf(
        DoctorItem(
            id = "doc-1",
            name = "Dra. Sofía García",
            specialty = "Cirugía CES · Medicina felina",
            distanceText = "Disponible · 0.8 km",
            patientsLoadText = "8 pacientes a cargo",
            isInternal = true,
            imageRes = R.drawable.img_doc_sofia
        ),
        DoctorItem(
            id = "doc-2",
            name = "Dr. Luis Pérez",
            specialty = "Medicina general",
            distanceText = "Disponible · 1.5 km",
            patientsLoadText = "2 pacientes a cargo",
            isInternal = true,
            imageRes = R.drawable.img_doc_luis
        ),
        DoctorItem(
            id = "doc-3",
            name = "Dr. Mateo Ruiz",
            specialty = "Cirugía de tejidos blandos",
            distanceText = "Disponible · 1.2 km",
            patientsLoadText = "0 pacientes · Colaborador externo",
            isInternal = false,
            imageRes = R.drawable.img_doc_mateo
        ),
        DoctorItem(
            id = "doc-4",
            name = "Dra. Irene Torres",
            specialty = "Medicina interna felina",
            distanceText = "Disponible · 2.4 km",
            patientsLoadText = "0 pacientes · Colaborador externo",
            isInternal = false,
            imageRes = R.drawable.img_doc_irene
        )
    )

    val pendingApprovals = mutableStateListOf(
        PendingCatRecord(
            id = "pend-1",
            nameAndCode = "Cake · MC-011",
            breedAgeSex = "Mestizo · 2 meses · Hembra",
            zone = "Zona Centro",
            submittedByAndTime = "Dra. Sofía García · Hoy, 09:10",
            imageRes = R.drawable.img_cat_cake
        ),
        PendingCatRecord(
            id = "pend-2",
            nameAndCode = "Kiwi · MC-012",
            breedAgeSex = "Mestizo · 1 año · Macho",
            zone = "Colonia Norte",
            submittedByAndTime = "Dr. Luis Pérez · Hoy, 08:45",
            imageRes = R.drawable.img_cat_kiwi
        ),
        PendingCatRecord(
            id = "pend-3",
            nameAndCode = "Willie · MC-013",
            breedAgeSex = "Mestizo · 1,5 años · Macho",
            zone = "Zona Sur",
            submittedByAndTime = "Dra. Elena Ruíz · Ayer, 17:30",
            imageRes = R.drawable.img_cat_willie
        )
    )

    val colonies = mutableStateListOf(
        ColonyItem(
            id = "col-1",
            name = "Parque Norte",
            zoneName = "Colonia Norte",
            address = "C. del Olivo, 12",
            feedingTime = "09:00",
            catsCountText = "4 gatos"
        ),
        ColonyItem(
            id = "col-2",
            name = "Plaza Mayor",
            zoneName = "Colonia Centro",
            address = "Plaza Mayor, 3",
            feedingTime = "10:00",
            catsCountText = "4 gatos"
        ),
        ColonyItem(
            id = "col-3",
            name = "Jardín del Sur",
            zoneName = "Colonia Sur",
            address = "C. del Sol, 8",
            feedingTime = "18:00",
            catsCountText = "2 gatos"
        )
    )

    val teamUsers = mutableStateListOf(
        TeamUserItem("u-1", "Dra. Sofía García", "Veterinaria", true),
        TeamUserItem("u-2", "Dr. Luis Pérez", "Veterinario", true),
        TeamUserItem("u-3", "Sofía Álvarez", "Voluntaria", true),
        TeamUserItem("u-4", "Mateo Ríos", "Voluntario", false)
    )

    val auditLogs = mutableStateListOf(
        AuditLogItem(
            id = "aud-1",
            dateGroup = "Hoy · 13 nov 2026",
            title = "Admin actualizó los parámetros del sistema",
            subtitle = "Admin · 13 nov · 09:42",
            iconType = "settings"
        ),
        AuditLogItem(
            id = "aud-2",
            dateGroup = "Hoy · 13 nov 2026",
            title = "Admin reincorporó a Manchas al censo",
            subtitle = "Admin · 13 nov · 08:15",
            iconType = "add"
        ),
        AuditLogItem(
            id = "aud-3",
            dateGroup = "Ayer · 12 nov 2026",
            title = "Ana eliminó a Manchas del censo",
            subtitle = "Ana · 12 nov · 18:10",
            iconType = "delete"
        ),
        AuditLogItem(
            id = "aud-4",
            dateGroup = "Ayer · 12 nov 2026",
            title = "Admin activó a Sofía Álvarez",
            subtitle = "Admin · 12 nov · 17:25",
            iconType = "user"
        )
    )

    fun getCatById(catId: String): CatProfile {
        return cats.find { it.id == catId || it.code == catId } ?: cats.first()
    }

    fun showToast(title: String, isError: Boolean = false) {
        toastMessage = title to isError
    }
}

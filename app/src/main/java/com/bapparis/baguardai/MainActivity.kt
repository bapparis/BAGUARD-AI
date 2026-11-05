package com.bapparis.baguardai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bapparis.baguardai.ui.theme.BAGUARDAITheme
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.animation.core.LinearEasing
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.activity.compose.BackHandler
import com.google.gson.Gson
import com.bapparis.baguardai.data.local.InvestigationDatabase
import com.bapparis.baguardai.data.local.InvestigationEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.foundation.clickable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable

import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

// ============================================================
// BAGUARD AI — Initial UI Prototype
// Dark Spatial UI • Glass-inspired surfaces • BAPPARIS DEV
// ============================================================

private val Midnight = Color(0xFF0B0B14)
private val DeepSpace = Color(0xFF121426)
private val CardSurface = Color(0xFF1A1C32)
private val ElectricViolet = Color(0xFF8B5CF6)
private val AuroraBlue = Color(0xFF38BDF8)
private val SoftWhite = Color(0xFFF5F5FF)
private val MutedText = Color(0xFF9CA3B8)
private val BorderColor = Color(0xFF30334D)
private val SuccessGreen = Color(0xFF34D399)

// ===== BAGUARD AI DASHBOARD PALETTE =====

private val DashboardBackground = Color(0xFF0B0720)
private val DashboardSurface = Color(0xFF1C1534)
private val DashboardViolet = Color(0xFF7C3AED)
private val DashboardMagenta = Color(0xFFC026D3)
private val DashboardAccent = Color(0xFFA78BFA)

private val DashboardText = Color(0xFFF5F2FF)
private val DashboardSecondary = Color(0xFFC9BDF2)

private val DashboardBorder = Color(0xFFA78BFA).copy(alpha = 0.20f)
private val DashboardGlass = Color(0xFF241B40).copy(alpha = 0.72f)

enum class AppDestination {
    HOME,
    INVESTIGATE,
    SCANNING,
    RESULTS,
    HISTORY,
    PROFILE,
    SETTINGS,
    SAFETY_CENTER,
    PRIVACY,
    REPORT_BUG,
    ABOUT,
    SIGNUP,
    LOGIN
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BAGUARDAITheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Midnight
                ) {
                    BAGUARDApp()
                }
            }
        }
    }
}
@Composable
fun BAGUARDApp() {
    var selectedDestination by remember { mutableStateOf(AppDestination.HOME) }
    var drawerOpen by remember { mutableStateOf(false) }
    var investigationInput by remember { mutableStateOf("") }
    var investigationRequested by remember { mutableStateOf(false) }
    var investigationReport by remember {
        mutableStateOf<com.bapparis.baguardai.data.model.InvestigationReport?>(null)
    }
    var investigationUsage by remember {
        mutableStateOf<com.bapparis.baguardai.data.model.InvestigationUsage?>(null)
    }
    var investigationError by remember { mutableStateOf<String?>(null) }
    val context = androidx.compose.ui.platform.LocalContext.current
    var isScanning by remember { mutableStateOf(false) }

    // VOICE RECOGNITION RESULT
    val speechRecognitionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == android.app.Activity.RESULT_OK) {

                val spokenText = result.data
                    ?.getStringArrayListExtra(
                        android.speech.RecognizerIntent.EXTRA_RESULTS
                    )
                    ?.firstOrNull()
                    .orEmpty()

                if (spokenText.isNotBlank()) {
                    investigationInput = spokenText
                    investigationRequested = false
                    investigationReport = null
                    investigationError = null

                    selectedDestination = AppDestination.INVESTIGATE
                    drawerOpen = false
                }
            }
        }

    // START SPEECH RECOGNITION
    fun launchSpeechRecognition() {

        val intent = android.content.Intent(
            android.speech.RecognizerIntent.ACTION_RECOGNIZE_SPEECH
        ).apply {

            putExtra(
                android.speech.RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                android.speech.RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )

            putExtra(
                android.speech.RecognizerIntent.EXTRA_LANGUAGE,
                java.util.Locale.getDefault()
            )

            putExtra(
                android.speech.RecognizerIntent.EXTRA_PROMPT,
                "Speak a suspicious message or link to investigate"
            )
        }

        speechRecognitionLauncher.launch(intent)
    }

// MICROPHONE PERMISSION
    val microphonePermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {
                launchSpeechRecognition()
            }
        }

// VOICE ASSISTANT ACTION
    val startVoiceAssistant: () -> Unit = {

        if (
            context.checkSelfPermission(
                android.Manifest.permission.RECORD_AUDIO
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {

            launchSpeechRecognition()

        } else {

            microphonePermissionLauncher.launch(
                android.Manifest.permission.RECORD_AUDIO
            )
        }
    }

    val coroutineScope = rememberCoroutineScope()

    val investigationDao = remember(context) {
        InvestigationDatabase
            .getDatabase(context)
            .investigationDao()
    }

    val investigationHistory by investigationDao
        .getAllInvestigations()
        .collectAsState(initial = emptyList())

// CENTRALIZED BACK NAVIGATION
    BackHandler(
        enabled = drawerOpen ||
                (selectedDestination != AppDestination.HOME &&
                        selectedDestination != AppDestination.SCANNING)
    ) {
        if (drawerOpen) {
            drawerOpen = false
        } else {
            when (selectedDestination) {
                AppDestination.INVESTIGATE -> {
                    selectedDestination = AppDestination.HOME
                    isScanning = false
                }

                AppDestination.RESULTS -> {
                    selectedDestination = AppDestination.INVESTIGATE
                    isScanning = false
                }

                else -> {
                    selectedDestination = AppDestination.HOME
                    isScanning = false
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        com.bapparis.baguardai.data.remote.BaguardApiClient.getUsage(context) { result ->
            result.onSuccess { investigationUsage = it }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DashboardBackground)
    ) {
        Scaffold(
            containerColor = DashboardBackground,
            bottomBar = {
                if (
                    selectedDestination != AppDestination.SETTINGS &&
                    selectedDestination != AppDestination.INVESTIGATE &&
                    selectedDestination != AppDestination.RESULTS &&
                    selectedDestination != AppDestination.PROFILE &&
                    selectedDestination != AppDestination.SCANNING
                ) {
                    BottomNavigation(
                        selected = selectedDestination,
                        onSelect = { destination ->
                            selectedDestination = destination
                            investigationRequested = false
                            investigationReport = null
                            drawerOpen = false
                        }
                    )
                }
            }
        ) { innerPadding ->
            when (selectedDestination) {
                AppDestination.HOME -> HomeScreen(
                    modifier = Modifier.padding(innerPadding),
                    onOpenDrawer = { drawerOpen = true },
                    onInvestigate = {
                        selectedDestination = AppDestination.INVESTIGATE
                        investigationRequested = false
                        investigationReport = null
                        isScanning = false
                    },
                    onHistory = {
                        selectedDestination = AppDestination.HISTORY
                    },
                    onOpenProfile = {
                        selectedDestination = AppDestination.PROFILE
                        drawerOpen = false
                    },
                    onVoiceAssistant = startVoiceAssistant

                )

                AppDestination.SCANNING -> {
                    InvestigationLoadingPage(
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                AppDestination.SIGNUP -> {
                    SignupScreen(
                        onBack = {
                            selectedDestination = AppDestination.PROFILE
                        },
                        onLogin = {
                            selectedDestination = AppDestination.LOGIN
                        },
                        onPrivacyPolicy = {
                            selectedDestination = AppDestination.PRIVACY
                        }
                    )
                }

                AppDestination.LOGIN -> {
                    SignupScreen(
                        onBack = {
                            selectedDestination = AppDestination.SIGNUP
                        },
                        onLogin = {
                            selectedDestination = AppDestination.LOGIN
                        },
                        onPrivacyPolicy = {
                            selectedDestination = AppDestination.PRIVACY
                        }
                    )
                }

                AppDestination.INVESTIGATE -> InvestigationScreen(
                    modifier = Modifier.padding(innerPadding),
                    initialText = investigationInput,
                    investigationRequested = investigationRequested,
                    investigationReport = investigationReport,
                    errorMessage = investigationError,
                    usage = investigationUsage,
                    onTextChange = {
                        investigationInput = it
                        investigationRequested = false
                        investigationReport = null
                    },
                    onBack = {
                        selectedDestination = AppDestination.HOME
                        isScanning = false
                    },
                    onInvestigate = {
                        if (investigationInput.isNotBlank() && !isScanning) {
                            investigationError = null
                            investigationRequested = false
                            isScanning = true
                            selectedDestination = AppDestination.SCANNING

                            com.bapparis.baguardai.data.remote.BaguardApiClient.investigate(
                                context = context,
                                content = investigationInput
                            ) { result ->
                                isScanning = false

                                result.onSuccess { apiResult ->

                                    investigationReport = apiResult.report
                                    investigationUsage = apiResult.usage
                                    investigationError = null

                                    coroutineScope.launch(Dispatchers.IO) {
                                        try {
                                            val reportJson = Gson().toJson(apiResult.report)

                                            investigationDao.insertInvestigation(
                                                InvestigationEntity(
                                                    originalContent = investigationInput,
                                                    riskLevel = apiResult.report.riskLevel,
                                                    summary = apiResult.report.summary,
                                                    reportJson = reportJson
                                                )
                                            )

                                            android.util.Log.d(
                                                "BAGUARD_HISTORY",
                                                "Investigation saved successfully"
                                            )

                                        } catch (error: Exception) {
                                            android.util.Log.e(
                                                "BAGUARD_HISTORY",
                                                "Failed to save investigation",
                                                error
                                            )
                                        }
                                    }

                                    selectedDestination = AppDestination.RESULTS
                                }
                                    .onFailure { error ->
                                    android.util.Log.e(
                                        "BAGUARD_INVESTIGATION",
                                        "Investigation failed",
                                        error
                                    )
                                        selectedDestination = AppDestination.INVESTIGATE


                                    investigationError = when {
    error is java.net.SocketTimeoutException ->
        "The investigation is taking longer than expected. Please try again."

    error is com.bapparis.baguardai.data.remote.ApiException ->
        when (error.statusCode) {
            409 -> "An investigation is already in progress."
            429 -> "You've reached today's investigation limit."
            502 -> "The investigation service is temporarily unavailable."
            else -> "Something went wrong while investigating this content. Please try again."
        }

    else ->
        "Something went wrong while investigating this content. Please try again."
}
                                }
                            }
                        }
                    }
                )

                AppDestination.SCANNING -> {
                    InvestigationLoadingPage(
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                AppDestination.RESULTS -> {
                    val report = investigationReport

                    if (report != null) {
                        InvestigationResultsScreen(
                            report = report,
                            usage = investigationUsage,
                            onBack = {
                                selectedDestination = AppDestination.INVESTIGATE
                                isScanning = false
                            },
                            onInvestigateAnother = {
                                investigationInput = ""
                                investigationReport = null
                                investigationRequested = false
                                isScanning = false
                                selectedDestination = AppDestination.INVESTIGATE
                            }
                        )
                    } else {
                        PlaceholderScreen(
                            symbol = "◉",
                            title = "No investigation report",
                            description = "Start an investigation to see your results."
                        )
                    }
                }
                AppDestination.HISTORY -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    AppTopBar(
                        onOpenDrawer = { drawerOpen = true },
                        title = "Your activity"
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp)
                    ) {
                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "Investigation History",
                            color = SoftWhite,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Review your previous security investigations.",
                            color = MutedText,
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(22.dp))

                        if (investigationHistory.isEmpty()) {

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "◷",
                                        color = ElectricViolet,
                                        fontSize = 48.sp
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(
                                        text = "No investigations yet",
                                        color = SoftWhite,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "Your completed investigations will appear here.",
                                        color = MutedText,
                                        fontSize = 13.sp,
                                        textAlign = TextAlign.Center
                                    )

                                    Spacer(modifier = Modifier.height(20.dp))

                                    Button(
                                        onClick = {
                                            selectedDestination = AppDestination.INVESTIGATE
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = ElectricViolet
                                        ),
                                        shape = RoundedCornerShape(14.dp)
                                    ) {
                                        Text("Start Investigation")
                                    }
                                }
                            }

                        } else {

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${investigationHistory.size} investigations",
                                    color = MutedText,
                                    fontSize = 13.sp
                                )

                                TextButton(
                                    onClick = {
                                        coroutineScope.launch(Dispatchers.IO) {
                                            investigationDao.deleteAllInvestigations()
                                        }
                                    }
                                ) {
                                    Text(
                                        text = "Clear All",
                                        color = Color(0xFFFF6B81)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(bottom = 24.dp)
                            ) {
                                items(
                                    items = investigationHistory,
                                    key = { it.id }
                                ) { item ->

                                    val riskColor = when (
                                        item.riskLevel.uppercase()
                                    ) {
                                        "CRITICAL", "HIGH" -> Color(0xFFFF647C)
                                        "MEDIUM" -> Color(0xFFFFB454)
                                        "LOW" -> SuccessGreen
                                        else -> AuroraBlue
                                    }

                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                try {
                                                    investigationReport =
                                                        Gson().fromJson(
                                                            item.reportJson,
                                                            com.bapparis.baguardai.data.model.InvestigationReport::class.java
                                                        )

                                                    selectedDestination =
                                                        AppDestination.RESULTS

                                                } catch (error: Exception) {
                                                    android.util.Log.e(
                                                        "BAGUARD_HISTORY",
                                                        "Could not open saved report",
                                                        error
                                                    )
                                                }
                                            },
                                        shape = RoundedCornerShape(18.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = CardSurface
                                        ),
                                        border = BorderStroke(
                                            1.dp,
                                            BorderColor
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = item.riskLevel.uppercase(),
                                                    color = riskColor,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )

                                                Text(
                                                    text = java.text.SimpleDateFormat(
                                                        "MMM dd, yyyy • hh:mm a",
                                                        java.util.Locale.getDefault()
                                                    ).format(
                                                        java.util.Date(item.createdAt)
                                                    ),
                                                    color = MutedText,
                                                    fontSize = 10.sp
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(12.dp))

                                            Text(
                                                text = item.summary,
                                                color = SoftWhite,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                maxLines = 3
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))

                                            Text(
                                                text = item.originalContent,
                                                color = MutedText,
                                                fontSize = 12.sp,
                                                maxLines = 2
                                            )

                                            Spacer(modifier = Modifier.height(12.dp))

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "View investigation →",
                                                    color = AuroraBlue,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Medium
                                                )

                                                TextButton(
                                                    onClick = {
                                                        coroutineScope.launch(Dispatchers.IO) {
                                                            investigationDao.deleteInvestigation(item.id)
                                                        }
                                                    }
                                                ) {
                                                    Text(
                                                        text = "Delete",
                                                        color = Color(0xFFFF647C),
                                                        fontSize = 12.sp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }


                AppDestination.PROFILE -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AppTopBar(
                            onOpenDrawer = { drawerOpen = true },
                            title = "Your Profile",
                            onOpenProfile = {}
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(88.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(ElectricViolet, AuroraBlue)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("👤", fontSize = 36.sp)
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            Text(
                                text = "Guest Account",
                                color = SoftWhite,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "You're using BAGUARD AI as a guest.",
                                color = MutedText,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = "Create an account or sign in when you're ready to access account features.",
                                color = MutedText,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 21.sp
                            )

                            Spacer(modifier = Modifier.height(28.dp))

                            Button(
                                onClick = {},
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ElectricViolet
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(
                                    text = "Create Account",
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedButton(
                                onClick = {},
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(
                                    text = "Sign In",
                                    color = SoftWhite
                                )
                            }
                        }
                    }
                }

                AppDestination.SETTINGS -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AppTopBar(
                            onOpenDrawer = { drawerOpen = true },
                            title = "Settings"
                        )

                        PlaceholderScreen(
                            symbol = "⚙",
                            title = "Your preferences",
                            description = "Privacy, language, notifications, and app preferences will be configured here."
                        )
                    }
                }

                AppDestination.SAFETY_CENTER -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AppTopBar(
                            onOpenDrawer = { drawerOpen = true },
                            title = "Safety Center"
                        )

                        SafetyCenterScreen(
                            onInvestigate = {
                                investigationInput = ""
                                selectedDestination = AppDestination.INVESTIGATE
                            }
                        )
                    }
                }

                AppDestination.PRIVACY -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AppTopBar(
                            onOpenDrawer = { drawerOpen = true },
                            title = "Privacy & Data"
                        )

                        PrivacyScreen()
                    }
                }

                AppDestination.REPORT_BUG -> {
                    ReportBugScreen(
                        onBack = {
                            selectedDestination = AppDestination.HOME
                        }
                    )
                }

                AppDestination.ABOUT -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AppTopBar(
                            onOpenDrawer = { drawerOpen = true },
                            title = "About BAGUARD AI"
                        )

                        AboutBaguardScreen()
                    }
                }
            }
        }

        if (drawerOpen) {
            NavigationDrawer(
                selected = selectedDestination,
                onSelect = { destination ->
                    selectedDestination = destination
                    drawerOpen = false
                    investigationRequested = false
                },
                onClose = {
                    drawerOpen = false
                }
            )
        }
        }
    }


@Composable
fun InvestigationLoadingPage(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Midnight)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "BAGUARD AI",
            color = AuroraBlue,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        CyberInvestigationPreview()

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Investigation in progress",
            color = SoftWhite,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "BAGUARD AI is examining your submitted content. Your findings will appear automatically when analysis is complete.",
            color = MutedText,
            fontSize = 11.sp,
            lineHeight = 18.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SafetyCenterScreen(
    onInvestigate: () -> Unit
)
{
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Midnight)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        // INTRODUCTION
        Text(
            text = "Your Digital Safety Hub",
            color = SoftWhite,
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Recognize threats, understand risks, and make safer digital decisions.",
            color = MutedText,
            fontSize = 11.sp,
            lineHeight = 18.sp
        )

        Button(
            onClick = onInvestigate,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ElectricViolet
            )
        ) {
            Text(
                text = "⌕  Investigate a Suspicious Message or Link",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // SECURITY OVERVIEW
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(
                    Brush.linearGradient(
                        listOf(DeepSpace, CardSurface)
                    )
                )
                .border(
                    1.dp,
                    ElectricViolet.copy(alpha = 0.45f),
                    RoundedCornerShape(22.dp)
                )
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "✦  SECURITY AWARENESS",
                color = AuroraBlue,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.3.sp
            )

            Text(
                text = "Stay alert. Stay protected.",
                color = SoftWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Learn to identify suspicious activity and verify digital information before taking action.",
                color = MutedText,
                fontSize = 11.sp,
                lineHeight = 18.sp
            )

            Text(
                text = "GUIDANCE MODE • NOT A LIVE DEVICE SECURITY SCAN",
                color = AuroraBlue,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // THREAT AWARENESS
        Text(
            text = "THREAT AWARENESS",
            color = AuroraBlue,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
        )

        SafetyTipCard(
            symbol = "↗",
            title = "Phishing and fake links",
            description = "Watch for misspelled domains, unfamiliar URLs, fake login pages, and messages asking you to verify your account urgently."
        )

        SafetyTipCard(
            symbol = "✉",
            title = "SMS and email scams",
            description = "Be cautious of unexpected prizes, delivery notifications, payment requests, and messages pretending to represent trusted organizations."
        )

        SafetyTipCard(
            symbol = "♙",
            title = "Identity impersonation",
            description = "Scammers may pretend to be friends, employers, banks, or government officials. Verify unusual requests through another trusted channel."
        )

        SafetyTipCard(
            symbol = "⌑",
            title = "Account protection",
            description = "Use unique passwords, enable two-factor authentication, and never share your passwords, banking PINs, or one-time verification codes."
        )

        // PRACTICAL RECOMMENDATIONS
        Text(
            text = "SECURITY RECOMMENDATIONS",
            color = AuroraBlue,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
        )

        SafetyTipCard(
            symbol = "✓",
            title = "Verify before clicking",
            description = "Check the official website address independently instead of trusting links inside unexpected messages."
        )

        SafetyTipCard(
            symbol = "⌘",
            title = "Keep your device updated",
            description = "Install Android security updates and download applications from trusted sources such as Google Play."
        )

        SafetyTipCard(
            symbol = "◈",
            title = "Review app permissions",
            description = "Check whether installed applications request access to contacts, microphone, camera, location, or other sensitive information without a clear reason."
        )

        // EMERGENCY GUIDANCE
        Text(
            text = "IF SOMETHING GOES WRONG",
            color = Color(0xFFFCA5A5),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
        )

        SafetyTipCard(
            symbol = "!",
            title = "You clicked a suspicious link",
            description = "Close the page. Do not download files or enter personal information. If you entered account credentials, change your password using the official website."
        )

        SafetyTipCard(
            symbol = "⚠",
            title = "You shared banking information",
            description = "Contact your bank immediately using its official phone number. Ask them to secure your account and review suspicious transactions."
        )

        SafetyTipCard(
            symbol = "✦",
            title = "You suspect account compromise",
            description = "Change affected passwords, sign out unfamiliar sessions, enable two-factor authentication, and review recent account activity."
        )

        // FINAL NOTICE
        Text(
            text = "BAGUARD AI SECURITY NOTE",
            color = AuroraBlue,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Security guidance helps you make informed decisions, but no single tool can guarantee complete protection. Always verify important information independently.",
            color = MutedText,
            fontSize = 10.sp,
            lineHeight = 17.sp
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun PrivacyScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Your data matters.",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "BAGUARD AI is being developed around transparent, evidence-grounded security assistance.",
            color = Color.LightGray,
            fontSize = 10.sp,
            lineHeight = 18.sp
        )

        SafetyTipCard(
            symbol = "⌕",
            title = "Current investigation method",
            description = "The prototype checks submitted text for selected warning patterns. These checks cannot definitively establish whether a message or link is safe or malicious."
        )

        SafetyTipCard(
            symbol = "☁",
            title = "AI and online services",
            description = "Live Meta AI analysis and backend processing are not yet connected in this prototype."
        )

        SafetyTipCard(
            symbol = "▤",
            title = "Investigation reports",
            description = "Persistent investigation history has not yet been implemented. Reports should not be assumed to survive an app restart."
        )

        SafetyTipCard(
            symbol = "⌑",
            title = "Sensitive information",
            description = "Avoid submitting passwords, banking PINs, one-time codes, or other secrets."
        )
    }
}

@Composable
fun AboutBaguardScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "🤖",
            color = Color(0xFF8B5CF6),
            fontSize = 46.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            text = "BAGUARD AI",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Intelligent Protection for Everyday Digital Life",
            color = Color(0xFF38BDF8),
            fontSize = 10.sp,
            textAlign = TextAlign.Center
        )

        SafetyTipCard(
            symbol = "◎",
            title = "Our mission",
            description = "Help people understand suspicious messages and links, recognize potential threats, and make safer digital decisions."
        )

        SafetyTipCard(
            symbol = "✦",
            title = "AI-assisted protection",
            description = "BAGUARD AI aims to combine practical security checks with AI-assisted explanations when the required services are available."
        )

        SafetyTipCard(
            symbol = "⌘",
            title = "Powered by Baglot 🛠",
            description = "Building practical AI-powered tools to make everyday digital life safer."
        )

        Text(
            text = "Built By Bapparis 👨‍💻",
            color = Color.LightGray,
            fontSize = 10.sp
        )
    }
}

@Composable
fun SafetyTipCard(
    symbol: String,
    title: String,
    description: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFF121426),
        border = BorderStroke(
            1.dp,
            Color(0x338B5CF6)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(Color(0x268B5CF6)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = symbol,
                    color = Color(0xFF38BDF8),
                    fontSize = 21.sp
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = description,
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
// ============================================================
// HOME SCREEN
// ============================================================

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onOpenDrawer: () -> Unit,
    onInvestigate: () -> Unit,
    onHistory: () -> Unit,
    onOpenProfile: () -> Unit,
    onVoiceAssistant: () -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DashboardBackground),
        contentPadding = PaddingValues(
            start = 18.dp,
            end = 18.dp,
            top = 8.dp,
            bottom = 24.dp
        ),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {

        // HEADER
        item {
            AppTopBar(
                onOpenDrawer = onOpenDrawer,
                title = "BAGUARD AI",
                onOpenProfile = onOpenProfile
            )
        }

        // AI THREAT MONITOR
        item {
            SecurityOrb(
            onTap = onVoiceAssistant
            )
        }

        // INVESTIGATION INPUT
        item {
            InvestigationLaunchCard(
                onClick = onInvestigate
            )
        }

        // QUICK INVESTIGATION SHORTCUTS
        item {
            QuickActionsSection(
                onHistory = onHistory
            )
        }

        // ANDROID DEVICE HEALTH
        item {
            AndroidHealthSection()
        }

        // BRAND FOOTER
        item {
            Text(
                text = "BAPPARIS DEV  •  INTELLIGENT PROTECTION",
                color = DashboardSecondary.copy(alpha = 0.55f),
                fontSize = 9.sp,
                letterSpacing = 1.2.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 8.dp)
            )
        }
    }
}

@Composable
fun AppTopBar(
    onOpenDrawer: () -> Unit,
    title: String,
    onOpenProfile: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // GLASS DRAWER BUTTON
        Surface(
            onClick = onOpenDrawer,
            shape = RoundedCornerShape(15.dp),
            color = DashboardGlass,
            border = BorderStroke(1.dp, DashboardBorder),
            modifier = Modifier.size(44.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "☰",
                    color = DashboardText,
                    fontSize = 20.sp
                )
            }
        }

        // CENTERED BRANDING
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = DashboardText,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.1.sp
            )

            Text(
                text = "INTELLIGENT SECURITY",
                color = DashboardSecondary.copy(alpha = 0.75f),
                fontSize = 8.sp,
                letterSpacing = 1.5.sp
            )
        }

        // PROFILE AVATAR
        Surface(
            onClick = onOpenProfile,
            shape = CircleShape,
            color = Color.Transparent,
            modifier = Modifier.size(44.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                DashboardViolet,
                                DashboardMagenta,
                                AuroraBlue
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        color = DashboardAccent.copy(alpha = 0.55f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "👤",
                    fontSize = 20.sp
                )
            }
        }
    }
}

@Composable
fun WelcomeSection() {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(
            text = "Your digital world,",
            color = SoftWhite,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "with a little more protection.",
            color = AuroraBlue,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Investigate suspicious messages and links, understand the warning signs, and make safer decisions.",
            color = MutedText,
            fontSize = 8.sp,
            lineHeight = 15.sp
        )
    }
}

@Composable
fun SecurityOrb(onTap: () -> Unit) {
    val transition = rememberInfiniteTransition(label = "threat_orb")

    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(18000, easing = LinearEasing)
        ),
        label = "orb_rotation"
    )

    val pulse by transition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb_pulse"
    )

    Column(
        modifier = Modifier.fillMaxWidth()
            .clickable(onClick = onTap),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp),
            contentAlignment = Alignment.Center
        ) {

            // AMBIENT GLOW
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .scale(pulse)
                    .background(
                        DashboardViolet.copy(alpha = 0.17f),
                        CircleShape
                    )
            )

            // ORBIT RINGS
            Canvas(
                modifier = Modifier
                    .size(260.dp)
                    .rotate(rotation)
            ) {
                drawCircle(
                    color = DashboardAccent.copy(alpha = 0.28f),
                    style = Stroke(width = 1.3.dp.toPx())
                )

                drawCircle(
                    color = DashboardMagenta.copy(alpha = 0.35f),
                    radius = size.minDimension * 0.39f,
                    style = Stroke(width = 1.dp.toPx())
                )

                drawCircle(
                    color = DashboardAccent.copy(alpha = 0.55f),
                    radius = size.minDimension * 0.29f,
                    style = Stroke(
                        width = 1.5.dp.toPx(),
                        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                            floatArrayOf(5f, 9f)
                        )
                    )
                )
            }

            // CENTRAL AI SPHERE
            Box(
                modifier = Modifier
                    .size(145.dp)
                    .scale(pulse)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF6040A8),
                                Color(0xFF28184D),
                                Color(0xFF130D2B)
                            )
                        ),
                        CircleShape
                    )
                    .border(
                        1.5.dp,
                        DashboardAccent.copy(alpha = 0.65f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "✦",
                        fontSize = 36.sp,
                        color = DashboardAccent
                    )

                    Text(
                        text = "BAGUARD AI",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = DashboardText
                    )

                    Text(
                        text = "Analyzing",
                        fontSize = 10.sp,
                        color = DashboardSecondary
                    )
                }
            }

            val bubbleTransition = rememberInfiniteTransition(
                label = "threat_bubbles"
            )

            val unknownLoginFloat = bubbleTransition.animateFloat(
                initialValue = -7f,
                targetValue = 7f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2600, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "unknown_login"
            )

            val phishingSmsFloat = bubbleTransition.animateFloat(
                initialValue = 6f,
                targetValue = -6f,
                animationSpec = infiniteRepeatable(
                    animation = tween(3100, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "phishing_sms"
            )

            val riskyAppFloat = bubbleTransition.animateFloat(
                initialValue = -5f,
                targetValue = 5f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2300, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "risky_app"
            )

            val openWifiFloat = bubbleTransition.animateFloat(
                initialValue = 7f,
                targetValue = -7f,
                animationSpec = infiniteRepeatable(
                    animation = tween(3500, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "open_wifi"
            )

            // FLOATING THREAT INDICATORS
            ThreatIndicator(
                title = "Unknown login",
                color = Color(0xFFFF6B91),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(
                        x = (unknownLoginFloat.value * 0.3f).dp,
                        y = unknownLoginFloat.value.dp
                    )
            )

            ThreatIndicator(
                title = "Phishing SMS",
                color = Color(0xFFFF6B91),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(
                        x = (phishingSmsFloat.value * 0.3f).dp,
                        y = phishingSmsFloat.value.dp
                    )
            )

            ThreatIndicator(
                title = "Risky app",
                color = Color(0xFFFFC857),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(
                        x = (riskyAppFloat.value * 0.3f).dp,
                        y = riskyAppFloat.value.dp
                    )
            )

            ThreatIndicator(
                title = "Open Wi-Fi",
                color = Color(0xFFFFC857),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(
                        x = (openWifiFloat.value * 0.3f).dp,
                        y = openWifiFloat.value.dp
                    )
            )
        }

        // THREAT STATUS
        Surface(
            color = Color(0xFFFF6B91).copy(alpha = 0.12f),
            shape = RoundedCornerShape(50.dp),
            border = BorderStroke(
                1.dp,
                Color(0xFFFF6B91).copy(alpha = 0.35f)
            )
        ) {
            Text(
                text = "●  Suspicious activity detected",
                color = Color(0xFFFF9BB3),
                fontSize = 10.sp,
                modifier = Modifier.padding(
                    horizontal = 14.dp,
                    vertical = 8.dp
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "4 threats under AI review",
            color = DashboardText,
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = "Tracing sources and checking your accounts",
            color = DashboardSecondary,
            fontSize = 11.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ThreatIndicator(
    title: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.padding(8.dp),
        color = DashboardGlass,
        shape = RoundedCornerShape(50.dp),
        border = BorderStroke(
            1.dp,
            color.copy(alpha = 0.35f)
        )
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 7.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(color, CircleShape)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = title,
                color = DashboardText,
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun InvestigationLaunchCard(
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = DashboardGlass,
        border = BorderStroke(1.dp, DashboardBorder)
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 17.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "✦",
                color = DashboardAccent,
                fontSize = 22.sp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Paste a link, email or text to investigate",
                color = DashboardSecondary,
                fontSize = 11.sp,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                DashboardViolet,
                                DashboardMagenta
                            )
                        ),
                        RoundedCornerShape(13.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⌕",
                    color = Color.White,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
@Composable
fun QuickActionsSection(
    onHistory: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DashboardShortcutCard(
                symbol = "✉",
                title = "Email",
                modifier = Modifier.weight(1f)
            )

            DashboardShortcutCard(
                symbol = "↗",
                title = "Link",
                modifier = Modifier.weight(1f)
            )

            DashboardShortcutCard(
                symbol = "▤",
                title = "Text",
                modifier = Modifier.weight(1f)
            )

            DashboardShortcutCard(
                symbol = "▣",
                title = "Device",
                modifier = Modifier.weight(1f),
                onClick = onHistory
            )
        }
    }
}

@Composable
private fun DashboardShortcutCard(
    symbol: String,
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(17.dp),
        color = DashboardGlass,
        border = BorderStroke(1.dp, DashboardBorder)
    ) {
        Column(
            modifier = Modifier.padding(
                vertical = 16.dp,
                horizontal = 4.dp
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = symbol,
                color = DashboardAccent,
                fontSize = 22.sp
            )

            Spacer(modifier = Modifier.height(7.dp))

            Text(
                text = title,
                color = DashboardText,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun AndroidHealthSection() {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            text = "Android health",
            color = DashboardText,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            AndroidHealthCard(
                title = "App permissions",
                status = "2 risky",
                description = "Two apps can read your SMS",
                statusColor = Color(0xFFFF6B91),
                symbol = "◈",
                modifier = Modifier.weight(1f)
            )

            AndroidHealthCard(
                title = "Malware scan",
                status = "Clean",
                description = "128 apps checked today",
                statusColor = Color(0xFF55D6A2),
                symbol = "✧",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(11.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            AndroidHealthCard(
                title = "Wi-Fi guard",
                status = "Unsafe",
                description = "Open network",
                statusColor = Color(0xFFFFC857),
                symbol = "⌁",
                modifier = Modifier.weight(1f)
            )

            AndroidHealthCard(
                title = "Security patch",
                status = "Updated",
                description = "Android 15 · Oct 2026",
                statusColor = Color(0xFF55D6A2),
                symbol = "✓",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun AndroidHealthCard(
    title: String,
    status: String,
    description: String,
    statusColor: Color,
    symbol: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = DashboardGlass,
        border = BorderStroke(
            1.dp,
            DashboardBorder
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {

            // Icon and status on the same row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(
                            DashboardViolet.copy(alpha = 0.18f),
                            RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = symbol,
                        color = DashboardAccent,
                        fontSize = 16.sp
                    )
                }

                Surface(
                    color = statusColor.copy(alpha = 0.13f),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text(
                        text = status,
                        color = statusColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 4.dp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                color = DashboardText,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(7.dp))

            Text(
                text = description,
                color = DashboardSecondary,
                fontSize = 9.sp,
                lineHeight = 14.sp
            )
        }
    }
}
@Composable
fun QuickActionCard(
    symbol: String,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(CardSurface)
            .border(1.dp, BorderColor, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = symbol,
            color = AuroraBlue,
            fontSize = 26.sp
        )
        Text(
            text = title,
            color = SoftWhite,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = subtitle,
            color = MutedText,
            fontSize = 11.sp
        )
    }
}

@Composable
fun PrivacyNotice() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DeepSpace)
            .border(1.dp, BorderColor, RoundedCornerShape(14.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "🛡",
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Your safety comes first",
                color = SoftWhite,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "This is an early UI prototype. No message is sent to an AI service yet. Avoid pasting passwords or verification codes.",
                color = MutedText,
                fontSize = 8.sp,
                lineHeight = 14.sp
            )
        }
    }
}

// ============================================================
// INVESTIGATION SCREEN
// ============================================================

@Composable
fun InvestigationScreen(
    modifier: Modifier = Modifier,
    initialText: String,
    investigationRequested: Boolean,
    investigationReport: com.bapparis.baguardai.data.model.InvestigationReport?,
    errorMessage: String?,
    usage: com.bapparis.baguardai.data.model.InvestigationUsage?,
    onTextChange: (String) -> Unit,
    onBack: () -> Unit,
    onInvestigate: () -> Unit
) {
    var selectedMode by remember { mutableStateOf("Text") }

    val glassSurface = Color.White.copy(alpha = 0.10f)
    val glassBorder = Color.White.copy(alpha = 0.20f)
    val lavenderText = Color(0xFFC9BDF2)

    val investigationModes = listOf("Device", "Email", "Link", "Text")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF170B38),
                        Color(0xFF0B0720),
                        Color(0xFF100824),
                        Color(0xFF0B0720)
                    )
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BaguardBackButton(onClick = onBack)

            Spacer(modifier = Modifier.weight(1f))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "SECURITY",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )

                Text(
                    text = "INVESTIGATION",
                    color = lavenderText,
                    fontSize = 9.sp,
                    letterSpacing = 2.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(glassSurface)
                    .border(
                        1.dp,
                        glassBorder,
                        RoundedCornerShape(13.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "AI",
                    color = AuroraBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(glassSurface)
                .border(
                    1.dp,
                    glassBorder,
                    RoundedCornerShape(20.dp)
                )
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            investigationModes.forEach { mode ->

                val selected = selectedMode == mode

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(15.dp))
                        .background(
                            if (selected) {
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF8B5CF6),
                                        Color(0xFF5B21B6)
                                    )
                                )
                            } else {
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Transparent
                                    )
                                )
                            }
                        )
                        .clickable {
                            selectedMode = mode
                        }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = mode,
                        color = if (selected) {
                            Color.White
                        } else {
                            lavenderText
                        },
                        fontSize = 12.sp,
                        fontWeight = if (selected) {
                            FontWeight.SemiBold
                        } else {
                            FontWeight.Medium
                        }
                    )
                }
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Let's investigate.",
                color = SoftWhite,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = when (selectedMode) {
                    "Device" -> "Explore device security and protection features."
                    "Email" -> "Investigate suspicious emails and messages."
                    "Link" -> "Examine suspicious URLs before opening them."
                    else -> "Analyze suspicious messages and identify potential threats."
                },
                color = MutedText,
                fontSize = 10.sp,
                lineHeight = 18.sp
            )
        }
        if (selectedMode == "Device") {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF211344),
                                Color(0xFF110C28),
                                Color(0xFF17102F)
                            )
                        )
                    )
                    .border(
                        1.dp,
                        glassBorder,
                        RoundedCornerShape(26.dp)
                    )
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Text(
                    text = "DEVICE PROTECTION",
                    color = AuroraBlue,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )

                Box(
                    modifier = Modifier
                        .size(112.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF291653))
                        .border(
                            2.dp,
                            Color(0xFF8B5CF6).copy(alpha = 0.7f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "AI",
                            color = Color.White,
                            fontSize = 29.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Text(
                            text = "GUARDIAN",
                            color = lavenderText,
                            fontSize = 8.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Text(
                    text = "Your digital guardian is getting ready.",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "BAGUARD AI device protection is under development. Device-level scanning is not active yet.",
                    color = lavenderText,
                    fontSize = 11.sp,
                    lineHeight = 18.sp
                )

                HorizontalDivider(color = glassBorder)

                val deviceFeatures = listOf(
                    "Installed app security",
                    "Application permission audit",
                    "Network security monitoring",
                    "Device threat detection"
                )

                deviceFeatures.forEach { feature ->

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF8B5CF6).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "◆",
                                color = AuroraBlue,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = feature,
                            color = Color.White,
                            fontSize = 11.sp,
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = "COMING SOON",
                            color = AuroraBlue,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                HorizontalDivider(color = glassBorder)

                Text(
                    text = "DEVICE SCANNING IS NOT YET AVAILABLE",
                    color = lavenderText,
                    fontSize = 9.sp,
                    letterSpacing = 0.8.sp
                )
            }

        } else {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF21163D),
                                Color(0xFF120D29),
                                Color(0xFF19102F)
                            )
                        )
                    )
                    .border(
                        1.dp,
                        glassBorder,
                        RoundedCornerShape(24.dp)
                    )
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (selectedMode) {
                            "Email" -> "EMAIL INVESTIGATION"
                            "Link" -> "LINK INVESTIGATION"
                            else -> "MESSAGE INVESTIGATION"
                        },
                        color = AuroraBlue,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "META AI",
                        color = MutedText,
                        fontSize = 8.sp
                    )
                }

                BasicTextField(
                    value = initialText,
                    onValueChange = onTextChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 180.dp)
                        .padding(vertical = 8.dp),
                    textStyle = TextStyle(
                        color = SoftWhite,
                        fontSize = 12.sp,
                        lineHeight = 20.sp
                    ),
                    cursorBrush = Brush.verticalGradient(
                        colors = listOf(ElectricViolet, AuroraBlue)
                    ),
                    decorationBox = { innerTextField ->
                        Box {
                            if (initialText.isEmpty()) {
                                Text(
                                    text = when (selectedMode) {
                                        "Email" -> "Paste the suspicious email here..."
                                        "Link" -> "Paste the suspicious URL here..."
                                        else -> "Paste the suspicious message here..."
                                    },
                                    color = MutedText,
                                    fontSize = 12.sp,
                                    lineHeight = 20.sp
                                )
                            }

                            innerTextField()
                        }
                    }
                )

                HorizontalDivider(
                    color = BorderColor,
                    thickness = 1.dp
                )

                Text(
                    text = "Remove passwords, verification codes, and other sensitive information before submitting.",
                    color = MutedText,
                    fontSize = 10.sp,
                    lineHeight = 15.sp
                )
            }
        }

        Button(
            onClick = onInvestigate,
            enabled = selectedMode != "Device" &&
                    initialText.isNotBlank() &&
                    !investigationRequested,
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ElectricViolet,
                contentColor = Color.White,
                disabledContainerColor = CardSurface,
                disabledContentColor = MutedText
            )
        ) {
            Text(
                text = when (selectedMode) {
                    "Email" -> "Investigate Email with BAGUARD AI"
                    "Link" -> "Analyze Link with BAGUARD AI"
                    "Device" -> "Device Scanning Coming Soon"
                    else -> "Investigate with BAGUARD AI"
                },
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        AnimatedVisibility(
            visible = investigationRequested,
            enter = androidx.compose.animation.fadeIn() +
                    androidx.compose.animation.expandVertically(),
            exit = androidx.compose.animation.fadeOut() +
                    androidx.compose.animation.shrinkVertically()
        ) {
            CyberInvestigationPreview()
        }

        AnimatedVisibility(
            visible = errorMessage != null,
            enter = androidx.compose.animation.fadeIn()
        ) {
            Text(
                text = errorMessage.orEmpty(),
                color = Color(0xFFFCA5A5),
                fontSize = 11.sp
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF21163D),
                            Color(0xFF130D29)
                        )
                    )
                )
                .border(
                    1.dp,
                    glassBorder,
                    RoundedCornerShape(15.dp)
                )
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Text(
                text = "🛡️ Stay protected",
                color = SoftWhite,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Never share passwords, PINs, or one-time codes. Do not open suspicious links while you are trying to verify them.",
                color = MutedText,
                fontSize = 8.sp,
                lineHeight = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(18.dp))
    }
}

@Composable
fun InvestigationResultsScreen(
    report: com.bapparis.baguardai.data.model.InvestigationReport,
    usage: com.bapparis.baguardai.data.model.InvestigationUsage?,
    onBack: () -> Unit,
    onInvestigateAnother: () -> Unit
) {
    val context = LocalContext.current

    // Voice states
    var ttsReady by remember { mutableStateOf(false) }
    var isSpeaking by remember { mutableStateOf(false) }

    val mainHandler = remember { Handler(Looper.getMainLooper()) }

    // Android Text-to-Speech
    val textToSpeech = remember(context) {
        TextToSpeech(context) { status ->
            ttsReady = status == TextToSpeech.SUCCESS
        }
    }

    DisposableEffect(textToSpeech) {
        textToSpeech.setOnUtteranceProgressListener(
            object : UtteranceProgressListener() {

                override fun onStart(utteranceId: String?) {
                    mainHandler.post {
                        isSpeaking = true
                    }
                }

                override fun onDone(utteranceId: String?) {
                    mainHandler.post {
                        isSpeaking = false
                    }
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    mainHandler.post {
                        isSpeaking = false
                    }
                }
            }
        )

        onDispose {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }

    // Build spoken report using REAL investigation data
    val spokenReport = remember(report) {
        buildString {
            append("BAGUARD AI investigation results. ")

            append("Risk level: ${report.riskLevel}. ")

            append("Confidence: ${report.confidence}. ")

            append("Investigation summary. ")
            append(report.summary)

            if (report.evidence.isNotEmpty()) {
                append(". Evidence and observations. ")

                report.evidence.forEachIndexed { index, evidence ->
                    append("Finding ${index + 1}. ")
                    append(evidence.observation)
                    append(". ")
                    append(evidence.significance)
                    append(". ")
                }
            }

            if (report.recommendedActions.isNotEmpty()) {
                append("Recommended actions. ")

                report.recommendedActions.forEachIndexed { index, action ->
                    append("Action ${index + 1}. ")
                    append(action)
                    append(". ")
                }
            }

            append("End of BAGUARD AI investigation report.")
        }
    }

    // Risk presentation
    val riskColor = when (report.riskLevel.trim().uppercase()) {
        "HIGH", "CRITICAL" -> Color(0xFFFF5577)
        "MEDIUM", "MODERATE" -> Color(0xFFFFB547)
        "LOW" -> Color(0xFF48D6A5)
        else -> Color(0xFFB9A7FF)
    }

    val glassBorder = Color.White.copy(alpha = 0.13f)

    val glassGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF281B48),
            Color(0xFF17102E),
            Color(0xFF211537)
        )
    )

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF170B38),
            Color(0xFF0B0720),
            Color(0xFF100824),
            Color(0xFF0B0720)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                onClick = {
                    textToSpeech.stop()
                    isSpeaking = false
                    onBack()
                },
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, glassBorder),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "←",
                        color = Color.White,
                        fontSize = 25.sp
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "INVESTIGATION",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )

                Text(
                    text = "RESULTS",
                    color = Color(0xFFBCA7FF),
                    fontSize = 10.sp,
                    letterSpacing = 3.sp
                )
            }

            Surface(
                shape = CircleShape,
                color = Color(0xFF8B5CF6).copy(alpha = 0.16f),
                border = BorderStroke(
                    1.dp,
                    Color(0xFF8B5CF6).copy(alpha = 0.5f)
                ),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "✦",
                        color = Color(0xFFD2BFFF),
                        fontSize = 21.sp
                    )
                }
            }
        }

        // VOICE REPORT CONTROL
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(glassGradient)
                .border(1.dp, glassBorder, RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "AI VOICE REPORT",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.3.sp
                    )

                    Text(
                        text = if (isSpeaking) {
                            "BAGUARD AI is reading your report..."
                        } else {
                            "Listen to your investigation results"
                        },
                        color = Color(0xFFBCAEDB),
                        fontSize = 10.sp
                    )
                }

                Button(
                    onClick = {
                        if (isSpeaking) {
                            textToSpeech.stop()
                            isSpeaking = false
                        } else if (ttsReady) {
                            textToSpeech.language = Locale.getDefault()

                            textToSpeech.speak(
                                spokenReport,
                                TextToSpeech.QUEUE_FLUSH,
                                null,
                                "baguard-investigation-report"
                            )

                            isSpeaking = true
                        }
                    },
                    enabled = ttsReady,
                    colors = ButtonDefaults.buttonColors(
                containerColor = if (isSpeaking) {
                    Color(0xFF5A2850)
                } else {
                    Color(0xFF7547C8)
                },
                disabledContainerColor = Color.DarkGray
                ),
                shape = RoundedCornerShape(12.dp)
                ) {
                Text(
                    text = when {
                        !ttsReady -> "Loading..."
                        isSpeaking -> "■ Stop"
                        else -> "▶ Listen"
                    },
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            }
        }

        // THREAT ASSESSMENT
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF49203F),
                            Color(0xFF26142F),
                            Color(0xFF1A102B)
                        )
                    )
                )
                .border(
                    1.dp,
                    riskColor.copy(alpha = 0.45f),
                    RoundedCornerShape(26.dp)
                )
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "AI THREAT ASSESSMENT",
                color = Color(0xFFD8C5EF),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(22.dp))

            Box(
                modifier = Modifier
                    .size(125.dp)
                    .border(
                        width = 7.dp,
                        color = riskColor.copy(alpha = 0.75f),
                        shape = CircleShape
                    )
                    .background(
                        riskColor.copy(alpha = 0.10f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = report.riskLevel.uppercase(),
                        color = riskColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = "RISK",
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 10.sp,
                        letterSpacing = 2.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = report.riskLevel.uppercase() + " RISK",
                color = riskColor,
                fontSize = 23.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "AI confidence: ${report.confidence}",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Assessment generated by Meta Muse Spark",
                color = Color(0xFFBDAED8),
                fontSize = 11.sp
            )
        }

        // AI INVESTIGATION SUMMARY
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(glassGradient)
                .border(1.dp, glassBorder, RoundedCornerShape(22.dp))
                .padding(17.dp)
        ) {

            Text(
                text = "✦  AI INVESTIGATION SUMMARY",
                color = Color(0xFFD2B9FF),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = report.summary,
                color = Color.White,
                fontSize = 14.sp,
                lineHeight = 23.sp
            )
        }

        // EVIDENCE & FINDINGS
        if (report.evidence.isNotEmpty()) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(glassGradient)
                    .border(1.dp, glassBorder, RoundedCornerShape(22.dp))
                    .padding(17.dp)
            ) {

                Text(
                    text = "◈  EVIDENCE & FINDINGS",
                    color = Color(0xFFD2B9FF),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(17.dp))

                report.evidence.forEachIndexed { index, finding ->

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {

                        Box(
                            modifier = Modifier
                                .padding(top = 3.dp)
                                .size(9.dp)
                                .background(
                                    Color(0xFFAC83FF),
                                    CircleShape
                                )
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = finding.observation,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 20.sp
                            )

                            Spacer(modifier = Modifier.height(5.dp))

                            Text(
                                text = finding.significance,
                                color = Color(0xFFBDB3D3),
                                fontSize = 12.sp,
                                lineHeight = 19.sp
                            )
                        }
                    }

                    if (index != report.evidence.lastIndex) {
                        Spacer(modifier = Modifier.height(17.dp))

                        HorizontalDivider(
                            color = Color.White.copy(alpha = 0.08f)
                        )

                        Spacer(modifier = Modifier.height(17.dp))
                    }
                }
            }
        }

        // ALTERNATIVE EXPLANATIONS
        if (report.alternativeExplanations.isNotEmpty()) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(glassGradient)
                    .border(1.dp, glassBorder, RoundedCornerShape(22.dp))
                    .padding(17.dp)
            ) {
                Text(
                    text = "◉  ALTERNATIVE EXPLANATIONS",
                    color = Color(0xFFB9B2FF),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                report.alternativeExplanations.forEach { explanation ->

                    Row(
                        verticalAlignment = Alignment.Top
                    ) {

                        Text(
                            text = "•",
                            color = Color(0xFFB9A7FF),
                            fontSize = 18.sp
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = explanation,
                            color = Color.White.copy(alpha = 0.88f),
                            fontSize = 13.sp,
                            lineHeight = 21.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }

        // RECOMMENDED ACTIONS
        if (report.recommendedActions.isNotEmpty()) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF142F32),
                                Color(0xFF15252F),
                                Color(0xFF191A30)
                            )
                        )
                    )
                    .border(
                        1.dp,
                        Color(0xFF48D6A5).copy(alpha = 0.30f),
                        RoundedCornerShape(22.dp)
                    )
                    .padding(17.dp)
            ) {

                Text(
                    text = "✓  RECOMMENDED ACTIONS",
                    color = Color(0xFF65E5B5),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                report.recommendedActions.forEachIndexed { index, action ->

                    Row(
                        verticalAlignment = Alignment.Top
                    ) {

                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF48D6A5).copy(alpha = 0.15f),
                            modifier = Modifier.size(25.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${index + 1}",
                                    color = Color(0xFF65E5B5),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = action,
                            color = Color.White,
                            fontSize = 13.sp,
                            lineHeight = 21.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(13.dp))
                }
            }
        }

        // INVESTIGATION LIMITATIONS
        if (report.limitations.isNotEmpty()) {

            Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0xFF352B1D).copy(alpha = 0.65f))
                .border(
                    1.dp,
                    Color(0xFFFFC66D).copy(alpha = 0.30f),
                    RoundedCornerShape(22.dp)
                )
                .padding(17.dp)
            ) {

                Text(
                    text = "ⓘ  INVESTIGATION LIMITATIONS",
                    color = Color(0xFFFFD18A),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(13.dp))

                report.limitations.forEach { limitation ->

                    Row(
                        verticalAlignment = Alignment.Top
                    ) {

                        Text(
                            text = "•",
                            color = Color(0xFFFFD18A),
                            fontSize = 17.sp
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = limitation,
                            color = Color(0xFFFFE6BF),
                            fontSize = 12.sp,
                            lineHeight = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(9.dp))
                }
            }
        }

        // DAILY USAGE
        if (usage != null) {

            val progress = if (usage.dailyLimit > 0) {
                (usage.used.toFloat() / usage.dailyLimit.toFloat())
                    .coerceIn(0f, 1f)
            } else {
                0f
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(glassGradient)
                    .border(1.dp, glassBorder, RoundedCornerShape(22.dp))
                    .padding(17.dp)
            ) {

                Text(
                    text = "DAILY INVESTIGATION USAGE",
                    color = Color(0xFFD2B9FF),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(17.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "${usage.used} / ${usage.dailyLimit} investigations used",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "${usage.remaining} left",
                        color = Color(0xFF65E5B5),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.10f))
                ) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF8B5CF6),
                                        Color(0xFF45B9F5)
                                    )
                                )
                    )
                    )
                }

                Spacer(modifier = Modifier.height(13.dp))

                Text(
                    text = "Resets at: ${usage.resetAt}",
                    color = Color(0xFFBDB3D3),
                    fontSize = 11.sp
                )
            }
        }

        // FINAL DISCLAIMER
        Text(
            text = "BAGUARD AI provides AI-assisted security analysis. " +
                    "Always verify important security decisions independently.",
            color = Color.White.copy(alpha = 0.48f),
            fontSize = 11.sp,
            lineHeight = 18.sp
        )

        // INVESTIGATE AGAIN
        Button(
            onClick = {
                textToSpeech.stop()
                isSpeaking = false
                onInvestigateAnother()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(17.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF8152D9)
            )
        ) {

            Text(
                text = "＋  Investigate Another",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(15.dp))
    }
}

// ============================================================
// CYBER INVESTIGATION VISUAL
// Animated scanner preview only. It does not claim to analyse input.
// ============================================================

@Composable
fun CyberInvestigationPreview() {
    val transition = rememberInfiniteTransition(label = "cyberScanner")
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radarRotation"
    )
    val reverseRotation by transition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 7200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "outerRotation"
    )
    val pulse by transition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "corePulse"
    )
    val glow by transition.animateFloat(
        initialValue = 0.22f,
        targetValue = 0.72f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "coreGlow"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF17162F), DeepSpace, Midnight)
                )
            )
            .border(1.dp, ElectricViolet.copy(alpha = 0.38f), RoundedCornerShape(26.dp))
            .padding(horizontal = 18.dp, vertical = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(AuroraBlue.copy(alpha = glow))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "CYBER SCANNER",
                color = AuroraBlue,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.1.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Text("AI", color = ElectricViolet, fontSize = 11.sp, fontWeight = FontWeight.Black)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(224.dp),
            contentAlignment = Alignment.Center
        ) {
            // Ambient glow behind the scanner core.
            Box(
                modifier = Modifier
                    .size(138.dp)
                    .scale(pulse)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                ElectricViolet.copy(alpha = glow * 0.30f),
                                AuroraBlue.copy(alpha = 0.08f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Static radar grid.
            Box(
                modifier = Modifier
                    .size(194.dp)
                    .clip(CircleShape)
                    .border(1.dp, AuroraBlue.copy(alpha = 0.18f), CircleShape)
            )
            Box(
                modifier = Modifier
                    .size(148.dp)
                    .clip(CircleShape)
                    .border(1.dp, ElectricViolet.copy(alpha = 0.25f), CircleShape)
            )
            Box(
                modifier = Modifier
                    .width(194.dp)
                    .height(1.dp)
                    .background(AuroraBlue.copy(alpha = 0.17f))
            )
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(194.dp)
                    .background(AuroraBlue.copy(alpha = 0.17f))
            )

            // Rotating outer and inner radar rings.
            Box(
                modifier = Modifier
                    .size(210.dp)
                    .rotate(reverseRotation)
                    .border(
                        width = 2.dp,
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                Color.Transparent,
                                AuroraBlue.copy(alpha = 0.08f),
                                AuroraBlue,
                                Color.Transparent,
                                ElectricViolet.copy(alpha = 0.85f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            )
            Box(
                modifier = Modifier
                    .size(164.dp)
                    .rotate(rotation)
                    .border(
                        width = 2.dp,
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                ElectricViolet,
                                Color.Transparent,
                                Color.Transparent,
                                AuroraBlue
                            )
                        ),
                        shape = CircleShape
                    )
            )

            // Radar sweep wedge rotates independently around the core.
            Canvas(
                modifier = Modifier
                    .size(194.dp)
                    .rotate(rotation)
            ) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            AuroraBlue.copy(alpha = 0.28f),
                            AuroraBlue.copy(alpha = 0.02f),
                            Color.Transparent
                        )
                    ),
                    startAngle = 0f,
                    sweepAngle = 78f,
                    useCenter = true
                )
            }

            // Small scanner markers orbit with the ring.
            Box(
                modifier = Modifier
                    .size(194.dp)
                    .rotate(rotation)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = (-3).dp)
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(AuroraBlue)
                        .shadow(8.dp, CircleShape, ambientColor = AuroraBlue, spotColor = AuroraBlue)
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(x = 2.dp)
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(ElectricViolet)
                )
            }

            // Core stays steady while its glow pulses.
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .scale(pulse)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFF28234C), Color(0xFF111426))
                        )
                    )
                    .border(1.5.dp, AuroraBlue.copy(alpha = 0.75f), CircleShape)
                    .shadow(20.dp, CircleShape, ambientColor = ElectricViolet, spotColor = AuroraBlue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✦",
                    color = SoftWhite,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Light
                )
            }
        }

        Text(
            text = "BAGUARD AI Investigation in Progress",
            color = SoftWhite,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Meta AI is examining your submitted content through BAGUARD's secure backend.",
            color = MutedText,
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            lineHeight = 16.sp
        )
        }
    }


// ============================================================
// PLACEHOLDER SCREENS
// ============================================================

@Composable
fun PlaceholderScreen(
    symbol: String,
    title: String,
    description: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Midnight)
            .padding(28.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                ElectricViolet.copy(alpha = 0.25f),
                                AuroraBlue.copy(alpha = 0.12f)
                            )
                        )
                    )
                    .border(1.dp, BorderColor, RoundedCornerShape(32.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = symbol,
                    color = AuroraBlue,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = title,
                color = SoftWhite,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = description,
                color = MutedText,
                fontSize = 14.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center
            )

            Surface(
                shape = RoundedCornerShape(50.dp),
                color = ElectricViolet.copy(alpha = 0.16f)
            ) {
                Text(
                    text = "COMING IN THE NEXT BUILD",
                    color = AuroraBlue,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 10.dp
                    )
                )
            }
        }
    }
}

// ============================================================
// BOTTOM NAVIGATION
// Keep bottom-bar items compact and independent from drawer rows.
// ============================================================

@Composable
fun BottomNavigation(
    selected: AppDestination,
    onSelect: (AppDestination) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = DashboardSurface.copy(alpha = 0.96f),
            shape = RoundedCornerShape(25.dp),
            border = BorderStroke(
                1.dp,
                DashboardAccent.copy(alpha = 0.22f)
            ),
            shadowElevation = 14.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 7.dp, vertical = 7.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem(
                    label = "Home",
                    icon = "⌂",
                    selected = selected == AppDestination.HOME,
                    onClick = { onSelect(AppDestination.HOME) },
                    modifier = Modifier.weight(1f)
                )

                BottomNavItem(
                    label = "Investigate",
                    icon = "⌕",
                    selected = selected == AppDestination.INVESTIGATE,
                    onClick = { onSelect(AppDestination.INVESTIGATE) },
                    modifier = Modifier.weight(1f)
                )

                BottomNavItem(
                    label = "Device",
                    icon = "▣",
                    selected = selected == AppDestination.SAFETY_CENTER,
                    onClick = { onSelect(AppDestination.SAFETY_CENTER) },
                    modifier = Modifier.weight(1f)
                )

                BottomNavItem(
                    label = "Settings",
                    icon = "⚙",
                    selected = selected == AppDestination.SETTINGS,
                    onClick = { onSelect(AppDestination.SETTINGS) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
@Composable
private fun BottomNavItem(
    label: String,
    icon: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contentColor = if (selected) {
        DashboardAccent
    } else {
        DashboardSecondary.copy(alpha = 0.70f)
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = if (selected) {
            DashboardViolet.copy(alpha = 0.25f)
        } else {
            Color.Transparent
        },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .padding(horizontal = 2.dp, vertical = 7.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = icon,
                color = contentColor,
                fontSize = 21.sp,
                lineHeight = 23.sp
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = label,
                color = contentColor,
                fontSize = 9.sp,
                lineHeight = 11.sp,
                fontWeight = if (selected) {
                    FontWeight.Bold
                } else {
                    FontWeight.Normal
                },
                maxLines = 1
            )

            if (selected) {
                Spacer(modifier = Modifier.height(3.dp))

                Box(
                    modifier = Modifier
                        .width(15.dp)
                        .height(2.dp)
                        .background(
                            DashboardAccent,
                            RoundedCornerShape(50.dp)
                        )
                )
            }
        }
    }
}
// ============================================================
// NAVIGATION DRAWER
// ============================================================
@Composable
fun NavigationDrawer(
    selected: AppDestination,
    onSelect: (AppDestination) -> Unit,
    onClose: () -> Unit
) {
    val drawerScroll = rememberScrollState()

    val glassBackground = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.16f),
            Color.White.copy(alpha = 0.04f)
        )
    )

    val drawerBackground = Color(0xFF121426)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.58f))
    ) {
        // Outside drawer overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onClose() }
        )

        // Drawer panel
        Column(
            modifier = Modifier
                .fillMaxHeight(0.90f)
                .fillMaxWidth(0.80f)
                .align(Alignment.CenterStart)
                .clip(
                    RoundedCornerShape(
                        topEnd = 36.dp,
                        bottomEnd = 36.dp
                    )
                )
                .background(drawerBackground)
                .border(
                    width = 1.dp,
                    color = Color(0xFF8B5CF6).copy(alpha = 0.25f),
                    shape = RoundedCornerShape(
                        topEnd = 36.dp,
                        bottomEnd = 36.dp
                    )
                )
                .verticalScroll(drawerScroll)
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 38.dp,
                    bottom = 24.dp
                )
        ) {

            // HEADER
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(glassBackground)
                        .border(
                            1.dp,
                            Color.White.copy(alpha = 0.20f),
                            RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🛡",
                        fontSize = 23.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "BAGUARD AI",
                        color = Color(0xFFF5F2FF),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.5.sp
                    )

                    Text(
                        text = "Intelligent Protection",
                        color = Color(0xFFC9BDF2),
                        fontSize = 11.sp
                    )
                }

                Surface(
                    onClick = onClose,
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White.copy(alpha = 0.08f),
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "×",
                            color = Color.White,
                            fontSize = 22.sp
                        )
                    }
                }
            }

            // WORKSPACE
            DrawerSectionTitle("Workspace")
            DrawerGroup {
                DrawerGlassItem(
                    icon = "▦",
                    title = "Dashboard",
                    selected = selected == AppDestination.HOME,
                    onClick = { onSelect(AppDestination.HOME) }
                )

                DrawerGlassItem(
                    icon = "⌕",
                    title = "Investigate",
                    selected = selected == AppDestination.INVESTIGATE,
                    onClick = { onSelect(AppDestination.INVESTIGATE) }
                )

                DrawerGlassItem(
                    icon = "◷",
                    title = "History",
                    selected = selected == AppDestination.HISTORY,
                    onClick = { onSelect(AppDestination.HISTORY) }
                )

                DrawerGlassItem(
                    icon = "▤",
                    title = "Saved Reports (♻)",
                    onClick = {}
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // PREFERENCES
            DrawerSectionTitle("Preferences")

            DrawerGroup {
                DrawerGlassItem(
                    icon = "◐",
                    title = "Themes (♻)",
                    onClick = {}
                )

                DrawerGlassItem(
                    icon = "♧",
                    title = "Notification",
                    badge = "3",
                    onClick = {}
                )

                DrawerGlassItem(
                    icon = "⚙",
                    title = "Settings",
                    selected = selected == AppDestination.SETTINGS,
                    onClick = { onSelect(AppDestination.SETTINGS) }
                )

                DrawerGlassItem(
                    icon = "◎",
                    title = "Language (♻)",
                    onClick = {}
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // SECURITY & PRIVACY
            DrawerSectionTitle("Security & Privacy")

            DrawerGroup {
                DrawerGlassItem(
                    icon = "🛡",
                    title = "Safety Center",
                    selected = selected == AppDestination.SAFETY_CENTER,
                    onClick = {
                        onSelect(AppDestination.SAFETY_CENTER)
                    }
                )

                DrawerGlassItem(
                    icon = "▣",
                    title = "Privacy & Data",
                    selected = selected == AppDestination.PRIVACY,
                    onClick = { onSelect(AppDestination.PRIVACY) }
                )

                DrawerGlassItem(
                    icon = "⚿",
                    title = "App Permissions (♻)",
                    onClick = {}
                )
            }

            Spacer(modifier = Modifier.height(11.dp))

            // INFORMATION
            DrawerSectionTitle("Information")

            DrawerGroup {
                DrawerGlassItem(
                    icon = "♧",
                    title = "Security Tips (♻)",
                    onClick = {}
                )

                DrawerGlassItem(
                    icon = "▤",
                    title = "Threat Library (♻)",
                    onClick = {}
                )

                DrawerGlassItem(
                    icon = "✦",
                    title = "What's New (♻)",
                    badge = "New",
                    onClick = {}
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // SUPPORT
            DrawerSectionTitle("Support")

            DrawerGroup {
                DrawerGlassItem(
                    icon = "♧",
                    title = "Report a bug",
                    selected = selected == AppDestination.REPORT_BUG,
                    onClick = { onSelect(AppDestination.REPORT_BUG) }
                )
                DrawerGlassItem(
                    icon = "✉",
                    title = "Contact Us (♻)",
                    onClick = {}
                )

                DrawerGlassItem(
                    icon = "?",
                    title = "Help (♻)",
                    onClick = {}
                )

                DrawerGlassItem(
                    icon = "ⓘ",
                    title = "About BAGUARD AI",
                    selected = selected == AppDestination.ABOUT,
                    onClick = { onSelect(AppDestination.ABOUT) }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // BRAND FOOTER
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(glassBackground)
                    .border(
                        1.dp,
                        Color.White.copy(alpha = 0.20f),
                        RoundedCornerShape(18.dp)
                    )
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "BUILT BY",
                    color = Color(0xFFB9A9F5),
                    fontSize = 6.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.5.sp
                )

                Text(
                    text = "BAPPARIS DEV",
                    color = Color.White,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )

                Text(
                    text = "AI that investigates suspicious emails, links and texts, and detects Android security issues.",
                    color = Color(0xFFD6CCF5),
                    fontSize = 7.sp,
                    lineHeight = 10.sp
                )

                Surface(
                    shape = RoundedCornerShape(40.dp),
                    color = Color(0xFFA78BFA).copy(alpha = 0.22f)
                ) {
                    Text(
                        text = "Powered by Meta",
                        color = Color(0xFFE4DAFF),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 3.dp
                        )
                    )
                }

                Text(
                    text = "Intelligence Protection For Everyday Digital Life.",
                    color = Color(0xFFC9BDF2),
                    fontSize = 10.sp,
                    fontStyle = FontStyle.Italic,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
fun DrawerSectionTitle(title: String) {
    Text(
        text = title.uppercase(),
        color = Color(0xFFB9A9F5),
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.5.sp,
        modifier = Modifier.padding(
            start = 8.dp,
            top = 2.dp,
            bottom = 6.dp
        )
    )
}

@Composable
fun DrawerGroup(
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.06f))
            .border(
                1.dp,
                Color.White.copy(alpha = 0.12f),
                RoundedCornerShape(20.dp)
            )
            .padding(4.dp),
        content = content
    )
}

@Composable
fun DrawerGlassItem(
    icon: String,
    title: String,
    selected: Boolean = false,
    badge: String? = null,
    onClick: () -> Unit
) {
    val itemShape = RoundedCornerShape(14.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(itemShape)
            .background(
                if (selected) {
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF8B5CF6).copy(alpha = 0.35f),
                            Color(0xFF5B21B6).copy(alpha = 0.18f)
                        )
                    )
                } else {
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Transparent
                        )
                    )
                }
            )
            .clickable { onClick() }
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            color = if (selected) {
                Color.White
            } else {
                Color(0xFFC4B5FD)
            },
            fontSize = 17.sp
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = title,
            color = if (selected) {
                Color.White
            } else {
                Color(0xFFEDE7FF)
            },
            fontSize = 11.sp,
            fontWeight = if (selected) {
                FontWeight.SemiBold
            } else {
                FontWeight.Medium
            },
            modifier = Modifier.weight(1f)
        )

        if (badge != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(40.dp))
                    .background(
                        if (badge == "New") {
                            Color(0xFFA78BFA).copy(alpha = 0.25f)
                        } else {
                            Color(0xFFF472B6).copy(alpha = 0.20f)
                        }
                    )
                    .padding(horizontal = 7.dp, vertical = 2.5.dp)
            ) {
                Text(
                    text = badge,
                    color = if (badge == "New") {
                        Color(0xFFDDD1FF)
                    } else {
                        Color(0xFFF9A8D4)
                    },
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        } else {
            Text(
                text = "›",
                color = if (selected) {
                    Color.White
                } else {
                    Color(0xFF8F82C0)
                },
                fontSize = 18.sp
            )
        }
    }
}

// ============================================================
// REUSABLE NAVIGATION ITEM
// ============================================================

@Composable
fun DrawerItem(
    destination: AppDestination,
    selected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (selected) {
        ElectricViolet.copy(alpha = 0.18f)
    } else {
        Color.Transparent
    }

    val contentColor = if (selected) SoftWhite else MutedText

    val icon = when (destination) {
        AppDestination.HOME -> "⌂"
        AppDestination.SCANNING -> "⌕"
        AppDestination.INVESTIGATE -> "⌕"
        AppDestination.RESULTS -> "⁜"
        AppDestination.PROFILE -> "👤"
        AppDestination.HISTORY -> "◷"
        AppDestination.SETTINGS -> "⚙"
        AppDestination.SAFETY_CENTER -> "✦"
        AppDestination.REPORT_BUG -> "⚑"
        AppDestination.PRIVACY -> "◇"
        AppDestination.ABOUT -> "ⓘ"
        AppDestination.LOGIN -> "👤"
        AppDestination.SIGNUP -> "👤"
    }

    val label = when (destination) {
        AppDestination.HOME -> "Dashboard"
        AppDestination.INVESTIGATE -> "Investigate"
        AppDestination.SCANNING -> "Investigation in Progress"
        AppDestination.RESULTS -> "Investigation Results"
        AppDestination.PROFILE -> "Profile"
        AppDestination.HISTORY -> "Investigation History"
        AppDestination.SETTINGS -> "Settings"
        AppDestination.SAFETY_CENTER -> "Safety Center"
        AppDestination.REPORT_BUG -> "Report a Bug"
        AppDestination.PRIVACY -> "Privacy & Data"
        AppDestination.ABOUT -> "About BAGUARD AI"
        AppDestination.LOGIN -> "login"
        AppDestination.SIGNUP -> "Signup"
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = icon,
                color = if (selected) AuroraBlue else contentColor,
                fontSize = 23.sp,
                modifier = Modifier.width(36.dp)
            )

            Text(
                text = label,
                color = contentColor,
                fontSize = 14.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )

            if (selected) {
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(AuroraBlue)
                )
            }
        }
    }
}

@Composable
fun BaguardBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.size(44.dp),
        shape = RoundedCornerShape(15.dp),
        color = CardSurface,
        border = BorderStroke(
            1.dp,
            Brush.linearGradient(
                listOf(
                    ElectricViolet.copy(alpha = 0.65f),
                    AuroraBlue.copy(alpha = 0.45f)
                )
            )
        ),
        shadowElevation = 5.dp
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "‹",
                color = SoftWhite,
                fontSize = 34.sp,
                fontWeight = FontWeight.Light
            )
        }
    }
}

@Composable
fun ReportBugScreen(
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Other") }
    var categoryExpanded by remember { mutableStateOf(false) }
    var showThanks by remember { mutableStateOf(false) }

    val categories = listOf(
        "UI / Design",
        "Investigation",
        "Performance",
        "App Crash",
        "Other"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Midnight)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            BaguardBackButton(onClick = onBack)

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    "Report a Bug",
                    color = SoftWhite,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Help us improve BAGUARD AI",
                    color = MutedText,
                    fontSize = 11.sp
                )
            }
        }

        SafetyTipCard(
            symbol = "⚑",
            title = "Found something wrong?",
            description = "Tell us what happened. Your feedback helps us make BAGUARD AI better."
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                "BUG TITLE",
                color = AuroraBlue,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Briefly describe the problem") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = SoftWhite,
                    unfocusedTextColor = SoftWhite,
                    focusedBorderColor = ElectricViolet,
                    unfocusedBorderColor = BorderColor,
                    cursorColor = AuroraBlue
                )
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                "BUG CATEGORY",
                color = AuroraBlue,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )

            Box {
                OutlinedButton(
                    onClick = { categoryExpanded = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        category,
                        color = SoftWhite
                    )
                }

                DropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    categories.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                category = item
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                "DESCRIPTION",
                color = AuroraBlue,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                placeholder = {
                    Text("Explain what happened and how to reproduce it...")
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = SoftWhite,
                    unfocusedTextColor = SoftWhite,
                    focusedBorderColor = ElectricViolet,
                    unfocusedBorderColor = BorderColor,
                    cursorColor = AuroraBlue
                )
            )
        }

        Button(
            onClick = {
                showThanks = true
            },
            enabled = title.isNotBlank() && description.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ElectricViolet
            )
        ) {
            Text(
                "Submit Report",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            "Please don't include passwords, banking PINs or verification codes.",
            color = MutedText,
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }

    if (showThanks) {
        AlertDialog(
            onDismissRequest = { showThanks = false },
            containerColor = CardSurface,
            title = {
                Text(
                    "Thank You! ✨",
                    color = SoftWhite,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "Your bug report has been prepared. Thank you for helping improve BAGUARD AI. Report delivery will be enabled when our support service is connected.",
                    color = MutedText
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showThanks = false
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricViolet
                    )
                ) {
                    Text("Done")
                }
            }
        )
    }
}







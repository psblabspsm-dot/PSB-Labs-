package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppViewModel
import com.example.ui.AuthState
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkModePref by viewModel.isDarkMode.collectAsState()
            val useDark = when (isDarkModePref) {
                true -> true
                false -> false
                null -> androidx.compose.foundation.isSystemInDarkTheme()
            }
            MyApplicationTheme(darkTheme = useDark) {
                val authState by viewModel.authState.collectAsState()
                if (authState == AuthState.AUTHENTICATED) {
                    MainLayout(viewModel)
                } else {
                    AuthLayout(viewModel, authState)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLayout(viewModel: AppViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val snackMessage by viewModel.snackMessage.collectAsState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("app_bottom_bar"),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                val items = listOf(
                    NavigationTabItem("dashboard", Icons.Default.Dashboard, Icons.Outlined.Dashboard, "Home"),
                    NavigationTabItem("services", Icons.Default.ElectricalServices, Icons.Outlined.ElectricalServices, "Services"),
                    NavigationTabItem("market", Icons.Default.Storefront, Icons.Outlined.Storefront, "Procure"),
                    NavigationTabItem("ai", Icons.Default.Psychology, Icons.Outlined.Psychology, "AI Copilot"),
                    NavigationTabItem("more", Icons.Default.MoreHoriz, Icons.Outlined.MoreHoriz, "More")
                )

                items.forEach { item ->
                    val isSelected = currentScreen == item.screenId
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.setScreen(item.screenId) },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                fontSize = 9.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Color(0xFFFF8C00),
                            indicatorColor = Color(0xFFFF8C00),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Screen router
            Crossfade(
                targetState = currentScreen,
                animationSpec = tween(250)
            ) { screen ->
                when (screen) {
                    "dashboard" -> DashboardScreen(viewModel)
                    "services" -> ServicesScreen(viewModel)
                    "market" -> MarketplaceScreen(viewModel)
                    "ai" -> AiCopilotScreen(viewModel)
                    "more" -> MoreOptionsScreen(viewModel)
                    else -> DashboardScreen(viewModel)
                }
            }

            // High-fidelity Floating Snackbar
            AnimatedVisibility(
                visible = snackMessage != null,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
            ) {
                snackMessage?.let { msg ->
                    Surface(
                        color = Color(0xFF2E3E5C),
                        shape = RoundedCornerShape(12.dp),
                        tonalElevation = 6.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = msg,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF00C853),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

data class NavigationTabItem(
    val screenId: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val label: String
)

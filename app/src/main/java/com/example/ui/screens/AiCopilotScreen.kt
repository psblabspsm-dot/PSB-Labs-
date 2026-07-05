package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChatMessage
import com.example.ui.AiState
import com.example.ui.AppViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiCopilotScreen(viewModel: AppViewModel, modifier: Modifier = Modifier) {
    val chatHistory by viewModel.chatHistory.collectAsState()
    val aiState by viewModel.aiState.collectAsState()

    var userText by remember { mutableStateOf("") }
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Scroll to bottom when history size changes or AI state transitions
    LaunchedEffect(chatHistory.size, aiState) {
        if (chatHistory.isNotEmpty()) {
            lazyListState.animateScrollToItem(chatHistory.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Chat Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF8C00)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Psychology, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Surya Core AI Copilot",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(modifier = Modifier.size(6.dp), shape = CircleShape, color = Color(0xFF00C853)) {}
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "gemini-3.1-pro-preview • HIGH Thinking",
                            fontSize = 10.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            IconButton(onClick = { viewModel.clearChat() }) {
                Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Clear thread", tint = Color.Gray)
            }
        }

        Divider()

        // Chat List
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (chatHistory.isEmpty() && aiState is AiState.Idle) {
                // Empty State suggestions
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFFFF8C00),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Surya High-Reasoning Companion",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Text(
                        text = "I use Google's advanced model with high thinking levels to process complex FinTech and credit inquiries. Select a starter prompt:",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                    )

                    val prompts = listOf(
                        "Analyze B2B commission structure for ₹25,000 DMT transaction",
                        "Project tax & CGST/SGST liability on ₹4,500 Micro ATM orders",
                        "Explain B2B Credit Line reducing interest rates vs regular bank funding",
                        "Draft a 12-month kiosk growth roadmap for a newly onboarded retailer"
                    )

                    prompts.forEach { prompt ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    viewModel.sendAiMessage(prompt)
                                },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Lightbulb,
                                    contentDescription = null,
                                    tint = Color(0xFFFFB300),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = prompt,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(chatHistory) { msg ->
                        ChatMessageRow(msg)
                    }

                    // Simulated thinking visual block
                    if (aiState is AiState.Thinking) {
                        item {
                            val statusText = (aiState as AiState.Thinking).process
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 32.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDE7)),
                                border = BorderStroke(1.dp, Color(0xFFFFF59D))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            strokeWidth = 2.dp,
                                            color = Color(0xFFFF8C00)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = "Surya Reasoning Pipeline Active (Thinking Level: HIGH)...",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFF57F17)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = statusText,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color.DarkGray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Chat Input Panel
        Surface(
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = userText,
                    onValueChange = { userText = it },
                    placeholder = { Text("Ask Surya AI about credit lines, margins...") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ai_chat_input"),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF8C00),
                        unfocusedBorderColor = Color.Gray
                    ),
                    maxLines = 3
                )
                Spacer(modifier = Modifier.width(8.dp))
                FloatingActionButton(
                    onClick = {
                        if (userText.isNotBlank()) {
                            viewModel.sendAiMessage(userText)
                            userText = ""
                        }
                    },
                    containerColor = Color(0xFFFF8C00),
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("send_ai_button")
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Send prompt", modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun ChatMessageRow(msg: ChatMessage) {
    val isUser = msg.sender == "USER"
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF8C00))
                    .align(Alignment.Top),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Psychology, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier.weight(1f, fill = false)
        ) {
            // If AI, and has thinking process, show it as an expandable box
            if (!isUser && msg.thinkingProcess != null) {
                var expandedReasoning by remember { mutableStateOf(false) }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedReasoning = !expandedReasoning },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Psychology, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("High-Level Thinking Process", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            }
                            Icon(
                                imageVector = if (expandedReasoning) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        AnimatedVisibility(visible = expandedReasoning) {
                            Text(
                                text = msg.thinkingProcess,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color.DarkGray,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }

            Surface(
                color = if (isUser) Color(0xFFFFE0B2) else MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isUser) 16.dp else 0.dp,
                    bottomEnd = if (isUser) 0.dp else 16.dp
                ),
                border = BorderStroke(1.dp, if (isUser) Color(0xFFFFB74D) else MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = msg.content,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
            }
        }
    }
}

package com.example.parcial2pc.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.parcial2pc.models.Goal
import com.example.parcial2pc.models.states.GoalState
import com.example.parcial2pc.viewmodels.GoalViewModel

private val AppGreen = Color(0xFF1DB954)
private val AppGreenDark = Color(0xFF158a3e)

@Composable
fun HomeScreen(
    onGoalClick: (String) -> Unit,
    onCreateGoal: () -> Unit,
    viewModel: GoalViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadGoals() }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateGoal,
                containerColor = AppGreen,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva meta")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Mis metas",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(20.dp))

            when (val state = uiState) {
                is GoalState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AppGreen)
                    }
                }
                is GoalState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("No se pudieron cargar las metas", color = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(onClick = { viewModel.loadGoals() }) { Text("Reintentar") }
                        }
                    }
                }
                is GoalState.Success -> {
                    val goals = state.goals
                    if (goals.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Aún no tienes metas. ¡Crea la primera!", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        FeaturedGoalCard(goal = goals.first(), onClick = { onGoalClick(goals.first().id) })
                        if (goals.size > 1) {
                            Spacer(modifier = Modifier.height(20.dp))
                            Text("Otras metas", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(goals.drop(1)) { goal ->
                                    GoalRowCard(goal = goal, onClick = { onGoalClick(goal.id) })
                                }
                            }
                        }
                    }
                }
                else -> Unit
            }
        }
    }
}

@Composable
private fun FeaturedGoalCard(goal: Goal, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppGreenDark)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(goal.name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Meta", fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
                    Text(formatMoney(goal.totalValue), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                // Imagen o iniciales
                Box(
                    modifier = Modifier.size(70.dp).clip(RoundedCornerShape(10.dp)).background(Color.White.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (!goal.imageUrl.isNullOrBlank()) {
                        androidx.compose.foundation.Image(
                            painter = coil.compose.rememberAsyncImagePainter(goal.imageUrl),
                            contentDescription = null,
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(goal.name.take(2).uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text("${formatMoney(goal.totalSaved)} / ${formatMoney(goal.totalValue)}", fontSize = 13.sp, color = Color.White.copy(alpha = 0.85f))
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { (goal.progressPercent / 100f).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text("${goal.progressPercent}% completado", fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
        }
    }
}

@Composable
private fun GoalRowCard(goal: Goal, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(44.dp).clip(RoundedCornerShape(8.dp)).background(AppGreen.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                if (!goal.imageUrl.isNullOrBlank()) {
                    androidx.compose.foundation.Image(
                        painter = coil.compose.rememberAsyncImagePainter(goal.imageUrl),
                        contentDescription = null,
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(goal.name.take(2).uppercase(), color = AppGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(goal.name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Text("${formatMoney(goal.totalSaved)} / ${formatMoney(goal.totalValue)}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { (goal.progressPercent / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                    color = AppGreen,
                    trackColor = AppGreen.copy(alpha = 0.2f)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text("${goal.progressPercent}%", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = AppGreen)
        }
    }
}

fun formatMoney(amount: Double): String = "$ %,.0f".format(amount).replace(",", ".")
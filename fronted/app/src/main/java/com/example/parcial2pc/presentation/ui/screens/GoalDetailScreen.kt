package com.example.parcial2pc.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.ud.riddle.models.Goal
import com.ud.riddle.models.Member
import com.ud.riddle.viewmodels.GoalViewModel

private val AppGreen = Color(0xFF1DB954)
private val AppGreenDark = Color(0xFF158a3e)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailScreen(
    goalId: String,
    onBack: () -> Unit,
    onMakePayment: (String) -> Unit,
    onViewPayments: (String) -> Unit,
    viewModel: GoalViewModel = viewModel()
) {
    val goal by viewModel.selectedGoal.collectAsState()

    LaunchedEffect(goalId) { viewModel.loadGoalDetail(goalId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(goal?.name ?: "Detalle", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (goal == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AppGreen)
            }
            return@Scaffold
        }

        val g = goal!!

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppGreenDark)
                    .padding(20.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Meta", fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
                        Text(formatMoney(g.totalValue), fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("${formatMoney(g.totalSaved)} / ${formatMoney(g.totalValue)}", fontSize = 13.sp, color = Color.White.copy(alpha = 0.85f))
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { (g.progressPercent / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = Color.White,
                            trackColor = Color.White.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${g.progressPercent}% completado", fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
                            Text("Faltan ${formatMoney(g.totalValue - g.totalSaved)}", fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    // Placeholder imagen de la meta
                    Box(
                        modifier = Modifier.size(80.dp).clip(RoundedCornerShape(10.dp)).background(Color.White.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(g.name.take(2).uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {

                // Aporte equitativo
                Text("Aporte equitativo", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Text("Cada miembro debe aportar", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(formatMoney(g.equitableContribution), fontSize = 26.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(16.dp))

                // Resumen
                Text("Resumen", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(10.dp))
                ResumenFila("Total aportado", formatMoney(g.totalSaved))
                ResumenFila("Aporte por miembro", formatMoney(g.equitableContribution))
                ResumenFila("Miembros", "${g.members.size}")
                ResumenFila("Fecha objetivo", g.targetDate.ifBlank { "Sin definir" })

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(16.dp))

                // Miembros y aportes
                Text("Miembros y aportes", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(10.dp))

                if (g.members.isEmpty()) {
                    Text("Sin miembros aún", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    g.members.forEach { member ->
                        MemberFila(member = member, equitable = g.equitableContribution)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = { onViewPayments(goalId) },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Ver aportes")
                    }
                    Button(
                        onClick = { onMakePayment(goalId) },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppGreen)
                    ) {
                        Text("Realizar aporte")
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}

@Composable
private fun ResumenFila(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun MemberFila(member: Member, equitable: Double) {

    val percent = if (equitable > 0) ((member.totalPaid / equitable) * 100).toInt().coerceAtMost(100) else 0
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(36.dp).clip(CircleShape).background(AppGreen.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(member.name.take(1).uppercase(), color = AppGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(member.name, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text("${formatMoney(member.totalPaid)}  $percent%", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { (percent / 100f).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                color = AppGreen,
                trackColor = AppGreen.copy(alpha = 0.2f)
            )
        }
    }
}
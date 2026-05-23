package com.example.parcial2pc.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.ud.riddle.models.Payment
import com.ud.riddle.viewmodels.GoalViewModel

private val AppGreen = Color(0xFF1DB954)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentsListScreen(
    goalId: String,
    onBack: () -> Unit,
    viewModel: GoalViewModel = viewModel()
) {
    // Obtiene los pagos desde el ViewModel que ya los cargó del backend
    val goal by viewModel.selectedGoal.collectAsState()

    // Lista de pagos: se filtra localmente del selectedGoal o se puede
    // exponer un StateFlow separado en el ViewModel si se prefiere
    val payments by viewModel.paymentsForGoal.collectAsState()
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(goalId) {
        viewModel.loadGoalDetail(goalId)
        // Si el ViewModel expone paymentsForGoal, úsalo. Si no, carga aquí:
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Aportes", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                        goal?.name?.let { Text(it, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                    }
                },
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
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            // Banner verde con el total aportado
            goal?.let { g ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF158a3e))
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("Total aportado", fontSize = 13.sp, color = Color.White.copy(alpha = 0.75f))
                            Text(formatMoney(g.totalSaved), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Surface(shape = RoundedCornerShape(20.dp), color = Color.White.copy(alpha = 0.2f)) {
                                Text("${g.members.size} miembros", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), fontSize = 12.sp, color = Color.White)
                            }
                        }
                    }
                }
            }

            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AppGreen)
                }
                return@Scaffold
            }

            if (payments.isEmpty()) {
                Box(Modifier.fillMaxSize().padding(horizontal = 20.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Sin aportes aún", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Los aportes registrados aparecerán aquí", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                    }
                }
                return@Scaffold
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(payments, key = { it.id }) { payment ->
                    val memberName = goal?.members?.find { it.id == payment.memberId }?.name ?: "Miembro"
                    PaymentCard(payment = payment, memberName = memberName)
                }
            }
        }
    }
}

@Composable
private fun PaymentCard(payment: Payment, memberName: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(42.dp).clip(CircleShape).background(AppGreen.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(memberName.take(1).uppercase(), color = AppGreen, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(memberName, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                if (payment.description.isNotBlank()) {
                    Text(payment.description, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = RoundedCornerShape(4.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)) {
                        Text(payment.method, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    if (payment.date.isNotBlank()) {
                        Text(payment.date.substringBefore("T"), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                    }
                }
            }
            Text(formatMoney(payment.amount), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = AppGreen)
        }
    }
}
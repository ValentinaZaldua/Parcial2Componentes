package com.example.parcial2pc.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ud.riddle.models.Payment
import com.ud.riddle.viewmodels.GoalViewModel

private val AppGreen = Color(0xFF1DB954)
private val AppGreenDark = Color(0xFF158a3e)

private val PAYMENT_METHODS = listOf("Transferencia bancaria", "Efectivo", "PSE", "Nequi", "Daviplata")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    goalId: String,
    onBack: () -> Unit,
    onPaymentConfirmed: () -> Unit,
    viewModel: GoalViewModel = viewModel()
) {
    val goal by viewModel.selectedGoal.collectAsState()

    var selectedMemberId by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var selectedMethod by remember { mutableStateOf(PAYMENT_METHODS.first()) }
    var description by remember { mutableStateOf("") }
    var methodExpanded by remember { mutableStateOf(false) }
    var memberExpanded by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }
    var memberError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(goalId) { viewModel.loadGoalDetail(goalId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Realizar aporte", fontWeight = FontWeight.SemiBold) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Tarjeta resumen de la meta
            goal?.let { g ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(52.dp).clip(RoundedCornerShape(8.dp)).background(AppGreenDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(g.name.take(2).uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(g.name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                            Text("Meta", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(formatMoney(g.totalValue), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Text("${g.progressPercent}% completado", fontSize = 12.sp, color = AppGreen)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Selector de miembro
            Text("¿Quién realiza el aporte?", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))
            val members = goal?.members ?: emptyList()
            ExposedDropdownMenuBox(expanded = memberExpanded, onExpandedChange = { memberExpanded = it }) {
                OutlinedTextField(
                    value = members.find { it.id == selectedMemberId }?.name ?: "Seleccionar miembro",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(memberExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(10.dp),
                    isError = memberError,
                    supportingText = { if (memberError) Text("Selecciona quién realiza el aporte") }
                )
                ExposedDropdownMenu(expanded = memberExpanded, onDismissRequest = { memberExpanded = false }) {
                    members.forEach { member ->
                        DropdownMenuItem(
                            text = { Text(member.name) },
                            onClick = { selectedMemberId = member.id; memberExpanded = false; memberError = false }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Monto
            Text("Mi aporte", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it.filter { c -> c.isDigit() }; amountError = false },
                prefix = { Text("$ ", fontWeight = FontWeight.Medium) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = amountError,
                supportingText = { if (amountError) Text("Ingresa un monto válido mayor a 0") },
                textStyle = LocalTextStyle.current.copy(fontSize = 22.sp, fontWeight = FontWeight.SemiBold),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Método de pago
            Text("Método de pago", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))
            ExposedDropdownMenuBox(expanded = methodExpanded, onExpandedChange = { methodExpanded = it }) {
                OutlinedTextField(
                    value = selectedMethod,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(methodExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(10.dp)
                )
                ExposedDropdownMenu(expanded = methodExpanded, onDismissRequest = { methodExpanded = false }) {
                    PAYMENT_METHODS.forEach { method ->
                        DropdownMenuItem(text = { Text(method) }, onClick = { selectedMethod = method; methodExpanded = false })
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Descripción opcional
            Text("Descripción (opcional)", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("Ej. Aporte mensual") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val parsed = amount.toDoubleOrNull() ?: 0.0
                    memberError = selectedMemberId.isBlank()
                    amountError = parsed <= 0.0
                    if (!memberError && !amountError) {
                        isLoading = true
                        val payment = Payment(
                            memberId = selectedMemberId,
                            goalId = goalId,
                            amount = parsed,
                            method = selectedMethod,
                            description = description.trim()
                        )
                        viewModel.registerPayment(payment)
                        // La navegación la maneja quien llama a esta screen
                        // observando viewModel.paymentState si se implementa ese estado
                        onPaymentConfirmed()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppGreen),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                } else {
                    Text("Confirmar aporte", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
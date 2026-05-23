package com.example.parcial2pc.presentation.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ud.riddle.models.Goal
import com.ud.riddle.models.Member
import com.ud.riddle.models.states.GoalState
import com.ud.riddle.viewmodels.GoalViewModel

private val AppGreen = Color(0xFF1DB954)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGoalScreen(
    onBack: () -> Unit,
    onGoalCreated: () -> Unit,
    viewModel: GoalViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var totalValue by remember { mutableStateOf("") }
    var targetDate by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var memberNames by remember { mutableStateOf(listOf<String>()) }
    var newMemberName by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }
    var valueError by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is GoalState.Success) onGoalCreated()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear nueva meta", fontWeight = FontWeight.SemiBold) },
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
            Spacer(modifier = Modifier.height(16.dp))

            // Nombre
            Text("Nombre de la meta", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it; nameError = false },
                placeholder = { Text("Ej. Fondo para Carro") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                isError = nameError,
                supportingText = { if (nameError) Text("El nombre es obligatorio") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Descripción
            Text("Producto / Descripción", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("Ej. Toyota Corolla 2024") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Valor total
            Text("Valor total de la meta", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = totalValue,
                onValueChange = { totalValue = it.filter { c -> c.isDigit() }; valueError = false },
                placeholder = { Text("25000000") },
                prefix = { Text("$ ") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = valueError,
                supportingText = { if (valueError) Text("Ingresa un valor válido mayor a 0") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Fecha objetivo
            Text("Fecha objetivo", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = targetDate,
                onValueChange = { targetDate = it },
                placeholder = { Text("2025-08-15") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Imagen opcional
            Text("Imagen (opcional)", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                placeholder = { Text("https://...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(16.dp))

            // Miembros
            Text("Miembros", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = newMemberName,
                    onValueChange = { newMemberName = it },
                    placeholder = { Text("Nombre del miembro") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )
                IconButton(
                    onClick = {
                        val t = newMemberName.trim()
                        if (t.isNotBlank() && !memberNames.contains(t)) {
                            memberNames = memberNames + t
                            newMemberName = ""
                        }
                    },
                    modifier = Modifier.size(48.dp).border(1.dp, AppGreen, RoundedCornerShape(10.dp))
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar", tint = AppGreen)
                }
            }

            if (memberNames.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                    memberNames.forEach { m ->
                        InputChip(
                            selected = false,
                            onClick = { memberNames = memberNames.filter { it != m } },
                            label = { Text(m, fontSize = 13.sp) },
                            colors = InputChipDefaults.inputChipColors(containerColor = AppGreen.copy(alpha = 0.1f)),
                            border = InputChipDefaults.inputChipBorder(enabled = true, selected = false, borderColor = AppGreen.copy(alpha = 0.4f))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {
                    val parsed = totalValue.toDoubleOrNull() ?: 0.0
                    nameError = name.isBlank()
                    valueError = parsed <= 0.0
                    if (!nameError && !valueError) {
                        val goal = Goal(
                            name = name.trim(),
                            description = description.trim(),
                            totalValue = parsed,
                            targetDate = targetDate.trim(),
                            imageUrl = imageUrl.trim().ifBlank { null }
                        )
                        viewModel.createGoal(goal)
                        // Agrega miembros si los hay (requiere el id de la meta creada,
                        // idealmente se maneja en el ViewModel al recibir la respuesta del POST)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppGreen),
                enabled = uiState !is GoalState.Loading
            ) {
                if (uiState is GoalState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                } else {
                    Text("Crear meta", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BigNumberText
import com.example.ui.components.MathCard
import com.example.ui.components.MathInstruction
import com.example.ui.components.VibrantButton
import kotlin.random.Random

@Composable
fun NumberBondsScreen() {
    var dividend by remember { mutableStateOf(91) }
    var divisor by remember { mutableStateOf(7) }
    var part1Input by remember { mutableStateOf("") }
    var part2Input by remember { mutableStateOf("") }
    var step by remember { mutableStateOf(0) } // 0: Input parts, 1: Calculate sub-divisions, 2: Final sum
    var result1Input by remember { mutableStateOf("") }
    var result2Input by remember { mutableStateOf("") }
    var finalResultInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val part1 = part1Input.toIntOrNull() ?: 0
    val part2 = part2Input.toIntOrNull() ?: 0

    fun generateNewProblem() {
        val divisors = listOf(3, 4, 6, 7, 8, 9)
        val selectedDivisor = divisors.random()
        val p1 = selectedDivisor * (Random.nextInt(5, 12))
        val p2 = selectedDivisor * (Random.nextInt(2, 6))
        dividend = p1 + p2
        divisor = selectedDivisor
        part1Input = ""
        part2Input = ""
        step = 0
        result1Input = ""
        result2Input = ""
        finalResultInput = ""
        errorMessage = null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MathCard {
            Text(
                "PROBLEMA ACTUAL",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            BigNumberText(text = "$dividend ÷ $divisor")
        }

        Spacer(Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(Color.White.copy(alpha = 0.5f))
                .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(32.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Top Circle (Main Dividend)
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape,
                    modifier = Modifier.size(80.dp),
                    shadowElevation = 8.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("$dividend", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
                    }
                }

                Spacer(Modifier.height(40.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    BondInputCircle(
                        value = part1Input,
                        onValueChange = { part1Input = it },
                        enabled = step == 0,
                        label = "Parte 1",
                        isCorrect = step > 0
                    )
                    BondInputCircle(
                        value = part2Input,
                        onValueChange = { part2Input = it },
                        enabled = step == 0,
                        label = "Parte 2",
                        isCorrect = step > 0
                    )
                }
            }
            
            // Hint box
            if (errorMessage != null || step == 0) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    color = Color.White.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("💡", fontSize = 20.sp)
                        Text(
                            errorMessage ?: "Busca números que sean fáciles de dividir por $divisor.",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (errorMessage != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        if (step == 0) {
            VibrantButton(
                onClick = {
                    val p1Val = part1Input.toIntOrNull()
                    val p2Val = part2Input.toIntOrNull()
                    if (p1Val == null || p2Val == null) {
                        errorMessage = "Por favor ingresa números válidos"
                    } else if (p1Val + p2Val != dividend) {
                        errorMessage = "La suma de las partes debe ser $dividend"
                    } else if (p1Val % divisor != 0 || p2Val % divisor != 0) {
                        errorMessage = "Las partes no se dividen exactamente por $divisor"
                    } else {
                        errorMessage = null
                        step = 1
                    }
                },
                modifier = Modifier.padding(top = 24.dp)
            ) {
                Text("SIGUIENTE PASO", fontWeight = FontWeight.Black)
            }
        }

        AnimatedVisibility(visible = step >= 1) {
            Column(
                modifier = Modifier.padding(top = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MathInstruction(text = "¡Muy bien! Ahora resuelve las divisiones:")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CalculationCard(
                        expression = "$part1 ÷ $divisor =",
                        value = result1Input,
                        onValueChange = { result1Input = it },
                        enabled = step == 1,
                        isCorrect = result1Input.toIntOrNull() == part1 / divisor,
                        modifier = Modifier.weight(1f)
                    )
                    CalculationCard(
                        expression = "$part2 ÷ $divisor =",
                        value = result2Input,
                        onValueChange = { result2Input = it },
                        enabled = step == 1,
                        isCorrect = result2Input.toIntOrNull() == part2 / divisor,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (step == 1) {
                    VibrantButton(
                        onClick = {
                            if (result1Input.toIntOrNull() == part1 / divisor && 
                                result2Input.toIntOrNull() == part2 / divisor) {
                                step = 2
                            }
                        },
                        modifier = Modifier.padding(top = 16.dp),
                        enabled = result1Input.isNotEmpty() && result2Input.isNotEmpty()
                    ) {
                        Text("ÚLTIMO PASO", fontWeight = FontWeight.Black)
                    }
                }
            }
        }

        AnimatedVisibility(visible = step >= 2) {
            Column(
                modifier = Modifier.padding(top = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MathInstruction(text = "Finalmente, suma los resultados:")
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val r1 = part1 / divisor
                    val r2 = part2 / divisor
                    Text("$r1 + $r2 =", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                    BondInput(
                        value = finalResultInput,
                        onValueChange = { finalResultInput = it },
                        enabled = step == 2,
                        label = "?"
                    )
                }

                if (step == 2) {
                    if (finalResultInput.toIntOrNull() == dividend / divisor) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "¡Respuesta: ${dividend / divisor}! \uD83C\uDF89",
                                fontSize = 32.sp,
                                color = MaterialTheme.colorScheme.tertiary,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                            Text(
                                text = "Tarea completada. Mini aplauso interno. \uD83D\uDC4F",
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            
                            VibrantButton(
                                onClick = { generateNewProblem() },
                                containerColor = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.padding(top = 24.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("NUEVO PROBLEMA", fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }
        }
        
        Spacer(Modifier.height(48.dp))
    }
}

@Composable
fun BondInputCircle(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    label: String,
    isCorrect: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Surface(
            modifier = Modifier.size(72.dp),
            shape = CircleShape,
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(
                width = 3.dp,
                color = if (isCorrect) MaterialTheme.colorScheme.primary else if (enabled) MaterialTheme.colorScheme.outlineVariant else Color.Transparent
            ),
            shadowElevation = if (enabled) 4.dp else 0.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                BasicTextField(
                    value = value,
                    onValueChange = { if (it.length <= 3) onValueChange(it) },
                    enabled = enabled,
                    textStyle = TextStyle(
                        textAlign = TextAlign.Center,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (value.isEmpty() && enabled) {
                    Text("?", color = MaterialTheme.colorScheme.outline, fontSize = 22.sp)
                }
            }
        }
        Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, color = if (isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
    }
}

@Composable
fun BondInput(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    label: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = { if (it.length <= 3) onValueChange(it) },
        enabled = enabled,
        modifier = Modifier.width(90.dp),
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    )
}

@Composable
fun CalculationCard(
    expression: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    isCorrect: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (isCorrect && !enabled) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(expression, fontSize = 18.sp, fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                enabled = enabled,
                modifier = Modifier
                    .width(70.dp)
                    .padding(top = 8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
            )
            if (isCorrect && !enabled) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}

package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BigNumberText
import com.example.ui.components.MathCard
import com.example.ui.components.MathInstruction
import com.example.ui.components.VibrantButton
import kotlin.random.Random

@Composable
fun VisualBlocksScreen() {
    var dividend by remember { mutableStateOf(42) }
    var divisor by remember { mutableStateOf(3) }
    
    // Game State
    var tensLeft by remember { mutableStateOf(4) }
    var onesLeft by remember { mutableStateOf(2) }
    
    // Distributed blocks in 3 groups
    var tensInGroups = remember { mutableStateListOf(0, 0, 0) }
    var onesInGroups = remember { mutableStateListOf(0, 0, 0) }
    
    var step by remember { mutableStateOf(0) } // 0: Distribute tens, 1: Regroup & Distribute ones, 2: Done

    fun generateNewProblem() {
        val divisors = listOf(2, 3, 4)
        divisor = divisors.random()
        val tens = Random.nextInt(divisor, 6)
        val ones = (Random.nextInt(2, 6)) * divisor // Ensure divisible after regrouping
        dividend = tens * 10 + ones
        tensLeft = tens
        onesLeft = ones
        tensInGroups.clear()
        repeat(divisor) { tensInGroups.add(0) }
        onesInGroups.clear()
        repeat(divisor) { onesInGroups.add(0) }
        step = 0
    }

    LaunchedEffect(divisor) {
        tensInGroups.clear()
        repeat(divisor) { tensInGroups.add(0) }
        onesInGroups.clear()
        repeat(divisor) { onesInGroups.add(0) }
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
        
        // Inventory
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = Color.White.copy(alpha = 0.5f),
            border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("TIENES PARA REPARTIR", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    InventoryItem(count = tensLeft, label = "Decenas", color = MaterialTheme.colorScheme.primary)
                    InventoryItem(count = onesLeft, label = "Unidades", color = MaterialTheme.colorScheme.secondary)
                }
            }
        }

        MathInstruction(
            text = when(step) {
                0 -> "Paso 1: Reparte las decenas equitativamente entre los $divisor grupos."
                1 -> "Paso 2: ¡Reagrupamos! Convierte las decenas sobrantes en unidades y reparte las $onesLeft unidades."
                else -> "¡Excelente! Cada grupo tiene lo mismo."
            }
        )

        // Groups
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            for (i in 0 until divisor) {
                GroupArea(
                    index = i,
                    tensCount = tensInGroups.getOrElse(i) { 0 },
                    onesCount = onesInGroups.getOrElse(i) { 0 },
                    canAddTens = step == 0 && tensLeft > 0,
                    canAddOnes = step == 1 && onesLeft > 0,
                    onAddBlock = { type ->
                        if (type == "ten") {
                            tensInGroups[i]++
                            tensLeft--
                        } else {
                            onesInGroups[i]++
                            onesLeft--
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (step == 0 && tensLeft < divisor) {
            VibrantButton(
                onClick = {
                    onesLeft += tensLeft * 10
                    tensLeft = 0
                    step = 1
                },
                modifier = Modifier.padding(top = 24.dp)
            ) {
                Text("REPARTIR UNIDADES", fontWeight = FontWeight.Black)
            }
        }

        if (step == 1 && onesLeft == 0) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 24.dp)) {
                Text(
                    text = "¡Cada grupo tiene: ${tensInGroups[0] * 10 + onesInGroups[0]}! \uD83C\uDF8A",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.tertiary
                )
                VibrantButton(
                    onClick = { generateNewProblem() },
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("NUEVO PROBLEMA", fontWeight = FontWeight.Black)
                }
            }
        }

        Spacer(Modifier.height(48.dp))
    }
}

@Composable
fun InventoryItem(count: Int, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = RoundedCornerShape(12.dp),
            color = color,
            shadowElevation = 4.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("$count", color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
            }
        }
        Text(label.uppercase(), style = MaterialTheme.typography.labelSmall)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GroupArea(
    index: Int,
    tensCount: Int,
    onesCount: Int,
    canAddTens: Boolean,
    canAddOnes: Boolean,
    onAddBlock: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .heightIn(min = 160.dp)
            .clickable(enabled = canAddTens || canAddOnes) {
                if (canAddTens) onAddBlock("ten")
                else if (canAddOnes) onAddBlock("one")
            },
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(
            width = 2.dp,
            color = if (canAddTens || canAddOnes) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        ),
        shadowElevation = if (canAddTens || canAddOnes) 6.dp else 2.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("GRUPO ${index + 1}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black)
            
            Spacer(Modifier.height(12.dp))
            
            // Tens
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(tensCount) {
                    Box(modifier = Modifier.size(width = 40.dp, height = 10.dp).clip(RoundedCornerShape(2.dp)).background(MaterialTheme.colorScheme.primary))
                }
            }
            
            Spacer(Modifier.height(12.dp))
            
            // Ones (Grid-like)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                maxItemsInEachRow = 5
            ) {
                repeat(onesCount) {
                    Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(2.dp)).background(MaterialTheme.colorScheme.secondary))
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    maxItemsInEachRow: Int = Int.MAX_VALUE,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = verticalArrangement,
        maxItemsInEachRow = maxItemsInEachRow
    ) {
        content()
    }
}

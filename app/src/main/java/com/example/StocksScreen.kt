package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.BorderStroke

import androidx.lifecycle.compose.collectAsStateWithLifecycle

// StocksScreen as a copy/reimagined version of DashboardScreen
@Composable
fun StocksScreen(modifier: Modifier = Modifier) {
    var isScanning by remember { mutableStateOf(false) }
    var allResults by remember { mutableStateOf<List<ScanResult>>(emptyList()) }
    val liveQuotes by ShoonyaWebSocketManager.liveQuotes.collectAsStateWithLifecycle()
    
    // Combine index and commodity results
    val dashboardResults = remember(liveQuotes, allResults) {
        val indexResults = liveQuotes.values.filter { 
            it.symbol == "NIFTY" || it.symbol == "BANKNIFTY" || it.symbol == "SENSEX" 
        }.map {
            ScanResult(
                ticker = it.symbol,
                name = it.symbol,
                price = it.price,
                strategies = "Live",
                score = 80,
                reasons = "Live feed",
                signalStrength = "NEUTRAL",
                stopLoss = 0.0,
                target1 = 0.0,
                target2 = 0.0,
                assetType = "INDEX"
            )
        }
        (indexResults + allResults).sortedByDescending { it.score }
    }

    LaunchedEffect(Unit) {
        isScanning = true
        IndexScanner.scanIndices()
        allResults = StockScanner.scanMultiple()
        isScanning = false
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF1F3F6))
    ) {
        // Unified Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Breakout Dashboard", modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }

        if (isScanning) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(dashboardResults) { res ->
                    IndexCard(res = res)
                }
            }
        }
    }
}

@Composable
fun IndexCard(res: ScanResult) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(res.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("₹${String.format("%.2f", res.price)}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(res.signalStrength, fontSize = 10.sp, color = Color.Gray)
                Text(res.strategies, fontSize = 10.sp, color = Color.Gray)
            }
        }
    }
}
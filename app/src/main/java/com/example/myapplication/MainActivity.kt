package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.viewmodel.MarketViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MarketScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun MarketScreen(
    modifier: Modifier = Modifier,
    viewModel: MarketViewModel = viewModel()
) {
    val marketStatus by viewModel.marketStatus.collectAsState()
    val latestQuote by viewModel.latestQuote.collectAsState()
    val secretsLoaded = SecretsConfig.areSecretsLoaded()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Shoonya Live Market",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (marketStatus.contains("Open")) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Market Status: $marketStatus", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = if (marketStatus.contains("Open")) "Trading Allowed" else "Trading Restricted",
                    color = if (marketStatus.contains("Open")) Color(0xFF2E7D32) else Color(0xFFC62828)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (secretsLoaded) {
            Button(onClick = { viewModel.startStreaming() }) {
                Text("Connect WebSocket")
            }
        } else {
            Text(
                text = "Secrets NOT Loaded. Please add them to local.properties",
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        latestQuote?.let { quote ->
            Text(text = "Symbol Token: ${quote.tk ?: "N/A"}")
            Text(
                text = "Price: ₹${quote.lp ?: "0.00"}",
                style = MaterialTheme.typography.displaySmall
            )
            Text(text = "Change: ${quote.pc ?: "0"}%")
        } ?: Text(text = "Waiting for live data...")
        
        Spacer(modifier = Modifier.height(24.dp))
        
        OutlinedButton(onClick = { viewModel.subscribeToToken("22") }) {
            Text("Subscribe to NSE Token 22 (e.g. NIFTY)")
        }
    }
}

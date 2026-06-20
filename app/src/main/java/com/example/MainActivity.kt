package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ChronosferaScreen(
                        viewModel = mainViewModel,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(MaterialTheme.colorScheme.background)
                    )
                }
            }
        }
    }
}

@Composable
fun ChronosferaScreen(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val index by viewModel.dissipativeLoadIndex.collectAsState()
    val kp by viewModel.kpIndex.collectAsState()
    val magValues by viewModel.magneticFieldValues.collectAsState()

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        
        Text(
            text = "CHRONOSFERA",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp
            ),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 16.dp)
        )
        
        Text(
            text = "Monitor de Carga Disipativa Local",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        Divider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 1.dp)

        IndexDisplay(index = index)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Índice Kp (NOAA)",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = kp ?: "0",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Flujo Magnético",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "X: ${"%.1f".format(magValues[0])}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Y: ${"%.1f".format(magValues[1])}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Z: ${"%.1f".format(magValues[2])}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Estado del Sistema",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                val stateText = when {
                    index < 25 -> "Equilibrio Entrópico. El entorno permite una disipación eficiente y rítmica."
                    index < 50 -> "Flujo Disipativo Activo. Variaciones y excitabilidad ambiental."
                    else -> "Perturbación. Ruido termodinámico y estrés electromagnético detectado."
                }
                
                Text(
                    text = stateText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun IndexDisplay(index: Float) {
    val statusColor = when {
        index < 25 -> MaterialTheme.colorScheme.secondary // Green
        index < 50 -> MaterialTheme.colorScheme.tertiary // Orange/Yellow
        else -> Color(0xFFEF4444) // Red
    }

    Box(
        modifier = Modifier
            .size(240.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = statusColor.copy(alpha = 0.2f),
                radius = size.width / 2
            )
            drawCircle(
                color = statusColor,
                radius = size.width / 2,
                style = Stroke(width = 8.dp.toPx())
            )
            // Draw an orbital electron or pulse based on index
            val pulseRadius = size.width / 4 + (index % 10) * 5
            drawCircle(
                color = statusColor.copy(alpha = 0.5f),
                radius = pulseRadius,
                style = Stroke(width = 2.dp.toPx())
            )
        }
        
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "%.1f".format(index),
                style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Bold),
                color = statusColor
            )
            Text(
                text = "Índice de Carga",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }
    }
}


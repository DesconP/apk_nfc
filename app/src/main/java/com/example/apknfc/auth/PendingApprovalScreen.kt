package com.example.apknfc.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PendingApprovalScreen(code: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Aguardando aprovação",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "Mostre este código ao responsável para liberar seu acesso:",
            modifier = Modifier.padding(top = 16.dp, bottom = 24.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Text(
            text = code,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 8.sp
        )

        CircularProgressIndicator(modifier = Modifier.padding(top = 32.dp))

        Text(
            text = "Verificando aprovação automaticamente...",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

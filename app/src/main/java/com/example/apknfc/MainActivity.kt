package com.example.apknfc

import android.nfc.NfcAdapter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.apknfc.auth.AuthUiState
import com.example.apknfc.auth.AuthViewModel
import com.example.apknfc.auth.DeviceIdProvider
import com.example.apknfc.auth.LoginScreen
import com.example.apknfc.auth.PendingApprovalScreen

class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val deviceId = DeviceIdProvider.getOrCreate(this)
        val nfcDisponivel = NfcAdapter.getDefaultAdapter(this) != null
        val nfcAtivado = NfcAdapter.getDefaultAdapter(this)?.isEnabled ?: false

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val uiState by authViewModel.state.collectAsState()

                    when (val state = uiState) {
                        is AuthUiState.LoggedOut -> LoginScreen(
                            isLoading = false,
                            errorMessage = null,
                            onLoginClick = { user, pass ->
                                authViewModel.login(user, pass, deviceId)
                            }
                        )

                        is AuthUiState.Loading -> LoginScreen(
                            isLoading = true,
                            errorMessage = null,
                            onLoginClick = { _, _ -> }
                        )

                        is AuthUiState.Error -> LoginScreen(
                            isLoading = false,
                            errorMessage = state.message,
                            onLoginClick = { user, pass ->
                                authViewModel.login(user, pass, deviceId)
                            }
                        )

                        is AuthUiState.PendingApproval -> PendingApprovalScreen(code = state.code)

                        is AuthUiState.LoggedIn -> TelaStatus(
                            nfcDisponivel = nfcDisponivel,
                            nfcAtivado = nfcAtivado,
                            userId = state.userId
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TelaStatus(nfcDisponivel: Boolean, nfcAtivado: Boolean, userId: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "App de Presença NFC", style = MaterialTheme.typography.headlineSmall)

        Text(
            text = "NFC do aparelho: " + if (nfcDisponivel) "disponível" else "não disponível",
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = "NFC ativado: " + if (nfcAtivado) "sim" else "não (ative nas configurações)",
            modifier = Modifier.padding(top = 8.dp)
        )

        Text(
            text = "ID que será transmitido ao totem:",
            modifier = Modifier.padding(top = 24.dp)
        )
        Text(text = userId, style = MaterialTheme.typography.headlineMedium)

        Text(
            text = "Aproxime o celular do leitor NFC para registrar presença.",
            modifier = Modifier.padding(top = 24.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
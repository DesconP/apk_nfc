package com.example.apknfc.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Os possíveis estados de tela do app, do ponto de vista de autenticação.
 * A MainActivity decide o que mostrar com base nesse estado.
 */
sealed class AuthUiState {
    object LoggedOut : AuthUiState()
    object Loading : AuthUiState()
    data class PendingApproval(val code: String) : AuthUiState()
    data class LoggedIn(val userId: String) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _state = MutableStateFlow<AuthUiState>(AuthUiState.LoggedOut)
    val state: StateFlow<AuthUiState> = _state

    // TODO: persistir token/userId (ex: DataStore) para não precisar logar
    // toda vez que o app abrir — por enquanto fica só em memória.
    private var sessionToken: String? = null

    fun login(username: String, password: String, deviceId: String) {
        _state.value = AuthUiState.Loading
        viewModelScope.launch {
            when (val result = repository.login(username, password, deviceId)) {
                is LoginResult.Approved -> {
                    sessionToken = result.token
                    _state.value = AuthUiState.LoggedIn(result.userId)
                }
                is LoginResult.PendingApproval -> {
                    _state.value = AuthUiState.PendingApproval(result.code)
                    pollStatus(result.requestId)
                }
                is LoginResult.Error -> {
                    _state.value = AuthUiState.Error(result.message)
                }
            }
        }
    }

    /**
     * Enquanto a tela de "aguardando aprovação" estiver visível,
     * pergunta ao backend a cada 5 segundos se já foi aprovado.
     * Para quando aprovar, rejeitar, expirar ou der erro.
     */
    private fun pollStatus(requestId: String) {
        viewModelScope.launch {
            while (_state.value is AuthUiState.PendingApproval) {
                delay(5000)
                when (val result = repository.checkStatus(requestId)) {
                    is StatusResult.Approved -> {
                        sessionToken = result.token
                        _state.value = AuthUiState.LoggedIn(result.userId)
                    }
                    StatusResult.Rejected -> {
                        _state.value = AuthUiState.Error("Pedido de acesso rejeitado pelo responsável.")
                    }
                    StatusResult.Expired -> {
                        _state.value = AuthUiState.Error("O código expirou. Tente fazer login novamente.")
                    }
                    is StatusResult.Error -> {
                        _state.value = AuthUiState.Error(result.message)
                    }
                    StatusResult.StillPending -> {
                        // continua esperando, não muda nada
                    }
                }
            }
        }
    }

    fun resetToLoggedOut() {
        _state.value = AuthUiState.LoggedOut
    }
}

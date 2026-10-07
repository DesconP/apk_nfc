package com.example.apknfc.auth

/**
 * Modelos de dados trocados com o backend durante o login
 * e o processo de aprovação do dispositivo.
 */

data class LoginRequest(
    val username: String,
    val password: String,
    val deviceId: String
)

/**
 * O backend pode responder de duas formas ao login:
 * - já aprovado: devolve token + userId direto
 * - pendente: devolve um requestId pra gente consultar o status depois,
 *   além do código que será mostrado na tela pro usuário mostrar ao responsável
 */
data class LoginResponse(
    val status: String,      // "approved" ou "pending"
    val token: String? = null,
    val userId: String? = null,
    val requestId: String? = null,
    val code: String? = null,
    val expiresAt: String? = null
)

data class StatusResponse(
    val status: String,      // "pending", "approved", "rejected" ou "expired"
    val token: String? = null,
    val userId: String? = null
)

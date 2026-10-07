package com.example.apknfc.auth

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Contrato esperado do backend. Quem implementar o backend
 * (você ou outra pessoa da equipe) precisa seguir essas rotas
 * e formatos de resposta — ou a gente ajusta aqui depois.
 */
interface AuthApi {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("auth/status/{requestId}")
    suspend fun checkStatus(@Path("requestId") requestId: String): StatusResponse
}

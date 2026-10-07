package com.example.apknfc.auth

import android.content.Context
import java.util.UUID

/**
 * Gera um UUID na primeira execução do app e o mantém salvo
 * em SharedPreferences, para identificar este celular de forma
 * estável nas próximas vezes — é esse valor que vincula o
 * usuário a ESTE aparelho específico.
 */
object DeviceIdProvider {

    private const val PREFS_NAME = "presenca_prefs"
    private const val KEY_DEVICE_ID = "device_id"

    fun getOrCreate(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val existing = prefs.getString(KEY_DEVICE_ID, null)
        if (existing != null) return existing

        val newId = UUID.randomUUID().toString()
        prefs.edit().putString(KEY_DEVICE_ID, newId).apply()
        return newId
    }
}

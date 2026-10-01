package com.example.apknfc

import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log

/**
 * Serviço de emulação de cartão (HCE) responsável por responder
 * ao totem (Raspberry Pi + PN532) com o ID do usuário quando o
 * celular é aproximado do leitor NFC.
 *
 * Precisa ser registrado no AndroidManifest.xml com um
 * apduservice.xml declarando o AID (Application Identifier)
 * que o totem vai usar para selecionar este serviço.
 */
class PresencaHceService : HostApduService() {

    companion object {
        private const val TAG = "PresencaHceService"

        // Status codes ISO 7816-4 padrão
        private val SW_OK = byteArrayOf(0x90.toByte(), 0x00.toByte())
        private val SW_ERROR = byteArrayOf(0x6F.toByte(), 0x00.toByte())

        // AID que deve bater com o apduservice.xml e com o comando
        // SELECT enviado pelo totem
        private const val AID = "F0010203040506"
    }

    /**
     * Chamado toda vez que o totem envia um comando APDU
     * enquanto o celular está próximo do leitor.
     */
    override fun processCommandApdu(commandApdu: ByteArray?, extras: Bundle?): ByteArray {
        if (commandApdu == null) {
            Log.w(TAG, "Comando APDU nulo recebido")
            return SW_ERROR
        }

        Log.d(TAG, "APDU recebido: ${commandApdu.toHexString()}")

        return if (isSelectAidApdu(commandApdu)) {
            // Aqui você monta a resposta com o ID do usuário.
            // Em produção, troque por algo assinado/criptografado
            // para evitar clonagem simples do ID.
            val userId = obterIdDoUsuario()
            userId.toByteArray(Charsets.UTF_8) + SW_OK
        } else {
            SW_ERROR
        }
    }

    /**
     * Chamado quando a conexão NFC é interrompida
     * (celular afastado do totem antes de terminar a troca).
     */
    override fun onDeactivated(reason: Int) {
        Log.d(TAG, "Conexão HCE desativada. Motivo: $reason")
    }

    private fun isSelectAidApdu(apdu: ByteArray): Boolean {
        // Um comando SELECT começa com 00 A4 04 00
        if (apdu.size < 4) return false
        return apdu[0] == 0x00.toByte() &&
                apdu[1] == 0xA4.toByte() &&
                apdu[2] == 0x04.toByte() &&
                apdu[3] == 0x00.toByte()
    }

    private fun obterIdDoUsuario(): String {
        // TODO: substituir pelo ID real do usuário logado no app
        // (ex: vindo de SharedPreferences ou de um login prévio)
        return "USER-0001"
    }

    private fun ByteArray.toHexString(): String =
        joinToString(" ") { "%02X".format(it) }
}
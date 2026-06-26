package com.terminator.shared.util

expect object EncryptionUtil {
    fun encrypt(plainText: String, key: ByteArray): ByteArray
    fun decrypt(cipherData: ByteArray, key: ByteArray): String
    fun generateKey(): ByteArray
    fun generateSalt(): ByteArray
    fun deriveKey(password: String, salt: ByteArray): ByteArray
}

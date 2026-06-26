package com.terminator.shared.util

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.refTo
import platform.Security.SecRandomCopyBytes
import platform.Security.kSecRandomDefault
import platform.CommonCrypto.*

@OptIn(ExperimentalForeignApi::class)
actual object EncryptionUtil {
    private const val GCM_IV_LENGTH = 12
    private const val SALT_LENGTH = 16
    private const val KEY_LENGTH = 32

    actual fun encrypt(plainText: String, key: ByteArray): ByteArray {
        val iv = generateIv()
        val plainBytes = plainText.encodeToByteArray()

        val cipherData = memScoped {
            val outBuffer = allocArrayOf(ByteArray(plainBytes.size + kCCBlockSizeAES128.toInt()))
            val outLength = allocArrayOf(0L)

            val status = CCCrypt(
                kCCEncrypt,
                kCCAlgorithmAES,
                kCCOptionPKCS7Padding,
                key.refTo(0), key.size.toULong(),
                iv.refTo(0),
                plainBytes.refTo(0), plainBytes.size.toULong(),
                outBuffer, (plainBytes.size + kCCBlockSizeAES128.toInt()).toULong(),
                outLength
            )

            if (status != kCCSuccess) {
                throw IllegalStateException("AES encryption failed with status: $status")
            }

            val resultLength = outLength[0].toInt()
            outBuffer.readBytes(resultLength)
        }

        return iv + cipherData
    }

    actual fun decrypt(cipherData: ByteArray, key: ByteArray): String {
        val iv = cipherData.sliceArray(0 until GCM_IV_LENGTH)
        val encrypted = cipherData.sliceArray(GCM_IV_LENGTH until cipherData.size)

        val plainData = memScoped {
            val outBuffer = allocArrayOf(ByteArray(encrypted.size + kCCBlockSizeAES128.toInt()))
            val outLength = allocArrayOf(0L)

            val status = CCCrypt(
                kCCDecrypt,
                kCCAlgorithmAES,
                kCCOptionPKCS7Padding,
                key.refTo(0), key.size.toULong(),
                iv.refTo(0),
                encrypted.refTo(0), encrypted.size.toULong(),
                outBuffer, (encrypted.size + kCCBlockSizeAES128.toInt()).toULong(),
                outLength
            )

            if (status != kCCSuccess) {
                throw IllegalStateException("AES decryption failed with status: $status")
            }

            val resultLength = outLength[0].toInt()
            outBuffer.readBytes(resultLength)
        }

        return plainData.decodeToString()
    }

    actual fun generateKey(): ByteArray {
        val key = ByteArray(KEY_LENGTH)
        SecRandomCopyBytes(kSecRandomDefault, KEY_LENGTH.toULong(), key.refTo(0))
        return key
    }

    actual fun generateSalt(): ByteArray {
        val salt = ByteArray(SALT_LENGTH)
        SecRandomCopyBytes(kSecRandomDefault, SALT_LENGTH.toULong(), salt.refTo(0))
        return salt
    }

    actual fun deriveKey(password: String, salt: ByteArray): ByteArray {
        val passwordBytes = password.encodeToByteArray()
        val derivedKey = ByteArray(KEY_LENGTH)

        memScoped {
            val output = allocArrayOf(ByteArray(KEY_LENGTH))
            CCKeyDerivationPBKDF(
                kCCPBKDF2,
                passwordBytes.refTo(0), passwordBytes.size.toULong(),
                salt.refTo(0), salt.size.toULong(),
                kCCPRFHmacAlgSHA256,
                10000u,
                output, KEY_LENGTH.toULong()
            )
            output.readBytes(KEY_LENGTH).copyInto(derivedKey)
        }

        return derivedKey
    }

    private fun generateIv(): ByteArray {
        val iv = ByteArray(GCM_IV_LENGTH)
        SecRandomCopyBytes(kSecRandomDefault, GCM_IV_LENGTH.toULong(), iv.refTo(0))
        return iv
    }
}

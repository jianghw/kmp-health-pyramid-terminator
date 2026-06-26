package com.terminator.shared.storage

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import com.terminator.shared.util.EncryptionUtil

class TokenStorage(private val settings: Settings) {

    private val encryptionKey: ByteArray by lazy {
        val existingSalt: String? = settings[KEY_SALT]
        val salt = if (existingSalt != null) {
            existingSalt.split(",").map { it.toByte() }.toByteArray()
        } else {
            val newSalt = EncryptionUtil.generateSalt()
            settings[KEY_SALT] = newSalt.joinToString(",") { it.toString() }
            newSalt
        }
        EncryptionUtil.deriveKey(MASTER_PASSWORD, salt)
    }

    companion object {
        private const val KEY_TOKEN = "auth_token_enc"
        private const val KEY_REFRESH_TOKEN = "refresh_token_enc"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_SALT = "token_encryption_salt"
        private const val MASTER_PASSWORD = "kmp_terminator_token_2026"
    }

    var token: String?
        get() = getEncryptedValue(KEY_TOKEN)
        set(value) = setEncryptedValue(KEY_TOKEN, value)

    var refreshToken: String?
        get() = getEncryptedValue(KEY_REFRESH_TOKEN)
        set(value) = setEncryptedValue(KEY_REFRESH_TOKEN, value)

    var userId: Long?
        get() {
            val value: Long? = settings[KEY_USER_ID]
            return value
        }
        set(value) {
            if (value != null) {
                settings[KEY_USER_ID] = value
            } else {
                settings.remove(KEY_USER_ID)
            }
        }

    fun clear() {
        settings.remove(KEY_TOKEN)
        settings.remove(KEY_REFRESH_TOKEN)
        settings.remove(KEY_USER_ID)
        settings.remove(KEY_SALT)
    }

    private fun getEncryptedValue(key: String): String? {
        val encrypted: String? = settings[key]
        return if (encrypted != null) {
            try {
                val bytes = encrypted.split(",").map { it.toByte() }.toByteArray()
                EncryptionUtil.decrypt(bytes, encryptionKey)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    private fun setEncryptedValue(key: String, value: String?) {
        if (value != null) {
            val encrypted = EncryptionUtil.encrypt(value, encryptionKey)
            settings[key] = encrypted.joinToString(",") { it.toString() }
        } else {
            settings.remove(key)
        }
    }
}

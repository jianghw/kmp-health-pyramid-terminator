package com.terminator.shared.storage

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import com.terminator.shared.util.EncryptionUtil
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class CredentialStorage(private val settings: Settings) {

    private val json = Json { ignoreUnknownKeys = true }
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
        private const val KEY_CREDENTIALS = "app_credentials"
        private const val KEY_SALT = "credential_encryption_salt"
        private const val MASTER_PASSWORD = "kmp_terminator_cred_2026"
    }

    fun saveCredential(appId: Long, credentialType: String, encryptedData: String, alias: String) {
        val credentials = getAllCredentials().toMutableList()
        val key = generateCredentialKey(appId, credentialType)
        credentials.removeAll { it.key == key }
        credentials.add(
            StoredCredential(
                key = key,
                appId = appId,
                credentialType = credentialType,
                encryptedData = encryptData(encryptedData),
                alias = alias
            )
        )
        settings[KEY_CREDENTIALS] = json.encodeToString(credentials)
    }

    fun getCredential(appId: Long, credentialType: String): StoredCredential? {
        val key = generateCredentialKey(appId, credentialType)
        return getAllCredentials().find { it.key == key }?.let {
            it.copy(encryptedData = decryptData(it.encryptedData))
        }
    }

    fun getCredentialsByApp(appId: Long): List<StoredCredential> {
        return getAllCredentials()
            .filter { it.appId == appId }
            .map { it.copy(encryptedData = decryptData(it.encryptedData)) }
    }

    fun removeCredential(appId: Long, credentialType: String) {
        val key = generateCredentialKey(appId, credentialType)
        val credentials = getAllCredentials().toMutableList()
        credentials.removeAll { it.key == key }
        settings[KEY_CREDENTIALS] = json.encodeToString(credentials)
    }

    fun removeCredentialsByApp(appId: Long) {
        val credentials = getAllCredentials().toMutableList()
        credentials.removeAll { it.appId == appId }
        settings[KEY_CREDENTIALS] = json.encodeToString(credentials)
    }

    fun hasCredential(appId: Long, credentialType: String): Boolean {
        val key = generateCredentialKey(appId, credentialType)
        return getAllCredentials().any { it.key == key }
    }

    fun clear() {
        settings.remove(KEY_CREDENTIALS)
        settings.remove(KEY_SALT)
    }

    private fun getAllCredentials(): List<StoredCredential> {
        val data: String? = settings[KEY_CREDENTIALS]
        return if (data != null) {
            try {
                json.decodeFromString<List<StoredCredential>>(data)
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    private fun generateCredentialKey(appId: Long, credentialType: String): String {
        return "${appId}_${credentialType}"
    }

    private fun encryptData(data: String): String {
        val encrypted = EncryptionUtil.encrypt(data, encryptionKey)
        return encrypted.joinToString(",") { it.toString() }
    }

    private fun decryptData(encryptedData: String): String {
        val bytes = encryptedData.split(",").map { it.toByte() }.toByteArray()
        return EncryptionUtil.decrypt(bytes, encryptionKey)
    }
}

@kotlinx.serialization.Serializable
data class StoredCredential(
    val key: String,
    val appId: Long,
    val credentialType: String,
    val encryptedData: String,
    val alias: String
)

package com.terminator.backend.util

import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * AES加密工具类 - 用于加密存储敏感数据（如API密钥）
 *
 * 使用AES/CBC/PKCS5Padding算法进行加密，密钥从环境变量读取。
 * 如果环境变量未设置，使用默认密钥（生产环境必须设置环境变量）。
 *
 * 使用方法：
 * 1. 加密：val encrypted = EncryptionUtil.encrypt("sk-123456")
 * 2. 解密：val decrypted = EncryptionUtil.decrypt(encrypted)
 */
object EncryptionUtil {
    // AES密钥长度：256位（32字节）
    private const val ALGORITHM = "AES/CBC/PKCS5Padding"
    private const val KEY_SIZE = 256

    // 从环境变量读取加密密钥，如果未设置使用默认值
    // 生产环境必须设置 TERMINATOR_ENCRYPTION_KEY 环境变量
    private val secretKey: SecretKeySpec by lazy {
        val keyString = System.getenv("TERMINATOR_ENCRYPTION_KEY")
            ?: "kmp-terminator-default-key-32bytes!!"  // 32字节默认密钥
        val keyBytes = keyString.toByteArray(Charsets.UTF_8).copyOf(32)
        SecretKeySpec(keyBytes, "AES")
    }

    // 初始化向量（IV），CBC模式需要
    private val ivBytes: ByteArray by lazy {
        val ivString = System.getenv("TERMINATOR_ENCRYPTION_IV")
            ?: "terminator-iv-16b"  // 16字节默认IV
        ivString.toByteArray(Charsets.UTF_8).copyOf(16)
    }

    /**
     * 加密字符串
     *
     * @param plainText 明文（如API密钥 "sk-1234567890"）
     * @return 加密后的Base64编码字符串（如 "a1b2c3d4..."）
     */
    fun encrypt(plainText: String): String {
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, IvParameterSpec(ivBytes))
        val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        return Base64.getEncoder().encodeToString(encryptedBytes)
    }

    /**
     * 解密字符串
     *
     * @param encryptedText Base64编码的加密字符串
     * @return 解密后的明文
     * @throws Exception 解密失败时抛出异常
     */
    fun decrypt(encryptedText: String): String {
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(ivBytes))
        val decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText))
        return String(decryptedBytes, Charsets.UTF_8)
    }

    /**
     * 安全解密 - 解密失败时返回null而不是抛出异常
     * 用于处理可能未加密的历史数据
     */
    fun decryptOrNull(encryptedText: String): String? {
        return try {
            decrypt(encryptedText)
        } catch (e: Exception) {
            // 解密失败，可能是未加密的旧数据，直接返回原文
            encryptedText
        }
    }

    /**
     * 检查字符串是否已加密（Base64格式）
     */
    fun isEncrypted(text: String): Boolean {
        return try {
            Base64.getDecoder().decode(text)
            text.matches(Regex("^[A-Za-z0-9+/]*={0,2}$"))
        } catch (e: Exception) {
            false
        }
    }
}

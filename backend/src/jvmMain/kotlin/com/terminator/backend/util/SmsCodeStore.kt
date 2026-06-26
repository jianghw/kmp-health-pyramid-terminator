package com.terminator.backend.util

import java.util.concurrent.ConcurrentHashMap
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

/**
 * 短信验证码存储 - 内存中存储验证码及其过期时间
 *
 * 生产环境建议使用Redis替代内存存储，以支持多实例部署。
 *
 * 使用方法：
 * 1. 发送验证码：SmsCodeStore.save("13800138000", "123456")
 * 2. 验证验证码：val isValid = SmsCodeStore.verify("13800138000", "123456")
 */
object SmsCodeStore {
    // 验证码有效期（分钟）
    private const val EXPIRATION_MINUTES = 5L

    // 存储结构：手机号 -> (验证码, 生成时间)
    private val codeStore = ConcurrentHashMap<String, Pair<String, LocalDateTime>>()

    /**
     * 保存验证码
     *
     * @param phone 手机号
     * @param code 6位数字验证码
     */
    fun save(phone: String, code: String) {
        codeStore[phone] = Pair(code, LocalDateTime.now())
    }

    /**
     * 验证验证码
     *
     * @param phone 手机号
     * @param code 用户输入的验证码
     * @return 验证是否成功（验证码正确且未过期）
     */
    fun verify(phone: String, code: String): Boolean {
        val stored = codeStore[phone] ?: return false

        // 检查验证码是否过期
        val minutesSinceCreation = ChronoUnit.MINUTES.between(stored.second, LocalDateTime.now())
        if (minutesSinceCreation > EXPIRATION_MINUTES) {
            // 验证码已过期，删除它
            codeStore.remove(phone)
            return false
        }

        // 验证码匹配则删除（一次性使用）
        if (stored.first == code) {
            codeStore.remove(phone)
            return true
        }

        return false
    }

    /**
     * 生成6位随机验证码
     */
    fun generateCode(): String {
        return (100000..999999).random().toString()
    }

    /**
     * 清除过期的验证码（可定期调用以释放内存）
     */
    fun cleanExpired() {
        val now = LocalDateTime.now()
        codeStore.entries.removeIf { (_, value) ->
            ChronoUnit.MINUTES.between(value.second, now) > EXPIRATION_MINUTES
        }
    }
}

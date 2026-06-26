package com.terminator.backend.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.terminator.backend.db.Users
import com.terminator.backend.model.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

/**
 * 认证服务 - 处理用户登录、注册和JWT令牌管理
 *
 * 实现了 access_token + refresh_token 双令牌机制：
 * - access_token: 短期令牌（2小时），用于API认证
 * - refresh_token: 长期令牌（7天），用于刷新access_token
 *
 * 这种机制的好处：
 * 1. access_token短期有效，即使泄露，影响范围有限
 * 2. 用户不需要频繁重新登录（用refresh_token刷新）
 * 3. 可以通过refresh_token实现"记住我"功能
 */
class AuthService {
    // JWT密钥（生产环境应从环境变量读取）
    private val secret = System.getenv("JWT_SECRET") ?: "terminator-secret-key-2024"

    // access_token 有效期：2小时
    private val accessTokenExpirationMs = 2 * 60 * 60 * 1000L

    // refresh_token 有效期：7天
    private val refreshTokenExpirationMs = 7 * 24 * 60 * 60 * 1000L

    /**
     * 用户登录（使用短信验证码）
     *
     * 执行流程：
     * 1. 验证短信验证码是否正确
     * 2. 查找或创建用户
     * 3. 生成 access_token 和 refresh_token
     *
     * @param phone 手机号
     * @param code 短信验证码
     * @return 包含双令牌和用户信息的登录响应
     * @throws Exception 验证码错误或用户不存在时抛出异常
     */
    fun sendVerificationCode(phone: String): String {
        val code = com.terminator.backend.util.SmsCodeStore.generateCode()
        com.terminator.backend.util.SmsCodeStore.save(phone, code)
        println("验证码: $code -> $phone")
        return code
    }

    fun login(phone: String, code: String): LoginResponse {
        // 验证短信验证码（这里简化处理，实际项目中应该验证真实的验证码）
        val isValid = com.terminator.backend.util.SmsCodeStore.verify(phone, code)
        if (!isValid) {
            throw Exception("验证码错误或已过期")
        }

        // 生成双令牌
        val accessToken = generateAccessToken(phone)
        val refreshToken = generateRefreshToken(phone)

        // 查找或创建用户
        val user = findOrCreateUser(phone)

        return LoginResponse(
            token = accessToken,
            refreshToken = refreshToken,
            user = user
        )
    }

    /**
     * 使用手机号自动登录（免验证码）
     *
     * 适用于用户首次绑定手机号后的快速登录场景。
     */
    fun loginWithPhone(phone: String): LoginResponse {
        val accessToken = generateAccessToken(phone)
        val refreshToken = generateRefreshToken(phone)
        val user = findOrCreateUser(phone)

        return LoginResponse(
            token = accessToken,
            refreshToken = refreshToken,
            user = user
        )
    }

    /**
     * 刷新令牌 - 使用 refresh_token 获取新的 access_token
     *
     * 当 access_token 过期时，前端可以使用 refresh_token 调用此接口
     * 获取新的 access_token，而不需要用户重新登录。
     *
     * @param refreshToken 有效的refresh_token
     * @return 新的登录响应（包含新的access_token和refresh_token）
     * @throws Exception refresh_token无效或过期时抛出异常
     */
    fun refreshAccessToken(refreshToken: String): LoginResponse {
        try {
            // 验证 refresh_token 的有效性
            val verifier = JWT.require(Algorithm.HMAC256(secret))
                .withIssuer("terminator")
                .build()
            val decodedJWT = verifier.verify(refreshToken)

            // 检查令牌类型（确保传入的是refresh_token而不是access_token）
            val tokenType = decodedJWT.getClaim("type").asString()
            if (tokenType != "refresh") {
                throw Exception("无效的令牌类型，请使用refresh_token")
            }

            // 从令牌中提取手机号
            val phone = decodedJWT.getClaim("phone").asString()
                ?: throw Exception("令牌中缺少用户信息")

            // 生成新的双令牌
            val newAccessToken = generateAccessToken(phone)
            val newRefreshToken = generateRefreshToken(phone)
            val user = findUserByPhone(phone)
                ?: throw Exception("用户不存在")

            return LoginResponse(
                token = newAccessToken,
                refreshToken = newRefreshToken,
                user = user
            )
        } catch (e: Exception) {
            throw Exception("refresh_token无效或已过期，请重新登录")
        }
    }

    /**
     * 验证 access_token 的有效性
     *
     * @param token JWT令牌
     * @return 如果令牌有效返回true，否则返回false
     */
    fun verifyToken(token: String): Boolean {
        return try {
            val verifier = JWT.require(Algorithm.HMAC256(secret))
                .withIssuer("terminator")
                .build()
            verifier.verify(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 从 access_token 中提取用户手机号
     *
     * @param token JWT令牌
     * @return 用户手机号，令牌无效时返回null
     */
    fun getPhoneFromToken(token: String): String? {
        return try {
            val decoded = JWT.decode(token)
            decoded.getClaim("phone").asString()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 生成 access_token（短期令牌）
     *
     * 令牌中包含：
     * - phone: 用户手机号
     * - type: 令牌类型（"access"）
     * - iss: 签发者（"terminator"）
     * - exp: 过期时间
     */
    private fun generateAccessToken(phone: String): String {
        return JWT.create()
            .withIssuer("terminator")
            .withClaim("phone", phone)
            .withClaim("type", "access")
            .withExpiresAt(Date(System.currentTimeMillis() + accessTokenExpirationMs))
            .sign(Algorithm.HMAC256(secret))
    }

    /**
     * 生成 refresh_token（长期令牌）
     *
     * refresh_token 的有效期比 access_token 长得多，
     * 用于在 access_token 过期后获取新的令牌。
     */
    private fun generateRefreshToken(phone: String): String {
        return JWT.create()
            .withIssuer("terminator")
            .withClaim("phone", phone)
            .withClaim("type", "refresh")
            .withExpiresAt(Date(System.currentTimeMillis() + refreshTokenExpirationMs))
            .sign(Algorithm.HMAC256(secret))
    }

    /**
     * 查找或创建用户
     *
     * 如果用户不存在，自动创建一个新用户（角色为"elder"）。
     */
    private fun findOrCreateUser(phone: String): UserResponse {
        return transaction {
            val existingUser = Users.selectAll().where { Users.phone eq phone }.firstOrNull()

            if (existingUser != null) {
                // 用户存在，返回用户信息
                UserResponse(
                    userId = existingUser[Users.userId],
                    phone = existingUser[Users.phone],
                    nickname = existingUser[Users.nickname],
                    role = existingUser[Users.role],
                    status = existingUser[Users.status],
                    createdAt = existingUser[Users.createdAt].toString(),
                    updatedAt = existingUser[Users.updatedAt].toString()
                )
            } else {
                // 用户不存在，创建新用户
                val userId = Users.insert {
                    it[Users.phone] = phone
                    it[nickname] = "用户$phone"
                    it[role] = "elder"   // 默认角色为老人
                    it[status] = "active"
                    it[createdAt] = java.time.LocalDateTime.now()
                    it[updatedAt] = java.time.LocalDateTime.now()
                } get Users.userId

                UserResponse(
                    userId = userId,
                    phone = phone,
                    nickname = "用户$phone",
                    role = "elder",
                    status = "active",
                    createdAt = java.time.LocalDateTime.now().toString(),
                    updatedAt = java.time.LocalDateTime.now().toString()
                )
            }
        }
    }

    /**
     * 根据手机号查找用户
     */
    private fun findUserByPhone(phone: String): UserResponse? {
        return transaction {
            Users.selectAll().where { Users.phone eq phone }.firstOrNull()?.let {
                UserResponse(
                    userId = it[Users.userId],
                    phone = it[Users.phone],
                    nickname = it[Users.nickname],
                    role = it[Users.role],
                    status = it[Users.status],
                    createdAt = it[Users.createdAt].toString(),
                    updatedAt = it[Users.updatedAt].toString()
                )
            }
        }
    }
}

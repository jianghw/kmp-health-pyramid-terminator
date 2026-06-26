package com.terminator.shared.model

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class UserTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun testUserSerialization() {
        val user = User(
            userId = 1L,
            phone = "13800138000",
            nickname = "张大爷",
            role = UserRole.ELDER,
            status = UserStatus.ENABLED,
            createdAt = "2026-01-01T00:00:00Z",
            updatedAt = "2026-01-01T00:00:00Z"
        )
        val encoded = json.encodeToString(User.serializer(), user)
        val decoded = json.decodeFromString(User.serializer(), encoded)

        assertEquals(user.userId, decoded.userId)
        assertEquals(user.phone, decoded.phone)
        assertEquals(user.nickname, decoded.nickname)
        assertEquals(user.role, decoded.role)
        assertEquals(user.status, decoded.status)
    }

    @Test
    fun testUserJsonDeserialization() {
        val jsonString = """
            {
                "user_id": 42,
                "phone": "13912345678",
                "nickname": "李奶奶",
                "role": "elder",
                "status": "enabled",
                "created_at": "2026-03-15T10:30:00Z",
                "updated_at": "2026-06-01T08:00:00Z"
            }
        """.trimIndent()

        val user = json.decodeFromString(User.serializer(), jsonString)

        assertEquals(42L, user.userId)
        assertEquals("13912345678", user.phone)
        assertEquals("李奶奶", user.nickname)
        assertEquals(UserRole.ELDER, user.role)
        assertEquals(UserStatus.ENABLED, user.status)
    }

    @Test
    fun testUserRoleSerialization() {
        assertEquals("elder", json.encodeToString(UserRole.serializer(), UserRole.ELDER).trim('"'))
        assertEquals("family_member", json.encodeToString(UserRole.serializer(), UserRole.FAMILY_MEMBER).trim('"'))
        assertEquals("admin", json.encodeToString(UserRole.serializer(), UserRole.ADMIN).trim('"'))
    }

    @Test
    fun testUserRoleDeserialization() {
        assertEquals(UserRole.ELDER, json.decodeFromString(UserRole.serializer(), "\"elder\""))
        assertEquals(UserRole.FAMILY_MEMBER, json.decodeFromString(UserRole.serializer(), "\"family_member\""))
        assertEquals(UserRole.ADMIN, json.decodeFromString(UserRole.serializer(), "\"admin\""))
    }

    @Test
    fun testUserStatusSerialization() {
        assertEquals("enabled", json.encodeToString(UserStatus.serializer(), UserStatus.ENABLED).trim('"'))
        assertEquals("disabled", json.encodeToString(UserStatus.serializer(), UserStatus.DISABLED).trim('"'))
    }

    @Test
    fun testUserStatusDeserialization() {
        assertEquals(UserStatus.ENABLED, json.decodeFromString(UserStatus.serializer(), "\"enabled\""))
        assertEquals(UserStatus.DISABLED, json.decodeFromString(UserStatus.serializer(), "\"disabled\""))
    }

    @Test
    fun testUserFamilyMemberRole() {
        val jsonString = """
            {
                "user_id": 100,
                "phone": "13600001111",
                "nickname": "小明",
                "role": "family_member",
                "status": "enabled",
                "created_at": "2026-01-01T00:00:00Z",
                "updated_at": "2026-01-01T00:00:00Z"
            }
        """.trimIndent()

        val user = json.decodeFromString(User.serializer(), jsonString)
        assertEquals(UserRole.FAMILY_MEMBER, user.role)
    }

    @Test
    fun testUserDisabledStatus() {
        val jsonString = """
            {
                "user_id": 101,
                "phone": "13700002222",
                "nickname": "王爷爷",
                "role": "elder",
                "status": "disabled",
                "created_at": "2026-01-01T00:00:00Z",
                "updated_at": "2026-06-01T00:00:00Z"
            }
        """.trimIndent()

        val user = json.decodeFromString(User.serializer(), jsonString)
        assertEquals(UserStatus.DISABLED, user.status)
    }
}

package br.com.com.softtek.mentalapp.models

import kotlinx.serialization.Serializable

@Serializable
data class UserCreateRequest(
    val email: String,
    val password: String,
    val name: String
)

@Serializable
data class UserPublicResponse(
    val id: String,
    val email: String,
    val name: String,
    val role: String,
    val createdAt: Long
)

@Serializable
data class UserUpdateRequest(
    val name: String? = null,
    val email: String? = null,
    val role: String? = null
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: UserPublicResponse
)

data class UserDto(
    val id: String?,
    val name: String,
    val email: String
)
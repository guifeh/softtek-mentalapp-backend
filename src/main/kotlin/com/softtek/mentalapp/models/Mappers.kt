package br.com.com.softtek.mentalapp.models

import org.bson.types.ObjectId
import java.util.Date

fun User.toDto() = UserDto(
    id = this.id.toHexString(),
    name = this.name,
    email = this.email
)

fun UserDto.toEntity(passwordHash: String) = User(
    id = ObjectId(),
    name = this.name,
    email = this.email,
    passwordHash = passwordHash,
    role = "USER",
    createdAt = Date()
)
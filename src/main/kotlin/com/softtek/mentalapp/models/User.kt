package br.com.com.softtek.mentalapp.models

import org.bson.codecs.pojo.annotations.*
import org.bson.types.ObjectId
import java.util.Date

data class User @BsonCreator constructor(
    @BsonId val id: ObjectId = ObjectId(),
    @BsonProperty("email") val email: String,
    @BsonProperty("name") val name: String,
    @BsonProperty("passwordHash") val passwordHash: String,
    @BsonProperty("role") val role: String = "USER",
    @BsonProperty("createdAt") val createdAt: Date = Date()
)

fun User.toPublic(): UserPublicResponse =
    UserPublicResponse(
        id = this.id.toHexString(),
        email = this.email,
        name = this.name,
        role = this.role,
        createdAt = this.createdAt.time
    )

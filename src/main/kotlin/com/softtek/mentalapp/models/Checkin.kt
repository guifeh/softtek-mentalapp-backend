package br.com.com.softtek.mentalapp.models

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.*

@Serializable
data class Checkin @BsonCreator constructor(
    @BsonId
    @BsonProperty("id") val id: String,
    @BsonProperty("userId") val userId: String,
    @BsonProperty("emotion") val emotion: String,
    @BsonProperty("note") val note: String?,
    @BsonProperty("createdAt") val createdAt: Long
)

@Serializable
data class CheckinRequest(
    val emotion: String,
    val note: String? = null
)

@Serializable
data class CheckinResponse(
    val id: String,
    val userId: String,
    val emotion: String,
    val note: String?,
    val createdAt: Long
)
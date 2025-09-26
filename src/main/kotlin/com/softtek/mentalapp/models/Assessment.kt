package br.com.com.softtek.mentalapp.models

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.ObjectId

@Serializable
data class Assessment @BsonCreator constructor(
    @BsonId
    @BsonProperty("id") val id: String = ObjectId().toHexString(),
    @BsonProperty("userId") val userId: String,
    @BsonProperty("question") val question: String,
    @BsonProperty("answer") val answer: Int,
    @BsonProperty("score") val score: String,
    @BsonProperty("createdAt") val createdAt: Long = System.currentTimeMillis()
)

@Serializable
data class AssessmentRequest(
    val question: String,
    val answer: Int
)

@Serializable
data class AssessmentResponse(
    val id: String,
    val question: String,
    val answer: Int,
    val score: String,
    val createdAt: Long
)

@Serializable
data class AssessmentSummaryResponse(
    val total: Int,
    val avarageScore: Double,
    val status: String
)
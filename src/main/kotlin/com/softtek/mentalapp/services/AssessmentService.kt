package br.com.com.softtek.mentalapp.services

import br.com.com.softtek.mentalapp.models.*
import br.com.com.softtek.mentalapp.repositories.AssessmentRepository
import org.bson.types.ObjectId
import kotlin.math.round

class AssessmentService (private val repo: AssessmentRepository){

    fun create(userId: String, req: AssessmentRequest): AssessmentResponse{
        val status = calculateScore(req.answer)

        val assessment = Assessment(
            id = ObjectId().toHexString(),
            userId = userId,
            question = req.question,
            answer = req.answer,
            score = status
        )

        repo.create(assessment)

        return AssessmentResponse(
            id = assessment.id,
            question = assessment.question,
            answer = assessment.answer,
            score = assessment.score,
            createdAt = assessment.createdAt
        )
    }

    fun listByUser(userId: String): List<AssessmentResponse> =
        repo.findByUser(userId).map {
            AssessmentResponse(
                id = it.id,
                question = it.question,
                answer = it.answer,
                score = it.score,
                createdAt = it.createdAt
            )
        }

    private fun calculateScore(answer: Int): String{
        return when(answer){
            in 1..2-> "Estável"
            3 -> "Atenção"
            in 4..5 -> "Crítico"
            else -> "Inválido"
        }
    }

    fun getSummary(userId: String):AssessmentSummaryResponse{
        val assessment = repo.findByUser(userId)

        if (assessment.isEmpty()){
            return AssessmentSummaryResponse(
                total = 0,
                avarageScore = 0.0,
                status = "Sem avaliações suficientes"
            )
        }

        val avg = assessment.map { it.answer }.average()
        val avgRounded2 = (round(avg * 100) / 100.0)
        val avgRoundedInt = round(avg).toInt()

        val status = when (avgRoundedInt){
            in 1..2 -> "Estável"
            3 -> "Atenção"
            in 4..5 -> "Crítico"
            else -> "Sem avaliações suficientes"
        }

        return AssessmentSummaryResponse(
            total = assessment.size,
            avarageScore = avgRounded2,
            status = status
        )
    }
}
package br.com.com.softtek.mentalapp.repositories

import br.com.com.softtek.mentalapp.models.Assessment
import com.softtek.mentalapp.db.DatabaseFactory
import org.litote.kmongo.eq
import org.litote.kmongo.getCollection

class AssessmentRepository {
    private val col = DatabaseFactory.database.getCollection<Assessment>()

    fun create(assessment: Assessment): Assessment{
        col.insertOne(assessment)
        return assessment
    }

    fun findByUser(userId: String): List<Assessment> =
        col.find(Assessment::userId eq userId).toList()
}
package br.com.com.softtek.mentalapp.repositories

import br.com.com.softtek.mentalapp.models.Checkin
import br.com.com.softtek.mentalapp.models.CheckinSummary
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.*
import org.bson.Document
import org.bson.conversions.Bson
import org.litote.kmongo.eq
import org.litote.kmongo.getCollection
import org.litote.kmongo.and
import java.time.LocalDate

class CheckinRepository(database: MongoDatabase) {
    private val col: MongoCollection<Checkin> = database.getCollection()

    fun create(checkin: Checkin): Checkin {
        col.insertOne(checkin)
        return checkin
    }

    fun findByUser(userId: String): List<Checkin> =
        col.find(Checkin::userId eq userId).toList()

    fun findByDateRange(userId: String?, startMillis: Long, endMillis: Long): List<Checkin> {
        val filters = mutableListOf<org.bson.conversions.Bson>()
        filters.add(Filters.gte("createdAt", startMillis))
        filters.add(Filters.lte("createdAt", endMillis))
        if (!userId.isNullOrBlank()) filters.add(Filters.eq("userId", userId))
        val combined = if (filters.size == 1) filters[0] else Filters.and(filters)
        return col.find(combined).sort(Sorts.descending("createdAt")).toList()
    }

    fun getSummary(userId: String?, startMillis: Long, endMillis: Long): CheckinSummary {
        val filters = mutableListOf<Bson>()
        filters.add(Filters.gte("createdAt", startMillis))
        filters.add(Filters.lte("createdAt", endMillis))
        if (!userId.isNullOrBlank()) filters.add(Filters.eq("userId", userId))

        val pipeline = listOf(
            Document("\$match", Filters.and(filters)),
            Document("\$group", Document()
                .append("_id", null)
                .append("totalCheckins", Document("\$sum", 1))
                .append("uniqueUsers", Document("\$addToSet", "\$userId"))
            ),
            Document("\$project", Document()
                .append("totalCheckins", 1)
                .append("uniqueUsers", Document("\$size", "\$uniqueUsers"))
            )
        )

        val result: Document? = col.aggregate(pipeline, Document::class.java).firstOrNull()

        return if (result != null) {
            val total = (result.get("totalCheckins") as? Number)?.toInt() ?: 0
            val unique = (result.get("uniqueUsers") as? Number)?.toInt() ?: 0
            CheckinSummary(total, unique)
        } else {
            CheckinSummary(0, 0)
        }
    }

    fun deleteById(id: String, userId: String? = null): Boolean {
        val filter = if (userId != null) {
            and(Checkin::id eq id, Checkin::userId eq userId)
        } else {
            Checkin::id eq id
        }
        val result = col.deleteOne(filter)
        return result.deletedCount > 0
    }
}

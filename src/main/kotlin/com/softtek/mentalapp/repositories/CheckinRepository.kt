package br.com.com.softtek.mentalapp.repositories

import br.com.com.softtek.mentalapp.models.Checkin
import org.litote.kmongo.eq
import org.litote.kmongo.getCollection
import com.mongodb.client.MongoDatabase
import com.softtek.mentalapp.db.DatabaseFactory
import org.litote.kmongo.and

class CheckinRepository {
    private val col = DatabaseFactory.database.getCollection<Checkin>()

    fun create(checkin: Checkin): Checkin {
        col.insertOne(checkin)
        return checkin
    }

    fun findByUser(userId: String): List<Checkin> =
        col.find(Checkin::userId eq userId).toList()

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


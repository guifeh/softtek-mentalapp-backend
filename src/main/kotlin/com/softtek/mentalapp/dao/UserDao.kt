package com.softtek.mentalapp.dao

import com.softtek.mentalapp.db.DatabaseFactory
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters.eq
import org.bson.Document

class UserDao(
    private val users: MongoCollection<Document> = DatabaseFactory.database.getCollection("users")
) {
    fun getAll(): List<Document> =
        users.find().toList()

    fun getById(id: String): Document? =
        users.find(eq("_id", id)).first()

    fun create(user: Document): Document {
        users.insertOne(user)
        return user
    }

    fun delete(id: String): Boolean {
        val result = users.deleteOne(eq("_id", id))
        return result.deletedCount > 0
    }
}

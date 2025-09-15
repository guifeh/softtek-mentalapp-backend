package com.softtek.mentalapp.repositories

import br.com.com.softtek.mentalapp.models.User
import br.com.com.softtek.mentalapp.models.UserPublicResponse
import com.softtek.mentalapp.db.DatabaseFactory
import org.bson.types.ObjectId
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters.eq
import java.util.Date

class UserRepository {
    private val col: MongoCollection<User> =
        DatabaseFactory.database.getCollection("users", User::class.java)

    fun findByEmail(email: String): User? =
        col.find(eq("email", email)).firstOrNull()

    fun findById(id: String): User? =
        col.find(eq("_id", ObjectId(id))).firstOrNull()

    fun insertUser(email: String, name: String, passwordHash: String, role: String = "USER"): String {
        val user = User(
            id = ObjectId(),
            email = email,
            name = name,
            passwordHash = passwordHash,
            role = role,
            createdAt = Date()
        )
        col.insertOne(user)
        return user.id.toHexString()
    }

    fun delete(id: String): Boolean {
        val result = col.deleteOne(eq("_id", ObjectId(id)))
        return result.deletedCount > 0
    }

    fun listAll(): List<UserPublicResponse> =
        col.find().map { it.toPublic() }.toList()

    private fun User.toPublic(): UserPublicResponse =
        UserPublicResponse(
            id = this.id.toHexString(),
            email = this.email,
            name = this.name,
            role = this.role,
            createdAt = this.createdAt.time
        )
}

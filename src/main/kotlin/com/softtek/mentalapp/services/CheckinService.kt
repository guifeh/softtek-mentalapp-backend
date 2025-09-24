package br.com.com.softtek.mentalapp.services

import br.com.com.softtek.mentalapp.models.*
import br.com.com.softtek.mentalapp.repositories.CheckinRepository
import org.bson.types.ObjectId

class CheckinService(private val repository: CheckinRepository) {

    fun create(userId: String, req: CheckinRequest): Checkin {
        val newCheckin = Checkin(
            id = ObjectId().toHexString(),
            userId = userId,
            emotion = req.emotion,
            note = req.note,
            createdAt = System.currentTimeMillis()
        )
        return repository.create(newCheckin)
    }

    fun listByUser(userId: String): List<CheckinResponse> {
        return repository.findByUser(userId).map {
            CheckinResponse(
                id = it.id,
                emotion = it.emotion,
                note = it.note,
                createdAt = it.createdAt,
                userId =  it.userId
            )
        }
    }
    fun delete(userId: String, id: String, isAdmin: Boolean): Boolean {
        return if (isAdmin) {
            repository.deleteById(id)
        } else {
            repository.deleteById(id, userId)
        }
    }
}

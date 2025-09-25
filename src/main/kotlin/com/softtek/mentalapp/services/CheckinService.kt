package br.com.com.softtek.mentalapp.services

import br.com.com.softtek.mentalapp.models.*
import br.com.com.softtek.mentalapp.repositories.CheckinRepository
import org.bson.types.ObjectId
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

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
        return repository.findByUser(userId).map { it.toResponse() }
    }

    fun getCheckinsByDateRange(userIdFilter: String?, startDate: LocalDate, endDate: LocalDate): List<CheckinResponse> {
        val zone = ZoneId.systemDefault()
        val startMillis = startDate.atStartOfDay(zone).toInstant().toEpochMilli()
        val endMillis = endDate.atTime(LocalTime.MAX).atZone(zone).toInstant().toEpochMilli()

        val checkins = repository.findByDateRange(userIdFilter, startMillis, endMillis)
        return checkins.map { it.toResponse() }
    }

    fun getCheckinSummary(userIdFilter: String?, startDate: LocalDate, endDate: LocalDate): CheckinSummary {
        val zone = ZoneId.systemDefault()
        val startMillis = startDate.atStartOfDay(zone).toInstant().toEpochMilli()
        val endMillis = endDate.atTime(LocalTime.MAX).atZone(zone).toInstant().toEpochMilli()

        return repository.getSummary(userIdFilter, startMillis, endMillis)
    }

    fun delete(userId: String, id: String, isAdmin: Boolean): Boolean {
        return if (isAdmin) {
            repository.deleteById(id)
        } else {
            repository.deleteById(id, userId)
        }
    }
}

fun Checkin.toResponse() = CheckinResponse(
    id = this.id,
    userId = this.userId,
    emotion = this.emotion,
    note = this.note,
    createdAt = this.createdAt
)

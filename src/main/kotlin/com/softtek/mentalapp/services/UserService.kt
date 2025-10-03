package br.com.com.softtek.mentalapp.services

import br.com.com.softtek.mentalapp.models.User
import br.com.com.softtek.mentalapp.models.UserCreateRequest
import br.com.com.softtek.mentalapp.models.UserPublicResponse
import br.com.com.softtek.mentalapp.models.toPublic
import br.com.com.softtek.mentalapp.auth.JwtConfig
import br.com.com.softtek.mentalapp.models.UserUpdateRequest
import com.mongodb.client.model.Filters.eq
import com.softtek.mentalapp.repositories.UserRepository
import org.mindrot.jbcrypt.BCrypt

class UserService(private val repo: UserRepository = UserRepository()) {

    fun register(req: UserCreateRequest): UserPublicResponse {
        if (!req.email.contains("@") || req.password.length < 6) {
            throw IllegalArgumentException("Email inválido ou senha muito curta (mín 6).")
        }

        val existing = repo.findByEmail(req.email)
        if (existing != null) {
            throw IllegalArgumentException("Já existe um usuário com esse email.")
        }

        val hash = BCrypt.hashpw(req.password, BCrypt.gensalt(12))
        val id = repo.insertUser(req.email.lowercase(), req.name, hash, "USER")
        val user = repo.findById(id) ?: throw IllegalArgumentException("Erro ao criar usuário")

        return user.toPublic()
    }

    fun authenticate(email: String, password: String): UserPublicResponse? {
        val doc = repo.findByEmail(email.lowercase()) ?: return null
        val hash = doc.passwordHash
        return if (BCrypt.checkpw(password, hash)) {
            doc.toPublic()
        } else null
    }

    fun getById(id: String): UserPublicResponse? {
        val user = repo.findById(id) ?: return null
        return user.toPublic()
    }

    fun delete(id: String): Boolean {
        return repo.delete(id)
    }

    fun updateUser(id: String, req: UserUpdateRequest): UserPublicResponse? {
        val existing = repo.findById(id) ?: return null

        val updatedUser = existing.copy(
            name = req.name ?: existing.name,
            email = req.email ?: existing.email,
            role = req.role ?: existing.role
        )
        repo.update(updatedUser)
        return updatedUser.toPublic()
    }

    fun listAll(): List<UserPublicResponse> = repo.listAll()
}

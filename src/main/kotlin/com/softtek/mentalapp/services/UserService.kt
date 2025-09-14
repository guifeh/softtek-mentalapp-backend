package br.com.com.softtek.mentalapp.services

import br.com.com.softtek.mentalapp.models.UserCreateRequest
import br.com.com.softtek.mentalapp.models.UserPublicResponse
import br.com.com.softtek.mentalapp.models.toPublic
import com.softtek.mentalapp.repositories.UserRepository
import org.mindrot.jbcrypt.BCrypt

class UserService(private val repo: UserRepository = UserRepository()) {

    fun register(req: UserCreateRequest): UserPublicResponse {
        // validações
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
        val user = repo.findByEmail(email.lowercase()) ?: return null
        return if (BCrypt.checkpw(password, user.passwordHash)) {
            user.toPublic()
        } else null
    }

    fun getById(id: String): UserPublicResponse? {
        val user = repo.findById(id) ?: return null
        return user.toPublic()
    }

    fun delete(id: String): Boolean {
        return repo.delete(id)
    }

    fun listAll(): List<UserPublicResponse> = repo.listAll()
}

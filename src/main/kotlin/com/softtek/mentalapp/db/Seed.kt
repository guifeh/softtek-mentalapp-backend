package com.softtek.mentalapp.db

import com.softtek.mentalapp.repositories.UserRepository
import org.mindrot.jbcrypt.BCrypt

object Seed {
    private val repo = UserRepository()

    fun createAdminIfNotExists(){
        val adminEmail = "admin@softtek.local"
        if (repo.findByEmail(adminEmail) == null){
            val hash = BCrypt.hashpw("Admin123!", BCrypt.gensalt(12))
            repo.insertUser(adminEmail, "Admin Softtek", hash, "ADMIN")
            println("Dev admin criado: $adminEmail / password: Admin123!")
        }
    }
}
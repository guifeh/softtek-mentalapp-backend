package br.com.com.softtek.mentalapp.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.http.*
import java.util.Date

object JwtConfig {
    private val secret = System.getenv("JWT_SECRET") ?: "dev-secret-change-me"
    private val issuer = "softtek-mental"
    private val audience = "softtek-mental-app"
    private val algorithm = Algorithm.HMAC256(secret)

    fun sign(userId: String, role: String, ttlSeconds: Long = 900): String =
        JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("uid", userId)
            .withClaim("role", role)
            .withExpiresAt(Date(System.currentTimeMillis() + ttlSeconds * 1000))
            .sign(algorithm)

    fun configureKtorAuth(application: Application){
        application.install(Authentication){
            jwt("auth-jwt"){
                realm = "softtek-mental"
                verifier(
                    JWT
                        .require(algorithm)
                        .withAudience(audience)
                        .withIssuer(issuer)
                        .build()
                )
                validate { credental ->
                    val uid = credental.payload.getClaim("uid").asString()
                    val role = credental.payload.getClaim("role").asString()
                    if (!uid.isNullOrBlank()) JWTPrincipal(credental.payload) else null
                }
                challenge { _, _ ->
                    throw AuthenticationException("Token inválido ou expirado")
                }
            }
        }
    }
    class AuthenticationException(message: String): RuntimeException(message)
}
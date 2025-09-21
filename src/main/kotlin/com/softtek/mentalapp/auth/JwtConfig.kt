package br.com.com.softtek.mentalapp.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import java.util.*

object JwtConfig {
    private val secret = System.getenv("JWT_SECRET") ?: "dev-secret-change-me"
    private val issuer = "softtek-mental"
    private val audience = "softtek-mental-app"
    private val algorithm = Algorithm.HMAC256(secret)

    fun verifyToken(token: String) = try {
        JWT.require(algorithm)
            .withIssuer(issuer)
            .withAudience(audience)
            .build()
            .verify(token)
    } catch (e: Exception) {
        null
    }

    fun generateAccessToken(userId: String, role: String, ttlSeconds: Long = 900): String =
        JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("uid", userId)
            .withClaim("role", role)
            .withExpiresAt(Date(System.currentTimeMillis() + ttlSeconds * 1000))
            .sign(algorithm)

    fun generateRefreshToken(userId: String, role: String, ttlSeconds: Long = 60 * 60 * 24 * 7): String =
        JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("uid", userId)
            .withClaim("role", role)
            .withExpiresAt(Date(System.currentTimeMillis() + ttlSeconds * 1000))
            .sign(algorithm)

    // === Helpers para extrair dados do token ===
    fun extractUserId(principal: JWTPrincipal): String? =
        principal.payload.getClaim("uid").asString()

    fun extractRole(principal: JWTPrincipal): String? =
        principal.payload.getClaim("role").asString()

    // === Configuração do Ktor ===
    fun configureKtorAuth(application: Application) {
        application.install(Authentication) {
            jwt("auth-jwt") {
                realm = issuer
                verifier(
                    JWT
                        .require(algorithm)
                        .withAudience(audience)
                        .withIssuer(issuer)
                        .build()
                )
                validate { credential ->
                    val uid = credential.payload.getClaim("uid").asString()
                    if (!uid.isNullOrBlank()) JWTPrincipal(credential.payload) else null
                }
                challenge { _, _ ->
                    throw AuthenticationException("Token inválido ou expirado")
                }
            }
        }
    }

    class AuthenticationException(message: String) : RuntimeException(message)
}

package cz.fei.upce.checkman.component.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date
import javax.annotation.PostConstruct
import javax.crypto.SecretKey

@Component
class JWTUtil() {
    @Value("\${jwt.secret}")
    private val secret: String = ""

    @Value("\${jwt.expiration}")
    private val expiration: String = ""

    private lateinit var key: SecretKey

    @PostConstruct
    fun init() = run { key = Keys.hmacShaKeyFor(secret.toByteArray()) }

    fun parseAllClaimsFromToken(token: String) =
        Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).body

    fun parseUsernameFromToken(token: String) =
        parseAllClaimsFromToken(token).subject

    fun parseExpirationDateFromToken(token: String) =
        parseAllClaimsFromToken(token).expiration

    fun isExpired(token: String) =
        parseExpirationDateFromToken(token).before(Date())

    fun isNotExpired(token: String) =
        !parseExpirationDateFromToken(token).before(Date())

    fun isValid(token : String) =
        isNotExpired(token)

    fun generateToken(username: String): String {
        val issueAtDate = Date()
        val expirationDate = Date(issueAtDate.time + expiration.toLong())

        return generateTokenInfo(username, issueAtDate, expirationDate).jwtToken
    }

    fun generateTokenInfo(username: String): JwtTokenInfo {
        val issueAtDate = Date()
        val expirationDate = Date(issueAtDate.time + expiration.toLong())

        return generateTokenInfo(username, issueAtDate, expirationDate)
    }

    fun generateTokenInfo(username: String, issueAtDate: Date, expirationDate: Date): JwtTokenInfo {
        val jwtToken = Jwts.builder()
            .setClaims(mapOf<String, Any>())
            .setSubject(username)
            .setIssuedAt(issueAtDate)
            .setExpiration(expirationDate)
            .signWith(key)
            .compact()

        return JwtTokenInfo(jwtToken, issueAtDate, expirationDate)
    }
}
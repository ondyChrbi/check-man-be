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

    fun generateToken(username : String) = Jwts.builder()
        .setClaims(mapOf<String, Any>())
        .setSubject(username)
        .setIssuedAt(Date())
        .setExpiration(Date(Date().time + expiration.toLong()))
        .signWith(key)
        .compact()
}
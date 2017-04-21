package utils

import java.time.{LocalDateTime, ZoneOffset}

import authentikat.jwt.{JwtClaimsSetJValue, JsonWebToken, JwtClaimsSet, JwtHeader}
import models.{Role, User}
import play.api.Play.current
import play.api.mvc.RequestHeader


object WebTokenUtils {
  val jsonTokenKey = current.configuration.getString("jwtKey").get

  def getToken(requestHeader: RequestHeader) =
    requestHeader.headers.get("Authorization") flatMap {
      authHeader => authHeader.filter(_ != '"').split(' ').drop(1).headOption
    }

  def createJWT(username: String, userId: String, role: Role): String = {
    val header = JwtHeader("HS256")
    val claimsSet = JwtClaimsSet(
      Map(
        "username" -> username,
        "userId" -> userId,
        "role" -> role,
        "exp" -> LocalDateTime.now.plusSeconds(12 * 3600).toEpochSecond(ZoneOffset.UTC))
    )
    JsonWebToken(header, claimsSet, jsonTokenKey)
  }

  def validateToken(jsonWebToken: String): Boolean =
    JsonWebToken.validate(jsonWebToken, jsonTokenKey) && !isExpired(jsonWebToken)

  implicit class JwtClaimsSetJValueOps(jwtClaimsSetJValue: JwtClaimsSetJValue) {
    def extract(key: String): String = jwtClaimsSetJValue.asSimpleMap.get(key)

    def extractLong(key: String): Long = extract(key).toLong
  }

  def isExpired(jsonWebToken: String): Boolean = jsonWebToken match {
    case JsonWebToken(_, jwtClaimsSetJValue, _) =>
      jwtClaimsSetJValue.extractLong("exp") < LocalDateTime.now.toEpochSecond(ZoneOffset.UTC)
  }

  def getRoles(request: RequestHeader): String = getToken(request) map {
    case JsonWebToken(_, jwtClaimsSetJValue, _) =>
      jwtClaimsSetJValue.extract("role")
  } getOrElse "no_role"

  def getUsername(request: RequestHeader) = getToken(request) map {
    case JsonWebToken(_, jwtClaimsSetJValue, _) =>
      jwtClaimsSetJValue.extract("username")
  } getOrElse "no_username"

}
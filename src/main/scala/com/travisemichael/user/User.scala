package com.travisemichael.user

import java.sql.{PreparedStatement, ResultSet}

import org.json4s.DefaultFormats
import org.json4s.native.Serialization._

case class User(login: String,
                id: Int,
                `type`: String,
                name: Option[String],
                company: Option[String],
                blog: Option[String],
                location: Option[String],
                email: Option[String],
                publicRepos: Option[Int],
                publicGists: Option[Int],
                followers: Option[Int],
                following: Option[Int],
                createdAt: Option[String],
                updatedAt: Option[String]) {

  def setFieldsOnStatement(statement: PreparedStatement): PreparedStatement = {
    statement.setString(1, login)
    statement.setInt(2, id)
    statement.setString(3, `type`)
    statement.setString(4, name.orNull)
    statement.setString(5, company.orNull)
    statement.setString(6, blog.orNull)
    statement.setString(7, location.orNull)
    statement.setString(8, email.orNull)
    statement.setInt(9, publicRepos.getOrElse(0))
    statement.setInt(10, publicGists.getOrElse(0))
    statement.setInt(11, followers.getOrElse(0))
    statement.setInt(12, following.getOrElse(0))
    statement.setString(13, createdAt.orNull)
    statement.setString(14, updatedAt.orNull)
    statement
  }

  def toJson: String = write(this)(DefaultFormats)
}

object User {
  def fromResultSet(result: ResultSet): User = {
    new User(
      result.getString(1),
      result.getInt(2),
      result.getString(3),
      Option(result.getString(4)),
      Option(result.getString(5)),
      Option(result.getString(6)),
      Option(result.getString(7)),
      Option(result.getString(8)),
      Option(result.getInt(9)),
      Option(result.getInt(10)),
      Option(result.getInt(11)),
      Option(result.getInt(12)),
      Option(result.getString(13)),
      Option(result.getString(14))
    )
  }
}

package com.travisemichael.repo

import java.sql.{PreparedStatement, ResultSet}

import org.json4s.DefaultFormats
import org.json4s.native.Serialization._

case class Repo(id: Int,
                name: String,
                fullName: String,
                ownerId: Int,
                htmlUrl: String,
                description: Option[String],
                fork: Boolean) {
  def setFieldsOnStatement(statement: PreparedStatement): PreparedStatement = {
    statement.setInt(1, id)
    statement.setString(2, name)
    statement.setString(3, fullName)
    statement.setInt(4, ownerId)
    statement.setString(5, htmlUrl)
    statement.setString(6, description.orNull)
    statement.setBoolean(7, fork)
    statement
  }

  def toJson: String = write(this)(DefaultFormats)
}

object Repo {
  def fromResultSet(resultSet: ResultSet): Repo = {
    new Repo(
      resultSet.getInt(1),
      resultSet.getString(2),
      resultSet.getString(3),
      resultSet.getInt(4),
      resultSet.getString(5),
      Option(resultSet.getString(6)),
      resultSet.getBoolean(7)
    )
  }
}

package com.travisemichael.user

import java.sql.{ResultSet, SQLException}

import com.travisemichael.db.DatabaseConnectionProvider
import com.travisemichael.service.PersistenceService

class UserService(dbConnectionProvider: DatabaseConnectionProvider)
    extends PersistenceService[User] {

  private val connection = dbConnectionProvider.connection

  @throws[SQLException]
  override def createTableIfNotExists(): Unit = {
    connection
      .createStatement()
      .execute("""
        |CREATE TABLE IF NOT EXISTS users (
        |  PRIMARY KEY (id),
        |  login varchar(255),
        |  id int,
        |  type varchar(255),
        |  name varchar(255),
        |  company varchar(255),
        |  blog varchar(255),
        |  location varchar(255),
        |  email varchar(255),
        |  hireable boolean,
        |  public_repos int,
        |  public_gists int,
        |  followers int,
        |  following int,
        |  created_at varchar(30),
        |  updated_at varchar(30)
        |)
      """.stripMargin)
  }

  private val upsertUsers =
    "REPLACE INTO users VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

  @throws[SQLException]
  override def upsert(users: Iterator[User]): Unit = {
    users.grouped(10).foreach { batch =>
      val statement = connection.prepareStatement(upsertUsers)
      batch.foreach(_.setFieldsOnStatement(statement).addBatch())
      statement.executeBatch()
    }
  }

  @throws[SQLException]
  override def find(login: String): Option[User] = {
    val statement =
      connection.prepareStatement("SELECT * FROM users WHERE login = ?")
    statement.setString(1, login)
    val resultSet = statement.executeQuery()
    val exists = resultSet.next()
    if (exists) Option(User.fromResultSet(resultSet)) else None
  }

  @throws[SQLException]
  override def find(id: Int): Option[User] = {
    val statement =
      connection.prepareStatement("SELECT * FROM users WHERE id = ?")
    statement.setInt(1, id)
    val resultSet = statement.executeQuery()
    val exists = resultSet.next()
    if (exists) Option(User.fromResultSet(resultSet)) else None
  }

  @throws[SQLException]
  override def findAll(): Iterator[User] = {
    val statement = connection.createStatement()
    new UserIterator(statement.executeQuery("SELECT * FROM users"))
  }

  class UserIterator private[UserService] (resultSet: ResultSet)
      extends Iterator[User] {
    override def hasNext: Boolean = resultSet.next()
    override def next(): User = User.fromResultSet(resultSet)
  }
}

object UserService {
  def apply(): UserService =
    new UserService(DatabaseConnectionProvider())
}

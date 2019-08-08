package com.travisemichael.repo

import java.sql.{ResultSet, SQLException}

import com.travisemichael.db.DatabaseConnectionProvider
import com.travisemichael.service.PersistenceService

class RepoService(dbConnectionProvider: DatabaseConnectionProvider)
    extends PersistenceService[Repo] {

  private val connection = dbConnectionProvider.connection

  @throws[SQLException]
  override def createTableIfNotExists(): Unit = {
    connection
      .createStatement()
      .execute("""
          |CREATE TABLE IF NOT EXISTS repos (
          |  PRIMARY KEY (id),
          |  id int,
          |  name varchar(255),
          |  full_name varchar(255),
          |  owner_id int,
          |  htmlUrl varchar(255),
          |  description varchar(255),
          |  fork boolean
          |)
        """.stripMargin)
  }

  private val upsertRepos =
    "REPLACE INTO repos VALUES(?,?,?,?,?,?,?)"

  @throws[SQLException]
  override def upsert(repos: Iterator[Repo]): Unit = {
    repos.grouped(10).foreach { batch =>
      val statement = connection.prepareStatement(upsertRepos)
      batch.foreach(_.setFieldsOnStatement(statement).addBatch())
      statement.executeBatch()
    }
  }

  @throws[SQLException]
  override def find(fullName: String): Option[Repo] = {
    val statement =
      connection.prepareStatement("SELECT * FROM repos WHERE full_name = ?")
    statement.setString(1, fullName)
    val resultSet = statement.executeQuery()
    val exists = resultSet.next()
    if (exists) Option(Repo.fromResultSet(resultSet)) else None
  }

  @throws[SQLException]
  override def find(id: Int): Option[Repo] = {
    val statement =
      connection.prepareStatement("SELECT * FROM repos WHERE id = ?")
    statement.setInt(1, id)
    val resultSet = statement.executeQuery()
    val exists = resultSet.next()
    if (exists) Option(Repo.fromResultSet(resultSet)) else None
  }

  @throws[SQLException]
  override def findAll(): Iterator[Repo] = {
    val statement = connection.createStatement()
    new RepoIterator(statement.executeQuery("SELECT * FROM repos"))
  }

  class RepoIterator private[RepoService] (resultSet: ResultSet)
      extends Iterator[Repo] {
    override def hasNext: Boolean = resultSet.next()

    override def next(): Repo = Repo.fromResultSet(resultSet)
  }
}

object RepoService {
  def apply(): RepoService = new RepoService(DatabaseConnectionProvider())
}

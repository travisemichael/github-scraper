package com.travisemichael.db

import java.sql.{Connection, DriverManager}

import org.slf4j.LoggerFactory

class MySqlConnectionProvider private[db] extends DatabaseConnectionProvider {
  private val logger = LoggerFactory.getLogger(getClass)

  override val connection: Connection = DriverManager.getConnection(
    "jdbc:mysql://mysql/db?serverTimezone=UTC",
    "travis",
    "timetoscrape"
  )

  testConnection()

  def testConnection(): Unit = {
    if (connection != null) logger.info("Connected to the database!")
    else throw new Exception("Error while connecting to Database")
  }
}

object MySqlConnectionProvider {
  def apply(): MySqlConnectionProvider = new MySqlConnectionProvider()
}

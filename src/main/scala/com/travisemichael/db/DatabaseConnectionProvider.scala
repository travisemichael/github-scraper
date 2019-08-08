package com.travisemichael.db

import java.sql.Connection

trait DatabaseConnectionProvider {
  def connection: Connection
}

object DatabaseConnectionProvider {
  private var instance: Option[DatabaseConnectionProvider] = None

  def apply(): DatabaseConnectionProvider = instance.getOrElse {
    val provider = MySqlConnectionProvider()
    instance = Option(provider)
    provider
  }
}

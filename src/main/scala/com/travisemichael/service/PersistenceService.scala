package com.travisemichael.service

trait PersistenceService[T] {
  def createTableIfNotExists(): Unit
  def upsert(records: Iterator[T])
  def find(id: Int): Option[T]
  def find(name: String): Option[T]
  def findAll(): Iterator[T]
}

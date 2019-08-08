package com.travisemichael.user

import com.travisemichael.service.PersistenceService

trait UserApi {
  def getAll: Iterator[User]
  def scrapeAll(start: Int, count: Int)
  def get(id: Int): Option[User]
  def get(login: String): Option[User]
  def scrape(login: String): Option[User]
}

object UserApi {
  def apply(): UserApi = UserApiImpl()
}

class UserApiImpl(userService: PersistenceService[User],
                  ghService: GithubUserService)
    extends UserApi {
  override def getAll: Iterator[User] = userService.findAll()

  override def scrapeAll(start: Int, count: Int): Unit =
    userService.upsert(ghService.getAll(start).take(count))

  override def get(id: Int): Option[User] = userService.find(id)

  override def get(login: String): Option[User] = userService.find(login)

  override def scrape(login: String): Option[User] = {
    val user = ghService.get(login)
    userService.upsert(user.toIterator)
    user
  }
}

object UserApiImpl {
  def apply(): UserApiImpl = new UserApiImpl(UserService(), GithubUserService())
}

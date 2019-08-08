package com.travisemichael.repo

import com.travisemichael.service.PersistenceService

trait RepoApi {
  def getAll: Iterator[Repo]
  def scrapeAll(start: Int, count: Int): Unit
  def get(id: Int): Option[Repo]
  def get(owner: String, name: String): Option[Repo]
  def scrape(owner: String, name: String): Option[Repo]
}

object RepoApi {
  def apply(): RepoApi = RepoApiImpl()
}

class RepoApiImpl private[RepoApiImpl] (repoService: PersistenceService[Repo],
                                        ghService: GithubRepoService)
    extends RepoApi {
  override def getAll: Iterator[Repo] = repoService.findAll()

  override def scrapeAll(start: Int, count: Int): Unit =
    repoService.upsert(ghService.getAll(start).take(count))

  override def get(id: Int): Option[Repo] = repoService.find(id)

  override def get(owner: String, name: String): Option[Repo] =
    repoService.find(s"$owner/$name")

  override def scrape(owner: String, name: String): Option[Repo] = {
    val repo = ghService.get(owner, name)
    repoService.upsert(repo.toIterator)
    repo
  }
}

object RepoApiImpl {
  def apply(): RepoApiImpl = new RepoApiImpl(RepoService(), GithubRepoService())
}

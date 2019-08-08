package com.travisemichael.repo

import com.travisemichael.user.User

case class GithubRepo(id: Int,
                      name: String,
                      fullName: String,
                      owner: Option[User],
                      htmlUrl: String,
                      description: Option[String],
                      fork: Boolean) {
  def toRepo: Repo =
    Repo(
      id,
      name,
      fullName,
      owner.map(_.id).getOrElse(0),
      htmlUrl,
      description,
      fork
    )
}

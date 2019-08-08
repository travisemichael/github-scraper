package com.travisemichael.user

import com.travisemichael.util.HttpUtil
import org.json4s._
import org.json4s.native.JsonMethods._

import scala.util.Try

trait GithubUserService {
  def getAll(since: Int): Iterator[User]
  def getNextPage(since: Int): Array[User]
  def get(login: String): Option[User]
}

object GithubUserService {
  def apply(): GithubUserService = GithubUserServiceImpl()
}

class GithubUserServiceImpl private[GithubUserServiceImpl]
    extends GithubUserService {
  implicit val formats: DefaultFormats.type = DefaultFormats

  override def getAll(since: Int): Iterator[User] =
    new UserIterator(since, getNextPage, get)

  override def getNextPage(since: Int): Array[User] = {
    val url = s"https://api.github.com/users?since=$since"
    val resp = HttpUtil.executeGet(url)
    parse(resp).camelizeKeys.extract[Array[User]]
  }

  override def get(login: String): Option[User] = {
    val url = s"https://api.github.com/users/$login"
    val resp = HttpUtil.executeGet(url)
    Try(parse(resp).camelizeKeys.extract[User]).toOption
  }

  class UserIterator private[GithubUserServiceImpl] (
    start: Int,
    getBatch: Int => Array[User],
    getUser: String => Option[User]
  ) extends Iterator[User] {
    private var array: Array[User] = _
    private var l: Int = _
    private var i: Int = _
    private var nextStart: Int = _
    nextBatch(start)

    private def nextBatch(start: Int): Unit = {
      array = getBatch(start)
      l = array.length
      i = 0
      nextStart = array.lastOption.map(_.id).getOrElse(0)
    }

    override def hasNext: Boolean =
      if (l == 0) false
      else if (i < l) true
      else {
        nextBatch(nextStart)
        hasNext
      }

    override def next(): User = {
      val u = getUser(array(i).login)
      i += 1
      u.orNull
    }
  }
}

object GithubUserServiceImpl {
  def apply(): GithubUserServiceImpl = new GithubUserServiceImpl()
}

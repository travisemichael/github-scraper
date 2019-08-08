package com.travisemichael.repo

import com.travisemichael.util.HttpUtil
import org.json4s._
import org.json4s.native.JsonMethods._

import scala.util.Try

trait GithubRepoService {
  def getAll(since: Int): Iterator[Repo]
  def get(owner: String, name: String): Option[Repo]
  def getNextPage(since: Int): Array[Repo]
}

object GithubRepoService {
  def apply(): GithubRepoService = GithubRepoServiceImpl()
}

class GithubRepoServiceImpl private[GithubRepoServiceImpl]
    extends GithubRepoService {
  implicit val formats: DefaultFormats.type = DefaultFormats

  override def getAll(since: Int): Iterator[Repo] =
    new RepoIterator(since, getNextPage)

  override def get(owner: String, name: String): Option[Repo] = {
    val url = s"https://api.github.com/repos/$owner/$name"
    val resp = HttpUtil.executeGet(url)
    Try(parse(resp).camelizeKeys.extract[GithubRepo]).toOption.map(_.toRepo)
  }

  override def getNextPage(since: Int): Array[Repo] = {
    val url = s"https://api.github.com/repositories?since=$since"
    val resp = HttpUtil.executeGet(url)
    parse(resp).camelizeKeys.extract[Array[GithubRepo]].map(_.toRepo)
  }

  class RepoIterator private[GithubRepoServiceImpl] (
    start: Int,
    getBatch: Int => Array[Repo]
  ) extends Iterator[Repo] {
    private var array: Array[Repo] = _
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

    override def hasNext: Boolean = {
      if (l == 0) false
      else if (i < l) true
      else {
        nextBatch(nextStart)
        hasNext
      }
    }

    override def next(): Repo = {
      val r = array(i)
      i += 1
      r
    }
  }
}

object GithubRepoServiceImpl {
  def apply(): GithubRepoServiceImpl = new GithubRepoServiceImpl()
}

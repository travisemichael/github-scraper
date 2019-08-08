package com.travisemichael.user

import org.json4s.DefaultFormats
import org.json4s.native.JsonParser._
import org.quartz.{DisallowConcurrentExecution, Job, JobExecutionContext}
import org.slf4j.{Logger, LoggerFactory}

import scala.io.Source

@DisallowConcurrentExecution
class UserScrapeJob extends Job {
  implicit val format: DefaultFormats.type = DefaultFormats

  override def execute(context: JobExecutionContext): Unit = {
    UserScrapeJob.logger.info("Executing Users Job")

    val userService = UserService()
    val ghService = GithubUserService()

    val scrapeFile = Source.fromFile(UserScrapeJob.confPath)
    val scrapeConf = parse(scrapeFile.mkString).camelizeKeys
      .extract[UserScrapeConf]

    val userIter =
      ghService
        .getAll(scrapeConf.start)
        .take(scrapeConf.count)
    val additionalUsers =
      scrapeConf.additionalUsers.flatMap(ghService.get).toIterator

    userService.upsert(additionalUsers ++ userIter)

    UserScrapeJob.logger.info("Finished executing Users Job")
  }
}

object UserScrapeJob {
  final val confPath = "users.json"
  val logger: Logger = LoggerFactory.getLogger(getClass)
}

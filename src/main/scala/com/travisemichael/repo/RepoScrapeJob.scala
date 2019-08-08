package com.travisemichael.repo

import org.json4s.DefaultFormats
import org.json4s.native.JsonParser._
import org.quartz.{DisallowConcurrentExecution, Job, JobExecutionContext}
import org.slf4j.{Logger, LoggerFactory}

import scala.io.Source

@DisallowConcurrentExecution
class RepoScrapeJob extends Job {
  implicit val format: DefaultFormats.type = DefaultFormats

  override def execute(context: JobExecutionContext): Unit = {
    RepoScrapeJob.logger.info("Executing Repos Job")

    val repoService = RepoService()
    val ghService = GithubRepoService()

    val file = Source.fromFile(RepoScrapeJob.confPath)
    val conf = parse(file.mkString).camelizeKeys.extract[RepoConf]

    val repoIter =
      ghService.getAll(conf.start).take(conf.count)
    val additionalRepos = conf.additionalRepos
      .flatMap(r => ghService.get(r.owner, r.name))
      .toIterator

    repoService.upsert(additionalRepos ++ repoIter)
    RepoScrapeJob.logger.info("Finished executing Repos Job")
  }
}

object RepoScrapeJob {
  val logger: Logger = LoggerFactory.getLogger(getClass)
  val confPath: String = "repos.json"
}

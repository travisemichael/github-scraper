package com.travisemichael

import com.travisemichael.repo.RepoScrapeJob
import com.travisemichael.user.UserScrapeJob
import com.travisemichael.util.QuartzUtil
import org.quartz.Scheduler
import org.quartz.impl.StdSchedulerFactory
import org.slf4j.LoggerFactory

import scala.io.Source

object Quartz {
  private val logger = LoggerFactory.getLogger(getClass)

  def scheduleJobs(): Unit = {
    val factory = new StdSchedulerFactory
    implicit val scheduler: Scheduler = factory.getScheduler
    scheduler.start()

    val cronSource = Source.fromFile("scrape.cron")
    val cron = cronSource.mkString
    cronSource.close()

    logger.info("Scheduling quartz jobs on cron schedule: {}", cron)
    QuartzUtil.runJob("user", classOf[UserScrapeJob])
    QuartzUtil.runJob("repo", classOf[RepoScrapeJob])
    QuartzUtil.scheduleCronJob("user", cron, classOf[UserScrapeJob])
    QuartzUtil.scheduleCronJob("repo", cron, classOf[RepoScrapeJob])
  }
}

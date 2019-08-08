package com.travisemichael.util

import org.quartz.{
  CronScheduleBuilder,
  Job,
  JobBuilder,
  Scheduler,
  TriggerBuilder
}

object QuartzUtil {
  def runJob[T <: Job](name: String,
                       clazz: Class[T])(implicit scheduler: Scheduler): Unit = {
    val nowJob = JobBuilder
      .newJob(clazz)
      .withIdentity(s"$name-NowJob", "group1")
      .build()

    val nowTrigger = TriggerBuilder
      .newTrigger()
      .withIdentity(s"$name-NowTrigger", "group1")
      .startNow()
      .build()

    scheduler.scheduleJob(nowJob, nowTrigger)
  }

  def scheduleCronJob[T <: Job](name: String, cron: String, clazz: Class[T])(
    implicit scheduler: Scheduler
  ): Unit = {
    val cronJob = JobBuilder
      .newJob(clazz)
      .withIdentity(s"$name-CronJob", "group1")
      .build()

    val cronTrigger = TriggerBuilder
      .newTrigger()
      .withIdentity(s"$name-CronTrigger", "group1")
      .startNow()
      .withSchedule(CronScheduleBuilder.cronSchedule(cron))
      .build()

    scheduler.scheduleJob(cronJob, cronTrigger)
  }
}

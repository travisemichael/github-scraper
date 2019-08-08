package com.travisemichael.repo

case class RepoConf(start: Int, count: Int, additionalRepos: Array[RepoInfo])
case class RepoInfo(owner: String, name: String)

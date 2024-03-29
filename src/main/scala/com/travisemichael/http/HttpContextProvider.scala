package com.travisemichael.http

import org.apache.http.HttpHost
import org.apache.http.auth.{AuthScope, UsernamePasswordCredentials}
import org.apache.http.client.protocol.HttpClientContext
import org.apache.http.impl.auth.BasicScheme
import org.apache.http.impl.client.{BasicAuthCache, BasicCredentialsProvider}

object HttpContextProvider {
  private val provider = new BasicCredentialsProvider
  private val credentials = new UsernamePasswordCredentials(
    sys.env("GITHUB_USERNAME"),
    sys.env.getOrElse("GITHUB_TOKEN", sys.env("GITHUB_PASSWORD"))
  )
  provider.setCredentials(AuthScope.ANY, credentials)

  private val targetHost = new HttpHost("api.github.com", -1, "https")
  private val authCache = new BasicAuthCache
  authCache.put(targetHost, new BasicScheme)

  val context: HttpClientContext = HttpClientContext.create()
  context.setCredentialsProvider(provider)
  context.setAuthCache(authCache)
}

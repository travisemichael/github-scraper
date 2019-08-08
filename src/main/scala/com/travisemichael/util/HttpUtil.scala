package com.travisemichael.util

import java.io.{BufferedReader, InputStreamReader}

import com.travisemichael.http.HttpContextProvider
import org.apache.http.client.methods.{CloseableHttpResponse, HttpGet}
import org.apache.http.impl.client.HttpClientBuilder

object HttpUtil {
  private val context = HttpContextProvider.context

  def executeGet(url: String): String = {
    val client = HttpClientBuilder.create().build()
    val resp = client.execute(new HttpGet(url), context)

    try {
      getRespContent(resp)
    } finally {
      client.close()
    }
  }

  private def getRespContent(resp: CloseableHttpResponse): String = {
    val reader = new BufferedReader(
      new InputStreamReader(resp.getEntity.getContent)
    )

    try {
      val stringBuilder = new StringBuilder
      reader.lines.forEach(line => stringBuilder.append(line))
      stringBuilder.toString()
    } finally {
      reader.close()
    }
  }
}

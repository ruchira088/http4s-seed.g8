package com.ruchij.config

import com.comcast.ip4s.{Host, Port}

final case class HttpConfiguration(host: Host, port: Port, private val allowedOrigins: Option[Set[String]]) {
  def allowedOriginHosts: Set[String] = allowedOrigins.getOrElse(Set.empty)
}
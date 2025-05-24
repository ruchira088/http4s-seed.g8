package com.ruchij.config

import com.comcast.ip4s.{Host, Port}
import pureconfig.ConfigReader

object ConfigReaders {
  implicit val hostConfigReader: ConfigReader[Host] = ConfigReader.fromNonEmptyStringOpt(Host.fromString)

  implicit val portConfigReader: ConfigReader[Port] = ConfigReader.fromNonEmptyStringOpt(Port.fromString)

  implicit val stringListConfigReader: ConfigReader[Set[String]] =
    ConfigReader.fromString(stringValue => Right(stringValue.split(",").map(_.trim).filter(_.nonEmpty).toSet))
}

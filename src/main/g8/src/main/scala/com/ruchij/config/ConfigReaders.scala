package com.ruchij.config

import com.comcast.ip4s.{Host, Port}
import pureconfig.ConfigReader

object ConfigReaders {
  implicit val hostConfigReader: ConfigReader[Host] = ConfigReader.fromNonEmptyStringOpt(Host.fromString)

  implicit val portConfigReader: ConfigReader[Port] = ConfigReader.fromNonEmptyStringOpt(Port.fromString)
}
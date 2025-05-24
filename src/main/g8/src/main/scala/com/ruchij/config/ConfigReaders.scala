package com.ruchij.config

import com.comcast.ip4s.{Host, Port}
import pureconfig.ConfigReader

import scala.collection.Factory

object ConfigReaders {
  implicit val hostConfigReader: ConfigReader[Host] = ConfigReader.fromNonEmptyStringOpt(Host.fromString)

  implicit val portConfigReader: ConfigReader[Port] = ConfigReader.fromNonEmptyStringOpt(Port.fromString)

  implicit def stringIterableConfigReader[Itr[x] <: IterableOnce[x]](
    implicit factory: Factory[String, Itr[String]]
  ): ConfigReader[Itr[String]] =
    ConfigReader[String].map { string =>
      factory.fromSpecific {
        string.split(",").map(_.trim).filter(_.nonEmpty)
      }
    }
}

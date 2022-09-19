package com.ruchij.config

import cats.ApplicativeError
import com.ruchij.config.ConfigReaders._
import com.ruchij.types.FunctionKTypes._
import pureconfig.ConfigObjectSource
import pureconfig.error.ConfigReaderException
import pureconfig.generic.auto._

final case class ServiceConfiguration(httpConfiguration: HttpConfiguration)

object ServiceConfiguration {
  def parse[F[_]: ApplicativeError[*[_], Throwable]](configObjectSource: ConfigObjectSource): F[ServiceConfiguration] =
    configObjectSource.load[ServiceConfiguration].left.map(ConfigReaderException.apply).toType[F, Throwable]
}

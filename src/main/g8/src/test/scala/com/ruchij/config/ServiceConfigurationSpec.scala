package com.ruchij.config

import cats.effect.IO
import com.comcast.ip4s.IpLiteralSyntax
import com.ruchij.test.utils.IOUtils.{IOErrorOps, runIO}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import pureconfig.ConfigSource

class ServiceConfigurationSpec extends AnyFlatSpec with Matchers {

  "ServiceConfiguration" should "parse the ConfigObjectSource" in runIO {
    val configObjectSource =
      ConfigSource.string {
        s"""
          http-configuration {
            host = "127.0.0.1"
            host = \$\${?HTTP_HOST}

            port = 80
            port = \$\${?HTTP_PORT}

            allowed-origins = "localhost,*.home.ruchij.com,*.dev.ruchij.com"
            allowed-origins = \$\${?HTTP_ALLOWED_ORIGINS}
          }
        """
      }

    ServiceConfiguration.parse[IO](configObjectSource).flatMap { serviceConfiguration =>
      IO.delay {
        serviceConfiguration.httpConfiguration mustBe HttpConfiguration(
          ipv4"127.0.0.1",
          port"80",
          Some(Set("localhost", "*.home.ruchij.com", "*.dev.ruchij.com"))
        )
      }
    }
  }

  it should "return an error if ConfigObjectSource is not parsable" in runIO {
    val configObjectSource =
      ConfigSource.string {
        s"""
          http-configuration {
            host = "0.0.0.0"

            port = my-invalid-port

            allowed-origins = "localhost,*.home.ruchij.com,*.dev.ruchij.com"
            allowed-origins = \$\${?HTTP_ALLOWED_ORIGINS}
          }
        """
      }

    ServiceConfiguration.parse[IO](configObjectSource).error
      .flatMap { throwable =>
        IO.delay {
          throwable.getMessage must include("Cannot convert 'my-invalid-port' to com.comcast.ip4s.Port")
        }
      }
  }

}

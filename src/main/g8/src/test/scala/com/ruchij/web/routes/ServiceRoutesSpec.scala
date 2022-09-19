package com.ruchij.web.routes

import cats.effect.IO
import com.ruchij.services.health.models.ServiceInformation
import com.ruchij.test.matchers._
import com.ruchij.test.mixins.io.MockedRoutesIO
import com.ruchij.test.utils.IOUtils.runIO
import io.circe.literal._
import org.http4s.Method.GET
import org.http4s.implicits.http4sLiteralsSyntax
import org.http4s.{Request, Status}
import org.joda.time.{DateTime, DateTimeZone}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

class ServiceRoutesSpec extends AnyFlatSpec with Matchers with MockedRoutesIO {

  "GET /service/info" should "return a successful response containing service information" in runIO {

    val expectedJsonResponse =
      json"""{
        "serviceName": "$name;format="normalize"$",
        "serviceVersion": "1.0.0",
        "organization": "com.ruchij",
        "scalaVersion": "2.13.8",
        "sbtVersion": "1.7.1",
        "javaVersion": "17.0.4",
        "gitBranch" : "my-branch",
        "gitCommit" : "my-commit",
        "buildTimestamp": "2022-08-14T20:40:00.000Z",
        "timestamp": "2022-09-06T10:10:00.000Z"
      }"""

    for {
      _ <- IO.delay {
        (() => healthService.serviceInformation).expects()
          .returns {
            IO.pure {
              ServiceInformation(
                "$name;format="normalize"$",
                "1.0.0",
                "com.ruchij",
                "2.13.8",
                "1.7.1",
                "17.0.4",
                Some("my-branch"),
                Some("my-commit"),
                new DateTime(2022, 8, 14, 20, 40, 0, 0, DateTimeZone.UTC),
                new DateTime(2022, 9, 6, 10, 10, 0, 0, DateTimeZone.UTC)
              )
            }
          }
      }

      request = Request[IO](GET, uri"/service/info")

      routes = createRoutes

      response <- routes.run(request)

      _ <- IO.delay {
        response must beJsonContentType
        response must haveJson(expectedJsonResponse)
        response must haveStatus(Status.Ok)
      }
    }
    yield (): Unit
  }
}
package com.ruchij.web.middleware

import cats.Show
import cats.arrow.FunctionK
import cats.data.{Kleisli, NonEmptyList}
import cats.effect.Sync
import cats.implicits._
import com.ruchij.exceptions.ResourceNotFoundException
import com.ruchij.types.Logger
import com.ruchij.web.responses.ErrorResponse
import io.circe.DecodingFailure
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityEncoder.circeEntityEncoder
import org.http4s.dsl.impl.EntityResponseGenerator
import org.http4s.{HttpApp, Request, Response, Status}

object ExceptionHandler {
  private val logger = Logger[ExceptionHandler.type]

  def apply[F[_]: Sync](httpApp: HttpApp[F]): HttpApp[F] =
    Kleisli[F, Request[F], Response[F]] { request =>
      Sync[F].handleErrorWith(httpApp.run(request)) { throwable =>
        entityResponseGenerator[F](throwable)(throwableResponseBody(throwable))
          .map(errorResponseMapper(throwable))
          .flatTap(response => errorLogger(response, throwable))
      }
    }

  private def errorLogger[F[_]: Sync](response: Response[F], throwable: Throwable): F[Unit] =
    if (response.status >= Status.InternalServerError)
      logger.error(s"\${response.status.code} status error code was returned.", throwable)
    else logger.warn(throwable.getMessage)

  private val throwableStatusMapper: Throwable => Status = {
    case _: ResourceNotFoundException => Status.NotFound

    case _ => Status.InternalServerError
  }

  private val throwableResponseBody: Throwable => ErrorResponse = {
    case decodingFailure: DecodingFailure =>
      ErrorResponse {
        NonEmptyList.one {
          Show[DecodingFailure].show(decodingFailure)
        }
      }

    case throwable =>
      Option(throwable.getCause).fold(ErrorResponse(NonEmptyList.of(throwable.getMessage)))(throwableResponseBody)
  }

  private def errorResponseMapper[F[_]](throwable: Throwable)(response: Response[F]): Response[F] =
    throwable match {
      case _ => response
    }

  private def entityResponseGenerator[F[_]](throwable: Throwable): EntityResponseGenerator[F, F] =
    new EntityResponseGenerator[F, F] {
      override def status: Status = throwableStatusMapper(throwable)

      override def liftG: FunctionK[F, F] = FunctionK.id[F]
    }
}

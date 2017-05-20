package com.olchovy.lineup

import com.twitter.finagle.{Http, Filter, Service}
import com.twitter.finagle.http.{ Request, Response, Status}
import com.twitter.util.Future

object ExceptionHandlingFilter extends Filter[Request, Response, Request, Response] {
  override def apply(request: Request, continue: Service[Request, Response]): Future[Response] = {
    continue(request).rescue {
      case e: Throwable =>
        val msg = s"An unexpected error was encountered when processing your request"
        val response = Response(request.version, Status.InternalServerError)
        response.contentString = s"$msg: ${e.getMessage}"
        Future.value(response)
    }
  }
}

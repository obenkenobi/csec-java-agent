package com.newrelic.instrumentable.scala.http4s.ember.server

import cats.effect.Sync
import com.newrelic.api.agent.weaver.scala.{ScalaMatchType, ScalaWeave}
import org.http4s.Request

@ScalaWeave(`type` = ScalaMatchType.Object, originalName = "com.newrelic.instrumentable.scala.http4s.ember.server.RequestProcessor")
object RequestProcessor_Instrumentation {

  def processRequest[F[_]: Sync](request: Request[F]): F[Unit] = {
    RequestUtils.processReq(request)
  }

}
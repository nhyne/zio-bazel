package dev.nhyne.todo.metrics

import zio.{RIO, Runtime}
import io.prometheus.client.CollectorRegistry
import zio.metrics.{Label => ZLabel, Show}
import zio.metrics.prometheus._
import zio.metrics.prometheus.helpers._
import zio.metrics.prometheus.exporters._

// also for printing debug messages to the console
import zio.console.{Console, putStrLn}
// and for sleeping/clocks
import zio.clock.Clock
import zio.duration.Duration
import scala.concurrent.duration._
// and for inspecting prometheus
import java.util

class MetricsService {}

object MetricsService {
  trait Service {}

}

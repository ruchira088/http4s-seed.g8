package com.ruchij.test.mixins.io

import cats.effect.IO
import cats.effect.kernel.Sync
import com.ruchij.test.mixins.MockedRoutes
import fs2.compression.Compression
import org.scalatest.TestSuite

trait MockedRoutesIO extends MockedRoutes[IO] { self: TestSuite =>
  override val sync: Sync[IO] = IO.asyncForIO

  override val compression: Compression[IO] = Compression.forIO
}
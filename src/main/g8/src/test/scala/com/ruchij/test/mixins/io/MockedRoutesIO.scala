package com.ruchij.test.mixins.io

import cats.effect.IO
import cats.effect.kernel.Sync
import com.ruchij.test.mixins.MockedRoutes
import fs2.compression.Compression

trait MockedRoutesIO extends MockedRoutes[IO] {
  override val sync: Sync[IO] = IO.asyncForIO

  override val compression: Compression[IO] = Compression.forIO
}
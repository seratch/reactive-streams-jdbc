package org.reactivestreams.jdbc

import org.reactivestreams._
import org.slf4j.LoggerFactory

trait RowSubscriber extends Subscriber[Row] {

  private val logger = LoggerFactory.getLogger(this.getClass)

  protected def throwNPEWhenNullValueFound(v: Any): Unit = {
    if (v == null) throw new NullPointerException
  }

  override def onSubscribe(subscription: Subscription): Unit = {
    throwNPEWhenNullValueFound(subscription)
  }

  override def onError(throwable: Throwable): Unit = {
    throwNPEWhenNullValueFound(throwable)
    logger.error(s"Error ${throwable.getMessage}", throwable)
  }

  override def onComplete(): Unit = {
  }

  override def onNext(rs: Row): Unit

}

package org.reactivestreams.jdbc

import org.reactivestreams._

trait ResultSetSubscriber extends Subscriber[ResultSet] {

  override def onSubscribe(subscription: Subscription): Unit = {}

  override def onError(throwable: Throwable): Unit = {}

  override def onComplete(): Unit = {}

  override def onNext(rs: ResultSet): Unit

}

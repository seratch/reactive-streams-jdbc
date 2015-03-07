package org.reactivestreams.jdbc

import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean
import org.reactivestreams._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.existentials
import scalikejdbc._

trait RowPublisher extends Publisher[Row] {

  def connectionPoolName: Any = ConnectionPool.DEFAULT_NAME

  def createNewSession(): DBSession = NamedAutoSession(connectionPoolName)

  def sql: SQL[_, _ <: scalikejdbc.WithExtractor]

  override def subscribe(subscriber: Subscriber[_ >: Row]): Unit = {
    if (subscriber != null) {
      Future {
        using(createNewSession()) { session =>
          val subscription = new RowFetcherSubscription(createNewSession(), sql, subscriber)
          subscriber.onSubscribe(subscription)
          subscription.run()
        }
      }
    } else {
      throw new NullPointerException
    }
  }

  class RowFetcherSubscription(
      val session: DBSession,
      val sql: SQL[_, _ <: scalikejdbc.WithExtractor],
      val subscriber: Subscriber[_ >: Row]) extends Subscription {

    private[this] lazy val bufferedRows: ConcurrentLinkedQueue[Row] = new ConcurrentLinkedQueue[Row]()
    private[this] lazy val cancelled: AtomicBoolean = new AtomicBoolean(false)

    override def request(n: Long): Unit = {
      var counter: Long = 0
      try {
        while (counter < n) {
          try subscriber.onNext(bufferedRows.peek())
          catch { case t: Throwable => subscriber.onError(t) }
          counter += 1
        }
      } finally subscriber.onComplete()
    }

    override def cancel(): Unit = {
      cancelled.set(true)
      session.close()
    }

    def run(): Unit = {
      sql.foreach { rs =>
        if (!cancelled.get) {
          val row = rs.toMap()
          while (bufferedRows.size() > 10000) {
            bufferedRows.poll()
          }
          bufferedRows.offer(row)

          try subscriber.onNext(row)
          catch { case t: Throwable => subscriber.onError(t) }
        }
      }(session)
    }

  }

}


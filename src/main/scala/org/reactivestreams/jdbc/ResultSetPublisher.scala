package org.reactivestreams.jdbc

import org.reactivestreams._
import scalikejdbc._

trait ResultSetPublisher extends Publisher[ResultSet] {

  def session: DBSession = AutoSession

  def sql: SQL[_, _]

  override def subscribe(subscriber: Subscriber[_ >: ResultSet]): Unit = {
    sql.foreach(rs => subscriber.onNext(rs))(session)
  }

}


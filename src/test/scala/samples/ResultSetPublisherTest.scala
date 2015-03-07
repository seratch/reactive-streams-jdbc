package samples

import org.reactivestreams.{ Subscriber, Publisher }
import org.reactivestreams.jdbc._
import org.reactivestreams.tck._
import org.scalatest.testng.TestNGSuiteLike

class ResultSetPublisherTest(env: TestEnvironment, publisherShutdownTimeout: Long)
    extends PublisherVerification[Row](env, publisherShutdownTimeout)
    with TestNGSuiteLike
    with DatabaseInitializer {

  def this() {
    this(new TestEnvironment(500), 1000)
    prepareArticleTableIfAbsent
  }

  override def createPublisher(elements: Long): Publisher[Row] = {
    new RowPublisher {
      import scalikejdbc._
      override def sql = sql"select id, title, body from article"
    }
  }

  override def createErrorStatePublisher(): Publisher[Row] = {
    new RowPublisher {
      import scalikejdbc._
      override def sql = sql"select id, title, body from article"
      override def subscribe(subscriber: Subscriber[_ >: Row]): Unit = {
        super.subscribe(subscriber)
        subscriber.onError(new RuntimeException)
      }
    }
  }

  override def maxElementsFromPublisher = 10
  override def boundedDepthOfOnNextAndRequestRecursion = 1

}

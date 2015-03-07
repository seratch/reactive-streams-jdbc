package samples

import java.util.concurrent.atomic.AtomicInteger

import org.scalatest._
import org.slf4j.LoggerFactory
import org.reactivestreams._
import org.reactivestreams.jdbc._

class SimpleQuerySpec extends FunSpec with Matchers with DatabaseInitializer {

  prepareArticleTableIfAbsent

  val logger = LoggerFactory.getLogger(getClass)

  case class Article(id: Int, title: String, body: Option[String] = None)

  object Article {
    def apply(row: Row): Article = new Article(
      id = row.get("ID").map(_.toString.toInt).get,
      title = row.get("TITLE").map(_.toString).get,
      body = row.get("BODY").map(_.toString)
    )
  }

  describe("Simple query example") {
    it("works") {
      val publisher: Publisher[Row] = new RowPublisher {
        import scalikejdbc._
        override def sql = sql"select id, title, body from article order by id"
      }
      val onNextCount = new AtomicInteger(0)
      publisher.subscribe(new RowSubscriber {
        override def onNext(row: Row) = {
          onNextCount.incrementAndGet
          // do something with ResultSet here
          logger.info(s"onNext: ${Article(row)}")
        }
      })
      Thread.sleep(500L)
      onNextCount.get() should be > (0)
    }
  }

}

package samples

import org.scalatest._
import org.slf4j.LoggerFactory
import scalikejdbc._
import org.reactivestreams._
import org.reactivestreams.jdbc._

class SimpleQuerySpec extends FunSpec with Matchers {

  val logger = LoggerFactory.getLogger(getClass)

  Class.forName("org.h2.Driver")
  ConnectionPool.singleton("jdbc:h2:mem:simple;MODE=PostgreSQL", "user", "pass")
  GlobalSettings.loggingSQLAndTime = LoggingSQLAndTimeSettings(enabled = false)

  def prepareArticleTable(): Unit = {
    DB.autoCommit { implicit s =>
      sql"create table article (id serial not null primary key, title varchar(100) not null, body text)".execute.apply()
      (1 to 100).foreach { no =>
        val title = s"Lesson $no"
        sql"insert into article (title) values ($title)".update.apply()
      }
    }
  }

  case class Article(id: Int, title: String, body: Option[String] = None)
  object Article {
    def apply(rs: ResultSet): Article = new Article(rs.get("id"), rs.get("title"), rs.get("body"))
  }

  describe("Simple Query") {
    it("works") {
      prepareArticleTable()

      val publisher: Publisher[ResultSet] = new ResultSetPublisher {
        override def sql = sql"select id, title, body from article order by id"
      }
      publisher.subscribe(new ResultSetSubscriber {
        override def onNext(rs: ResultSet) = {
          // do something with ResultSet here
          logger.info(s"onNext: ${Article(rs)}")
        }
      })

    }
  }

}

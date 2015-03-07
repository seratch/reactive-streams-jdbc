package samples

import scalikejdbc._

private object DatabaseInitializer {

  lazy val prepareArticleTable: Unit = {
    Class.forName("org.h2.Driver")

    val cpSettings = ConnectionPoolSettings(
      maxSize = 100,
      connectionPoolFactoryName = "commons-dbcp2"
    )
    ConnectionPool.singleton("jdbc:h2:mem:default;MODE=PostgreSQL", "user", "pass", cpSettings)
    GlobalSettings.loggingSQLAndTime = LoggingSQLAndTimeSettings(enabled = false)

    DB.autoCommit { implicit s =>
      sql"create table article (id serial not null primary key, title varchar(100) not null, body text)".execute.apply()
      (1 to 1000).foreach { no =>
        val title = s"Lesson $no"
        sql"insert into article (title) values ($title)".update.apply()
      }
    }
  }

}

trait DatabaseInitializer {

  def prepareArticleTableIfAbsent = DatabaseInitializer.prepareArticleTable

}

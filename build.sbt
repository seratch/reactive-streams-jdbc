lazy val reactiveStreamsVersion = "1.0.0.RC3"

lazy val root = (project in file("."))
  .settings(
    organization := "net.seratch",
    name := "reactive-streams-jdbc",
    version := "0.1",
    scalaVersion := "2.11.6",
    crossScalaVersions := Seq("2.10.5", "2.11.6"),
    libraryDependencies ++= Seq(
      "org.scalikejdbc"      %% "scalikejdbc"          % "2.2.+",
      "org.scalikejdbc"      %% "scalikejdbc-config"   % "2.2.+",
      "org.apache.commons"   %  "commons-dbcp2"        % "2.1",
      "org.reactivestreams"  %  "reactive-streams"     % reactiveStreamsVersion,
      "org.reactivestreams"  %  "reactive-streams-tck" % reactiveStreamsVersion % "test",
      "com.h2database"       %  "h2"                   % "1.4.+"                % "provided;test",
      "ch.qos.logback"       %  "logback-classic"      % "1.1.+"                % "test",
      "org.skinny-framework" %  "skinny-logback"       % "1.0.+"                % "test",
      "org.scalatest"        %% "scalatest"            % "2.2.+"                % "test"
    ),
    initialCommands := """
import scalikejdbc._
import org.reactivestreams._
import org.reactivestreams.jdbc._

ConnectionPool.singleton("jdbc:h2:mem:default;MODE=PostgreSQL", "user", "pass")
GlobalSettings.loggingSQLAndTime = LoggingSQLAndTimeSettings(enabled = false)
DB.autoCommit { implicit s =>
  sql"create table article (id serial not null primary key, title varchar(100) not null, body text)".execute.apply()
  (1 to 1000).foreach { no =>
    val title = s"Lesson $no"
    sql"insert into article (title) values ($title)".update.apply()
  }
}

val publisher = new RowPublisher {
  import scalikejdbc._
  override def sql = sql"select id, title, body from article order by id"
}
publisher.subscribe(new RowSubscriber {
  override def onNext(row: Row) = {
    println(s"onNext: $row")
  }
})
    """,
    parallelExecution in Test := false,
    logBuffered in Test := false,
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature"),
    publishTo <<= version { (v: String) => 
      val nexus = "https://oss.sonatype.org/"
      if (v.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus + "content/repositories/snapshots")
      else Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    publishMavenStyle := true,
    pomIncludeRepository := { x => false },
    pomExtra := <url>https://github.com/seratch/reactive-streams-jdbc/</url>
  <licenses>
    <license>
      <name>MIT License</name>
      <url>http://www.opensource.org/licenses/mit-license.php</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:seratch/reactive-streams-jdbc.git</url>
    <connection>scm:git:git@github.com:seratch/reactive-streams-jdbc.git</connection>
  </scm>
  <developers>
    <developer>
      <id>seratch</id>
      <name>Kazuhiro Sera</name>
      <url>http://git.io/sera</url>
    </developer>
  </developers>
  ).settings(scalariformSettings: _*)
   .settings(sonatypeSettings: _*)


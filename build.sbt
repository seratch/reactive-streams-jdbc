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
      "org.reactivestreams"  %  "reactive-streams"     % reactiveStreamsVersion,
      "org.reactivestreams"  %  "reactive-streams-tck" % reactiveStreamsVersion % "test",
      "com.h2database"       %  "h2"                   % "1.4.+"                % "test",
      "ch.qos.logback"       %  "logback-classic"      % "1.1.+"                % "test",
      "org.scalatest"        %% "scalatest"            % "2.2.+"                % "test"
    ),
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


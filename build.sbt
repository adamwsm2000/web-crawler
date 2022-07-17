scalaVersion := "2.13.8"

val http4sVersion = "0.23.13"
val CirceVersion = "0.14.2"
val MunitVersion = "0.7.29"
val LogbackVersion = "1.2.10"
val MunitCatsEffectVersion = "1.0.7"

organization := "ph.adamwsm"
name := "web-crawler"
version := "0.0.1-SNAPSHOT"

// Only necessary for SNAPSHOT releases
resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies ++= Seq(
  "org.http4s"      %% "http4s-dsl"           % http4sVersion,
  "org.http4s"      %% "http4s-ember-server"  % http4sVersion,
  "org.http4s"      %% "http4s-ember-client"  % http4sVersion,
  "org.http4s"      %% "http4s-circe"         % http4sVersion,
  "io.circe"        %% "circe-generic"        % CirceVersion,
  "org.scalameta"   %% "munit"                % MunitVersion           % Test,
  "org.typelevel"   %% "munit-cats-effect-3"  % MunitCatsEffectVersion % Test,
  "ch.qos.logback"  %  "logback-classic"      % LogbackVersion         % Runtime
)

enablePlugins(JavaServerAppPackaging)

mainClass in Compile := Some("ph.adamwsm.webcrawler.WebCrawlerServer")


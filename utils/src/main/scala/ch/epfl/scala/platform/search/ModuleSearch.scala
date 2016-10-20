package ch.epfl.scala.platform.search

import gigahorse._

trait BintrayApi {
  val baseUrl = "https://api.bintray.com"
  def search: Request
}

/** Represent a simple Scala module. */
case class ScalaModule(orgId: String, artifactId: String, scalaBinVersion: String)

/** Represent a resolved module by the Bintray API endpoint. */
case class ResolvedModule(name: String,
                          repo: String,
                          owner: String,
                          desc: Option[String],
                          system_ids: List[String],
                          versions: List[String],
                          latestVersion: String)

case class MavenSearch(info: ScalaModule) extends BintrayApi {
  override def search: Request = {
    Gigahorse
      .url(s"$baseUrl/search/packages/maven")
      .addHeader(HeaderNames.ACCEPT -> MimeTypes.JSON)
      .addQueryString(("g", info.orgId),
                      ("a", s"${info.artifactId}_${info.scalaBinVersion}"))
  }
}

object ModuleSearch {
  import io.circe._
  import generic.auto._
  import parser._

  def searchInMaven(module: ScalaModule): List[ResolvedModule] = {
    import scala.concurrent.ExecutionContext.Implicits.global
    import scala.concurrent._
    import scala.concurrent.duration._
    Gigahorse
      .withHttp(Gigahorse.config) { http =>
        // Stacks are only be published to jcenter
        val librarySearchResults =
          http
            .run(MavenSearch(module).search,
                 Gigahorse.asString andThen (s => {println(s); s}) andThen decode[List[ResolvedModule]])
            .map(_.map(_.filter(_.repo == "jcenter")))
        Await.result(librarySearchResults, 90.seconds)
      }
      .toOption
      .get
  }
}

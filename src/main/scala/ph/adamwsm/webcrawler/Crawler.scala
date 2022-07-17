package ph.adamwsm.webcrawler

import cats.effect._
import cats.syntax.all._
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import org.http4s.client.Client
import io.circe.syntax._
import org.http4s.circe._
import scala.concurrent.ExecutionContext
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.server._

case class Links(urls: List[String])

object Links {
  implicit val linksDecoder: Decoder[Links] = deriveDecoder[Links]
  implicit def linksEntityDecoder[F[_]: Concurrent]: EntityDecoder[F, Links] =
    jsonOf
}

sealed abstract class Link

case class Page(url: String, data: String) extends Link
object Page {
  implicit val encoder: Encoder[Page] = deriveEncoder[Page]
  implicit def entityEncoder[F[_]]: EntityEncoder[F, Page] =
    jsonEncoderOf
}

case class Error(url: String, msg: String) extends Link
object Error {
  implicit val encoder: Encoder[Error] = deriveEncoder[Error]
  implicit def entityEncoder[F[_]]: EntityEncoder[F, Error] =
    jsonEncoderOf
}

case class CrawlPages(result: Option[List[Page]], error: Option[List[Error]])
object CrawlPages {
  implicit val encoder: Encoder[CrawlPages] = deriveEncoder[CrawlPages]
  implicit def entityEncoder[F[_]]: EntityEncoder[F, CrawlPages] =
    jsonEncoderOf
}

class Crawler[F[_]](C: Client[F])(implicit F: Async[F]) extends Http4sDsl[F] {

  implicit val executionContext = ExecutionContext.global

  def routes: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case req @ POST -> Root / "api" / "crawl" =>
        for {
           links <- req.as[Links]
           pages <- page(links.urls)
           crawlPages = transform(pages)
           resp <- Ok(crawlPages.asJson)
        } yield resp
    }

  def page(urls: List[String]): F[List[Link]] = urls.traverse(crawl)

  /**
   * Transform to result into response object
   */
  def transform(list: List[Link]): CrawlPages = {
    val pages = list.partitionMap(_ match {
      case p @ Page(_, _) => Right(p)
      case e @ Error(_, _) => Left(e)
    })
    val errors = Option(pages._1).filter(l => !l.isEmpty)
    CrawlPages(Option(pages._2), errors)
  }

  /**
   * Crawl the URL/Page
   * Add caching layer here for each URL/Page
   */
  def crawl(url: String): F[Link] =
    C.expect[String](url)
      .redeem(e => Error(url, e.getMessage), s => Page(url, s))

}

object Crawler {
  def apply[F[_]: Async](C: Client[F]): Crawler[F] =
    new Crawler[F](C)
}

package ch.epfl.scala.platform.releases

import java.nio.file.{Files, Paths}

import stoml.TomlParserApi._
import ch.epfl.scala.platform.releases.utils._
import fastparse.core.Parsed

import scala.collection.JavaConverters._
import scala.util.Try

object ModulesReader {
  def read(presumedFile: String): ReleaseResult[Seq[Module]] = {
    val readContent = Try {
      val allLines = Files.readAllLines(Paths.get(presumedFile))
      allLines.asScala.mkString("\n")
    }.toReleaseResult(Feedback.UnexpectedFileArgument)
    val tomlContent: ReleaseResult[TomlContent] = {
      readContent.right.flatMap { content =>
        toToml(content) match {
          case s: Parsed.Success[TomlContent] =>
            Right(s.value)
          case f: Parsed.Failure =>
            val msg = s"${Feedback.UnexpectedFileArgument}\n${f.msg}"
            Left(Error(msg, None))
        }
      }
    }
/*    tomlContent.right.map { content =>
      content.c.map { t =>
        val (key, node) = t
        key.endIndex

      }
    }*/
    ???
  }
}

package ch.epfl.scala.platform.releases

import java.nio.file.{Files, Paths}

import stoml.TomlParserApi._
import ch.epfl.scala.platform.releases.util._
import fastparse.core.Parsed
import stoml.Toml

import scala.collection.JavaConverters._
import scala.util.Try

import cats.instances.all._
import cats.syntax.traverse._

object ModulesReader {
  private def readAttribute(
      attribute: String,
      data: Map[String, Toml.Elem]): ReleaseResult[String] = {
    data
      .get(attribute)
      .toRight(Error(Feedback.missingModuleAttribute(attribute)))
      .right
      .flatMap {
        case Toml.Str(idValue) => Right(idValue)
        case tomlElem =>
          val msg = Feedback.unexpectedAttribute(attribute, tomlElem, "string")
          Left(Error(msg))
      }
  }

  /** Parse a TOML file with the specification of every module. */
  def read(presumedFile: String): ReleaseResult[List[Module]] = {
    val readContent = Try {
      val allLines = Files.readAllLines(Paths.get(presumedFile))
      allLines.asScala.mkString("\n")
    }.toReleaseResult(Feedback.UnexpectedFileArgument)
    val tomlContent: ReleaseResult[TomlContent] = {
      readContent.right.flatMap { content =>
        parseToml(content) match {
          case s: Parsed.Success[TomlContent, _, _] =>
            Right(s.value)
          case f: Parsed.Failure[_, _] =>
            val msg = s"${Feedback.UnexpectedFileArgument}\n${f.msg}"
            Left(Error(msg))
        }
      }
    }
    tomlContent.right.flatMap { content =>
      val nodes = content.childOf("modules").toList
      nodes.map {
        case Toml.Table((moduleKey, keyValues)) =>
          val moduleName = moduleKey.stripPrefix("modules.")
          val id = readAttribute("id", keyValues)
          val repo = readAttribute("repo", keyValues)
          for {
            idValue <- id.right
            repoValue <- repo.right
          } yield Module(moduleName, repoValue)
        case x =>
          val msg = Feedback.foundUnexpectedElement(x, "Toml.Table")
          Left(Error(msg))
      }.sequenceU
    }
  }
}

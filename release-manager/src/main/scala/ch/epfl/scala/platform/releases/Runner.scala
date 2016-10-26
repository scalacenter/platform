package ch.epfl.scala.platform.releases

import ch.epfl.scala.platform.releases.util.{Error, ReleaseResult}

import cats.instances.all._
import cats.syntax.traverse._

/** Read modules and release nightly, beta and stable for each of them. */
object Runner {
  def main(args: Array[String]): Unit = {
    val result: ReleaseResult[List[String]] = if (args.length == 1) {
      ModulesReader.read(args(0)).right.flatMap { modules =>
        modules.map(m => ReleaseManager(m).releaseEverything).sequenceU
      }
    } else Left(Error(Feedback.ExpectedOnlyOneArgument))
    result.fold(e => Console.err.println(e), s => Console.println(s))
  }
}

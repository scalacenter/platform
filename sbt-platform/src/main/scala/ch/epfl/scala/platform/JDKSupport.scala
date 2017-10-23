package ch.epfl.scala.platform

import sbt.{AutoPlugin, Def, PluginTrigger, Plugins, Keys, Compile, Test}
import sbt.librarymanagement.CrossVersion
import java.io.File

object JDKSupport extends AutoPlugin {
  val autoImport = JDKAutoImported
  override def trigger: PluginTrigger = allRequirements
  override def requires: Plugins = ch.epfl.scala.platform.PlatformPlugin
  override def globalSettings: Seq[Def.Setting[_]] = JavaImplementations.globalSettings
  override def buildSettings: Seq[Def.Setting[_]] = super.buildSettings
  override def projectSettings: Seq[Def.Setting[_]] = JavaImplementations.projectSettings
}

object JDKAutoImported extends JDKSupportKeys

trait JDKSupportKeys {
  import sbt.settingKey
  val platformJavaHome7 = settingKey[Option[File]]("The java home directory for JDK7.")
  val platformJavaHome8 = settingKey[Option[File]]("The java home directory for JDK8.")
  val platformJavaHome9 = settingKey[Option[File]]("The java home directory for JDK9.")
}

object JavaImplementations {
  import sbt.file
  import ch.epfl.scala.platform.{JDKAutoImported => ThisPluginKeys}
  import ch.epfl.scala.platform.PlatformPlugin.{autoImport => SbtPlatformKeys}

  val globalSettings: Seq[Def.Setting[_]] = List(
    ThisPluginKeys.platformJavaHome7 := Some(Defaults.JavaHome.JDK7),
    ThisPluginKeys.platformJavaHome8 := Some(Defaults.JavaHome.JDK8),
    ThisPluginKeys.platformJavaHome9 := Some(Defaults.JavaHome.JDK9),
  )

  val projectSettings: Seq[Def.Setting[_]] = SbtPlatformKeys.inCompileAndTest(
    Keys.javaHome := Defaults.javaHome.value,
    Keys.fork := Keys.javaHome.value.isDefined,
  )

  object Defaults {
    object JavaHome {
      // This is true only in scalaplatform/scala and scalaplatform/scala-publish
      val JDK7 = file("/usr/lib/jvm/jdk7")
      val JDK8 = file("/usr/lib/jvm/jdk8")
      val JDK9 = file("/usr/lib/jvm/jdk9")
    }

    private final val JDK_VERSION_ENV = "SCALA_PLATFORM_JDK_VERSION"
    private final val JDK_VERSION_PROPS = "scala.platform.jdk.version"
    def useJDK(version: String): Boolean = {
      sys.env.get(JDK_VERSION_ENV).exists(_ == version) ||
      sys.props.get(JDK_VERSION_PROPS).exists(_ == version)
    }

    private final val CURRENT_JAVA_HOME = sys.props("java.home")
    val javaHomeId = Keys.javaHome.key.label
    val javaHome: Def.Initialize[Option[File]] = Def.setting {
      val logger = Keys.sLog.value
      val scalaVersion = Keys.scalaVersion.value
      if (SbtPlatformKeys.platformInsideCi.value) {
        val (supposedHome, isPostJDK7) =
          if (useJDK("7")) (ThisPluginKeys.platformJavaHome7.value, false)
          else if (useJDK("9")) (ThisPluginKeys.platformJavaHome9.value, true)
          else (ThisPluginKeys.platformJavaHome8.value, true) // default is JDK8

        supposedHome.flatMap {
          case home if home.exists() && CURRENT_JAVA_HOME != home.getAbsolutePath() =>
            if (isPostJDK7) {
              // The JDK home is correct, but let's check that it's valid with our scala version
              CrossVersion.partialVersion(scalaVersion) match {
                case Some((2, n)) if n >= 12 => supposedHome
                case Some((2, n)) =>
                  logger.error(s"Scala version $scalaVersion needs JDK8 or previous."); supposedHome
                case n @ None => logger.error(s"Unrecognised scala version $scalaVersion."); n
              }
            } else supposedHome
          case home =>
            if (!home.exists()) logger.warn(s"`$javaHomeId` is undefined; '$home' does not exist.")
            else logger.warn(s"`$javaHomeId` is undefined; it's the same as the running JDK.")
            None
        }
      } else None
    }
  }
}

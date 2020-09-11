import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.LoggerFactory

object Hello extends App {

  // Logback SLF4J for logging debug, info, and warn statements
  val logger = LoggerFactory.getLogger("Hello")
  // Load Typesafe Configuration Library for input
  val config : Config = ConfigFactory.load("lightbend.conf")

  logger.info("Hello, my name is " + config.getString("conf.name"))
}

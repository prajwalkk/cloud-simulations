import com.typesafe.config.{Config, ConfigFactory}
import evaluate_example_datacenters.ExampleDataCentersEval
import org.slf4j.LoggerFactory

object Main extends App {

  // Logback SLF4J for logging debug, info, and warn statements
  val logger = LoggerFactory.getLogger("Hello")
  // Load Typesafe Configuration Library for input
  val config : Config = ConfigFactory.load("lightbend.conf")

  // Evaluate two data centers that are both performing in their own simulations
  ExampleDataCentersEval.start()
}

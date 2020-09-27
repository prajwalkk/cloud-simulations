package com.prajwalkk.cloudsim.utils.ProviderUtilities

import java.io.InvalidClassException

import com.prajwalkk.cloudsim.utils.ProviderModels.SwitchModel
import com.typesafe.scalalogging.LazyLogging
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.network.NetworkDatacenter
import org.cloudbus.cloudsim.hosts.network.NetworkHost
import org.cloudbus.cloudsim.network.switches.{AggregateSwitch, EdgeSwitch, RootSwitch, Switch}

import scala.jdk.CollectionConverters._
import scala.reflect.runtime.universe._


/*
*
* Created by: prajw
* Date: 23-Sep-20
*
*/
object SwitchUtils extends LazyLogging {

  def connectRootSwitchToAggregate(rootSwitch: RootSwitch, aggregateSwitches: List[AggregateSwitch]) = {
    for (aggregateSwitch <- aggregateSwitches) {
      aggregateSwitch.getUplinkSwitches.add(rootSwitch)
      rootSwitch.getDownlinkSwitches.add(aggregateSwitch)
    }
  }

  /**
   *
   * @param sim
   * @param edgeSwitchModel
   * @param datacenter
   * @return
   */
  def createEdgeSwitch(sim: CloudSim, edgeSwitchModel: SwitchModel, datacenter: NetworkDatacenter): List[EdgeSwitch] = {
    val edgeSwitches = (0 until edgeSwitchModel.number).map {
      i =>
        val edgeSwitch = createSwitch[EdgeSwitch](
          sim,
          datacenter,
          edgeSwitchModel.numPorts,
          edgeSwitchModel.bw,
          edgeSwitchModel.switchingDelay,
        )
        // Add to dc
        datacenter.addSwitch(edgeSwitch)
        // Add the edgeswitch to the list of edgeswitches
        edgeSwitch
    }.toList
    edgeSwitches
  }

  /**
   * Abstract method to create Switch of specified type
   *
   * @param sim
   * @param datacenter
   * @param ports
   * @param bw
   * @param switchingDelay
   * @param b
   * @tparam T
   * @return
   */
  def createSwitch[T <: Switch](sim: CloudSim, datacenter: NetworkDatacenter, ports: Int, bw: Long, switchingDelay: Double)(implicit b: TypeTag[T]): T = {
    val switch = (
      b.tpe.toString match {
        case "org.cloudbus.cloudsim.network.switches.EdgeSwitch" => new EdgeSwitch(sim, datacenter)
        case "org.cloudbus.cloudsim.network.switches.AggregateSwitch" => new AggregateSwitch(sim, datacenter)
        case "org.cloudbus.cloudsim.network.switches.RootSwitch" => new RootSwitch(sim, datacenter)
        case _ => throw new InvalidClassException(s"Not a proper switch")
      }
      ).asInstanceOf[T]

    switch.setPorts(ports)
    switch.setDownlinkBandwidth(bw)
    switch.setUplinkBandwidth(bw)
    switch.setSwitchingDelay(switchingDelay)

    switch
  }

  /**
   *
   * @param sim
   * @param aggregateSwitchModel
   * @param datacenter
   * @return
   */
  def createAggregateSwitch(sim: CloudSim, aggregateSwitchModel: SwitchModel, datacenter: NetworkDatacenter): List[AggregateSwitch] = {
    val aggregateSwitches = (0 until aggregateSwitchModel.number).map {
      i =>
        val aggregateSwitch = createSwitch[AggregateSwitch](
          sim,
          datacenter,
          aggregateSwitchModel.numPorts,
          aggregateSwitchModel.bw,
          aggregateSwitchModel.switchingDelay,
        )
        // add to dc
        datacenter.addSwitch(aggregateSwitch)
        // add the switch object to the list of aggregateSwitches
        aggregateSwitch
    }.toList
    aggregateSwitches
  }

  // I know I am supposed to create a single funtion to
  // create all 3 types switches, But I am unable to at this moment
  // as I am still learning scala

  /**
   *
   * @param sim
   * @param rootSwitchModel
   * @param datacenter
   * @return
   */
  def createRootSwitch(sim: CloudSim, rootSwitchModel: SwitchModel, datacenter: NetworkDatacenter): RootSwitch = {
    val rootSwitch = createSwitch[RootSwitch](sim, datacenter, rootSwitchModel.numPorts, rootSwitchModel.bw, rootSwitchModel.switchingDelay)
    logger.info(rootSwitch.getClass.toString)
    rootSwitch
  }

  /**
   * // Taken from NetworkVmExampleAbstract.java from official examples
   *
   * @param datacenter
   * @param edgeSwitches
   * @param edgeSwitchModel
   */
  def connectEdgeSwitchesToHost(datacenter: NetworkDatacenter, edgeSwitches: List[EdgeSwitch], edgeSwitchModel: SwitchModel): Unit = {
    datacenter.getHostList[NetworkHost].asScala.foreach { host =>
      val switchNum = getSwitchIndex(host, edgeSwitchModel.numPorts)
      logger.info(s"Switch Num: $switchNum")
      edgeSwitches(switchNum).connectHost(host)
    }
  }

  /**
   * Taken from NetworkVmExampleAbstract in the official cloudsim examples
   *
   * Gets the index of a switch where a Host will be connected,
   * considering the number of ports the switches have.
   * Ensures that each set of N Hosts is connected to the same switch
   * (where N is defined as the number of switch's ports).
   * Since the host ID is long but the switch array index is int,
   * the module operation is used safely convert a long to int
   * For instance, if the id is 2147483648 (higher than the max int value 2147483647),
   * it will be returned 0. For 2147483649 it will be 1 and so on.
   *
   * @param host        the Host to get the index of the switch to connect to
   * @param switchPorts the number of ports (N) the switches where the Host will be connected have
   * @return the index of the switch to connect the host
   */
  def getSwitchIndex(host: NetworkHost, switchPorts: Int): Int = Math.round(host.getId % Int.MaxValue) / switchPorts

  def connectAggregateSwitchToEdgeSwitch(edgeSwitches: List[EdgeSwitch], aggregateSwitches: List[AggregateSwitch]) = {
    edgeSwitches.zipWithIndex.foreach { case (edgeSwitch, index) => {
      val aggregateSwitch = aggregateSwitches(index / aggregateSwitches.head.getPorts)
      edgeSwitch.getUplinkSwitches.add(aggregateSwitch)
      aggregateSwitch.getDownlinkSwitches.add(edgeSwitch)
    }

    }

  }
}

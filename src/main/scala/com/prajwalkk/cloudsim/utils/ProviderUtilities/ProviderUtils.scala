package com.prajwalkk.cloudsim.utils.ProviderUtilities

import com.prajwalkk.cloudsim.utils.ProviderModels._
import com.prajwalkk.cloudsim.utils.ServiceModels.IaasServiceModel
import com.typesafe.config.{Config, ConfigBeanFactory}
import com.typesafe.scalalogging.LazyLogging
import org.cloudbus.cloudsim.allocationpolicies._
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.network.NetworkDatacenter
import org.cloudbus.cloudsim.datacenters.{Datacenter, DatacenterSimple}
import org.cloudbus.cloudsim.hosts.Host
import org.cloudbus.cloudsim.hosts.network.NetworkHost
import org.cloudbus.cloudsim.network.topologies.{BriteNetworkTopology, NetworkTopology}
import org.cloudbus.cloudsim.resources.{Pe, PeSimple}
import org.cloudbus.cloudsim.schedulers.vm.{VmSchedulerSpaceShared, VmSchedulerTimeShared}
import com.prajwalkk.cloudsim.utils.ProviderUtilities.SwitchUtils._

import scala.jdk.CollectionConverters._
import scala.util.Random

/*
*
* Created by: prajw
* Date: 19-Sep-20
*
*/
object ProviderUtils extends LazyLogging {

  def getRandomIndex(start: Int, end: Int): Int = {
    val rnd = new Random
    start + rnd.nextInt((end - start) + 1)
  }

  def loadDatacenterFromConfig2(conf: Config) = {
    val providerModel = conf.getString("model-type") match {
      case "IaaS" => {
        logger.trace(s"loading Iaas $conf")
        IaaSModel(loadDatacenterFromConfig(conf))
      }
      case "SaaS" => {
        logger.trace(s"loading Saas $conf")
        SaaSModel(loadDatacenterFromConfig(conf), loadVmFromConfig(conf), loadCloudletsFromConfig(conf))
      }
      case "PaaS" => {
        logger.trace(s"loadingPaas $conf")
        PaaSModel(loadDatacenterFromConfig(conf), loadVmFromConfig(conf))
      }

    }
    providerModel
  }


  def loadCloudletsFromConfig(conf: Config): CloudletModel =
    ConfigBeanFactory.create(conf.getConfig("cloudlet-chars"), classOf[CloudletModel])


  def loadVmFromConfig(conf: Config): VMModel = {
    ConfigBeanFactory.create(conf.getConfig("vm-chars"), classOf[VMModel])
  }

  /**
   * Creates the Datacenter from the config file passed from the main
   *
   * @param conf
   * @return List of DataCenterModel objects
   */
  def loadDatacenterFromConfig(conf: Config): List[DataCenterModel] = {
    logger.trace(s"funct: loadDataCenterFromConfig(${conf})")
    conf
      .getConfigList("datacenters")
      .asScala.map {
      ConfigBeanFactory.create(_, classOf[DataCenterModel])
    }.toList
  }

  def configureNetworkTopology(simulation: CloudSim, datacenterList: List[Datacenter]): Unit = {
    val networkTopology: NetworkTopology = BriteNetworkTopology.getInstance("topology.brite")
    simulation.setNetworkTopology(networkTopology)
    networkTopology.mapNode(datacenterList.head.getId, 0)
    networkTopology.mapNode(datacenterList.tail.head.getId, 1)
    networkTopology.mapNode(datacenterList.tail.tail.head.getId, 2)
  }

  def createPaasDataCenter(simulation: CloudSim, paasCenterList: PaaSModel): List[Datacenter] = {
    logger.trace(s"Func: createPaas($simulation, $paasCenterList")
    val dataCenters: List[DataCenterModel] = paasCenterList.datacenters
    createDataCenters(simulation, dataCenters)
  }

  def createSaasDataCenter(simulation: CloudSim, SaaSCenterList: SaaSModel): List[Datacenter] = {
    logger.trace(s"Func: createSaas($simulation, $SaaSCenterList")
    val dataCenters = SaaSCenterList.datacenters
    createDataCenters(simulation, dataCenters)
  }

  /**
   * Using The datacenter Models, The datacenters are created
   *
   * @param simulation     CloudSim Object
   * @param dataCenterList DataCenterModel object list created from config
   * @return List of Datacenters
   */
  def createDataCenters(simulation: CloudSim, dataCenterList: List[DataCenterModel]): List[Datacenter] = {
    logger.trace(s"Func: createIaasDataCenters(${simulation}, ${dataCenterList})")

    dataCenterList.map { dc =>
      val hostList = createHostList(dc.hosts)

      // Check type and create that DataCenter
      val dataCenter = dc.dcType match {
        case "Simple" => new DatacenterSimple(simulation, hostList.asJava)
        case "Network" => new NetworkDatacenter(simulation, hostList.asJava, getVmAllocationPolicy(dc.vmAllocationPolicy))
        case _ => new DatacenterSimple(simulation, hostList.asJava)
      }

      // Set all the costs
      dataCenter.getCharacteristics
        .setCostPerBw(dc.costPerBw)
        .setCostPerMem(dc.costPerMem)
        .setCostPerSecond(dc.costPerSecond)
        .setCostPerStorage(dc.costPerStorage)

      // If network datacenter, also build the network switches
      if (dc.dcType == "Network")
        createNetwork(
          simulation,
          dataCenter.asInstanceOf[NetworkDatacenter],
          dc.edgeSwitch,
          dc.rootSwitch,
          dc.aggregateSwitch
        )
      // return the datacenter to the Map
      dataCenter
    } //end of map

  }

  def getVmAllocationPolicy(policy: String): VmAllocationPolicy = {
    policy match {
      case "FirstFit" => new VmAllocationPolicyFirstFit()
      case "BestFit" => new VmAllocationPolicyBestFit()
      case "WorstFit" => new VmAllocationPolicyWorstFit()
      case _ => new VmAllocationPolicySimple()
    }
  }

  /**
   *
   * @param hostList
   * @return
   */
  def createHostList(hostList: List[HostModel]): List[Host] = {
    logger.trace(s"Func: createHostList(${hostList})")

    // Create a single dimensional list of hosts
    hostList.flatMap { h =>
      (1 to h.number).map { _ =>
        createHost(h.cores, h.mips, h.ram, h.storage, h.bw, h.vmScheduler)
      }
    }
  }

  /**
   * Creates host with specified Scheduler as in configuration
   *
   * @param cores       Number of cores
   * @param mips        Max instructions per second
   * @param ram         Ram value in MB
   * @param storage     storage size in MB
   * @param bw          bandwidth in mbps
   * @param vmScheduler "TimeShared" or "SpaceShared" with default as timeShared
   * @return Host object with specified parameters
   */
  def createHost(cores: Int, mips: Double, ram: Long, storage: Long, bw: Long, vmScheduler: String): Host = {
    val peList = (1 to cores).map { _ =>
      new PeSimple(mips).asInstanceOf[Pe]
    }.toList

    val host = new NetworkHost(ram, bw, storage, peList.asJava)
    host.setVmScheduler(vmScheduler match {
      case "SpaceShared" =>
        logger.info("Choosing SpaceShared")
        new VmSchedulerSpaceShared()
      case "TimeShared" =>
        logger.info("Choosing TimeShared")
        new VmSchedulerTimeShared()
      case _ => new VmSchedulerTimeShared()
    })
  }

  /**
   *
   * @param sim
   * @param datacenter
   * @param edgeSwitchModel
   * @param rootSwitchModel
   * @param aggregateSwitchModel
   */
  def createNetwork(sim: CloudSim, datacenter: NetworkDatacenter, edgeSwitchModel: SwitchModel, rootSwitchModel: SwitchModel, aggregateSwitchModel: SwitchModel) = {
    val edgeSwitches = createEdgeSwitch(sim, edgeSwitchModel, datacenter)
    logger.info(s"$edgeSwitches")
    val rootSwitch = createRootSwitch(sim, rootSwitchModel, datacenter)
    val aggregateSwitches = createAggregateSwitch(sim, aggregateSwitchModel, datacenter)

    // We have to connect the Root switch to aggregate switches
    connectRootSwitchToAggregate(rootSwitch, aggregateSwitches)
    // connect all host to edgeSwitches
    connectEdgeSwitchesToHost(datacenter, edgeSwitches, edgeSwitchModel)

  }

  def createIaasDataCenter(simulation: CloudSim, iaasCenterList: IaaSModel, iaasServiceModel: IaasServiceModel): List[Datacenter] = {
    logger.trace(s"Func: createSaas($simulation, $iaasCenterList")
    val dataCenters: List[DataCenterModel] = iaasCenterList.datacenters
    // total control
    createDataCenters(simulation, dataCenters)
  }
}

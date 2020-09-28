package com.prajwalkk.cloudsim

import java.lang.System.Logger.Level

import ch.qos.logback.classic
import com.prajwalkk.cloudsim.utils.ProviderModels.{IaaSModel, PaaSModel, SaaSModel}
import com.prajwalkk.cloudsim.utils.ProviderUtilities.ProviderUtils._
import com.prajwalkk.cloudsim.utils.ServiceModels.{IaasServiceModel, PaasServiceModel, SaasServiceModel}
import com.prajwalkk.cloudsim.utils.ServiceUtilities.ServiceUtils
import com.prajwalkk.custombuilder.CustomBuilder
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging
import org.cloudbus.cloudsim.brokers.{DatacenterBroker, DatacenterBrokerSimple}
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.Datacenter
import org.cloudbus.cloudsim.hosts.Host
import org.cloudsimplus.builders.tables.CloudletsTableBuilder
import org.cloudsimplus.util.Log

import scala.jdk.CollectionConverters._

/*
*
* Created by: prajw
* Date: 25-Sep-20
*
*/
object Simulation5 extends App with LazyLogging {


  val iaasConfig: Config = ConfigFactory.load("simulation_5/sim5_iaas.conf")

  Log.setLevel(classic.Level.INFO)
  val saasConfig: Config = ConfigFactory.load("simulation_5/sim5_saas.conf")
  val paasConfig: Config = ConfigFactory.load("simulation_5/sim5_paas.conf")
  // Load all the provider configs
  val saasModel = loadDatacenterFromConfig2(saasConfig.getConfig("simulation-five")).asInstanceOf[SaaSModel]
  val paasModel = loadDatacenterFromConfig2(paasConfig.getConfig("simulation-five")).asInstanceOf[PaaSModel]
  val iaasModel = loadDatacenterFromConfig2(iaasConfig.getConfig("simulation-five")).asInstanceOf[IaaSModel]
  // Load all the Client configs
  val iaasServiceModel = new IaasServiceModel("simulation_5/iaas_service.conf")
  val paasServiceModel = new PaasServiceModel("simulation_5/paas_service.conf")
  val saasServiceModel = new SaasServiceModel("simulation_5/saas_service.conf")
  val simulation = new CloudSim()
  // create the Datacenters and the topology
  val SaaSDC = createSaasDataCenter(simulation, saasModel)
  val PaaSDC = createPaasDataCenter(simulation, paasModel)
  logger.info(s"SaaS $SaaSDC")
  val IaasDC = createIaasDataCenter(simulation, iaasModel, iaasServiceModel)
  logger.info(s"PaaS $PaaSDC")
  // load create a service util object
  val serviceUtil: ServiceUtils = ServiceUtils(paasServiceModel, iaasServiceModel, saasServiceModel)
  logger.info(s"IaaS $IaasDC")
  configureNetworkTopology(simulation, SaaSDC ::: PaaSDC ::: IaasDC)
  logger.info("The entities in this simulation are: $simulation.getEntityList.toString")
  // create the broker
  // create VMs. 0 and null defines the control of the provider (that is, they do not have)
  // Iaas has full control of the sizes and the number of VMs they need and can create
  val iaasVms = serviceUtil.createVMList(0, null, "IaaS")
  // saas has no control of the VMs they need and can create. All the provider configs are passed
  val saasVms = serviceUtil.createVMList(saasModel.getVmChars.getVmNumber, saasModel.getVmChars, "SaaS")
  iaasVms.foreach(vm => vm.setHost(getRandomHostInDC(IaasDC.head)))
  // paas has control of the number of VMs but not the configurations they need and can create. All the provider configs are passed
  val paasVms = serviceUtil.createVMList(0, paasModel.getVmChars, "PaaS")
  saasVms.foreach(vm => vm.setHost(getRandomHostInDC(SaaSDC.head)))
  // load cloudlets using the service configs. add tasks
  val iaasCloudlets = serviceUtil.createCloudlets(null, "IaaS")
  paasVms.foreach(vm => vm.setHost(getRandomHostInDC(PaaSDC.head)))
  val saasCloudlets = serviceUtil.createCloudlets(saasModel.getCloudletChars, "SaaS")
  val paasCloudlets = serviceUtil.createCloudlets(null, "PaaS")
  val broker = new DatacenterBrokerSimple(simulation)
  val finishedCloudlets = broker.getCloudletCreatedList

  broker.submitVmList((iaasVms ::: saasVms ::: paasVms).asJava)
  broker.setVmDestructionDelayFunction(vm => 10.0)
  broker.submitCloudletList((iaasCloudlets ::: paasCloudlets ::: saasCloudlets).asJava)

  saasVms.map(println)

  simulation.start()

  def getRandomHostInDC(DC: Datacenter): Host = {
    DC.getHost(getRandomIndex(0, (DC.getHostList.size()) - 1))
  }
  new CustomBuilder(finishedCloudlets).build()


}

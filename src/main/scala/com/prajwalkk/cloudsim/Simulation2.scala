package com.prajwalkk.cloudsim

import com.prajwalkk.cloudsim.utils.ServiceUtilities.SimpleJob
import com.prajwalkk.cloudsim.utils.ProviderUtilities.ProviderUtils._
import com.prajwalkk.cloudsim.utils.ServiceModels.{IaasServiceModel, PaasServiceModel}
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudsimplus.builders.tables.CloudletsTableBuilder

import scala.jdk.CollectionConverters._

/*
*
* Created by: prajw
* Date: 14-Sep-20
*
*/
object Simulation2 extends LazyLogging {

  def main(args: Array[String]): Unit = {
    val config: Config = ConfigFactory.load("simulation2.conf")
    val simConf = config.getConfig("simulation-two")
    val serviceModel = new IaasServiceModel("service1.conf")
    logger.info(s"${simConf.getString("description")}")
    val simulation = new CloudSim()
    val datacenterModelList = loadDatacenterFromConfig(simConf)
    createDataCenters(simulation, datacenterModelList)
    val broker = new DatacenterBrokerSimple(simulation)
    val simpleJob = new SimpleJob(simulation, broker, serviceModel)
    broker.submitVmList(simpleJob.createVMList.asJava)
    broker.submitCloudletList(simpleJob.createCloudletSimpleList.asJava)
    simulation.start()
    val finishedCloudlets = broker.getCloudletFinishedList
    new CloudletsTableBuilder(finishedCloudlets).build()
  }
}

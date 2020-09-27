package com.prajwalkk.cloudsimtest

import com.prajwalkk.cloudsim.Simulation5
import com.prajwalkk.cloudsim.utils.ProviderModels.{DataCenterModel, SaaSModel}
import com.prajwalkk.cloudsim.utils.ProviderUtilities.ProviderUtils.{createDataCenters, createHost, createSaasDataCenter, getVmAllocationPolicy, loadDatacenterFromConfig, loadDatacenterFromConfig2}
import com.prajwalkk.cloudsim.utils.ServiceModels.IaasServiceModel
import com.typesafe.config.{Config, ConfigFactory}
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.Datacenter
import org.cloudbus.cloudsim.hosts.Host
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers._

import scala.collection.immutable.List


/*
*
* Created by: prajw
* Date: 27-Sep-20
*
*/
class modelsTest extends AnyFunSuite{

  test("Test to check if host is created "){
    val cores = 3
    val mips: Double = 2.3
    val ram: Long = 2048
    val storage: Long = 1000
    val bw: Long = 1000
    val vmScheduler = "SpaceShared"
    val host = createHost(cores, mips, ram, storage, bw, vmScheduler)
    host shouldBe a [Host]
  }

  test(" Check Allocation policy object"){
    val policy = "FirstFit"
    val policycrea = getVmAllocationPolicy(policy)
    policycrea shouldBe a [VmAllocationPolicy]
  }

  test("Check if datacenter is created"){
    val saasConfig: Config = ConfigFactory.load("sim5_saas.conf")
    val saasModel = loadDatacenterFromConfig2(saasConfig.getConfig("simulation-test")).asInstanceOf[SaaSModel]
    val saasDC = createSaasDataCenter(new CloudSim(), saasModel)
    saasDC shouldBe a [List[Datacenter]]

  }

  test(" Getting a Random Host in DC"){
    val saasConfig: Config = ConfigFactory.load("sim5_saas.conf")
    val saasModel = loadDatacenterFromConfig2(saasConfig.getConfig("simulation-test")).asInstanceOf[SaaSModel]
    val saasDC = createSaasDataCenter(new CloudSim(), saasModel)
    val host = Simulation5.getRandomHostInDC(saasDC.head)
    host shouldBe a [Host]
  }

  test("Check if right model is created"){
    val iaasConfig: Config = ConfigFactory.load("sim_1_test.conf")
    val simConf = iaasConfig.getConfig("simulation-one")
    val simulation = new CloudSim()
    val datacenterModelList = loadDatacenterFromConfig(simConf)
    datacenterModelList shouldBe a [List[DataCenterModel]]
    val datacenters = createDataCenters(simulation, datacenterModelList)
    datacenters shouldBe a [List[Datacenter]]
  }



}

package com.prajwalkk.cloudsim.utils.ServiceModels

import com.prajwalkk.cloudsim.utils.ProviderModels.HostModel
import com.typesafe.config.{Config, ConfigFactory}

import scala.jdk.CollectionConverters._

/*
*
* Created by: prajw
* Date: 24-Sep-20
*
*/
class IaasServiceModel(simFile: String)  {

  val conf: Config = ConfigFactory.load(simFile)
  val vmConf: Config = conf.getObject("service.vm").toConfig
  val cloudletConf: Config = conf.getObject("service.cloudlet-chars").toConfig

  val CLOUDLET_NUMBER: Int = cloudletConf.getInt("number")
  val CLOUDLET_PES: Int = cloudletConf.getInt("pes")
  val CLOUDLET_MIN_LEN: Int = cloudletConf.getInt("minLength")
  val CLOUD_MAX_LEN: Int = cloudletConf.getInt("maxLength")
  val CLOUDLET_FILE_SIZE: Int = cloudletConf.getInt("fileSize")
  val CLOUDLET_OUTPUT_SIZE: Int = cloudletConf.getInt("outputSize")
  val CLOUDLET_RAM: Int = cloudletConf.getInt("ram")


  /**
   * IAAS also has control over the VMs and Host machine it needs
   */

  val VM_NUM: Int = vmConf.getInt("vm-number")
  val VM_MIPS: Int = vmConf.getInt("vm-mips")
  val VM_SIZE: Int = vmConf.getInt("vm-size")
  val VM_RAM: Int = vmConf.getInt("vm-ram")
  val VM_BW: Int = vmConf.getInt("vm-bw")
  val VM_PES: Int = vmConf.getInt("vm-pes")
  val VM_VMM: String = vmConf.getString("vm-vmm")


}

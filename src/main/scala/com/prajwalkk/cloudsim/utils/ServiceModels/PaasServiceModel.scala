package com.prajwalkk.cloudsim.utils.ServiceModels

import com.typesafe.config.{Config, ConfigFactory}

/*
*
* Created by: prajw
* Date: 16-Sep-20
*
*/

/**
 * Service and it's
 * specifications that
 * needs to be executed
 */
class PaasServiceModel(simFile: String){



  val conf: Config = ConfigFactory.load(simFile)
  val cloudletConf: Config = conf.getObject("service.cloudlet-chars").toConfig

  val VM_NUMBER: Int = conf.getInt("service.vm.vm-number")

  val CLOUDLET_NUMBER: Int = cloudletConf.getInt("number")
  val CLOUDLET_PES: Int = cloudletConf.getInt("pes")
  val CLOUDLET_MIN_LEN: Int = cloudletConf.getInt("minLength")
  val CLOUD_MAX_LEN: Int = cloudletConf.getInt("maxLength")
  val CLOUDLET_FILE_SIZE: Int = cloudletConf.getInt("fileSize")
  val CLOUDLET_OUTPUT_SIZE: Int = cloudletConf.getInt("outputSize")
  val CLOUDLET_RAM: Int = cloudletConf.getInt("ram")


}

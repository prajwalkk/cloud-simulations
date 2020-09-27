package com.prajwalkk.cloudsim.utils.ServiceModels

import com.typesafe.config.{Config, ConfigFactory}

/*
*
* Created by: prajw
* Date: 24-Sep-20
*
*/
class SaasServiceModel(simFile: String) {

  val conf: Config = ConfigFactory.load(simFile)
  val cloudletConf: Config = conf.getObject("service.cloudlet-chars").toConfig

  val CLOUDLET_NUMBER: Int = cloudletConf.getInt("number")
  val CLOUDLET_MIN_LEN: Int = cloudletConf.getInt("minLength")
  val CLOUD_MAX_LEN: Int = cloudletConf.getInt("maxLength")
  val CLOUDLET_RAM: Int = cloudletConf.getInt("ram")


}

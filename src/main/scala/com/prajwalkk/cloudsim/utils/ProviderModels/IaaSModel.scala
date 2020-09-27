package com.prajwalkk.cloudsim.utils.ProviderModels

import scala.jdk.CollectionConverters._


/*
*
* Created by: prajw
* Date: 14-Sep-20
*
* Spring style POJO class.
* vars are needed for this
* @BeanProperty is needed as typesafe makes java style bean
* var is needed, else the data members' value goes empty
* Class that holds configuration of
* a single datacenter
*
*
*/
case class IaaSModel(var datacenters: List[DataCenterModel]) {

  def getDatacenters: java.util.List[DataCenterModel] = datacenters.asJava

  def setDatacenters(datacenters: java.util.List[DataCenterModel]): Unit = {
    this.datacenters = datacenters.asScala.toList
  }


}


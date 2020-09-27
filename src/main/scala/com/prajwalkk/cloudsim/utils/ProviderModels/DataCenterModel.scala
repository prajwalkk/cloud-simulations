package com.prajwalkk.cloudsim.utils.ProviderModels

import scala.jdk.CollectionConverters._
import scala.beans.BeanProperty

/*
*
* Created by: prajw
* Date: 25-Sep-20
*
*/

class DataCenterModel( var hosts: List[HostModel],
                       @BeanProperty var costPerSecond: Double,
                       @BeanProperty var costPerMem: Double,
                       @BeanProperty var costPerStorage: Double,
                       @BeanProperty var costPerBw: Double,
                       @BeanProperty var dcType: String,
                       @BeanProperty var vmAllocationPolicy: String,
                       @BeanProperty var edgeSwitch: SwitchModel,
                       @BeanProperty var rootSwitch: SwitchModel,
                       @BeanProperty var aggregateSwitch: SwitchModel) {


  def getHosts: java.util.List[HostModel] = hosts.asJava

  def setHosts(hosts: java.util.List[HostModel]): Unit = {
    this.hosts = hosts.asScala.toList
  }


  def this() = this(null, 0, 0,0,0,"","", null, null, null)

  // override def toString = s"DataCenterModel($hosts, $costPerSecond, $costPerMem, $costPerStorage, $costPerBw, $dcType, $vmAllocationPolicy, $edgeSwitch, $rootSwitch, $aggregateSwitch)"
}

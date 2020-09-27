package com.prajwalkk.cloudsim.utils.ProviderModels

import scala.beans.BeanProperty
import scala.jdk.CollectionConverters._

/*
*
* Created by: prajw
* Date: 24-Sep-20
*
*/

/*
  the Number of hosts for this are fixed.
  The consumer has the ability to choose VMs, RESOURCES, cpu.
  The Provider creates new hosts on demand

  This is with respect to provider
 */
case class PaaSModel(var datacenters: List[DataCenterModel],
                     @BeanProperty var vmChars: VMModel
                    ) {

  def getDatacenters: java.util.List[DataCenterModel] = datacenters.asJava

  def setDatacenters(datacenters: java.util.List[DataCenterModel]): Unit = {
    this.datacenters = datacenters.asScala.toList
  }


  //override def toString: String = s"${vmChars.toString}, ${datacenters.map(_ => toString)}"

  def this() = this(null, null)


}

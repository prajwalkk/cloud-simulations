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
  All the parameters are set by the cloudProvider
  none are selected by USER

  there is a "has a" rather than is-a relationship with the datacenter.
  Might be a bad design decision.
  Any feed back to improve wold be helpful
 */
case class SaaSModel(var datacenters: List[DataCenterModel],
                     @BeanProperty var vmChars: VMModel,
                     @BeanProperty var cloudletChars: CloudletModel) {


  def getDatacenters: java.util.List[DataCenterModel] = datacenters.asJava

  def setDatacenters(datacenters: java.util.List[DataCenterModel]): Unit = {
    this.datacenters = datacenters.asScala.toList
  }

  // override def toString: String = s"${vmChars.toString}, ${cloudletChars.toString}, ${datacenters.map(_ => toString)}"

  def this() = this(null, null, null)
}
